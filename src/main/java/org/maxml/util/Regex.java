package org.maxml.util;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class Regex {
	public static char		regexDelimChar	= '/';
	public static String	regexDelimStr	= "/";

	public static char		regexDelimChar2	= '`';
	public static String	regexDelimStr2	= "`";

	private Pattern			pattern;
	private Matcher			matcher;
	
	public static int numTimes = 0;

	public Regex(String pat) {
		pattern = Pattern.compile(pat);
	}

	public boolean doMatch(String input) {
		matcher = pattern.matcher(input);
		numTimes++;
		return matcher.matches();
	}

	public String getGroup(int i) {
		return matcher.group(i);
	}
	
	public static boolean isRegexDelimitted(String exp) {
    	return exp.startsWith(Regex.regexDelimStr) &&
    			exp.endsWith(Regex.regexDelimStr);
	}
	
	public static String getRegexp(String exp) {
		if(isRegexDelimitted(exp)) {
			return exp.substring(1, exp.length()-1);
		}
		return null;
	}

	public static boolean isRegexDelimitted2(String exp) {
    	return exp.startsWith(Regex.regexDelimStr2) &&
    			exp.endsWith(Regex.regexDelimStr2);
	}
	
	public static String getRegexp2(String exp) {
		if(isRegexDelimitted2(exp)) {
			return exp.substring(1, exp.length()-1);
		}
		return null;
	}

	public static void main(String[] a) {
		//Regex regex = new Regex("setAttribute(.*)");
		Regex regex = new Regex("^.*setAttribute\\(\\s*\\\"([^\\\"]+)\\\"\\s*,(.*)\\).*$");
		//Regex regex2 = new Regex("^.*setAttribute\\(\\s*\\\'([^\\\']+)\\\'\\s*,(.*)\\).*$");
		
		//Regex regex = new Regex("^.*setAttribute\\(([^,]+),([^\\)]+)\\).*$");
		// new Regex("r\\(([^\\" + AtomicRuleFactory.PARAM_START_TOKEN + "]+)\\"
		// + AtomicRuleFactory.PARAM_START_TOKEN + "([^\\" +
		// AtomicRuleFactory.PARAM_END_TOKEN + "]+)"
		// + AtomicRuleFactory.PARAM_END_TOKEN + "\\)");
		// Regex regex = new Regex("");

		//if (regex.doMatch("r(this/stuff/is{1,3,4})")) {
		
//		if (regex.doMatch(" this.setAttribute(\"x\", tabWinWidth - this.maxwidth - 10 );")) {
//			System.out.println(" this.setAttribute(\"x\", tabWinWidth - this.maxwidth - 10 );");
//			System.out.println(regex.getGroup(1));
//			System.out.println( regex.getGroup(2));
//		}
		
//		if (regex2.doMatch(" this.setAttribute('x', tabWinWidth - this.maxwidth - 10 );")) {
//			System.out.println(" this.setAttribute('x', tabWinWidth - this.maxwidth - 10 );");
//			System.out.println(regex2.getGroup(1));
//			System.out.println( regex2.getGroup(2));
//		}
		
		String methodBody = "         this.target.setAttribute(this.target_attr, v+1);\n       this.target.setAttribute(this.target_attr, v+1);\n this.setAttribute(\"x\", tabWinWidth - this.maxwidth - 10 );\n" + " this.setAttribute(\"x\", tabWinWidth - this.maxwidth - 80 );\n" + " this.setAttribute(\"x\", tabWinWidth - this.maxwidth - 3830 );\n";
		String regex1 =  "\\.setAttribute\\(\\s*\\\"([^\\\"]+)\\\"\\s*,(.*)\\)";
		String regex2 = "\\.setAttribute\\(\\s*\\\'([^\\\']+)\\\'\\s*,(.*)\\)";
		String regex3 = "\\.setAttribute\\(\\s*(.+)\\s*,(.*)\\)";

		methodBody = methodBody.replaceAll(regex1, ".$1 = $2");
		System.out.println(methodBody);
		//System.out.println(methodBody.replaceAll(regex2, ".$1 = $2"));
		methodBody = methodBody.replaceAll(regex3, ".$1 = $2");
		System.out.println(methodBody);
		//System.out.println(methodBody.replaceAll(regex3, ".$1 = $2"));
	}

}
