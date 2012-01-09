package org.maxml.common;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.PrintStream;
import java.util.EmptyStackException;
import java.util.Iterator;
import java.util.List;
import java.util.Stack;
import java.util.TreeMap;

public class SortedTreeNode {

	TreeMap children;
	boolean isEndPoint;
	boolean visited=false;
	private static final String END_POINT_SEP=":";
	private static final String NONEND_POINT_SEP="|";
	
	private SortedTreeNode parent = null; 
	private Object key;
	public static List endPointList = null;
	
	public SortedTreeNode() {
		children = new TreeMap();
		isEndPoint = false;
	}

//	public SortedTreeNode(List endPointList) {
//		this();
//		this.endPointList = endPointList;
//	}

	public void addChild(Object key, SortedTreeNode child) {
		children.put(key, child);
		child.setParent(this);
		child.setKey(key);
	}

	public void addChildAndSetEndPoint(String key, SortedTreeNode child) {
		addChild(key.substring(1), child);
		if(key.startsWith(END_POINT_SEP))child.setEndPoint(true);
	}
	
	public void removeChild(Object key) {
		children.remove(key);
	}

	public SortedTreeNode getChild(Object key) {
		return (SortedTreeNode) children.get(key);
	}

	public boolean hasChild(Object key) {
		return children.containsKey(key);
	}

	public void addDescendants(Stack keyStack) {

		if (!keyStack.empty()) {
			Object k = keyStack.pop();
			SortedTreeNode child = new SortedTreeNode();
			addChild(k, child);
			child.addDescendants(keyStack);
		} else {
			setEndPoint(true);
		}
	}

	public SortedTreeNode findDescendantMatch(Stack keyStack)
			throws EmptyStackException {

		if( keyStack.empty() && !isEndPoint()){
			return this;
		}

		if (children.containsKey(keyStack.peek())) {
			Object k = keyStack.pop();
			return getChild(k).findDescendantMatch(keyStack);
		}
		return this;
	}

	public boolean addDescendantsAtMatch(Stack keyStack) {
		try {
			SortedTreeNode sortedTreeNode = findDescendantMatch(keyStack);
			sortedTreeNode.addDescendants(keyStack);
		} catch (EmptyStackException e) {
			// if thrown, then this descendant is already an endpoint in the tree 
			return false;
		}
		return true;
	}

	public void printit(int level, PrintStream ps) {
		Iterator iter = children.keySet().iterator();
		while (iter.hasNext()) {
			Object k = iter.next();
			SortedTreeNode child = getChild(k);
			String separator = ( child.isEndPoint())? END_POINT_SEP:NONEND_POINT_SEP;
			ps.println(level + separator + k);
			child.printit(level+1, ps);
		}
	}

	private int parseOutInt(String s) {
		StringBuffer sb = new StringBuffer();
		for (int i = 0; i < s.length(); ++i)
			if (Character.isDigit(s.charAt(i)))
				sb.append(s.charAt(i));
			else
				break;
		return (sb.length()>0)?Integer.parseInt(sb.toString()):-1;
	}

	private String parseOutKey(String s) {
		int i = 0;
		for (; i < s.length(); ++i)
			if (!Character.isDigit(s.charAt(i)))
				break;
		return s.substring(i);
	}

	public int readIn(BufferedReader br, int parentLevel, StringBuffer sb) {
		try {

			String s = "";
			while ((s = br.readLine()) != null) {

				int nextLevel = parseOutInt(s);
				String key = parseOutKey(s);
				if(nextLevel<0)continue;
				if (nextLevel > parentLevel) {
					SortedTreeNode child = new SortedTreeNode();
					addChildAndSetEndPoint(key, child);
					int l;
					while((l = child.readIn(br, nextLevel, sb)) == nextLevel) {
						child = new SortedTreeNode();
						addChildAndSetEndPoint(sb.toString(), child);
					}
					return l;

				} else if (nextLevel <= parentLevel) {
					sb.setLength(0);
					sb.append(key);
					return nextLevel;
				}

			}
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return -1;
	}

	public boolean isEndPoint() {
		return isEndPoint;
	}

	public void setEndPoint(boolean isEndPoint) {
		if(this.endPointList != null) {
			this.endPointList.add(this);
		}
		this.isEndPoint = isEndPoint;
	}

	public boolean isVisited() {
		return visited;
	}

	public void setVisited(boolean visited) {
		this.visited = visited;
	}

	public SortedTreeNode getParent() {
		return parent;
	}

	public void setParent(SortedTreeNode parent) {
		this.parent = parent;
	}
	
	public String toString() {
		return (parent!=null)? parent.toString() + "/" + this.getName():this.getName();
	}
	public String getName() {
		return (key!=null)? key.toString():"";
	}

	public Object getKey() {
		return key;
	}

	public void setKey(Object name) {
		this.key = name;
	}
	

}
