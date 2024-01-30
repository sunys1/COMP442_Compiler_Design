package LexicalAnalyzer;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Scanner;
import LexicalAnalyzer.State;
import LexicalAnalyzer.Token;

public class LexicalDrive {
		private static final String[] RESERVED_WORDS = {
			"if", "then", "else", "integer", "float", "void",
		    "public", "private", "func", "var", "struct", "while",
		    "read", "write", "return", "self", "inherits", "let", "impl"
	    };
		private static final String[] letters = "abcdefghijklmnopqrstuvwxyzABCDEFGHIJKLMNOPQRSTUVWXYZ".split("");
		private static final String[] digits = "0123456789".split("");
		private static final String[] nonzeros = "123456789".split("");
		private static ArrayList<String> reserved = new ArrayList<>(Arrays.asList(RESERVED_WORDS));
		private static ArrayList<State> states = new ArrayList<>();
		private static PrintWriter tokenWriter = null;
		private static PrintWriter errorWriter = null;
		private static Scanner fileScanner = null;
		private static String charBackUp = "";
		private static int lineNum = 1;

		public static void main(String[] args) {			
			try {
				File f = new File("lexpositivegrading.src");
				
				if(f.canRead()) {
					String fileName = f.getName();
					fileScanner = new Scanner(new FileInputStream(f));
					fileScanner.useDelimiter("");
					tokenWriter = new PrintWriter(new FileOutputStream(fileName + ".outlextokens"));
					errorWriter = new PrintWriter(new FileOutputStream(fileName + ".outlexerrors"));
					
					// Initialize the state transition table
					createStateMap();
					
					// Scan tokens
					while(fileScanner.hasNext()) {
						Token token = nextToken();
					}
					
				}else {
					System.out.println("Unable to read this file.");
				}
				
				fileScanner.close();
				tokenWriter.close();
				errorWriter.close();
			}catch(FileNotFoundException e) {
				e.printStackTrace();
			}
		}
		
		public static boolean isReservedWord(String word, ArrayList<String> reserved) {
			if(reserved.contains(word)) {
				return true;
			}
			
			return false;
		}
		
		public static Token createToken(String tokenName, String tokenValue, int lineNum) {
			Token token = new Token(tokenName, tokenValue, lineNum);
			System.out.println(token.toString());
			tokenWriter.println(token.toString());
			tokenWriter.flush();
			
			return token;
		}
		
