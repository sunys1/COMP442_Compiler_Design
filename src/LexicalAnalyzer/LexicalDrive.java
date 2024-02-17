package LexicalAnalyzer;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.PrintWriter;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.Scanner;

public class LexicalDrive {
		public static String DEFAULT_INPUT = "./input/lexer";
		public static String DEFAULT_OUTPUT = "./output/lexer/";

		public static void main(String[] args) {			
			try {
				File folder = new File(DEFAULT_INPUT);
				File[] listOfFiles = folder.listFiles();
				Files.createDirectories(Paths.get(DEFAULT_OUTPUT));
				
				// Process files that end with ".src"
				for (int i = 0; i < listOfFiles.length; i++) {
					if(listOfFiles[i].getName().endsWith(".src")) {
						File file = listOfFiles[i];
						analyzeFile(file);
					}
				}				
				
				
			}catch(IOException e) {
				e.printStackTrace();
			}
		}
		
		public static void analyzeFile(File file) throws FileNotFoundException {
			Scanner fileScanner = null;
			PrintWriter tokenWriter = null;
			PrintWriter errorWriter = null;
			Token token = null; 
			
			if(file.canRead()) {
				String fileName = file.getName();
				fileScanner = new Scanner(new FileInputStream(file));
				fileScanner.useDelimiter("");
				tokenWriter = new PrintWriter(new FileOutputStream(DEFAULT_OUTPUT + fileName.split("[.]")[0] + ".outlextokens"));
				errorWriter = new PrintWriter(new FileOutputStream(DEFAULT_OUTPUT + fileName.split("[.]")[0] + ".outlexerrors"));
				
				Lexer lexer = new Lexer(fileScanner, tokenWriter, errorWriter);
				// Scan tokens
				while(fileScanner.hasNext()) {
					token = lexer.nextToken();
				}
				
			}else {
				System.out.println("Unable to read file: " + file.getName());
			}
			
			fileScanner.close();
			tokenWriter.close();
			errorWriter.close();
		}
}