package org.maxml.db.types;

import java.util.Collection;
import java.util.HashSet;
import java.util.Set;

//import org.maxml.app.fei.obj.connectivity.UserObject;
import org.maxml.db.DBException;
import org.maxml.db.DBObjectAccessorFactory;
import org.maxml.db.types.links.Link;


public class Node {
    

    private static final long serialVersionUID = 1L;
    private Integer id;
    private Node parent;
    private Set<Node> children;
    private Link userObjectLink;
    private transient Object userObject=null;
    
    public Node( Object userObject ) throws DBException{
        setUserObject(userObject);
    }
    
    public Node() {} 
    
    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public Set<Node> getChildren() {
        if( children == null) children = new HashSet();
        return children;
    }
    
    public void setChildren(Set children) {
        this.children = children;
    }
    
    public Node getParent() {
        return parent;
    }
    
    public void setParent(Node parent) {
        this.parent = parent;
    }

    public void addChild(Node child) {
        child.setParent(this);
        getChildren().add(child);
    }
    
    protected Link getUserObjectLink() {
        //if( this.userObjectLink == null) this.userObjectLink = Link.makeNewLink();
        return userObjectLink;
    }

    protected void setUserObjectLink( Link link) {
        this.userObjectLink = link;
    }

//    public Object getUserObject() {
//        if( userObject == null ) userObject = getUserObjectLink().getReferentObj();
//        return userObject;
//    }
//
//    public void setUserObject(Object value ) {
//        this.userObject = value;
//        getUserObjectLink().saveNewReferent(value);
//    }
    
	public Object getUserObject() {
		if ( userObject == null) {
			Link link = getUserObjectLink();
			if (link != null) {
				userObject = link.getReferentObj();
			}
		}
		return userObject;
	}

	public void setUserObject(Object value) throws DBException {
		Link link = getUserObjectLink();
		if (link == null) {
            if( getId() == null) {
                DBObjectAccessorFactory.i().getAccessor(Node.class).save(this);
            }
			link = Link.makeNewLink(this);
			setUserObjectLink(link);
		}
		link.saveNewReferent(value);
		this.userObject = value;
	}

}

