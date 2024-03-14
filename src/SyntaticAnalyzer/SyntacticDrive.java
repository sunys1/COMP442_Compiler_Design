package SyntaticAnalyzer;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.PrintWriter;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.Scanner;

import LexicalAnalyzer.Lexer;
import LexicalAnalyzer.Token;
import LexicalAnalyzer.TokenName;

public class SyntacticDrive {
	public static String DEFAULT_INPUT = "./input/parser";
	public static String DEFAULT_OUTPUT = "./output/parser/";
	
	public static void main(String[] args) {
		try {
			File folder = new File(DEFAULT_INPUT);
			File[] listOfFiles = folder.listFiles();
			Files.createDirectories(Paths.get(DEFAULT_OUTPUT));
			
			// Process files that end with ".src"
			for (int i = 0; i < listOfFiles.length; i++) {
				if(listOfFiles[i].getName().endsWith(".src")) {
					File file = listOfFiles[i];
					parseFile(file);
				}
			}				
			
		}catch(IOException e) {
			e.printStackTrace();
		}
	}
	
	public static void parseFile(File file) throws FileNotFoundException {
		if(file.canRead()) {
			String fileName = file.getName();
			Scanner fileScanner = new Scanner(new FileInputStream(file));
			fileScanner.useDelimiter("");
			PrintWriter tokenWriter = new PrintWriter(new FileOutputStream(DEFAULT_OUTPUT + fileName.split("[.]")[0] + ".outlextokens"));
			PrintWriter tokenErrorWriter = new PrintWriter(new FileOutputStream(DEFAULT_OUTPUT + fileName.split("[.]")[0] + ".outlexerrors"));
			PrintWriter derivationWriter = new PrintWriter(new FileOutputStream(DEFAULT_OUTPUT + fileName.split("[.]")[0] + ".outderivation"));
			PrintWriter errorWriter = new PrintWriter(new FileOutputStream(DEFAULT_OUTPUT + fileName.split("[.]")[0] + ".outsyntaxerrors"));
			PrintWriter astWriter = new PrintWriter(new FileOutputStream(DEFAULT_OUTPUT + fileName.split("[.]")[0] + ".ast.outast"));
			PrintWriter dotWriter = new PrintWriter(new FileOutputStream(DEFAULT_OUTPUT + fileName.split("[.]")[0] + ".dot.outast"));
			
			Lexer lexer = new Lexer(fileScanner, tokenWriter, tokenErrorWriter);
            Parser parser = new Parser(lexer, derivationWriter, errorWriter, astWriter, dotWriter);
            parser.parse();
            
            fileScanner.close();
            tokenWriter.close();
            tokenErrorWriter.close();
    		derivationWriter.close();
    		errorWriter.close();
    		astWriter.close();
    		dotWriter.close();
		}else {
			System.out.println("Unable to read file: " + file.getName());
		}
	}
}
