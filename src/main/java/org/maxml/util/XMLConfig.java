package org.maxml.util;

import org.maxml.xpath.ApplyXPath;

import org.w3c.dom.*;
import java.util.*;



/*
  xpathConfigElement:
  <paramXpaths>
  <param name="xxx" xpath="xxx"/>
  ...
  </paramXpaths>

*/

public class XMLConfig  {

    public final static String _CLASSNAME = "org.maxml.util.XMLConfig";

    public final static String OBJECT_BUILDER_ROOTNODE = "rootNode";
    public final static String OBJECT_BUILDER_TYPE = "type";
    public final static String OBJECT_BUILDER_INSTANCENAME = "instanceName";

    private Element mySpecElement;
    private HashMap myParamMap;
    private HashMap myObjectBuilderRootNodeXPaths;
    private HashMap myObjectBuilderTypeXPaths;
    private HashMap myObjectBuilderInstanceNameXPaths;

    private NestedHashMap myObjectBuilderXPaths;

    //    public final static String POINTX="PointX";
    public XMLConfig( String xpathConfigDocName, String xmlConfigDocName ) {
		this( ApplyXPath.i().getDoc( xpathConfigDocName ),
			  ApplyXPath.i().getDoc( xmlConfigDocName ) );
    }

    public XMLConfig( Document xpathConfigDoc, Document xmlConfig ) {
		this( xmlConfig.getDocumentElement(), 
			  xpathConfigDoc.getDocumentElement() );
    }


    public XMLConfig( Element cutSpecElement, HashMap paramMap ) {
		mySpecElement = cutSpecElement;
		myParamMap = new HashMap();
		myObjectBuilderRootNodeXPaths = new HashMap();
		myObjectBuilderTypeXPaths = new HashMap();
		myObjectBuilderInstanceNameXPaths = new HashMap();
		myObjectBuilderXPaths = new NestedHashMap();
		myParamMap.putAll( paramMap );
    }

    public XMLConfig( Element specElement, Element xpathConfigElement ) {
		mySpecElement = specElement;
		myParamMap = new HashMap();
		myObjectBuilderXPaths = new NestedHashMap();
		myObjectBuilderRootNodeXPaths = new HashMap();
		myObjectBuilderTypeXPaths = new HashMap();
		myObjectBuilderInstanceNameXPaths = new HashMap();
		populateSpecTable( xpathConfigElement );
    }

    protected void populateSpecTable( Element xpathConfigElement ) {
		Vector paramNodes = ApplyXPath.i().getXPathNodeVector( "xpath", xpathConfigElement );

		for( int i = 0; i < paramNodes.size(); ++i ) {
			Element paramNode = (Element) paramNodes.elementAt(i);
			String name = paramNode.getAttribute( "name" );
			//System.out.println( "name: " + name );
			String xpath = ApplyXPath.i().getElementContent( paramNode );
			//System.out.println( "xpath: " + xpath );
			myParamMap.put( name, xpath );

			String objectBuilderName = paramNode.getAttribute( "object-builder" );
			if( objectBuilderName != null ) {
				if( name.equals( OBJECT_BUILDER_ROOTNODE ) ) {
					myObjectBuilderRootNodeXPaths.put( objectBuilderName, xpath );
				} else if( name.equals( OBJECT_BUILDER_TYPE ) ) {
					myObjectBuilderTypeXPaths.put( objectBuilderName, xpath );
				} else if( name.equals( OBJECT_BUILDER_INSTANCENAME ) ) {
					myObjectBuilderInstanceNameXPaths.put( objectBuilderName, xpath );
				} else {
					myObjectBuilderXPaths.set( objectBuilderName, name, xpath );
				}
			}
		}
    }

	public Set getObjectBuilderNames() { return myObjectBuilderRootNodeXPaths.keySet(); }

    public HashMap buildObjects( String objectBuilderName) {
    	return buildObjects(objectBuilderName, mySpecElement);
    }
    
    public HashMap buildObjects( String objectBuilderName, Element rootContextNode ) {
		HashMap builtObjects = new HashMap();

		HashMap objectBuilderXPaths = myObjectBuilderXPaths.getNestedMap(objectBuilderName);
		XMLConfigObjectBuilder builder = new XMLConfigObjectBuilder( objectBuilderXPaths );

		String rootXPath = (String)myObjectBuilderRootNodeXPaths.get( objectBuilderName );
		String typeXPath = (String)myObjectBuilderTypeXPaths.get( objectBuilderName );
		String instanceNameXPath
			= (String)myObjectBuilderInstanceNameXPaths.get( objectBuilderName );

		Vector typeParamNodes = ApplyXPath.i().getXPathNodeVector( rootXPath, rootContextNode );
		
		if( typeParamNodes == null) { return builtObjects; }
		
		for( int i = 0; i < typeParamNodes.size(); ++i ) {
			Element typeParamNode = (Element) typeParamNodes.elementAt(i);

			String type =  ApplyXPath.i().getStringValFromXPath( typeParamNode, typeXPath );	    
			Object newObject = builder.createObject( typeParamNode, type );
			String instanceName = 
				ApplyXPath.i().getStringValFromXPath( typeParamNode, instanceNameXPath );

			builtObjects.put( instanceName, newObject );
		}
		return builtObjects;
    }
    
    public HashMap buildNestedObjects( String nestedObjectXPath, String nestedObjectNameXPath ) {
		return buildNestedObjects( mySpecElement, nestedObjectXPath, nestedObjectNameXPath );
	}

