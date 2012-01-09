package org.maxml.util;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.Collection;

import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Properties;
import java.util.Set;

import java.util.Stack;
import java.util.StringTokenizer;
import java.util.TreeSet;

import javax.swing.JFileChooser;

import org.maxml.reflect.CachedClass;


public class Util {
	public static final String	RD	= (File.separator.equals("\\")) ? Util.i().ensureSlashes("C:\\eclipse\\setfer\\src\\src")
			: Util.i().ensureSlashes("/home/jkirkley/jrp/setfer/src");

    private static String [] javaKeyWords = {"abstract",    "continue",     "for",  "new",  "switch",
        "assert",   "default",  "goto", "package",  "synchronized",
        "boolean",  "do",   "if",   "private",  "this",
        "break",    "double",   "implements",   "protected",    "throw",
        "byte", "else", "import",   "public",   "throws",
        "case", "enum", "instanceof",   "return",   "transient",
        "catch",    "extends",  "int",  "short",    "try",
        "char", "final",    "interface",    "static",   "void",
        "class",    "finally",  "long", "strictfp", "volatile",
        "const",    "float",    "native",   "super",    "while"

    };
    

	private static Util	instance	= null;

	public static Util i() {
		if (instance == null) {
			//ClassInstrumentor.instrumentorFactory.register(Util.class, Trace.class);
			instance = new Util();
		}
		return instance;
	}

	

	public boolean isJavaKeyWord(String w) {
        return isInArray(w,javaKeyWords);
    }
    
    public int[] increaseArrLen(int[] arr, int incAmount) {
    	int [] newarr = new int[arr.length+incAmount];
    	for (int i = 0; i < arr.length; i++) {
			newarr[i] = arr[i];
		}
    	return newarr;                        
    }
    
	public String convertElements2DelimSeparatedList(Collection elements, String delim) {
		StringBuffer sb = new StringBuffer();
		String tempDelim = "";
		for (Iterator iter = elements.iterator(); iter.hasNext();) {
			String element = iter.next().toString();
			sb.append(tempDelim);
			sb.append(element);
			tempDelim = delim;
		}
		return sb.toString();
	}

	public Collection getItemsMatchingSortedKeys(Map<String, ?> map, String matchKey, boolean ascending) {
		ArrayList matches = new ArrayList();
		for ( String key: map.keySet() ) {
			if (key.contains(matchKey)) {
				matches.add(map.get(key));
			}
		}
		return matches;
	}

	public Collection getMatchingKeys(Map<String, ?> map, String matchKey) {
		List matches = new ArrayList();
		for ( String key: map.keySet() ) {
			if (key.contains(matchKey)) {
				matches.add(key);
			}
		}
		return matches;
	}

	public Collection getKeysMatchingPrefix(Map<String, ?> map, String prefix) {
		List matches = new ArrayList();
		for ( String key: map.keySet() ) {
			if (key.startsWith(prefix)) {
				matches.add(key);
			}
		}
		return matches;
	}
	
	public Collection getKeysMatchingRegex(Map<String, ?> map, Regex regex) {
		List matches = new ArrayList();
		for ( String key: map.keySet() ) {
			if (regex.doMatch(key)) {
				matches.add(key);
			}
		}
		return matches;
	}

	public Collection getValuesMatchingRegex(Map<String, ?> map, Regex regex) {
		List matches = new ArrayList();
		for ( String key: map.keySet() ) {
			if (regex.doMatch(key)) {
				matches.add(map.get(key));
			}
		}
		return matches;
	}

	public Collection getKeysMatchingSuffix(Map<String, ?> map, String matchKey) {
		List matches = new ArrayList();
		for ( String key: map.keySet() ) {
			if (key.endsWith(matchKey)) {
				matches.add(key);
			}
		}
		return matches;
	}

	public Collection getItemsForKeys(Map map, Collection keys) {
		List matches = new ArrayList();
		for (Iterator iter = keys.iterator(); iter.hasNext();) {
			matches.add(map.get(iter.next()));
		}
		return matches;
	}