		public static void createStateMap() {
			// Populate the table with the state transitions translated from the DFA
			HashMap<String, Integer> transitions = new HashMap<>();
			
			//State 1
			//1->2 on letter inputs
			for (String s : letters) {
				transitions.put(s, 2);
			}
			for (String s : nonzeros) {
				transitions.put(s, 3);
			}
			transitions.put("0", 11);
			transitions.put("+", 12);
			transitions.put("-", 13);
			transitions.put(".", 14);
			transitions.put("!", 15);
			transitions.put(",", 16);
			transitions.put(";", 17);
			transitions.put("->", 18);
			transitions.put("&", 19);
			transitions.put("*", 20);
			transitions.put("|", 21);
			transitions.put("(", 22);
			transitions.put("{", 23);
			transitions.put("[", 24);
			transitions.put(")", 25);
			transitions.put("}", 26);
			transitions.put("]", 27);
			transitions.put("=", 28);
			transitions.put(">", 30);
			transitions.put("<", 32);
			transitions.put(":", 35);
			transitions.put("/", 36);
			State s1 = new State(1, transitions);
			states.add(s1);
			transitions.clear();
			
			//State 2
			for (String s : letters) {
				transitions.put(s, 2);
			}
			for (String s : digits) {
				transitions.put(s, 2);
			}
			transitions.put("_", 2);
			State s2 = new State(2, transitions, true, "id", true);
			states.add(s2);
			transitions.clear();
			
			//State 3
			for (String s : digits) {
				transitions.put(s, 3);
			}
			transitions.put(".", 4);
			State s3 = new State(3, transitions, true, "intnum", true);
			states.add(s3);
			transitions.clear();
			
			//State4
			for (String s : digits) {
				transitions.put(s, 5);
			}
			State s4 = new State(4, transitions);
			states.add(s4);
			transitions.clear();
			
			//State5
			for (String s : nonzeros) {
				transitions.put(s, 5);
			}
			transitions.put("0", 6);
			transitions.put("e", 7);
			State s5 = new State(5, transitions, true, "floatnum", true);
			states.add(s5);
			transitions.clear();
			
			//State6
			for (String s : nonzeros) {
				transitions.put(s, 5);
			}
			transitions.put("0", 6);
			State s6 = new State(6, transitions);
			states.add(s6);
			transitions.clear();
			
			//State7
			transitions.put("+", 8);
			transitions.put("-", 8);
			State s7 = new State(7, transitions);
			states.add(s7);
			transitions.clear();
			
			//State8
			for (String s : nonzeros) {
				transitions.put(s, 9);
			}
			transitions.put("0", 10);
			State s8 = new State(8, transitions);
			states.add(s8);
			transitions.clear();
			
			//State9
			for (String s : digits) {
				transitions.put(s, 9);
			}
			State s9 = new State(9, transitions, true, "floatnum", true);
			states.add(s9);
			transitions.clear();
			
			//State10
			State s10 = new State(10, transitions, true, "floatnum", true);
			states.add(s10);
			transitions.clear();
			
			//State11
			transitions.put(".", 4);
			State s11 = new State(11, transitions, true, "intnum", true);
			states.add(s11);
			transitions.clear();
			
			//State12
			State s12 = new State(12, transitions, true, "plus", false);
			states.add(s12);
			transitions.clear();
			
			//State13
			transitions.put(">", 18);
			State s13 = new State(13, transitions, true, "minus", true);
			states.add(s13);
			transitions.clear();
			
			//State14
			State s14 = new State(14, transitions, true, "dot", false);
			states.add(s14);
			transitions.clear();
			
			//State15
			State s15 = new State(15, transitions, true, "not", false);
			states.add(s15);
			transitions.clear();
			
			//State16
			State s16 = new State(16, transitions, true, "comma", false);
			states.add(s16);
			transitions.clear();
			
			//State17
			State s17 = new State(17, transitions, true, "semi", false);
			states.add(s17);
			transitions.clear();
			
			//State18
			State s18 = new State(18, transitions, true, "arrow", false);
			states.add(s18);
			transitions.clear();
			
			//State19
			State s19 = new State(19, transitions, true, "and", false);
			states.add(s19);
			transitions.clear();
			
			//State20
			State s20 = new State(20, transitions, true, "mult", false);
			states.add(s20);
			transitions.clear();
			
			//State21
			State s21 = new State(21, transitions, true, "or", false);
			states.add(s21);
			transitions.clear();
			
			//State22
			State s22 = new State(22, transitions, true, "openpar", false);
			states.add(s22);
			transitions.clear();
			
			//State23
			State s23 = new State(23, transitions, true, "opencubr", false);
			states.add(s23);
			transitions.clear();
			
			//State24
			State s24 = new State(24, transitions, true, "opensqbr", false);
			states.add(s24);
			transitions.clear();
			
			//State25
			State s25 = new State(25, transitions, true,  "closepar", false);
			states.add(s25);
			transitions.clear();
			
			//State26
			State s26 = new State(26, transitions, true, "closecubr", false);
			states.add(s26);
			transitions.clear();
			
			//State27
			State s27 = new State(27, transitions, true, "closesqbr", false);
			states.add(s27);
			transitions.clear();
			
			//State28
			transitions.put("=", 29);
			State s28 = new State(28, transitions, true, "assign", true);
			states.add(s28);
			transitions.clear();
			
			//State29
			State s29 = new State(29, transitions, true, "eq", false);
			states.add(s29);
			transitions.clear();
			
			//State30
			transitions.put("=", 31);
			State s30 = new State(30, transitions, true, "gt", true);
			states.add(s30);
			transitions.clear();
			
			//State31
			State s31 = new State(31, transitions, true, "geq", false);
			states.add(s31);
			transitions.clear();
			
			//State32
			transitions.put("=", 33);
			transitions.put(">", 34);
			State s32 = new State(32, transitions, true, "lt", true);
			states.add(s32);
			transitions.clear();
			
			//State33
			State s33 = new State(33, transitions, true, "leq", false);
			states.add(s33);
			transitions.clear();
			
			//State34
			State s34 = new State(34, transitions, true, "noteq", false);
			states.add(s34);
			transitions.clear();
			
			//State35
			State s35 = new State(35, transitions, true, "colon", false);
			states.add(s35);
			transitions.clear();
			
			//State36
			transitions.put("/", 37);
			transitions.put("*", 39);
			State s36 = new State(36, transitions, true, "div", true);
			states.add(s36);
			transitions.clear();
			
			//State37
			transitions.put(System.lineSeparator(), 38); //37->38 on reading end-of-line
			State s37 = new State(37, transitions);
			states.add(s37);
			transitions.clear();
			
			//State38
			State s38 = new State(38, transitions, true, "inlinecmt", false);
			states.add(s38);
			transitions.clear();
			
			//State39
			transitions.put("*", 40);
			State s39 = new State(39, transitions);
			states.add(s39);
			transitions.clear();
			
			//State40
			transitions.put("/", 41);
			State s40 = new State(41, transitions);
			states.add(s40);
			transitions.clear();

			//State41
			State s41 = new State(41, transitions, true, "blockcmt", false);
			states.add(s41);
			transitions.clear();
		}
		
		public static Token nextToken() {
			StringBuilder tokenBuilder = new StringBuilder();
			State state = states.get(0); //Start state 1
			String lookup = "", tokenName = "", tokenValue = "";
			int sid = 0;
			Token token = null;
			boolean isBacktrack = false;
			
			while(token == null) {
				if(!charBackUp.trim().isEmpty()){ // skip the meaningless backup characters
					lookup = charBackUp; //restore the backup character 
				}else {
					if(fileScanner.hasNext()) {
						lookup = fileScanner.next();
					}else {
						lookup = " "; // if the last token in the line needs backtracking, this character is appended as a placeholder 
					}
					
					if(lookup.equals("\n")) {
						lineNum++; // keep track of the line number
					}
				}
				tokenBuilder.append(lookup);
				
				HashMap<String, Integer> table = state.getTransitions();
				if(table.containsKey(lookup)) {
					sid = table.get(lookup); //next state via the transition
					state = states.get(sid-1);
					if(state.getIsFinal()) {
						tokenName = state.getTokenName();
						// check if this state needs backtracking
						if(state.getIsBacktrack()) {
							isBacktrack = true;
						}else {
							isBacktrack = false;
							tokenValue = tokenBuilder.toString().trim();
							token = createToken(tokenName, tokenValue, lineNum);
						}
					}
				}else {
					if(isBacktrack) {
						tokenBuilder.deleteCharAt(tokenBuilder.length() - 1); // remove the extra character read for backtracking
						charBackUp = lookup;
						tokenValue = tokenBuilder.toString().trim();
						
						// check for reserved word
						if(isReservedWord(tokenValue, reserved)) {
							tokenName = tokenValue;
						}
						if(lookup.equals("\n")) {
							token = createToken(tokenName, tokenValue, lineNum-1);
						}else {
							token = createToken(tokenName, tokenValue, lineNum);
						}
					}
				}
			}
			return token;
		}
}