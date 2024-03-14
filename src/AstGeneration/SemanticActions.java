package AstGeneration;

import LexicalAnalyzer.Token;

import java.io.PrintWriter;
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
        opNode.adoptChild(child1.makeSiblings(child2));
        semanticStack.push(opNode);
	}
	
	public static void makeNodeFactor(Stack<Node> semanticStack, String name, Token token) {
		Node factor = semanticStack.pop();
        Node parentNode = makeNode(name);
        parentNode.setToken(token);
        parentNode.adoptChild(factor);
        semanticStack.push(parentNode);
	}
	
	// make family for nodes having a list of children
	public static void makeFamily(Stack<Node> semanticStack, String name) {
        Node familyNode = makeNode(name);
        Node leftMostChild = null;
        
        if(!semanticStack.isEmpty() && semanticStack.peek() != null) {
        	leftMostChild = semanticStack.pop();
            familyNode.adoptChild(leftMostChild);
            
            // Pop until null
            while (!semanticStack.isEmpty() && semanticStack.peek() != null) {
            	leftMostChild.makeSiblings(semanticStack.pop());
            }
        	
        	if(!semanticStack.isEmpty() && semanticStack.peek() == null && semanticStack.size() > 1) {
        		semanticStack.pop();
        	}
        }else if(!semanticStack.isEmpty() && semanticStack.peek() == null && semanticStack.size() > 1) {
    		semanticStack.pop();
        }
       
        semanticStack.push(familyNode);
    }
	
	// make family for nodes having a fixed number of children
	public static void makeFamily(Stack<Node> semanticStack, String name, int numChildren) {
        Node familyNode = makeNode(name);
        Node leftMostChild = semanticStack.pop();
        familyNode.adoptChild(leftMostChild);
        numChildren--;
        
        while (numChildren > 0) {
        	if(semanticStack.peek() != null) {
        		leftMostChild.makeSiblings(semanticStack.pop());
        	}else {
        		semanticStack.pop();
        	}	
        	numChildren--;
        }
        
        semanticStack.push(familyNode);
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