	public Collection getItemsMatchingKey(Map<String, ?> map, String matchKey) {
		ArrayList matches = new ArrayList();
		for ( String key: map.keySet() ) {
			if (key.contains(matchKey)) {
				matches.add(map.get(key));
			}
		}
		return matches;
	}

	public Collection getItemsMatchingKeyPrefix(Map<String, ?> map, String matchKey) {
		ArrayList matches = new ArrayList();
		for ( String key: map.keySet() ) {
			if (key.startsWith(matchKey)) {
				matches.add(map.get(key));
			}
		}
		return matches;
	}

	public Collection getItemsMatchingKeySuffix(Map<String, ?> map, String matchKey) {
		ArrayList matches = new ArrayList();
		for ( String key: map.keySet() ) {
			if (key.endsWith(matchKey)) {
				matches.add(map.get(key));
			}
		}
		return matches;
	}

	public Collection getItemsMatchingPrefix(Collection<String> collection, String prefix) {
		ArrayList matches = new ArrayList();
		for ( String key: collection ) {
			if (key.startsWith(prefix)) {
				matches.add(key);
			}
		}
		return matches;
	}

	public Collection getItemsMatchingSuffix(Collection<String> collection, String suffix) {
		ArrayList matches = new ArrayList();
		for ( String key: collection ) {
			if (key.endsWith(suffix)) {
				matches.add(key);
			}
		}
		return matches;
	}

	public void serializeObject(Serializable obj, String fileName) {
		try {
			FileOutputStream fos = new FileOutputStream(fileName);
			ObjectOutputStream oos = new ObjectOutputStream(fos);

			oos.writeObject(obj);

			oos.close();
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}

	}

	public Object deSerializeObject(String fileName) {
		Object obj = null;
		try {

			FileInputStream fos = new FileInputStream(fileName);
			ObjectInputStream oos = new ObjectInputStream(fos);

			obj = oos.readObject();

			oos.close();
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		} catch (ClassNotFoundException e) {
			e.printStackTrace();
		}

		return obj;

	}

	public Object deSerializeObject(File objFile) {
		Object obj = null;
		try {

			FileInputStream fos = new FileInputStream(objFile);
			ObjectInputStream oos = new ObjectInputStream(fos);

			obj = oos.readObject();

			oos.close();
		} catch (IOException e) {
			e.printStackTrace();
		} catch (ClassNotFoundException e) {
			e.printStackTrace();
		}

		return obj;

	}

	public boolean notNullAndEqual(Object thisObj, Object otherObj) {
		return thisObj != null && thisObj.equals(otherObj);
	}

	public boolean notNullAndNotEqual(Object thisObj, Object otherObj) {
		return thisObj != null && !thisObj.equals(otherObj);
	}

	public String buildIndent(int numTimes) {
		return multiplyString("  ", numTimes);
	}
	
	public String multiplyString(String string, int numTimes) {
		String copy = string;
		for (int i = 0; i < numTimes; i++) {
			string += copy;
		}
		return string;
	}

	public boolean hasContent(String string) {
		return string != null && string.length() > 0;
	}

	public Collection sortCollection(Collection collection) {
		TreeSet treeSet = new TreeSet(collection);
        ArrayList newList = new ArrayList();
		for (Iterator iter = treeSet.iterator(); iter.hasNext();) {
			newList.add(iter.next());
		}
		return newList;
	}

	public Collection sortCollectionDescending(Collection collection) {
		TreeSet treeSet = new TreeSet(collection);
		ArrayList arrayList = new ArrayList();
		// collection.clear();
		for (Iterator iter = treeSet.iterator(); iter.hasNext();) {
			arrayList.add(0, iter.next());
		}
		return arrayList;
	}
    
    public String escQuotes(String s) {
        String ns = "";
        for (int i = 0; i < s.length(); i++) {
            if(s.charAt(i) == '\"') {
                ns+='\\';
            }
            ns += s.charAt(i);
        }
        return ns;
    }

