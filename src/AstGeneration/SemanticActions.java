package AstGeneration;

import LexicalAnalyzer.Token;

import java.util.HashMap;
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
	
	public static Node makeNode() {
		return new Node();
	}
	
	public static Node makeNode(int val) {
		Node node = new Node();
		node.setName("INTNUM");
		node.setValue(val);
		
		return node;
	}
	
	public static Node makeNode(String name) {
		Node node = new Node();
		node.setName(name);
		
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
	
	// make family for nodes having a list of children
	public static void makeFamily(Stack<Node> semanticStack, String name) {
        Node familyNode = makeNode(name);
        Node leftMostChild = null;
        
        if(semanticStack.peek() != null) {
        	leftMostChild = semanticStack.pop();
            familyNode.adoptChild(leftMostChild);
        }
        
        // Pop until null
        while (semanticStack.peek() != null) {
        	leftMostChild.makeSiblings(semanticStack.pop());
        }

        semanticStack.pop(); // Pop null
        semanticStack.push(familyNode);
    }
	
	// make family for nodes having a fixed number of children
	public static void makeFamily(Stack<Node> semanticStack, String name, int numChildren) {
        Node familyNode = makeNode(name);
        Node leftMostChild = semanticStack.pop();
        familyNode.adoptChild(leftMostChild);
        numChildren--;
        
        while (numChildren > 0) {
        	leftMostChild.makeSiblings(semanticStack.pop());
        	numChildren--;
        }
        
        semanticStack.push(familyNode);
    }
}
