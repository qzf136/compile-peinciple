package Semantic;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Set;
import java.util.Stack;

public class Grammar {

	public static char epsilon = (char)949;	// ε
	public static char Start = 'Z';
	public static List<String> P = new ArrayList<>();	// 语法
	public static Set<Character> V = new HashSet<>();		// 非终结符
	public static Set<Character> T = new HashSet<>();		// 终结符
	public static LinkedHashMap<Character, Set<Character>> first = new LinkedHashMap<>();  // first 集
	public static LinkedHashMap<Character, Set<Character>> follow = new LinkedHashMap<>();
	public static List<List<String>> closure = new ArrayList<>();
	public static List<List<Object>> DFA = new ArrayList<>();
	public static String[][] STA = null;
	public static List<List<Object>> logs = new ArrayList<>();
	public static List<Character> cs = new ArrayList<>();
	public static String sentence;
	public static List<Integer> closurehash = new ArrayList<>();
	
	public static Stack<String> state_stack = new Stack<>();
	public static Stack<String> char_stack = new Stack<>();
	public static int cur;
	
	public static int num = 0;
	
	public static void parseTV() {
		for (int i = 0; i < P.size(); i++) {
			String g = P.get(i);
			for (int j = 0; j < g.length(); j++) {
				if (j==1 || j==2)	continue;
				char c = g.charAt(j);
				if (c >= 'A' && c <= 'Z')	V.add(c);
				else	T.add(c);
			}	
		}
		T.add('#');
		cs.addAll(T);
		cs.addAll(V);
	}

	public static List<String> getGrammarStartWith(char startChar) {
		List<String> grammarListStartWith = new ArrayList<>();
		for (int i = 0 ; i < P.size(); i++) {
			if (P.get(i).startsWith(startChar + "")) {
				grammarListStartWith.add(P.get(i));
			}
		}
		return grammarListStartWith;
	}
	
	public static void FirstOFTV() {
		for (char terminalChar : T) {
			Set<Character> TerminalFirstSet = new HashSet<>();
			TerminalFirstSet.add(terminalChar);
			first.put(terminalChar, TerminalFirstSet);
		}
		for (char NotTerminal : V) {
			first.put(NotTerminal, new HashSet<>());
		}
		int hashFirst1 = first.hashCode();
		int hashFirst2 = 0;
		do {
			for (int i = 0; i < P.size(); i++) {
				String grammar = P.get(i);
				if (T.contains(grammar.charAt(3))) {		// -> 非终结符
					first.get(grammar.charAt(0)).add(grammar.charAt(3));	// first 集加入非终结符
				} else {
					Set<Character> copyofFirst_notTerminal = new HashSet<>(first.get(grammar.charAt(3)));
					copyofFirst_notTerminal.remove(epsilon);
					first.get(grammar.charAt(0)).addAll(copyofFirst_notTerminal);
					int index_hasEpsilon = 3;
					while (index_hasEpsilon+1<grammar.length() && P.contains(grammar.charAt(index_hasEpsilon)+"->"+epsilon)) {
						Set<Character> copyFirst_hasEpsilon = new HashSet<>(first.get(grammar.charAt(index_hasEpsilon+1)));
						copyFirst_hasEpsilon.remove(epsilon);
						first.get(grammar.charAt(0)).addAll(copyFirst_hasEpsilon);
						index_hasEpsilon++;
					}
					if (index_hasEpsilon+1 == grammar.length() && P.contains(grammar.charAt(index_hasEpsilon)+"->"+epsilon)) {
						first.get(grammar.charAt(0)).add(epsilon);
					}
				}
			}
			hashFirst1 = hashFirst2;
			hashFirst2 = first.hashCode();
		} while (hashFirst1 != hashFirst2);
	}
	
