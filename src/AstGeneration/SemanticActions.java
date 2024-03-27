package AstGeneration;

import LexicalAnalyzer.Token;
import java.io.PrintWriter;
import java.util.ArrayDeque;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.Queue;
import java.util.Stack;

public class SemanticActions {
	public static final HashMap<String, Integer> actionWithFixedChildren = new HashMap<>() {
		{
			put("ARITHEXPR", 1);
			put("RELEXPR", 3);
			put("FPARAM", 3);
			put("FUNCCALL", 2);
			put("VAR", 2);
			put("FUNCDEF", 2);
			put("FUNCHEAD", 3);
			put("FUNCDEF", 2);
			put("DOT", 2);
			put("IMPLDEF", 2);
			put("STAT", 1);
			put("IFSTAT", 3);
			put("WHILESTAT", 2);
			put("READSTAT", 1);
			put("WRITESTAT", 1);
			put("RETURNSTAT", 1);
			put("ASSIGNSTAT", 2);
			put("STRUCTDECL", 3);
			put("VARDECL", 3);
		} 
	};
	
	public static final ArrayList<String> actionWithReptChildren = new ArrayList<>() {
		{
			add("APARAMS");
			add("DIMLIST");
			add("FPARAMLIST");
			add("INDEXLIST");
			add("FUNCBODY");
			add("FUNCDEFLIST");
			add("PROG");
			add("STATBLOCK");
			add("INHERLIST");
			add("MEMBLIST");
		} 
	};
		
	public static Node makeNode() {
		return new Node();
	}
	
	public static Node makeNode(String name) {
		Node node = new Node(name);
		return node;
	}
	
	// make ADDOP / MULTOP node
	public static void makeNodeOp(Stack<Node> semanticStack) {
		Node child1 = semanticStack.pop();
        Node op = semanticStack.pop();
        Node child2 = semanticStack.pop();
        
        Node opNode = makeNode(op.getName());
        opNode.setToken(op.getToken());
        opNode.adoptChild(child2.makeSiblings(child1));
        semanticStack.push(opNode);
	}
	
	public static void makeNodeFactor(Stack<Node> semanticStack, String name, Token token) {
		Node factor = semanticStack.pop();
		Node op = semanticStack.pop(); // not or sign
        Node parentNode = makeNode(name);
        parentNode.setToken(op.getToken()); // assign operator token
        factor.setToken(token); // assign value token
        parentNode.adoptChild(factor);
        semanticStack.push(parentNode);
	}
	
	// make family for nodes having an unknown number of children
	public static void makeFamily(Stack<Node> semanticStack, String name) {
        Node familyNode = makeNode(name);
        ArrayDeque<Node> children = new ArrayDeque<>();
        
        if(!semanticStack.isEmpty() && semanticStack.peek() != null) {
            // Pop until null
            while (!semanticStack.isEmpty() && semanticStack.peek() != null) {
            	children.addFirst(semanticStack.pop());
            }
            
            // Adopt children in reverse order
            for (Node child : children) {
            	familyNode.adoptChild(child);
            }
        }
        
        if(!semanticStack.isEmpty() && semanticStack.peek() == null && semanticStack.size() > 1) {
    		semanticStack.pop();
    	}
       
        semanticStack.push(familyNode);
    }
	
	// make family for nodes having a fixed number of children
	public static Node makeFamily(Stack<Node> semanticStack, String name, int numChildren) {
        Node familyNode = makeNode(name);
        ArrayDeque<Node> children = new ArrayDeque<>();
        
        // handle special fCall case: e.g. id.id(a, b) => this is a fCall. 
        // But 'id' and 'id(a, b)' will be the children of a dot operator
        // Hence only 1 child instead of 2 for fCall in this case        
        if(name.equals("FUNCCALL")) {
        	Node child1 = semanticStack.peek();
        	if(child1.getName().equals("DOT")) {
        		familyNode.adoptChild(semanticStack.pop());
        		semanticStack.push(familyNode);
                return familyNode;
        	}
        }else if(name.equals("DOT")) {
        	// in 'statement', it's hard to put makeFamilyDot at the exact places needed
            // due to the repetition action and we don't know where a child of dot operator ends
            // so we push a makeNodeDotOp for every '.' and check if a '.' was actually consumed
            // every time we encounter a makeFamilyDot
        	Node child1 = semanticStack.pop();
        	Node child2 = semanticStack.peek();
        	
        	if(child2 != null && child2.getName().equals("DOTOP")) {
        		// pop the "DOTOP" temporary node
        		semanticStack.pop(); 
        		child2 = semanticStack.pop();
        		familyNode.adoptChild(child2.makeSiblings(child1));
        		semanticStack.push(familyNode);
        		return familyNode;
        	}else { 
        		// no '.' was consumed. Ignore the makeFamilyDot and restore child1
        		semanticStack.push(child1);
        		return familyNode;
        	}
        }
        
        while (numChildren > 0) {
        	if(semanticStack.peek() != null) {
        		children.addFirst(semanticStack.pop());
        	}else {
        		semanticStack.pop();
        	}	
        	numChildren--;
        }
        
        // Adopt children in reverse order
        for (Node child : children) {
        	familyNode.adoptChild(child);
        }
        
        semanticStack.push(familyNode);
        return familyNode;
    }
	
	// Create ast.outast file
	private static void traverseAstTree(PrintWriter pw, Node root, String indent) {
		if (root == null) {
	        return;
	    }
		
		pw.println(indent + root.getName());
		pw.flush();

	    // DFS Tree Traversal
	    // Find the deepest level
	    traverseAstTree(pw, root.getLeftMostChild(), indent + "| ");
	    // Recursively traverse the right siblings
	    traverseAstTree(pw, root.getRightSibling(), indent);
	}
	
	public static void createAstFile(Node root, PrintWriter pw) {
		traverseAstTree(pw, root, "");
	}
}
