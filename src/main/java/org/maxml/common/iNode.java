package org.maxml.common;

import java.io.PrintStream;
import java.util.Collection;

public interface iNode {
    
    public iNode getParent();
    public void setParent( iNode parent);
    
    public Collection<iNode> getChildren();
    public void setChildren( Collection children );
    public void clearChildren();
    public iNode findDescendant(Object withThisUserObject);
    public int findIndexOfChild(Object withThisUserObject);
    
    public void addChild(iNode child);
    public void removeChild(iNode child);    
    public iNode sortChildren(); 
    public void setUserObject(Object userObject);
    public Object getUserObject();
    
    public String getPathToRoot(String delim);
	public void printOut(PrintStream out, String indent);
}
