package org.maxml.db.types.links;

import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.Stack;

import org.maxml.common.VisitException;
import org.maxml.common.Visitor;
import org.maxml.common.Walker;
import org.maxml.db.DBObjectAccessorFactory;
import org.maxml.reflect.CachedClass;

public class LinkTreeNameMapBuilderVisitor implements Visitor {
	
	private Map map;
	private String [] nameProperties;
	private Walker walker;
	private Stack<String> nameStack;
    private String namePrefix = "";  
    
    public static final String ID_SEP = "@";  
    public static final String PARENT_CHILD_SEP = "|";  
    
	
	public LinkTreeNameMapBuilderVisitor() {
		this.map = new HashMap();
		nameStack = new Stack<String>(); 
	}

    public LinkTreeNameMapBuilderVisitor(String [] nameProperties, Walker walker) {
		this();
		this.nameProperties = nameProperties;
		this.walker = walker;
	}

    public LinkTreeNameMapBuilderVisitor(String [] nameProperties, Walker walker, String namePrefix) {
        this(nameProperties, walker);
        this.namePrefix = namePrefix;
    }

	public void visit(Object target) throws VisitException {
		map.put( target, getParentName() + buildName(target, nameProperties[walker.getDepth()]));
	}

    public void preVisitParent(Object parent) throws VisitException {
        int i = walker.getDepth()>0? walker.getDepth()-1:0;
        String parentName = buildName(parent, nameProperties[i]);
        nameStack.push(parentName);
    }

    public void postVisitParent(Object parent) throws VisitException {
        if(!nameStack.empty()) {
            nameStack.pop();
        }
    }
    
    protected String buildName(Object target, String nameProperty) {
        
        String name = CachedClass.getNestedPropOnObj(target, nameProperty).toString();
        String id = DBObjectAccessorFactory.i().getObjectId(target).toString();
        
        return name + ID_SEP + id;
    }
	
	protected String getParentName() {
		String name = namePrefix;
		for ( String ancestorName: nameStack ) {
			
            name += ancestorName;
            name += PARENT_CHILD_SEP;
		}
		return name;
	}

	public Map getMap() {
		return map;
	}

	public void setMap(Map map) {
		this.map = map;
	}

	public Walker getWalker() {
		// TODO Auto-generated method stub
		return null;
	}

	public void setWalker(Walker walker) {
		// TODO Auto-generated method stub
		
	}

}