    public HashMap buildNestedObjects( Element contextNode, String nestedObjectXPath, String nestedObjectNameXPath ) {

		HashMap builtObjects = new HashMap();

		Iterator iter = myObjectBuilderRootNodeXPaths.keySet().iterator();

		while(iter.hasNext()) {
			String  objectBuilderName = (String) iter.next();

			builtObjects.putAll( buildObjects( objectBuilderName, contextNode ) );

			Vector nestedParamNodes = 
				ApplyXPath.i().getXPathNodeVector( nestedObjectXPath, contextNode );

			for( int i = 0; i < nestedParamNodes.size(); ++i ) {
				Element nestedObjElement = (Element) nestedParamNodes.elementAt(i);
				HashMap nestedObjMap = buildNestedObjects( nestedObjElement, nestedObjectXPath, nestedObjectNameXPath );
				String  nestedObjectName = 
					ApplyXPath.i().getStringValFromXPath( nestedObjElement, nestedObjectNameXPath );
				
				builtObjects.put( nestedObjectName, nestedObjMap );
			}
		}
		return builtObjects;
    }
    
    
    public XMLConfig cloneProxy() {
		return new XMLConfig( getContentCopy(), myParamMap );
    }

    public Element getContent() { return mySpecElement; }
    public Element getContentCopy() { return (Element)mySpecElement.cloneNode(true); }
    public void setContent(Document content) { mySpecElement = content.getDocumentElement(); }
    public void setContent(Element content) { mySpecElement = content; }

    public void print() {
		if(Tracer.on && Tracer.on(this, "print"))Tracer.out( "myParamMap: " + myParamMap );
    }

    public Element getParamElement( String name ) {
		String xpath = (String)myParamMap.get( name );
		return (Element)ApplyXPath.i().getNodeForXPath( mySpecElement, xpath );
    }

    public Element getParamElement( Element fromThisElement, String name ) {
		String xpath = (String)myParamMap.get( name );
		return (Element)ApplyXPath.i().getNodeForXPath( fromThisElement, xpath );
    }

    public String getParamAsString( String name ) {
		String xpath = (String)myParamMap.get( name );
		return ApplyXPath.i().getStringValFromXPath( mySpecElement, xpath );
    }

    public int getParamAsInt( String name ) {
		String xpath = (String)myParamMap.get( name );
		return ApplyXPath.i().getIntValFromXPath( mySpecElement, xpath );
    }
    
    public float getParamAsFloat( String name ) {
		String xpath = (String)myParamMap.get( name );
		return ApplyXPath.i().getFloatValFromXPath( mySpecElement, xpath );
    }

    public double getParamAsDouble( String name ) {
		String xpath = (String)myParamMap.get( name );
		return ApplyXPath.i().getDoubleValFromXPath( mySpecElement, xpath );
    }

    public String getParamAsString( Element fromThisElement, String name ) {
		String xpath = (String)myParamMap.get( name );
		return ApplyXPath.i().getStringValFromXPath( fromThisElement, xpath );
    }

    public int getParamAsInt( Element fromThisElement, String name ) {
		String xpath = (String)myParamMap.get( name );
		return ApplyXPath.i().getIntValFromXPath( fromThisElement, xpath );
    }

    public float getParamAsFloat(  Element fromThisElement,String name ) {
		String xpath = (String)myParamMap.get( name );
		return ApplyXPath.i().getFloatValFromXPath( fromThisElement, xpath );
    }

    public double getParamAsDouble(  Element fromThisElement, String name ) {
		String xpath = (String)myParamMap.get( name );
		return ApplyXPath.i().getDoubleValFromXPath( fromThisElement, xpath );
    }

    public void setParam( String name, String value ) {
		String xpath = (String)myParamMap.get( name );
		Node n = ApplyXPath.i().getNodeForXPath( mySpecElement, xpath );

		n.setNodeValue( value );
    }

    public void setParam( Element specElem, String name, String value ) {
		String xpath = (String)myParamMap.get( name );
		Node n = ApplyXPath.i().getNodeForXPath( specElem, xpath );
		n.setNodeValue( value );
    }

    public void setParam( String name, int param ) {
		String paramStr = (new Integer(param)).toString();
		setParam( name, paramStr );
    }

    public void setParam( String name, float param ) {
		String paramStr = (new Float(param)).toString();
		setParam( name, paramStr );
    }

    public void setParam( String name, double param ) {
		String paramStr = (new Double(param)).toString();
		setParam( name, paramStr );
    }

    public void setParam( Element specElem, String name, int param ) {
		String paramStr = (new Integer(param)).toString();
		setParam( specElem, name, paramStr );
    }

    public void setParam( Element specElem, String name, float param ) {
		String paramStr = (new Float(param)).toString();
		setParam(  specElem, name, paramStr );
    }

    public void setParam(  Element specElem, String name, double param ) {
		String paramStr = (new Double(param)).toString();
		setParam(  specElem, name, paramStr );
    }


    public  static void main( String a[] ) {
		Tracer.on = true;

		//File f = new File( "/home/john/xforms/project/src/org.maxml/gui/threeD/cutSpec.xml" );
		//Document xpathConfDoc = ApplyXPath.i().getDoc( "/home/john/xforms/project/xml/cuts/xpaths/fullEllipseCut.xml" );

		//Document cutSpecDoc   = ApplyXPath.i().getDoc( "/home/john/xforms/project/src/org.maxml/gui/threeD/cutSpec.xml" );

		//XMLConfig xcp = new XMLConfig( cutSpecDoc.getDocumentElement(), xpathConfDoc.getDocumentElement() );
    }
    
}