	public static void FollowOFTV() {
		for (char terminal : T) {
			Set<Character> terminalFollowSet = new HashSet<>();
			follow.put(terminal, terminalFollowSet);
		}
		for (char NotTerminal : V) {
			follow.put(NotTerminal, new HashSet<>());
			if (NotTerminal == Start) {
				follow.get(NotTerminal).add('#');
			}
		}
		int hashFollow1 = follow.hashCode();
		int hashFollow2 = 0;
		do {
			for (int i = 0; i < P.size(); i++) {
				String grammar = P.get(i);
				for (int j = 3; j < grammar.length()-1; j++) {
					char thisChar = grammar.charAt(j);
					char nextChar = grammar.charAt(j+1);
					if (V.contains(thisChar)) {
						Set<Character> copyFirst = new HashSet<>(first.get(nextChar));
						copyFirst.remove(epsilon);
						follow.get(thisChar).addAll(copyFirst);
					}
				}
				int j = grammar.length()-1;
				char thisChar = grammar.charAt(j);
				if (V.contains(thisChar))
					follow.get(thisChar).addAll(follow.get(grammar.charAt(0)));
				while (P.contains(thisChar+"->"+epsilon)) {
					j--;
					thisChar = grammar.charAt(j);
					if (V.contains(thisChar)) {
						follow.get(thisChar).addAll(follow.get(grammar.charAt(0)));
					}
				}
			}	
			hashFollow1 = hashFollow2;
			hashFollow2 = follow.hashCode();
		} while (hashFollow1 != hashFollow2);
		for (char terminal : T) {
			follow.remove(terminal);
		}
	}
	
	public static String movepoint(String project) {
		if (!project.contains("."))
			return project.substring(0, 3) + "." + project.substring(3);	// 开始，插入 . 
		else {
			int index_point = project.indexOf(".");
			if (project.charAt(index_point+1) != ',') {
				String startString = project.substring(0,index_point);  	// A->
				char nextCharOfPoint = project.charAt(index_point+1);
				String next2Strings = project.substring(index_point+2);
				String res = startString + nextCharOfPoint + "." + next2Strings;
				return res;
			} else {
				return null;
			}
		}
	}
	
	public static String calFirst(String betaa) {
		String firstSet = "";
		int i = 0;
		for (char c :first.get(betaa.charAt(0))) {
			if (c != epsilon)
				firstSet += c;
		}
		while (i+1<betaa.length() && P.contains(betaa.charAt(i)+"->"+epsilon)) {
			for (char c :first.get(betaa.charAt(i+1))) {
				if (c != epsilon) {
					if (!firstSet.contains(c+""))
						firstSet += c;
				}
			}
			i++;
		}
		if (betaa.charAt(i) == epsilon)	firstSet+=epsilon;
		return firstSet;
		
	}
	
	public static String startI0() {
		List<String> grammarStartWith = getGrammarStartWith(Start);
		String grammar = grammarStartWith.get(0);
		String project = grammar + ",#";
		return project;
	}
	
	public static List<String> constructI(String startProject) {
		List<String> project_List = new ArrayList<>();
		project_List.add(startProject);
		int i = 0;
		do {
			String project = project_List.get(i);
			int indexPoint = project.indexOf('.');
			char nextChar = project.charAt(indexPoint+1);
			List<String> grammars = getGrammarStartWith(nextChar);
			String beta = "";
			if (indexPoint+2 > project.indexOf(","))	beta = "";
			else beta = project.substring(indexPoint+2, project.indexOf(","));
			for (String grammar : grammars) {
				String pre_this = project.substring(project.indexOf(",")+1);
				String pre_new = calFirst(beta+pre_this);
				for (int k = 0; k < pre_new.length(); k++) {
					String project_new = grammar.substring(0,3)+ "." + grammar.substring(3) + "," + pre_new.charAt(k);
	 				if (!project_List.contains(project_new))
	 					project_List.add(project_new);
				}
			}
			i++;
		}while (i != project_List.size());
		return project_List;
	}
	
