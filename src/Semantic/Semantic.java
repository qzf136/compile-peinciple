package Semantic;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Stack;

public class Semantic {

	public static String errorMsg = "";
	public static List<List<String>> codes = new ArrayList<>();
	public static List<String> codesString = new ArrayList<>();
	
	public static int index = 0;
	public static int offset = 0;
	public static String type;
	public static int width;
	public static LinkedHashMap<String, CharInfo> charTable = new LinkedHashMap<>(); 
	public static Map<String, Stack<String>> name = new HashMap<>();
	
	public static Map<String, List<Integer>> trueList = new HashMap<>();
	public static Map<String, List<Integer>> falseList = new HashMap<>();
	public static Stack<List<Integer>> SnextList = new Stack<>();
	public static Stack<List<Integer>> NnextList = new Stack<>();
	public static Stack<Integer> Mstack = new Stack<>();
	
	
	public static String newTemp(String type) {
		index++;
		String name = "t"+index;
		if (type.equals("int"))	{
			width = 4;
			offset += 4;
			CharInfo info = new CharInfo(name, type, width, offset);
			charTable.put(name, info);
		} else if (type.equals("float")) {
			width = 4;
			offset += 4;
			CharInfo info = new CharInfo(name, type, width, offset);
			charTable.put(name, info);
		} else if (type.equals("bool")) {
			width = 1;
			offset += 1;
			CharInfo info = new CharInfo(name, type, width, offset);
			charTable.put(name, info);
		}
		return name;
	}
	
	public static void genCode(String op, String v1, String v2, String res) {
		List<String> codeList = new ArrayList<>();
		codeList.add(op);
		codeList.add(v1);
		codeList.add(v2);
		codeList.add(res);
		codes.add(codeList);
	}
	
	public static List<Integer> makeList(int i) {
		List<Integer> list = new ArrayList<>();
		list.add(i);
		return list;
	}
	
	public static List<Integer> merge(List<Integer> p1, List<Integer> p2) {
		List<Integer> list = new ArrayList<>();
		if (!(p1 == null))	list.addAll(p1);
		if (!(p2 == null))	list.addAll(p2);
		return list;
	}
	
	public static boolean backPatch(List<Integer> p, int i) {
		if (p == null) {
			return false;
		}
		for (int j = 0; j < p.size(); j++) {
			String string = i+"";
			codes.get(p.get(j)).remove(3);
			codes.get(p.get(j)).add(string);
		}
		return true;
	}
	
