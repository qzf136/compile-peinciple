package Semantic;

import java.io.BufferedReader;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class Word {

	public static  String chs = new String();				// 将所有的字符存储为一个字符串
	public static  List<Character> cache = new ArrayList<>();	// 缓存要处理的字符集
	public static  List<String> keys = new ArrayList<>();	// 关键字集，语言定义的关键字 "int","float", "char","bool", "do", "while", "if", "else", "true", "false"
	public static  Map<String, Integer> codes = new HashMap<>();	// 种别码表
	public static  Map<String, String> attributes = new HashMap<>();	// 符号表（属性值表），存储标识符和常数及对应属性
	public static  List<List<Object>> tokens = new ArrayList<>();	// token表，解析输入中的符号
	public static  StringBuffer errorMsg = new StringBuffer();	// 错误信息
	
	public static  int cur = 0;	// 当前正在处理的字符的下标
	public static  List<Integer> numbers = new ArrayList<>();		// 记录每行的字符数
	public static  List<Integer> sum = new ArrayList<>();	// 记录每行以及之前行的总字符数
	
	public static List<String> Replace = new ArrayList<>();
	
	public static List<String> tokenAddr = new ArrayList<>();
	
	// 初始化种别码表，关键字表
	public static void initiate() {
		String[] id = {"id"};
		String[] numbers = { "intConst", "floatConst", "boolConst", "charConst", "stringConst"};
		String[] key = {"int","float", "char", "bool", "do", "while", "if", "else",	"then", "record", "true", "false", "and", "or", "call"};
		String[] boards = {"==", "!=", "<=", ">=", "!",  ">", "<",
						"+", "*", "=", "+=", "*=",  "(", ")", "[", "]", "{", "}",  ";"};
		List<String> list1 = Arrays.asList(id);
		List<String> list2 = Arrays.asList(numbers);
		List<String> list3 = Arrays.asList(key);
		List<String> list4 = Arrays.asList(boards);
		List<String> all = new ArrayList<>();
		all.addAll(list1);
		all.addAll(list2);
		all.addAll(list3);
		all.addAll(list4);
		for (int i = 0; i < all.size(); i++)
			codes.put(all.get(i), i);	// 种别码表
		for (int i = 0; i < key.length; i++) 
			keys.add(key[i]);	// 关键字表
		String[] repls = {"int","float", "char","bool", "do", "while", "if", "else", "then", "record", "and", "or", "call",
				"==", "!=", "<=", ">="}; 
		for (int i = 0; i < repls.length; i++)
			Replace.add(repls[i]);
	}
	
	// 文件读入
	public static void getInput(String path) {
		try {
			FileInputStream fstream = new FileInputStream(path);
			StringBuffer buffer = new StringBuffer();
			InputStreamReader reader = new InputStreamReader(fstream);
			BufferedReader bReader = new BufferedReader(reader);
			String line = null;
			int s=0;
			while ((line=bReader.readLine()) != null) {
				buffer.append(line + '\n');
				numbers.add(line.length()+1);	// 每行字符数读入numbers
				s = s + line.length()+1;
				sum.add(s);		// 每行以及之前行的总字符数读入sum
			}
			bReader.close();
			chs = buffer.toString();	// 读入字符串
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	
	// 根据处理字符的下标得到在源文件中位置
	public static List<Integer> getRowCol(int a) {
		int i = 0;
		for (i = 0; i < sum.size(); i++) {
			if (a < sum.get(i))
				break;
		}
		List<Integer> list = new ArrayList<>();
		list.add(i+1);
		list.add(numbers.get(i) - (sum.get(i) - a) + 1);
		return list;
	}
	
	// 判断是否是标识符中的字符
	public static boolean isID(char c) {
		if (isChar(c) || isNum(c))	return true;
		else	return false;
	}
	
	// 判断是否是变量名中的合法字符
	public static boolean isChar(char c) {
		if ((c>='a' && c<='z') || (c>='A'&&c<='Z') || c=='_')	return true;
		else 	return false;
	}
	
	// 判断是否是注释的开始
	public static boolean isComment(char c) {
		if (c == '/')	return true;
		else return false;
	}
	
	// 判断是否是数字的合法开始
	public static boolean isNum(char c) {
		if (c>='0' && c<='9')	return true;
		else	return false;
	}
	
	// 扫描字符
	public static void scan() {
		while (cur < chs.length()) {
			Character c = chs.charAt(cur);
			if (isChar(c)) {
				recogID();		// 识别标识符
			} else if (isComment(c)) {
				handleComment();	// 识别注释
			} else if (isNum(c)){
				recogNum();	// 
			} else if (c == ' ' || c == '\t' || c == '\n') {	// 跳过空格tab回车
				
			}else if (c == '\"'){	// " 字符串常量
				recogString();
			} else if (c == '\'') {	// ' 字符常量
				recogChar();
			} else if (!codes.keySet().contains(c.toString())) {	// 非法字符
				List<Integer> list = getRowCol(cur);
				errorMsg.append(list.get(0)+"行"+list.get(1)+"列: " + "非法字符 " + c + "\n");
			} else {
				recogDel();		// 界符
			}
			cur++;
		}
	}
	
	// 处理标识符
	public static void recogID() {
		String string = "";
		while (cur < chs.length() && isID(chs.charAt(cur))) {	// 是标识符的合法开始
			cache.add(chs.charAt(cur));		// 读入缓存
			cur++;
		}
		for (int i = 0; i < cache.size(); i++)
			string += cache.get(i);
		if (keys.contains(string)) {	// 关键字
			int classNum;
			String att;
			if (string.equals("true") || string.equals("false")) {	// bool常量
				classNum = codes.get("boolConst");
				att = string;
			} else {
				classNum = codes.get(string);
				att = "-";
			}
			List<Object> list = new ArrayList<>();
			list.add(string);
			list.add(classNum);
			list.add(att);
			tokens.add(list);	// token <种别码，属性值>
			List<Integer> addr = getRowCol(cur-cache.size());
			tokenAddr.add(addr.get(0)+"行" + addr.get(1)+"列");
			cache.clear();
		} else {
			int classNum = codes.get("id");
			String att = new String(string);
			if (attributes.get(string)== null) {
				attributes.put(string, att);
			} else {
				att = attributes.get(string);
			}
			List<Object> list = new ArrayList<>();
			list.add(string);
			list.add(classNum);
			list.add(att);
			tokens.add(list);
			List<Integer> addr = getRowCol(cur-cache.size());
			tokenAddr.add(addr.get(0)+"行" + addr.get(1)+"列");
			cache.clear();
		}
		cur -= 1;
	}
	
	// 处理注释
	public static void handleComment() {
		if (cur+1 < chs.length() && chs.charAt(cur+1) != '*') {	// 开始不是 /*
			List<Integer> list = getRowCol(cur);
			errorMsg.append(list.get(0)+"行"+list.get(1)+"列: " + "符号 / 错误\n");
			return;
		} else {
			cur+=2;
			while (!(chs.charAt(cur)=='*' && chs.charAt(cur+1)=='/')) {	// 读到*/
				cache.add(chs.charAt(cur));
				cur++;
				if (cur == chs.length()) {
					List<Integer> list = getRowCol(cur-cache.size()-2);
					errorMsg.append(list.get(0)+"行"+list.get(1)+"列: " + "注释格式错误\n");
					cache.clear();
					return;
				}
			}
			cache.clear();
			cur++;
			return;
		}	
	}
	
	// 处理整数和浮点数常量
	public static void recogNum() {
		while (cur < chs.length() && isNum(chs.charAt(cur))) {	// 读入所有数字型的字符
			cache.add(chs.charAt(cur));
			cur++;
		}
		String string = "";
		if (chs.charAt(cur)=='.') {
			if (isNum(chs.charAt(cur+1))) {
				cache.add(chs.charAt(cur));
				cur++;
				while (cur < chs.length() && isNum(chs.charAt(cur))) {
					cache.add(chs.charAt(cur));
					cur++;
				}
				for (int i = 0; i < cache.size(); i++)
					string += cache.get(i);
				int classNum = codes.get("floatConst");
				String att = string;
				if (attributes.get(string)==null) {
					attributes.put(string, string);
				} else {
					att = attributes.get(string);
				}
				List<Object> list = new ArrayList<>();
				list.add(string);
				list.add(classNum);
				list.add(att);
				tokens.add(list);
				List<Integer> addr = getRowCol(cur-cache.size());
				tokenAddr.add(addr.get(0)+"行" + addr.get(1)+"列");
				cache.clear();
				cur -= 1;
				return;
			} else {
				List<Integer> list = getRowCol(cur-cache.size());
				errorMsg.append(list.get(0)+"行"+list.get(1)+"列: " + "浮点常量不合法\n");
				return;
			}
		} else {
			for (int i = 0; i < cache.size(); i++)
				string += cache.get(i);
			int classNum = codes.get("intConst");
			String att = string;
			if (attributes.get(string)==null) {
				attributes.put(string, string);
			} else {
				att = attributes.get(string);
			}
			List<Object> list = new ArrayList<>();
			list.add(string);
			list.add(classNum);
			list.add(att);
			tokens.add(list);
			List<Integer> addr = getRowCol(cur-cache.size());
			tokenAddr.add(addr.get(0)+"行" + addr.get(1)+"列");
			cache.clear();
			cur -= 1;
			return;
		}
	}
	
	// 处理字符型常量
	public static void recogChar() {
		cache.add(chs.charAt(cur));
		cur++;
		while (cur < chs.length() && chs.charAt(cur) != '\'') {
			cache.add(chs.charAt(cur));
			cur++;
		}
		if (cur+1 < chs.length() && chs.charAt(cur) == '\'')	cache.add(chs.charAt(cur));
		if (cache.size() != 3) {
			List<Integer> list = getRowCol(cur-cache.size());
			errorMsg.append(list.get(0)+"行"+list.get(1)+"列: " + "字符常量不合法\n");
			cache.clear();
			return;
		}
		String string = "";
		for (int i = 0; i < cache.size(); i++)
			string += cache.get(i);
		int classNum = codes.get("charConst");
		String att = string;
		if (attributes.get(string)==null) {
			attributes.put(string, string);
		} else {
			att = attributes.get(string);
		}
		List<Object> list = new ArrayList<>();
		list.add(string);
		list.add(classNum);
		list.add(att);
		tokens.add(list);
		List<Integer> addr = getRowCol(cur-cache.size());
		tokenAddr.add(addr.get(0)+"行" + addr.get(1)+"列");
		cache.clear();
	}
		
	// 处理字符串常量
	public static void recogString() {
		cache.add(chs.charAt(cur));
		cur++;
		while (cur < chs.length() && chs.charAt(cur) != '\"') {
			cache.add(chs.charAt(cur));
			cur++;
		}
		if (cur+1 < chs.length() && chs.charAt(cur) == '\"')	cache.add(chs.charAt(cur));
		String string = "";
		for (int i = 0; i < cache.size(); i++)
			string += cache.get(i);
		if (cur == chs.length()) {
			List<Integer> list = getRowCol(cur-cache.size());
			errorMsg.append(list.get(0)+"行"+list.get(1)+"列: " + "字符串不合法\n");
			return;
		}
		int classNum = codes.get("stringConst");
		String att = string;
		if (attributes.get(string)==null) {
			attributes.put(string, string);
		} else {
			att = attributes.get(string);
		}
		List<Object> list = new ArrayList<>();
		list.add(string);
		list.add(classNum);
		list.add(att);
		tokens.add(list);
		List<Integer> addr = getRowCol(cur-cache.size());
		tokenAddr.add(addr.get(0)+"行" + addr.get(1)+"列");
		cache.clear();
	}
	
	// 处理界符
	public static void recogDel() {
		char c = chs.charAt(cur);
		String string = "";
		int classNum = -1;
		if (c=='(' || c==')' || c=='[' || c==']' || c=='{' || c=='}' || c==';' || c==',') {
			string = c+"";
			classNum = codes.get(string);
		} else if (c=='+') {
			if (cur+1 < chs.length() && chs.charAt(cur+1) == '+') {
				classNum = codes.get("++");
				string = "++";
				cur++;
			}
			else if (cur+1 < chs.length() && chs.charAt(cur+1) == '=') {
				classNum = codes.get("+=");
				string = "+=";
				cur++;
			}
			else {
				string = "+";
				classNum = codes.get("+");
			}
		} else if (c == '-') {
			if (cur+1 < chs.length() && chs.charAt(cur+1) == '-') {
				classNum = codes.get("--");
				string = "--";
				cur++;
			}
			else if (cur+1 < chs.length() && chs.charAt(cur+1) == '=') {
				classNum = codes.get("-=");
				string = "-=";
				cur++;
			}
			else {
				string = "-";
				classNum = codes.get("-");
			}
		} else if (c=='*') {
			if (cur+1 < chs.length() && chs.charAt(cur+1) == '=') {
				classNum = codes.get("*=");
				string = "*=";
				cur++;
			} else {
				classNum = codes.get("*");
				string = "*";
			}
		} else if(c == '!') {
			if (cur+1 < chs.length() && chs.charAt(cur+1) == '=') {
				classNum = codes.get("!=");
				string = "!=";
				cur++;
			} else {
				List<Integer> list = getRowCol(cur);
				errorMsg.append(list.get(0)+"行"+list.get(1)+"列: " + "非法字符 !");
				System.out.println(list.get(0)+"行"+list.get(1)+"列: " + "非法字符 !");
			}
		} else if (c == '=') {
			if (cur+1 < chs.length() && chs.charAt(cur+1) == '=') {
				classNum = codes.get("==");
				string = "==";
				cur++;
			} else {
				classNum = codes.get("=");
				string = "=";
			}
		} else if (c == '>') {
			if (cur+1 < chs.length() && chs.charAt(cur+1) == '=') {
				classNum = codes.get(">=");
				string = ">=";
				cur++;
			} else {
				classNum = codes.get(">");
				string = ">";
			}
		} else if (c == '<') {
			if (cur+1 < chs.length() && chs.charAt(cur+1) == '=') {
				classNum = codes.get("<=");
				string = "<=";
				cur++;
			} else {
				classNum = codes.get("<");
				string = "<";
			}
		} else if (c == '&') {
			if (cur+1 < chs.length() && chs.charAt(cur+1) == '&') {
				classNum = codes.get("&&");
				string = "&&";
				cur++;
			} else {
				classNum = codes.get("&");
				string = "&";
			}
		} else if (c == '|') {
			if (cur+1 < chs.length() && chs.charAt(cur+1) == '|') {
				classNum = codes.get("||");
				string = "||";
				cur++;
			} else {
				classNum = codes.get("|");
				string = "|";
			}
		} 
		List<Object> list = new ArrayList<>();
		list.add(string);
		list.add(classNum);
		list.add('-');
		tokens.add(list);
		List<Integer> addr = getRowCol(cur-cache.size());
		tokenAddr.add(addr.get(0)+"行" + addr.get(1)+"列");
		cache.clear();
	}
	
	public static void printTokens() {
		String text = "";
		for (int i = 0; i < tokens.size(); i++)
			text = text +  tokens.get(i).get(0) + "  " + "<" + tokens.get(i).get(1) + ", " + tokens.get(i).get(2) + ">\n";
		Read.writeFile("tokens.txt", text);
	}
	
	public static void printCharacters() {
		String text = "";
		for (String key : attributes.keySet()) 
			text = text + key+ "  " + attributes.get(key) + "\n";
		Read.writeFile("符号表.txt", text);
	}

	public static void work(String path) {
		initiate();
		getInput(path);
		scan();
		printTokens();
	}
	
}


