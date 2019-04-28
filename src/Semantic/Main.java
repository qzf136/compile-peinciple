package Semantic;

public class Main {

	
	
	public static void main(String[] args) {
		Grammar.parseGrammar("src/grammar.txt");
		Grammar.parseSentence("src/sentence.txt");
	}
	
}
