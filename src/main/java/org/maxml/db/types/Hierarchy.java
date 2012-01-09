package org.maxml.db.types;

import java.util.Iterator;

import org.maxml.common.SimpleNode;
import org.maxml.common.Test;
import org.maxml.common.iNode;
import org.maxml.db.DBException;
import org.maxml.db.DBObjectAccessor;
import org.maxml.db.DBObjectAccessorFactory;
import org.maxml.db.types.Node;
import org.maxml.db.types.RootNodeRef;
import org.maxml.util.ClassUtils;

public class Hierarchy {
	public String								rootRefName				= "orgHierarchyRoot";

	protected static DBObjectAccessorFactory	objectAccessorFactory	= DBObjectAccessorFactory.i();

	private RootNodeRef							rootNodeRef;

	public Hierarchy(String rootRefName) {
		this.rootRefName = rootRefName;
	}

	private RootNodeRef getRootNodeRef() {
		// if( rootNodeRef == null) {
		try {
			rootNodeRef = (RootNodeRef) getAccessor(RootNodeRef.class).find(new Object[] { "name", rootRefName });
		} catch (DBException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		// }
		return rootNodeRef;
	}

	public iNode getHierarchy() {
		Node dbRoot = getRootNodeRef().getRootNode();

		return getHierarchy(dbRoot);
	}

	public iNode getHierarchy(Test childTest) {
		Node dbRoot = getRootNodeRef().getRootNode();

		return getHierarchy(dbRoot, childTest);
	}

	public iNode getHierarchy(Node dbRootNode) {

		SimpleNode rootNode = new SimpleNode(dbRootNode.getUserObject());
		populateHierarchy(rootNode, dbRootNode);

		return rootNode;
	}

	public iNode getHierarchy(Node dbRootNode, Test childTest) {

		SimpleNode rootNode = new SimpleNode(dbRootNode.getUserObject());
		populateHierarchy(rootNode, dbRootNode, childTest);

		return rootNode;
	}

	private void populateHierarchy(SimpleNode parent, Node dbParent) {
		for ( Node childDBNode: dbParent.getChildren() ) {
			SimpleNode childNode = new SimpleNode(childDBNode.getUserObject());
			parent.addChild(childNode);
			populateHierarchy(childNode, childDBNode);
		}
	}

	private void populateHierarchy(SimpleNode parent, Node dbParent, Test childTest) {
		for ( Node childDBNode: dbParent.getChildren() ) {
			if (childTest.test(childDBNode.getUserObject())) {
				SimpleNode childNode = new SimpleNode(childDBNode.getUserObject());
				parent.addChild(childNode);
				populateHierarchy(childNode, childDBNode, childTest);
			}
		}
	}

	public iNode getChildNode(Test childTest) {
		return getChildNode(getRootNodeRef().getRootNode(), childTest);
	}

	private iNode getChildNode(Node dbParent, Test childTest) {
		if (dbParent.getChildren() != null) {
			for ( Node childDBNode: dbParent.getChildren() ) {
				
				if (childTest.test(childDBNode.getUserObject())) {
					return new SimpleNode(childDBNode.getUserObject());
				} else {
					return getChildNode(childDBNode, childTest);
				}
			}
		}
		return null;
	}

	public iNode getSubHierarchy(Object userObject) {
		iNode realRootNode = getHierarchy();
		if (realRootNode.getUserObject().equals(userObject)) {
			return realRootNode;
		}
		return getSubHierarchy(realRootNode, userObject);
	}

	public iNode getSubHierarchy(Object userObject, Test childTest) {
		iNode realRootNode = getHierarchy(childTest);
		if (realRootNode.getUserObject().equals(userObject)) {
			return realRootNode;
		}
		return getSubHierarchy(realRootNode, userObject);
	}

	public iNode getSubHierarchy(iNode currNode, Object userObject) {

		for ( iNode childNode: currNode.getChildren() ) {

			if (childNode.getUserObject().equals(userObject)) {
				return childNode;
			}
		}
		for ( iNode childNode: currNode.getChildren() ) {

			iNode theRoot = getSubHierarchy(childNode, userObject);
			if (theRoot != null) {
				return theRoot;
			}
		}

		return null;
	}

	private static DBObjectAccessor getAccessor(Class c) {
		return objectAccessorFactory.getDBObjectAccessor(c);
	}
}
