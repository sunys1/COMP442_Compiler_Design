package LexicalAnalyzer;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Scanner;

public class LexicalDrive {
	public static final String[] RESERVED_WORDS = {
			"if", "then", "else", "integer", "float", "void",
		    "public", "private", "func", "var", "struct", "while",
		    "read", "write", "return", "self", "inherits", "let", "impl"
	    };

		public static void main(String[] args) {
			ArrayList<String> reserved = new ArrayList<>(Arrays.asList(RESERVED_WORDS));
			System.out.println(isReservedWord("private", reserved));
		}
		
		public static boolean isReservedWord(String word, ArrayList<String> reserved) {
			if(reserved.contains(word)) {
				return true;
			}
			
			return false;
		}
}