	public static LinkedHashMap<Character, List<String>> getReadChars(List<String> I) {
		LinkedHashMap<Character, List<String>> char2ProjectMap = new LinkedHashMap<>();
		for (String project: I) {
			int indexPoint = project.indexOf('.');
			char nextChar = project.charAt(indexPoint+1);
			if (nextChar==epsilon)	continue;
			if (nextChar != ',') {
				if (!char2ProjectMap.containsKey(nextChar)) {
					List<String> projectList = new ArrayList<>();
					projectList.add(project);
					char2ProjectMap.put(nextChar, projectList);
				} else {
					char2ProjectMap.get(nextChar).add(project);
				}
			}
		}
		return char2ProjectMap;
	}
	
	public static void constructClosure() {
		String startI0Grammar = startI0();
		List<String> I0 = constructI(movepoint(startI0Grammar));
		closure.add(I0);
		closurehash.add(new HashSet<>(I0).hashCode());
		int n = 0;
		while (n != closure.size()) {
			List<String> I = closure.get(n);
			LinkedHashMap<Character, List<String>> char2ProjectMap = getReadChars(I);
			for (char terminalChar : char2ProjectMap.keySet()) {
				List<String> I_new = new ArrayList<>();
				List<String> projectList = char2ProjectMap.get(terminalChar);
				for (String project : projectList) {
					List<String> project_new_list = constructI(movepoint(project));
					for (String project_new : project_new_list) {
						if (!I_new.contains(project_new)) {
							I_new.add(project_new);
						}
					}
				}
				int hash = new HashSet<>(I_new).hashCode();
				if (!closurehash.contains(new HashSet<>(I_new).hashCode())) {
					closure.add(I_new);
					closurehash.add(hash);
				} 
				List<Object> transfer = new ArrayList<>();
				transfer.add(n);
				transfer.add(terminalChar);
				transfer.add(closurehash.indexOf(hash));
				DFA.add(transfer);
			}
			n++;
		}	
	}
	
	public static void constructSTA() {
		int n = closure.size();
		STA = new String[n][cs.size()];
		for (int i = 0; i < n; i++) {
			for (int j = 0; j < cs.size(); j++) {
				STA[i][j] = "";
			}
		}
		for (int i = 0; i < DFA.size(); i++) {		// action 表, 移入
			List<Object> teansfer = DFA.get(i);
			if (T.contains(teansfer.get(1))) {
				int from = (int) teansfer.get(0);
				int char_read = cs.indexOf(teansfer.get(1));
				int to = (int) teansfer.get(2);
				STA[from][char_read] = "s"+to;
			}
			if (V.contains(teansfer.get(1))) {		// goto 表
				int from = (int) teansfer.get(0);
				int char_read = cs.indexOf(teansfer.get(1));
				int to = (int) teansfer.get(2);
				STA[from][char_read] = to+"";
			}
		}
		for (int i = 0; i < n; i++) {
			List<String> I = closure.get(i);
			for (int j = 0; j < I.size(); j++) {
				String project = I.get(j);
				if (project.contains(".,")) {
					int indexPoint = project.indexOf(".");
					String grammar = project.substring(0,indexPoint);
					int k = P.indexOf(grammar);
					char[] pres = project.substring(indexPoint+2).toCharArray();
					for (char pre : pres) {
						int t = cs.indexOf(pre);
						STA[i][t] = "r"+k;
					}
				}
				if (project.contains("->.ε")) {
					int indexPoint = project.indexOf(".");
					String grammar = project.substring(0,indexPoint)+epsilon;
					int k = P.indexOf(grammar);
					char[] pres = project.substring(indexPoint+3).toCharArray();
					for (char pre : pres) {
						int t = cs.indexOf(pre);
						STA[i][t] = "r"+k;
					}
				}
				if (project.contains(Start+"->") && project.contains(".,")) {
					STA[i][cs.indexOf('#')] = "acc";
				}
			}
		}
	}
	
