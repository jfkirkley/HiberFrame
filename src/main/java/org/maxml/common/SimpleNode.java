package org.maxml.common;

import java.io.PrintStream;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Iterator;
import java.util.TreeMap;

import org.maxml.db.types.StringValue;
import org.maxml.reflect.CachedClass;
import org.maxml.util.Util;

public class SimpleNode implements iNode {

	private Collection<iNode>	children;
	private iNode		parent;
	private Object		userObject;

	public SimpleNode(Object userObject) {
		setUserObject(userObject);
	}

	public iNode getParent() {
		return parent;
	}

	public void setParent(iNode parent) {
		this.parent = parent;
	}

	public Collection getChildren() {
		if (children == null) {
			children = new ArrayList();
		}
		return children;
	}

	public void setChildren(Collection children) {
		this.children = children;
	}

	public void addChild(iNode child) {
		child.setParent(this);
		getChildren().add(child);
	}

	public void clearChildren() {
		getChildren().clear();
	}

	public void removeChild(iNode child) {
		getChildren().remove(child);
	}

	public iNode sortChildren() {// note - no duplicates allowed on
		// node.getUserObject().toString();
		if (children != null && children.size() > 0) {
			TreeMap sortMap = new TreeMap();
			for ( iNode node: children ) {
                if( node.getUserObject().toString() == null ) {
                    System.err.println(CachedClass.getNestedPropOnObj(node.getUserObject(), "id"));
                }
				sortMap.put(node.getUserObject().toString(), node);
			}
			children.clear();
			for (Iterator iter = sortMap.keySet().iterator(); iter.hasNext();) {
				iNode node = (iNode) sortMap.get(iter.next());
				children.add(node);
			}
			for ( iNode node: children ) {
				node.sortChildren();
			}
		}
		return this;
	}

	public iNode findDescendant(Object withThisUserObject) {
		if (withThisUserObject.equals(getUserObject())) {
			return this;
		}

		if (children != null && children.size() > 0) {
			for ( iNode node: children ) {
				if (withThisUserObject.equals(node.getUserObject())) {
					return node;
				}
			}
			for ( iNode node: children ) {
				iNode searchNode = node.findDescendant(withThisUserObject);
				if (searchNode != null) {
					return searchNode;
				}
			}
		}
		return null;
	}

	public int findIndexOfChild(Object withThisUserObject) {
		if (children != null && children.size() > 0) {
			int index = 0;
			for ( iNode node: children ) {
				if (withThisUserObject.equals(node.getUserObject())) {
					return index;
				}
				++index;
			}
		}
		return -1;
	}

	public Object getUserObject() {
		return userObject;
	}

	public void setUserObject(Object userObject) {
		this.userObject = userObject;
	}

	public void printOut(PrintStream out, String indent) {
		//out.println(indent + "n>:\n");
		//CachedClass.printProperties(getUserObject());
		out.println(indent + getUserObject());
		if (children != null) {
			for ( iNode element: children ) {
				element.printOut(out, indent + "  ");
			}
		}
	}

    public static class IndexHolder{
        int i=0;

        public int getI() {
            return i;
        }
        public void inc() {
            ++i;
        }
        
    }
    public static iNode buildFromDelimitedPaths(Collection similarNames, String pathSoFar, String newName, String delim) {
        return buildFromDelimitedPaths(similarNames,pathSoFar,newName,delim, new IndexHolder(), null);
    }
    public static iNode buildFromDelimitedPaths(Collection similarNames, String pathSoFar, String newName, String delim, Test test) {
        return buildFromDelimitedPaths(similarNames,pathSoFar,newName,delim, new IndexHolder(), test);
    }
	
	public static iNode buildFromDelimitedPaths(Collection similarNames, String pathSoFar, String newName, String delim, IndexHolder indexHolder, Test childTest) {
		SimpleNode simpleNode = new SimpleNode(new StringValue(newName, indexHolder.getI()));
		pathSoFar = pathSoFar + newName + delim; 
		Collection<String> subPathElements = Util.i().getSubPathElements(pathSoFar, similarNames, delim);
		
		for ( String pathElement: subPathElements ) {

            if( childTest != null && childTest.test(pathElement)) {
                indexHolder.inc();
                simpleNode.addChild(buildFromDelimitedPaths(Util.i().getItemsMatchingPrefix(similarNames,
                        pathSoFar + pathElement + delim), pathSoFar, pathElement, delim, indexHolder, childTest));
            }
		}
		return simpleNode;
	}
    
    public String getPathToRoot(String delim){
        if( parent != null) {
            //return parent.getPathToRoot(delim) + parent.getUserObject() + delim;
            return parent.getPathToRoot(delim) + getUserObject() + delim;
        }
        return getUserObject() + delim;
    }
}
