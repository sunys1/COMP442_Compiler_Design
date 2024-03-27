package SyntacticAnalyzer;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.Scanner;
import java.util.Set;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Stack;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;
import LexicalAnalyzer.*;
import AstGeneration.Node;
import AstGeneration.SemanticActions;

/**
*
* @Description COMP442 A2
* @author Yizhou Sun 40056775
* @date Feb 16, 2024
*/

public class Parser {
	private Lexer lexer  = null;
	private PrintWriter errorWriter = null;
	private PrintWriter derivationWriter = null;
	private PrintWriter astWriter = null;
	private PrintWriter dotWriter = null;
	private Map<String, Set<String>> firstSets = new HashMap<>();
    private Map<String, Set<String>> followSets = new HashMap<>();
	private Map<String, Map<String, String>> parsingTable = new HashMap<>();
	public static String END_OF_STACK = "$", START_SYMBOL = "START";
	public String derivation = START_SYMBOL;
	private List<String> terminals = new ArrayList<>();
	private List<String> nonTerminals = new ArrayList<>();
	private Stack<String> parsingStack = new Stack<>();
	private Stack<Node> semanticStack = new Stack<>();
	private Token lookahead = null;

	public Parser(Lexer lexer, PrintWriter derivationWriter, PrintWriter errorWriter, PrintWriter astWriter, PrintWriter dotWriter) {
		this.lexer = lexer;
		this.derivationWriter = derivationWriter;
		this.errorWriter = errorWriter;
		this.astWriter = astWriter;
		this.dotWriter = dotWriter;
		
		// Initialize the First&Follow sets as well as the parsing table
		try {
			createFirstFollowSets();
			createParsingTable();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	
	public String getRule(String key1, String key2) {
		String rule = "";
		Map<String, String> rules = parsingTable.get(key1);
		
		if(rules != null) {
			rule = rules.get(key2);
		}else {
			errorWriter.println("Syntax error: Invalid term - " + key1 
								+ " not derivable in this grammar - line " + lexer.getLineNum());
		}
		
		if(rule == null) {
			errorWriter.println("Syntax error: Invalid term - " + key2 
					+ " not derivable in this grammar - line " + lexer.getLineNum());
		}
		
		errorWriter.flush();
		return rule;
	}
	
	private void inverseRHSMultiplePush(String rule) {
		if(!rule.contains("epsilon")) { // Ignore epsilon production
			String[] terms = rule.split("→")[1].trim().split(" ");
			
			for (int i = terms.length-1; i >= 0; i--) {
				parsingStack.push(terms[i]);
			}
		}	
	}
	
	public void logDerivation(String nonTerminal, String rule) {
		String target = rule.split("→")[1];
		if(target.contains("epsilon")) {
			// Remove the non-terminal from derivation if taking the epsilon path
			derivation = derivation.replaceFirst(" " + nonTerminal, "");
		}else {
			derivation = derivation.replaceFirst(nonTerminal, target.trim());
		}
		
		derivationWriter.println("=> " + derivation.trim());
		derivationWriter.flush();
	}
	
	private void skipErrors() {
		String errorMsg = "Syntax error at: " + lookahead.getLineNum() + " Token: " + lookahead.toString();
		errorWriter.println(errorMsg);
		errorWriter.flush();
		String top = parsingStack.peek(), 
			   tokenName = lookahead.getName(), 
			   tokenValue = lookahead.getValue();
		Set<String> firstOfTop = firstSets.get(top);
		Set<String> followOfTop = followSets.get(top);
		
		if (tokenValue.equals(END_OF_STACK) || (followOfTop != null && followOfTop.contains(tokenName))) {
			parsingStack.pop(); // pop - equivalent to A → ε
		} else {
			while ((firstOfTop == null || !firstOfTop.contains(tokenName)) || 
				   (firstOfTop != null && firstOfTop.contains("∅") &&  	 	 // Epsilon symbol
				   (followOfTop == null || !followOfTop.contains(tokenName)))) {
				
				lookahead = lexer.nextToken();
				while(lookahead == null) { // Temporary fix for null token 
					lookahead = lexer.nextToken();
				}
				tokenName = lookahead.getName(); 
				tokenValue = lookahead.getValue();
				
				// If skipping until the end of the file 
				if (tokenValue.equals(END_OF_STACK)) {
					break;
				}
			}
		}
	}
	
	public Node getASTRoot() {
		return this.semanticStack.peek();
	}
	
	public boolean parse() {
		boolean error = false;
		String rule = "", top="";
		parsingStack.push(END_OF_STACK);
		parsingStack.push(START_SYMBOL);
		lookahead = lexer.nextToken();
		derivationWriter.println(derivation);
		derivationWriter.flush();
		
		while(!parsingStack.peek().equals(END_OF_STACK)) {
			top = parsingStack.peek();

			if(terminals.contains(top)) { // Terminal
				if(top.equals(lookahead.getName())) {
					parsingStack.pop();
					lookahead = lexer.nextToken();
					while(lookahead == null) { // Temporary fix for null token 
						lookahead = lexer.nextToken();
					}
				}else {
					skipErrors();
					error = true;
				}
			}else if(nonTerminals.contains(top)) { // Non-terminal
				rule = getRule(top, lookahead.getName());
				if(rule!= null && !rule.isBlank()) {
					parsingStack.pop();
					inverseRHSMultiplePush(rule);
					logDerivation(top, rule);
				}else {
					skipErrors();
					error = true;
				}
			}else { // Semantic Action
				callSemanticAction(top, lookahead);
				parsingStack.pop();
			}
		}
				
		// Print AST Tree structure to the output file
		SemanticActions.createAstFile(semanticStack.peek(), astWriter);
		
		if(!lookahead.getName().equals(END_OF_STACK) || error) { // Unsuccessful parsing
			return false;
		}else {
			return true;
		}
	}
	
	public void createFirstFollowSets() throws IOException {
		File inputFile = new File("./input/parser/FirstAndFollowSets.html");
        Document doc = Jsoup.parse(inputFile, "UTF-8", "");
        Elements rows = doc.select("table.stats tr");
        boolean firstRow = true;
        Set<String> firstSet = null;
        Set<String> followSet = null;
        String[] firstTerminals = null; 
        String[] followTerminals = null;
        
        // Parse the HTML table
        for (Element row : rows) {
        	if(firstRow) {
        		firstRow = false; // Skip first row as it contains only column titles
        		continue;
        	}
        	
            Elements columns = row.select("td");
            if (!columns.isEmpty()) {
                String nonTerminal = columns.get(0).text();
                firstSet = new HashSet<>();
                followSet = new HashSet<>();

                // Parsing First set
                firstTerminals = columns.get(1).text().split(" ");
                for (String terminal : firstTerminals) {
                    firstSet.add(terminal);
                }

                // Parsing Follow set
                followTerminals = columns.get(2).text().split(" ");
                for (String terminal : followTerminals) {
                    followSet.add(terminal);
                }

                firstSets.put(nonTerminal, firstSet);
                followSets.put(nonTerminal, followSet);
            }
        }
    }
	
	public Map<String, Map<String, String>> createParsingTable() throws IOException {
        File input = new File("./input/parser/BNF_LL1_Parsing_Table.html");
        Document doc = Jsoup.parse(input, "UTF-8", "");
        Elements rows = doc.select("table.parse_table tr");

        // Extract terminals from the first row
        Elements terminalElements = rows.get(0).select("th");
        for (Element terminal : terminalElements) {
            terminals.add(terminal.text());
        }

        // Iterate over rows (skip the first row which is headers)
        for (int i = 1; i < rows.size(); i++) {
            Element row = rows.get(i);
            Elements columns = row.select("td");

            String nonTerminal = row.select("th").first().child(0).text();
            nonTerminals.add(nonTerminal);
            Map<String, String> rowMap = new HashMap<>();

            // Iterate over columns
            for (int j = 0; j < columns.size(); j++) {
                String terminal = terminals.get(j);
                String production = columns.get(j).text();
                rowMap.put(terminal, production);
            }

            parsingTable.put(nonTerminal, rowMap);
        }

        return parsingTable;
    }
	
	private void callSemanticAction(String action, Token lookahead) {
		String actionName = action.toUpperCase().replace("MAKENODE", "").replace("MAKEFAMILY", "");
		Boolean hasFixedChildren = SemanticActions.actionWithFixedChildren.containsKey(actionName);
		Boolean hasReptChildren = SemanticActions.actionWithReptChildren.contains(actionName);
		
		if(actionName.equals("DOTOP")) { // not the real dot operator
			// a temporary node just for checking if a dot was actually consumed
			Node node = SemanticActions.makeNode(actionName);
			semanticStack.push(node);
		}else if(actionName.equals("ADDOP") || actionName.equals("MULTOP")) {
			SemanticActions.makeNodeOp(semanticStack);
		}else if(actionName.equals("NOT") || actionName.equals("SIGN")) {
			SemanticActions.makeNodeFactor(semanticStack, actionName, lookahead);
		}else if(actionName.equals("EMPTYSIZE")) {
			semanticStack.push(SemanticActions.makeNode("actionName"));
		}else if(actionName.equals("PUSHNULL")) {
			semanticStack.push(null);
		}else if(hasFixedChildren) {
			int numChildren = SemanticActions.actionWithFixedChildren.get(actionName);
			SemanticActions.makeFamily(semanticStack, actionName, numChildren);
		}else if(hasReptChildren) {
			SemanticActions.makeFamily(semanticStack, actionName);
		}else{
			Node node = SemanticActions.makeNode(actionName);
			node.setToken(lookahead);
			semanticStack.push(node);
		}
	}
}
