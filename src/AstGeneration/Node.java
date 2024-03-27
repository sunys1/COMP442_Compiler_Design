package AstGeneration;

import java.io.IOException;
import java.util.ArrayList;
import java.util.LinkedList;
import LexicalAnalyzer.Token;
import SymbolTable.SymbolTable;
import SymbolTable.SymbolTableEntry;
import Visitor.Visitor;

/**
*
* @Description COMP442 A3
* @author Yizhou Sun 40056775
* @date Mar 9, 2024
*/

public class Node {
	// node attributes
	private String name;
	private Token token;
	private int value;
	private Node parent;
	private Node rightSibling;
	private Node leftMostSibling;
	private Node leftMostChild;
	private String kind;
	private String type;

	// symbol table
	public SymbolTableEntry symbolTableEntry;
	public SymbolTable symbolTable;

	public Node() {
		this.name = "";
		this.leftMostSibling = this;
		this.rightSibling = null;
	}

	public Node(String name) {
		this.name = name;
		this.leftMostSibling = this;
		this.rightSibling = null;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}
	
	public Token getToken () {
		return token;
	}

	public void setToken(Token token) {
		this.token = token;
	}
	
	public String getKind() {
		return kind;
	}
	
	public void setKind(String kind) {
		this.kind = kind;
	}
	
	public String getType() {
		return type;
	}
	
	public void setType(String type) {
		this.type = type;
	}
	
	public int getValue() {
		return value;
	}
	
	public void setValue(int value) {
		this.value = value;
	}
	
	public Node getLeftMostChild() {
		return this.leftMostChild;
	}
	
	public Node getRightSibling() {
		return this.rightSibling;
	}
	
	public Node makeSiblings(Node sibling) {
		Node curNode = this;
		// Find the right most node in this list
		while(curNode.rightSibling != null) {
			curNode = curNode.rightSibling;
		}
		// Join the lists
		Node sibs = sibling.leftMostSibling;
		curNode.rightSibling = sibs;
		// Set pointers for the new siblings
		sibs.leftMostSibling = curNode.leftMostSibling;
		sibs.parent = curNode.parent;
		
		while(sibs.rightSibling != null) {
			sibs = sibs.rightSibling;
			sibs.leftMostSibling = curNode.leftMostSibling;
			sibs.parent = curNode.parent;
		}
		
		return sibs;
	}
	
	public Node adoptChild(Node child) {
		if(this.leftMostChild != null){
			this.leftMostChild.makeSiblings(child);
		}else {
			Node childSibs = child.leftMostSibling;
			this.leftMostChild = childSibs;
			
			while(childSibs != null) {
				childSibs.parent = this;
				childSibs = childSibs.rightSibling;
			}
		}
		
		return this.leftMostChild;
	}
	
	
	public void accept(Visitor visitor) throws IOException {
		visitor.visit(this);
	}
}