	public static void translate(String reduceGrammar) {
		String grammar = Print.transfer(reduceGrammar);
		if (grammar.equals("X->int")) {
			type = "int";
			width = 4;
		} else if (grammar.equals("X->float")) {
			type = "float";
			width = 4;
		} else if (grammar.equals("S->Xid;")) {
			String character = Read.program.get(Grammar.cur-2);
			CharInfo info = new CharInfo(character, type, width, offset);
			if (charTable.containsKey(character)) {
				String msg = Word.tokenAddr.get(Grammar.cur-2)+"变量" + character + "重复声明\n";
				errorMsg = errorMsg + msg;
				System.out.println(msg);
				return;
			}
			charTable.put(character, info);
			offset += charTable.get(character).getWidth();
			SnextList.push(null);
		} else if (grammar.equals("S->id=E;")) {
			if (!name.containsKey("E"))	return;
			int i = Grammar.cur-1;
			while (!Read.program.get(i).equals("="))	i--;
			String character = Read.program.get(i-1);
			if (!charTable.containsKey(character)) {
				String msg = Word.tokenAddr.get(i-1)+"变量" + character + "未声明\n";
				errorMsg = errorMsg + msg;
				System.out.println(msg);
			}
			else {
				String e = name.get("E").pop();
				if (!charTable.get(e).getType().equals(charTable.get(character).getType())) {
					String msg = Word.tokenAddr.get(i-1)+"变量" + character + "类型不匹配\n";
					errorMsg = errorMsg + msg;
					System.out.println(msg);
				}
				genCode("=", e, "~", charTable.get(character).getName()) ;
				codesString.add(charTable.get(character).getName()+" = "+e);
				SnextList.push(null);
			}
		} else if (grammar.equals("E->E+T")) {
			if (!name.containsKey("E"))	return;
			if (!name.containsKey("T"))	return;
			if (name.get("E").isEmpty())return;
			if (name.get("T").isEmpty())return;
			String t = null;
			String varNameE = name.get("E").pop();
			String varNameT = name.get("T").pop();
			if (charTable.get(varNameE).getType().equals("int") && charTable.get(varNameT).getType().equals("int")) {
				t = newTemp("int");			
			} else {
				t = newTemp("float");
			}
			genCode("+", varNameE, varNameT, t);
			codesString.add(t + " = " + varNameE + " + " + varNameT);
			name.get("E").push(t);
		} else if (grammar.equals("E->T")) {
			if (!name.containsKey("T"))	return;
			if (name.get("T").isEmpty())return;
			String varName = name.get("T").pop();
			if (name.get("E") == null) name.put("E", new Stack<String>());
			name.get("E").push(varName);
		} else if (grammar.equals("T->T*F")) {
			if (!name.containsKey("T"))	return;
			if (!name.containsKey("F"))	return;
			if (name.get("T").isEmpty())return;
			if (name.get("F").isEmpty())return;
			String varNameT = name.get("T").pop();
			String varNameF = name.get("F").pop();
			String t = null;
			if (charTable.get(varNameT).getType().equals("int") && charTable.get(varNameF).getType().equals("int")) {
				t = newTemp("int");			
			} else {
				t = newTemp("float");
			}
			genCode("*", varNameT, varNameF, t); 
			codesString.add(t + " = " + varNameT + " * " + varNameF);
			name.get("T").push(t);
		} else if (grammar.equals("T->F")) {
			if (!name.containsKey("F"))	return;
			if (name.get("F").isEmpty())return;
			String varNameF = name.get("F").pop();
			if (name.get("T") == null) name.put("T", new Stack<String>());
			name.get("T").push(varNameF);
		} else if (grammar.equals("F->(E)")) {
			if (!name.containsKey("E"))	return;
			String varNameE = name.get("E").pop();
			if (name.get("F") == null) name.put("F", new Stack<String>());
			name.get("F").push(varNameE);
		} else if (grammar.equals("F->id")) {
			String character = Read.program.get(Grammar.cur-1);
			if (!charTable.containsKey(character)) {
				String msg = Word.tokenAddr.get(Grammar.cur-1)+"变量" + character + "未声明\n";
				errorMsg = errorMsg + msg;
				System.out.println(msg);
			}
			else {
				if (name.get("F") == null) name.put("F", new Stack<String>());
				name.get("F").push(charTable.get(character).getName());
			}
		} else if (grammar.equals("F->intC")) {
			String t = newTemp("int");
			int val = Integer.parseInt(Read.program.get(Grammar.cur-1));
			genCode("=", val+"", "~", t);
			codesString.add(t + " = " + val);
			if (name.get("F") == null) name.put("F", new Stack<String>());
			name.get("F").push(t);
		} else if (grammar.equals("F->floatC")) {
			String t = newTemp("float");
			float val = Float.parseFloat(Read.program.get(Grammar.cur-1));
			genCode("=", val+"", "~", t);
			codesString.add(t + " = " + val);
			if (name.get("F") == null) name.put("F", new Stack<String>());
			name.get("F").push(t);
		} else if (grammar.equals("B->BandMB")) {
			String t = newTemp("bool");
			if (!name.containsKey("B"))	return;
			if (name.get("B").size()<2)	return;
			String varNameB2 = name.get("B").pop();
			String varNameB1 = name.get("B").pop();
			backPatch(trueList.get(varNameB1), Mstack.pop());
			trueList.put(t, trueList.get(varNameB2));
			falseList.put(t, merge(falseList.get(varNameB1), falseList.get(varNameB2)));
			name.get("B").push(t);
		} else if (grammar.equals("B->BorMB")) {
			String t = newTemp("bool");
			if (!name.containsKey("B"))	return;
			if (name.get("B").size()<2)	return;
			String varNameB2 = name.get("B").pop();
			String varNameB1 = name.get("B").pop();
			backPatch(falseList.get(varNameB1), Mstack.pop());
			trueList.put(t, merge(trueList.get(varNameB1), trueList.get(varNameB2)));
			falseList.put(t, falseList.get(varNameB2));
			name.get("B").push(t);
		} else if (grammar.equals("B->notB")) {
			String t = newTemp("bool");
			if (!name.containsKey("B"))	return;
			if (name.get("B").isEmpty())	return;
			String varNameB = name.get("B").pop();
			trueList.put(t, falseList.get(varNameB));
			falseList.put(t, trueList.get(varNameB));
			name.get("B").push(t);
		} else if (grammar.equals("B->boolC")) {
			String t = newTemp("bool");
			boolean val = Boolean.parseBoolean(Read.program.get(Grammar.cur-1));
			if (val == false) {
				genCode("=", "0", "~", t);
				codesString.add(t + " = 0");
				falseList.put(t, makeList(codes.size()));
				genCode("goto", "~", "~", "~");
				codesString.add("goto _");
			}
			else {
				genCode("=", "1", "~", t);
				codesString.add(t + " = 1");
				trueList.put(t, makeList(codes.size()));
				genCode("goto", "~", "~", "~");
				codesString.add("goto _");
			}
			if (name.get("B") == null) name.put("B", new Stack<String>());
			name.get("B").push(t);
		} else if (grammar.equals("B->EKE")) {
			if (name.get("K").size() < 1) {
				System.out.println("B stack error");
				return;
			}
			if (name.get("E").size() < 2) {
				System.out.println("E stack error");
				return;
			}
			String t = newTemp("bool");
			int nextQuad = codes.size();
			trueList.put(t, makeList(nextQuad));
			falseList.put(t, makeList(nextQuad+1));
			String relopName = name.get("K").pop();
			String varNameE2 = name.get("E").pop();
			String varNameE1 = name.get("E").pop();
			genCode("if"+relopName, varNameE1, varNameE2, "~");
			codesString.add("if " + varNameE1 + relopName + varNameE2 + " goto _");
			genCode("goto", "~", "~", "~");
			codesString.add("goto _");
			if (name.get("B") == null) name.put("B", new Stack<String>());
			name.get("B").push(t);
		} else if (grammar.contains("K->")) {
			if (name.get("K") == null) name.put("K", new Stack<String>());
			name.get("K").push(grammar.substring(3));
		} else if (grammar.equals("N->ε")) {
			NnextList.push(makeList(codes.size()));
			genCode("goto", "~", "~", "~");
		} else if (grammar.equals("S->ifBthenMSNelseMS")) {
			if (Mstack.size() < 2) {
				System.out.println("Mstack error");
				return;
			}
			if (SnextList.size() < 2) {
				System.out.println("S->if  Snextlist error");
				return;
			}
			if (NnextList.size() < 1) {
				System.out.println("Nnextlist error");
				return;
			}
			if (name.get("B").size() < 1) {
				System.out.println("B stack error");
				return;
			}
			int m2 = Mstack.pop();
			int m1 = Mstack.pop();
			String t = name.get("B").pop();
			backPatch(trueList.get(t), m1);
			backPatch(falseList.get(t), m2);
			List<Integer> S2 = SnextList.pop();
			List<Integer> S1 = SnextList.pop();
			List<Integer> N = NnextList.pop();
			SnextList.push(merge(merge(S1, N), S2));
		} else if (grammar.equals("S->whileMBdoMS")) {
			if (Mstack.size() < 2) {
				System.out.println("Mstack error");
				return;
			}
			if (SnextList.size() < 1) {
				System.out.println("while:  SnextList error");
				return;
			}
			if (name.get("B").size() < 1) {
				System.out.println("B stack error");
				return;
			}
			int m2 = Mstack.pop();
			int m1 = Mstack.pop();
			List<Integer> D = SnextList.pop();
			backPatch(D, m1);
			String t = name.get("B").pop();
			backPatch(trueList.get(t), m2);
			SnextList.push(falseList.get(t));
			genCode("goto", "~", "~", m1+"");
		} else if (grammar.equals("P->SMP")) {
			if (SnextList.size() < 2) {
				System.out.println("P->SMP  Snextlist error");
				return;
			}
			if (Mstack.size() < 1) {
				System.out.println("Mstack error");
				return;
			}
			List<Integer> P = SnextList.pop();
			List<Integer> S = SnextList.pop();
			int m = Mstack.pop();
			backPatch(S, m);
			SnextList.push(P);
		} else if (grammar.equals("M->ε")) {
			Mstack.push(codes.size());
		} else if (grammar.equals("P->ε")) {
			SnextList.push(null);
		}
	}
}
