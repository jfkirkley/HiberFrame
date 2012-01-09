package org.maxml.util;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Stack;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.maxml.util.Util;

public class HTMLBrokeCheck {

	protected static String[]	ignoreTags	= { "input", "br" };

	protected static String	baseFragDir	= Util.RD + Util.i().ensureSlashes("/../frags/");
	protected static String	fragSuffix	= ".html";
	protected String			frag;
	protected HashMap<String,String>			keyTagMap;
	protected List<String>			bits;
	protected Stack			tags;

	public HTMLBrokeCheck(String fragName) {
		this(fragName, 0);
	}

	public HTMLBrokeCheck(String fragName, int type) {
		this.keyTagMap = new HashMap();
		this.bits = new ArrayList();
		this.tags = new Stack();
		try {
			this.frag = Util.i().getFileContents(baseFragDir + fragName + fragSuffix);
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
		// System.out.println(frag);
		// System.out.println("\n\n\n------------------------------------------------------------------------------------\n\n\n");
		// long s = (new Date()).getTime();
		if (type == 0)
			parse();
		// putInSubFrags();
		// long e = (new Date()).getTime();
		// System.out.println("xxxxxxxxxxxx " + fragName + " took: " + (e-s));
	}

	protected boolean isIgnoreOpenTag(String tag) {
		return Util.i().matchesPrefix(tag.substring(1), ignoreTags);
	}

	protected boolean isIgnoreCloseTag(String tag) {
		return Util.i().matchesPrefix(tag.substring(2), ignoreTags);
	}

	protected List parseGrabTags(String[] grabTags) {
		ArrayList tags = new ArrayList();
		boolean inOpenTag = false;
		boolean inCloseTag = false;
		StringBuffer nextBit = new StringBuffer();

		for (int i = 0; i < frag.length(); i++) {
			if (i < frag.length() - 2 && frag.substring(i, i + 2).equals("</")) {
				// bits.add(nextBit.toString());
				nextBit.setLength(0);
				inCloseTag = true;
			} else if (i < frag.length() - 2 && frag.substring(i, i + 2).equals("<!")) {
				continue;
			} else if (frag.charAt(i) == '<') {
				// bits.add(nextBit.toString());
				nextBit.setLength(0);
				inOpenTag = true;
			}
			if (inOpenTag || inCloseTag) {
				nextBit.append(frag.charAt(i));
			}
			if (inCloseTag && frag.charAt(i) == '>') {
				// System.out.println(nextBit);
				inCloseTag = false;

				nextBit.setLength(0);
			}
			if (inOpenTag && frag.charAt(i) == '>') {
				// System.out.println(nextBit);
				inOpenTag = false;
				if (Util.i().matchesPrefix(nextBit.toString(), grabTags)) {
					tags.add(nextBit.toString());
				}
				nextBit.setLength(0);
			}
			// if( inOpenTag && Character.isSpaceChar(frag.charAt(i))) {
			// //System.out.println(nextBit);
			// bits.add(nextBit.toString());
			// //keyTagMap.put(nextBit.toString(), "");
			// inOpenTag = false;
			// nextBit.setLength(0);
			// }
		}
		return tags;
	}

	protected void parse() {

		boolean inOpenTag = false;
		boolean inCloseTag = false;
		StringBuffer nextBit = new StringBuffer();

		for (int i = 0; i < frag.length(); i++) {
			if (i < frag.length() - 2 && frag.substring(i, i + 2).equals("</")) {
				// bits.add(nextBit.toString());
				nextBit.setLength(0);
				inCloseTag = true;
			} else if (i < frag.length() - 2 && frag.substring(i, i + 2).equals("<!")) {
				continue;
			} else if (frag.charAt(i) == '<') {
				// bits.add(nextBit.toString());
				nextBit.setLength(0);
				inOpenTag = true;
			}
			if (inOpenTag || inCloseTag) {
				nextBit.append(frag.charAt(i));
			}
			if (inCloseTag && frag.charAt(i) == '>') {
				// System.out.println(nextBit);
				inCloseTag = false;
				if (!isIgnoreCloseTag(nextBit.toString()))
					bits.add(nextBit.toString());
				nextBit.setLength(0);
			}
			if (inOpenTag && frag.charAt(i) == '>') {
				// System.out.println(nextBit);
				inOpenTag = false;
				if (!isIgnoreOpenTag(nextBit.toString()))
					bits.add(nextBit.toString());
				nextBit.setLength(0);
			}
			// if( inOpenTag && Character.isSpaceChar(frag.charAt(i))) {
			// //System.out.println(nextBit);
			// bits.add(nextBit.toString());
			// //keyTagMap.put(nextBit.toString(), "");
			// inOpenTag = false;
			// nextBit.setLength(0);
			// }
		}
		// bits.add(nextBit.toString());
	}

	protected void putInSubFrags() {
		for ( String key: keyTagMap.keySet() ) {
			if (key.startsWith("${frag.")) {
				String fragName = key.substring("${frag.".length(), key.length() - 1);
				HTMLBrokeCheck subFragment = new HTMLBrokeCheck(fragName);
				keyTagMap.put(key, subFragment.toString());
			}
		}
	}

	protected String buildFrag() {
		int indent = 0;
		StringBuffer buf = new StringBuffer();
		for ( String bit: bits ) {
			// System.out.println(bit);
			if (bit.startsWith("</")) {
				indent--;
				String stag = "";
				if (!tags.empty()) {
					stag = (String) tags.pop();
					// System.out.println(bit.substring(2) + " | " +
					// stag.substring(1,3));
					if (!bit.substring(2).startsWith(stag.substring(1, 3))) {
						buf.append("\n\n--broked--\n\n");
					}
				}
				for (int i = 0; i < indent; i++) {
					buf.append("    ");
				}
				buf.append(bit);
				buf.append(stag);
				buf.append("    " + indent);
				buf.append("\n");
			} else {
				if (bit.length() > 30) {
					tags.push(bit.substring(0, 30));
				} else {
					tags.push(bit);
				}
				for (int i = 0; i < indent; i++) {
					buf.append("    ");
				}

				if (bit.length() > 30) {
					buf.append(bit.substring(0, 30));
				} else {
					buf.append(bit);
				}
				// buf.append(bit);

				buf.append("    " + indent);
				buf.append("\n");
				indent++;
			}
		}
		return buf.toString();
	}
	
	protected String recurse(int i, String s) {
		if( i < s.length() && Util.i().hasContent(s) && Util.i().notNullAndEqual(s,s)) {
			System.out.println(recurse(++i, s));
			return s.substring(i-1);
		}
		return "";
	}

	public void setTag(String key, String value) {
		keyTagMap.put(key, value);
	}

	public String toString() {
		return buildFrag();
	}

	/**
	 * @param args
	 */
	public static void main(String[] args) {
		// HTMLBrokeCheck fragment = new HTMLBrokeCheck("createDevice/cdf1");

//		ClassInstrumentor.instrumentorFactory.register(HTMLBrokeCheck.class, Trace.class);
//		ClassInstrumentor.instrumentorFactory.register(Util.class, Trace.class);
//
//		HTMLBrokeCheck fragment = new HTMLBrokeCheck("createDevice/cdf5", 1);
//		fragment.recurse(0,"this is");

		// fragment.setTag("${frag.common/backButton}", "fuole");
		// fragment.setTag("${frag.common/nextButton}", "fuole");
		// fragment.toString();
//		List tags = fragment.parseGrabTags(new String[] { "<input", "<select" });
//		Pattern pat = Pattern.compile(".*name=\"([^\"]+)\".*");
//		for (Iterator iter = tags.iterator(); iter.hasNext();) {
//			String tag = (String) iter.next();
//			Matcher matcher = pat.matcher(tag);
//			if (matcher.matches()) {
//				System.out.println("    protected String " + matcher.group(1) + ";");
//			}
//		}
		System.out.println();
		// System.out.println(fragment);
	}
}
