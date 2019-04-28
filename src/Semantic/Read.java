package Semantic;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class Read {
	
	public static Map<String, String> grammarConvert = new HashMap<>();
	public static Map<String, String> antiConvert = new HashMap<>();
	public static List<String> program = new ArrayList<>();
	
	public static void initConverterMap() {
		String[] keyWords = {"int", "float", "bool", "char" , "string", "if", "else", "then", 
							"while", "do", "and", "or", "not"// 关键字
						, "intC", "floatC", "boolC", "charC", "stringC"	// 常量 		种别码1-5
						, "id"	// 标识符 	种别码 0
						, "==", ">=", "<=", "!="
		};
		for (int i = 0; i < keyWords.length; i++) {
			grammarConvert.put(keyWords[i], (char)(950+i)+"");
			antiConvert.put((char)(950+i)+"", keyWords[i]);
		}
	}
	
	
	public static List<String> readGrammar(String path) {
		initConverterMap();
		List<String> grammars = new ArrayList<>();
		try {
			String line = null;
			FileInputStream stream = new FileInputStream(path);
			InputStreamReader reader = new InputStreamReader(stream);
			BufferedReader bReader = new BufferedReader(reader);
			while ((line = bReader.readLine()) != null) {
				if (line.equals(""))	continue;
				String[] words = line.split(" ");
				String grammar = "";
				for (String word : words) {
					if (word.equals(""))	continue;
					if (!grammarConvert.containsKey(word))
						grammar += word;
					else
						grammar += grammarConvert.get(word);
				}
				String start = grammar.substring(0, 3);  // A->
				String produce = grammar.substring(3) + "|";
				String[] produces = produce.split("\\|");
				for (int i = 0; i < produces.length; i++) {
					grammars.add(start+produces[i]);
				}
			}
			bReader.close();		
		} catch (IOException e) {
			e.printStackTrace();
		}
		return grammars;
	}	
	
	public static String readSentence(String path) {
		initConverterMap();
		Word.work(path);
		String sentence = "";
		for (int i = 0; i < Word.tokens.size(); i++) {
			List<Object> token = Word.tokens.get(i);
			program.add(token.get(0)+"");
			int classNum = (int) token.get(1);
			if (classNum == 0) {
				sentence += grammarConvert.get("id");
			} else if (classNum == 1 ) {
				sentence += grammarConvert.get("intC");
			} else if (classNum == 2) {
				sentence += grammarConvert.get("floatC");				
			} else if (classNum == 3) {
				sentence += grammarConvert.get("boolC");
			} else if (classNum == 4) {
				sentence += grammarConvert.get("charC");
			} else if (classNum == 5) {
				sentence += grammarConvert.get("stringC");
			} else if (Word.Replace.contains(token.get(0))) {
				sentence += grammarConvert.get(token.get(0));
			} else {
				sentence += token.get(0);
			}
		}
		sentence += "#";
		return sentence;	
	}
	
	public static void writeFile(String path, String text) {
		try {
			File file = new File(path);
			if (!file.exists()) {
				file.createNewFile();
			}
			FileWriter writer = new FileWriter(file);
			writer.write(text);
			writer.close();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	
}

