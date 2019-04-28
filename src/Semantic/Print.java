package Semantic;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;

public class Print {

	public static String transfer(String string) {
		String res = "";
		for (int i = 0; i < string.length(); i++) {
			if (Read.antiConvert.containsKey(string.charAt(i)+"")) {
				res += Read.antiConvert.get(string.charAt(i)+"");
			} else {
				res += string.charAt(i);
			}
		}
		return res;
	}
	
	public static void printConvert() {
		String text = "";
		for (String key: Read.grammarConvert.keySet()) {
			text = text + key + "  " + Read.grammarConvert.get(key) + "\n";
		}
		Read.writeFile("convert.txt", text);
	}
	
	public static void printGrammar() {
		String P_text = "语法产生式\n";
		int j = 0;
		for (int i = 0; i < Grammar.P.size(); i++) {
			P_text = P_text + j + ":  " + Grammar.P.get(i) + "\n";
			j++;
		}
//		System.out.println(transfer(P_text));
		Read.writeFile("文法.txt", transfer(P_text));
	}
	
	public static void printTV() {
		String TV_text = "";
		TV_text += "终结符:\n";
		for (char terminal : Grammar.T) {
			TV_text = TV_text + terminal + " ";
		}
		TV_text += "\n非终结符:\n";
		for (Character v: Grammar.V) {
			TV_text = TV_text + v + " ";
		}
//		System.out.println(transfer(TV_text));
	}

	public static void printFirstFollow() {
		String text = "first集:\n";
		for (char key : Grammar.first.keySet()) {
			text = text + key + ": " + Grammar.first.get(key) + "\n";
		}
		text += "\nfollow集:\n";
		for (char key : Grammar.follow.keySet()) {
			text = text + key + ": " + Grammar.follow.get(key) + "\n";
		}
		Read.writeFile("firstFollow.txt", transfer(text));
	}
	
	public static void printClosure() {
		StringBuffer closureBuffer = new StringBuffer("项目集规范族\n");
		for (int i = 0; i < Grammar.closure.size(); i++) {
			List<String> I = Grammar.closure.get(i);
			LinkedHashMap<String, String> grammarPreMap = new LinkedHashMap<>();
			for (int j = 0; j < I.size(); j++) {
				String[] grammar_pre = I.get(j).split(",");
				String grammar = grammar_pre[0];
				String pre = grammar_pre[1];
				if (!grammarPreMap.keySet().contains(grammar))
					grammarPreMap.put(grammar, pre);
				else {
					String pre2 = grammarPreMap.get(grammar);
					grammarPreMap.put(grammar, pre+pre2);
				}
			}
			List<String> closureList = new ArrayList<>();
			for (String grammar: grammarPreMap.keySet()) {
				closureList.add(grammar+","+grammarPreMap.get(grammar));
			}
			closureBuffer.append(i+": "+closureList + "\n");
		}
//		System.out.println(transfer(closureBuffer.toString()));
		Read.writeFile("项目集闭包.txt", transfer(closureBuffer.toString()));
	}

	public static void printSTA() {
		String STA_text = "分析表\n";
		for (int i = 0; i < Grammar.cs.size(); i++) {
			STA_text = STA_text + "\t" + Grammar.cs.get(i);
		}
		STA_text += "\n";
		for (int i = 0; i < Grammar.closure.size(); i++) {
			STA_text += i;
			for (int j = 0; j < Grammar.cs.size(); j++) {
				STA_text = STA_text + "\t" + Grammar.STA[i][j];
			}
			STA_text += "\n";
		}
//		System.out.println(transfer(STA_text));
		Read.writeFile("分析表.txt", transfer(STA_text));
	}
	
	public static void printSentence() {
//		System.out.println("语句：");
//		System.out.println(transfer(Grammar.sentence));
//		System.out.println();

	}
	
	public static void printIdenProcess() {
		String iden_text = "识别过程\n";
		String seq_text = "归约序列\n";
		for (int i = 0; i < Grammar.logs.size(); i++) {
			iden_text = iden_text + Grammar.logs.get(i).get(0) + "\t";
			iden_text = iden_text + Grammar.logs.get(i).get(1) + "\t";
			iden_text = iden_text + Grammar.logs.get(i).get(2) + "\t";
			iden_text = iden_text + Grammar.logs.get(i).get(3) + "\n";
			if (Grammar.logs.get(i).get(3).toString().contains("r")) {
				String string = Grammar.logs.get(i).get(3).toString();
				seq_text += string.substring(string.indexOf(":")+1);
				seq_text += "\n";
			}
		}
//		System.out.println(transfer(iden_text));
//		System.out.println(transfer(seq_text));
		Read.writeFile("识别过程.txt", transfer(iden_text));
		Read.writeFile("归约序列.txt", transfer(seq_text));
	}
	
	public static String printCharTabel() {
		String text = "符号表\n";
		for (String character : Semantic.charTable.keySet()) {
			text = text + character + ":  " + Semantic.charTable.get(character) + "\n";
		}
		Read.writeFile("符号表.txt", text);
		return text;
	}
	
	public static String printCodes() {
		String text = "Codes\n";
		for (int i = 0; i < Semantic.codes.size(); i++)
			text = text + i + "  " + Semantic.codes.get(i) + "\n";
		Read.writeFile("四元式序列.txt", text);
		return text;
	}
	
	public static String printErrorMsg() {
		String text = Semantic.errorMsg;
		Read.writeFile("错误信息.txt", text);
		return text;
	}
	
}