	public static void log(Stack<String> state_stack, Stack<String> char_stack, String buffer, String action) {
		List<Object> log = new ArrayList<>();
		log.add(new ArrayList<>(state_stack));
		log.add(new ArrayList<>(char_stack));
		log.add(buffer);
		log.add(action);
		logs.add(log);
//		String iden_text = "";
//		iden_text = iden_text + log.get(0) + "\t";
//		iden_text = iden_text + log.get(1) + "\t";
//		iden_text = iden_text + log.get(2) + "\t";
//		iden_text = iden_text + log.get(3) + "\n";
//		System.out.print(transfer(iden_text));
	}
	
	public static void idenSentence() {
		cur = 0;
		state_stack.push("0");
		char_stack.push("#");
		loop();
	}
	
	public static void loop() {
		while (true) {
			String state_top = state_stack.peek();
			char a = sentence.charAt(cur);
			if (cs.indexOf(a) == -1) {
				System.out.println("字符" + Print.transfer(a+"") + "错误");
				return;
			}
			String action = STA[Integer.parseInt(state_top)][cs.indexOf(a)];
			if (action.equals("")) {
				log(state_stack, char_stack, sentence.substring(cur), "["+state_top+"," + a + "]=error: ");
				num++;
				if (num > 100)	return;
				if (handleError())	continue;
				else return;
			} else if (action.charAt(0)=='s') {
				log(state_stack, char_stack, sentence.substring(cur), "["+state_top+"," + a + "]=" +action);
				char_stack.push(a+"");
				state_stack.push(action.substring(1));
				cur++;
			} else if (action.charAt(0) == 'r') {
				String reduceGrammar = P.get(Integer.parseInt(action.substring(1)));
				log(state_stack, char_stack, sentence.substring(cur), "["+state_top+"," + a + "]=" +action+":"+reduceGrammar);
				Semantic.translate(reduceGrammar);
				char produce_Start = reduceGrammar.charAt(0);
				String produce_value = reduceGrammar.substring(3);
				int len;
				if (produce_value.equals(epsilon+""))	len = 0;
				else len = produce_value.length();
				for (int j = 0; j < len; j++) {
					state_stack.pop();
					char_stack.pop();
				}
				char_stack.push(produce_Start+"");
				String nextState = STA[Integer.parseInt(state_stack.peek())][cs.indexOf(produce_Start)];
				log(state_stack, char_stack, sentence.substring(cur), "goto["+state_stack.peek()+","+produce_Start+"]="+nextState);
				state_stack.push(nextState);
			} else if (action.equals("acc")) {
				log(state_stack, char_stack, sentence.substring(cur), "["+state_top+"," + a + "]=" + action);
				return;
			}
		}
	}
	
	
	
	public static boolean handleError() {
		cur++;
		while (!state_stack.isEmpty()) {
			char character = char_stack.peek().charAt(0);
			if (V.contains(character)) {
				state_stack.pop();
				char A = character;
				while (cur<sentence.length() && !follow.get(A).contains(sentence.charAt(cur)))
					cur++;
				if (cur == sentence.length())	return false;
				String nextState = STA[Integer.parseInt(state_stack.peek())][cs.indexOf(A)];
				log(state_stack, char_stack, sentence.substring(cur), "goto["+state_stack.peek()+","+character+"]="+nextState);
				state_stack.push(nextState);
				return true;
			}
			state_stack.pop();
			char_stack.pop();
		}
		return false;		
	}

	public static void parseGrammar(String path) {
		
		P = Read.readGrammar(path);
		Print.printGrammar();
		
		parseTV();
		Print.printTV();
		
		FirstOFTV();
		FollowOFTV();
		Print.printFirstFollow();
		
		constructClosure();
		Print.printClosure();
		
		constructSTA();
		Print.printSTA();
	}
	
	public static void parseSentence(String path) {
		Print.printConvert();
		sentence = Read.readSentence(path);
		Print.printSentence();
		
		idenSentence();
		Print.printIdenProcess();
		Print.printCharTabel();
		Print.printCodes();
		Print.printErrorMsg();
	}
	
}