	public String removeExt(String fname) {
		return (fname.indexOf('.') != -1) ? fname.substring(0, fname.indexOf('.')) : fname;
	}

	public void saveProperties(Properties properties, String filePath) {
		try {
			properties.store(new FileOutputStream(new File(filePath)), null);
		} catch (FileNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	public Properties loadProperties(String filePath) {
		Properties properties = new Properties();
		try {
			properties.load(new FileInputStream(new File(filePath)));
		} catch (FileNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return properties;
	}

	public String getPkgName(String s) {
		int len = Util.RD.length() + 1;
		if (s.length() < len || !s.startsWith(Util.RD)) {
			return null;
		}
		String pkg = s.substring(Util.RD.length() + 1);
		if (pkg.indexOf(File.separator) == -1)
			return null;
		pkg = pkg.substring(0, pkg.lastIndexOf(File.separator));
		return pkg.replace(File.separator.charAt(0), '.').toLowerCase();
	}

	public File loadFile(File baseDirFile) {

		final JFileChooser fc = new JFileChooser(baseDirFile);
		// In response to a button click:
		int returnVal = fc.showOpenDialog(null);
		if (returnVal == JFileChooser.APPROVE_OPTION) {
			File file = fc.getSelectedFile();
			return file;
		}
		return null;
	}

	public String ensureSlashes(String filePath) {
		if (File.separator.equals("/") && filePath.indexOf("\\") >= 0) {
			return filePath.replace('\\', '/');
		} else if (File.separator.equals("\\") && filePath.indexOf("/") >= 0) {
			return filePath.replace('/', '\\').toUpperCase();
		}
		return filePath;
	}

	public File loadDir(File baseDirFile) {

		final JFileChooser fc = new JFileChooser(baseDirFile);
		// In response to a button click:
		// fc.setFileFilter(new FileFilter() {
		// public boolean accept(File f) {
		// return f.isDirectory();
		// }
		// public String getDescription() {
		// return "Dir Chooser";
		// }
		// });
		fc.setFileSelectionMode(JFileChooser.DIRECTORIES_ONLY);
		int returnVal = fc.showOpenDialog(null);

		if (returnVal == JFileChooser.APPROVE_OPTION) {
			File file = fc.getSelectedFile();
			return file;
		}
		return null;
	}

	public File saveFile(File baseDirFile) {

		final JFileChooser fc = new JFileChooser(baseDirFile);
		// In response to a button click:
		int returnVal = fc.showSaveDialog(null);
		if (returnVal == JFileChooser.APPROVE_OPTION) {
			File file = fc.getSelectedFile();
			return file;
		}
		return null;
	}

	public Object loadJavaClassFromFile(File baseDirFile) {

		File file = loadFile(baseDirFile);
		if (file != null) {
			String name = file.getName();
			String pkg = getPkgName(file.getAbsolutePath());
			String cname = (name.indexOf(".") >= 0) ? name.substring(0, name.indexOf(".")) : name;

			try {
				// System.out.println(pkg + "." + cname);
				Class clazz = Class.forName(pkg + "." + cname);
				Object o = clazz.newInstance();

				return o;

			} catch (SecurityException e) {
				e.printStackTrace();
			} catch (IllegalArgumentException e) {
				e.printStackTrace();
			} catch (ClassNotFoundException e) {
				e.printStackTrace();
			} catch (InstantiationException e) {
				e.printStackTrace();
			} catch (IllegalAccessException e) {
				e.printStackTrace();
			}
		}
		return null;
	}

	public int findIndexInStringArrayIgnoreCase(String target, String[] array) {
		for (int i = 0; i < array.length; ++i)
			if (array[i].equalsIgnoreCase(target))
				return i;
		return -1;
	}
	public int findIndexInArray(Object target, Object[] array) {
		for (int i = 0; i < array.length; ++i)
			if (array[i].equals(target))
				return i;
		return -1;
	}
	public int findIndexInArray(int target, int[] array) {
		for (int i = 0; i < array.length; ++i)
			if (array[i] == target)
				return i;
		return -1;
	}
	
	public int findIndexInArray(short target, short[] array) {
		for (int i = 0; i < array.length; ++i)
			if (array[i] == target)
				return i;
		return -1;
	}

	public boolean isInArray(Object target, Object[] array) {
		return findIndexInArray(target, array) != -1;
	}

	public boolean isInStringArrayIgnoreCase(String target, String[] array) {
		return findIndexInStringArrayIgnoreCase(target, array) != -1;
	}

    public Collection getKeysMappedToValue(Map map, Object value) {
        ArrayList keys = new ArrayList();
        
        for (Iterator iterator = map.keySet().iterator(); iterator.hasNext();) {
            Object key = iterator.next();
            if (value.equals(map.get(key))) {
                keys.add(key);
            }
        }
        return keys;
    }
    
    public Object getMapKey(Map map, Object value) {

        for (Iterator iterator = map.keySet().iterator(); iterator.hasNext();) {
            Object key = iterator.next();
            if (map.get(key) == value)
				return key;
		}
		return null;
	}

	public Class loadClassFromFile() {
		return loadClassFromFile(new File(Util.RD));
	}

	public Class loadClassFromFile(File rootDir) {
		File file = Util.i().loadFile(rootDir);
		if (file != null) {
			String name = file.getName();
			String pkg = Util.i().getPkgName(file.getAbsolutePath());
			String cname = (name.indexOf(".") >= 0) ? name.substring(0, name.indexOf(".")) : name;

			try {
				return Class.forName(pkg + "." + cname);
			} catch (ClassNotFoundException e) {
				e.printStackTrace();
			}
		}
		return null;
	}

	public Object loadObjectFromFile() {
		return loadObjectFromFile(new File(Util.RD));
	}

	public Object loadObjectFromFile(File rootDir) {
		try {
			Class clazz = loadClassFromFile(rootDir);
			if (clazz != null) {
				return clazz.newInstance();
			}
		} catch (InstantiationException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IllegalAccessException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return null;
	}

	public Stack parseStringIntoStack(String str, String delim) {
		Stack stack = new Stack();
		StringTokenizer stringTokenizer = new StringTokenizer(str, delim);
		while (stringTokenizer.hasMoreTokens()) {
			stack.add(0, stringTokenizer.nextElement());
		}
		return stack;
	}

    public void removeMap(Map map, Map fromThisMap) {
        for (Iterator iter = map.keySet().iterator(); iter.hasNext();) {
            fromThisMap.remove(iter.next());
        }
    }

    public void removeEntriesWithValue(Object value, Map map) {
        if( map.containsValue(value)) {
            Collection keys = getKeysMappedToValue(map, value);
            for (Iterator iter = keys.iterator(); iter.hasNext();) {
                map.remove(iter.next());
            }
        }
    }
    
    public void removeEntriesWithKeys(Collection keys, Map m) {
        for (Iterator iter = keys.iterator(); iter.hasNext();) {
            m.remove(iter.next());
        }
    }

	public String upCaseFirstLetter(String str) {
		if (str == null)
			return str;
		if (str.length() > 1) {
			return str.substring(0, 1).toUpperCase() + str.substring(1);
		}
		return str.toUpperCase();
	}

	public String lowerCaseFirstLetter(String str) {
		if (str == null)
			return str;
		if (str.length() > 1) {
			return str.substring(0, 1).toLowerCase() + str.substring(1);
		}
		return str.toLowerCase();
	}

	public Collection getIntersection(Collection ofThisCollection, Collection withThisCollection) {
		ArrayList intersection = new ArrayList();
		for (Iterator iter = ofThisCollection.iterator(); iter.hasNext();) {
			Object object = iter.next();
			if (withThisCollection.contains(object)) {
				intersection.add(object);
			}
		}
		return intersection;
	}

	public boolean hasOneOrMoreIncommon(Collection thisCollection, Collection thatCollection) {

		for (Iterator iter = thisCollection.iterator(); iter.hasNext();) {
			Object object = iter.next();
			if (thatCollection.contains(object)) {
				return true;
			}
		}
		return false;
	}

	public void printPairList(Collection collection) {
		for (Iterator iter = collection.iterator(); iter.hasNext();) {
			Object o1 = (Object) iter.next();
		
			if (iter.hasNext()) {
				Object o2 = (Object) iter.next();
				System.out.println(o1 + " : " + o2);
			}
		}
	}

	public void printList(Collection collection) {
		if (collection == null)
			return;
		for ( Object o1: collection ) {
			System.out.print(o1 + "|");
		}
		System.out.println();
	}

	public void printList(Collection collection, String delim) {
		if (collection == null)
			return;
		for ( Object o1: collection ) {
			System.out.print(o1 + delim);
		}
		System.out.println();
	}

	public void printMap(Map map) {
		for (Iterator iter = map.keySet().iterator(); iter.hasNext();) {
			Object key = iter.next();
			Object val = map.get(key);
			System.out.println(key + ": " + val);
		}
	}

	public Map reverseMap(Map map, Map newMap) {
		for (Iterator iter = map.keySet().iterator(); iter.hasNext();) {
			Object key = iter.next();
			Object val = map.get(key);
			newMap.put(val, key);
		}
		return newMap;
	}

	public Object findKeyForValue(Map targetMap, Object value) {
		if (targetMap.containsValue(value)) {
			for (Iterator iter = targetMap.keySet().iterator(); iter.hasNext();) {
				Object key = iter.next();
				Object val = targetMap.get(key);
				if (val != null && val.equals(value)) {
					return key;
				}
			}
		}
		return null;
	}

	public boolean matchesPrefix(String str, String[] prefixes) {

		for (int i = 0; i < prefixes.length; i++) {
			if (str.startsWith(prefixes[i])) {
				return true;
			}
		}
		return false;
	}

	public boolean matchesSuffix(String str, String[] suffixes) {

		for (int i = 0; i < suffixes.length; i++) {
			if (str.endsWith(suffixes[i])) {
				return true;
			}
		}
		return false;
	}

	public boolean matchesPartOf(String str, String[] parts) {

		for (int i = 0; i < parts.length; i++) {
			if (str.indexOf(parts[i]) != -1) {
				return true;
			}
		}
		return false;
	}

	public String getFileContents(String fileName) throws FileNotFoundException, IOException {
		return getFileContents(new File(fileName));
	}

	public String getFileContents(File file) throws FileNotFoundException, IOException {

		StringBuffer content = new StringBuffer();
		FileInputStream fileInputStream = new FileInputStream(file);
		byte[] buf = new byte[1024];
		int nbytes = 0;
		while ((nbytes = fileInputStream.read(buf)) != -1) {
			content.append(new String(buf, 0, nbytes));
		}

		return content.toString();
	}

	public Object getNthElement(Collection collection, int n) {
		if (collection.size() > n) {
			int cnt = 0;
			for (Iterator iter = collection.iterator(); iter.hasNext();) {
				Object elem = iter.next();
				if (cnt == n) {
					return elem;
				}
				++cnt;
			}
		}
		return null;
	}

	public Object getCollectionElement(Collection collection, Object equalToThis) {
		for (Iterator iter = collection.iterator(); iter.hasNext();) {
			Object element = iter.next();
			if (element.equals(equalToThis)) {
				return element;
			}
		}
		return null;
	}
	
	public Collection appendPrefixToElements(Collection collection, String prefix) {
		ArrayList newElements = new ArrayList();
		for (Iterator iter = collection.iterator(); iter.hasNext();) {
			Object element = iter.next();
			newElements.add( prefix + element );
		}
		return newElements;
	}

	public Collection getSubPathElements(String pathSoFar, Collection<String> paths, String delim) {
		ArrayList subPaths = new ArrayList();

		int checkIndex = pathSoFar.length();
		for ( String path: paths ) {
			
			if(path.length()<=checkIndex || !path.startsWith(pathSoFar)) continue;
			
			String rest = path.endsWith(delim) ? path.substring(checkIndex, path.length() - delim.length()) : path
					.substring(checkIndex);
			
			if (rest.indexOf(delim) == -1) {
				subPaths.add(rest);
			}
		}
		return subPaths;
	}

	public int countNumChars(char thisChar, String inThisString) {
		int cnt = 0;
		for (int i = 0; i < inThisString.length(); i++) {
			if (inThisString.charAt(i) == thisChar)
				++cnt;
		}
		return cnt;
	}

	public Collection copyCollection(Collection fromThis) {
		return (fromThis != null) ? copyCollection(fromThis, new ArrayList()) : null;
	}

	public Collection copyCollection(Collection fromThis, Collection toThis) {
		for (Iterator iter = fromThis.iterator(); iter.hasNext();) {
			toThis.add(iter.next());
		}
		return toThis;
	}

    public Map makeMapFromProp2Prop(Collection objs, String keyProp, String valueProp) {
        Map map = new HashMap();
        for (Iterator iter = objs.iterator(); iter.hasNext();) {
            Object obj =  iter.next();
            Object key = CachedClass.getNestedPropOnObj(obj, keyProp);
            Object val = CachedClass.getNestedPropOnObj(obj, valueProp);
            map.put(key, val);
        }
        return map;
    }

    
    public boolean isAllWhiteSpace(String s) {
        for (int i = 0; i < s.length(); i++) {
            if( !Character.isWhitespace(s.charAt(i))) {
                return false; 
            }
        }
        return true;
    }


    public List makeListFromArray(Object [] objs) {
        List list = new ArrayList();
        for (int i = 0; i < objs.length; i++) {
            list.add(objs[i]);
        }
        return list;
    }

    public String removePrefix(String prefix, String str) {
        if( str.startsWith(prefix)) {
            return str.substring(prefix.length());
        }
        return null;
    }


    public int getIndexOfNextLine(StringBuffer stringBuffer, int startIndex) {
    	String lineSep = File.separatorChar == '/'? "\n": "\\r\\n";
    	int i = stringBuffer.indexOf(lineSep, startIndex);
    	if( i == -1 ) {
    		lineSep = File.separatorChar == '/'? "\\r\\n": "\n";
    		i = stringBuffer.indexOf(lineSep, startIndex);
    		if( i != -1 ) {
    			return i + lineSep.length();
    		} else {
    			return stringBuffer.length();
    		}
    	}
		return i + lineSep.length();
    }
    
    public String getNextLine(StringBuffer stringBuffer, int startIndex) {
    	String lineSep = File.separatorChar == '/'? "\n": "\\r\\n";
    	int i = stringBuffer.indexOf(lineSep, startIndex);
    	if( i == -1 ) {
    		lineSep = File.separatorChar == '/'? "\\r\\n": "\n";
    		i = stringBuffer.indexOf(lineSep, startIndex);
    		if( i != -1 ) {
    			return stringBuffer.substring(startIndex, i);
    		} else {
    			return stringBuffer.substring(startIndex);
    		}
    	}
    	return stringBuffer.substring(startIndex, i);
    }

    public boolean isQuoted(String s) {
        s = s.trim();
        if( s.startsWith("\"") && s.endsWith("\"") ) {
            return true;
        }
        return s.startsWith("'") && s.endsWith("'"); 
    }

    public Map.Entry getMapEntry(Object key, Map m){
        Set<Map.Entry> entrySet = m.entrySet();
        for ( Map.Entry entry: entrySet ) {
            if( entry.getKey().equals(key)) {
                return entry;
            }
        }
        return null;
    }

    public String[] toStringArray(Collection collection) {
    	String [] strings = new String[collection.size()];
    	int i = 0;
    	for (Iterator iter = collection.iterator(); iter.hasNext();) {
			strings[i++] = iter.next().toString(); 
		}
    	return strings;
    }
}
