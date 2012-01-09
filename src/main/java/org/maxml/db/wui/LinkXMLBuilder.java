package org.maxml.db.wui;

import org.w3c.dom.Document;

import org.maxml.db.DBObjectAccessor;
import org.maxml.db.DBObjectAccessorFactory;
import org.maxml.db.types.links.Link;
import org.maxml.db.types.links.LinkTreeWalker;
import org.maxml.reflect.SpecialPropertyBuilder;
import org.maxml.xpath.ApplyXPath;

public class LinkXMLBuilder implements SpecialPropertyBuilder {

	public Object readProperty(Object target, Object otherObj) {

		
		return null;
	}

	public Object updateProperty(Object target, Object otherObj) {
		// TODO Auto-generated method stub
		return null;
	}

	public static void main(String[] a) {
        DBObjectAccessor targetObjectAccessor = DBObjectAccessorFactory.i().getDBObjectAccessor(
                Link.class);
        
        try {

        	for (int i = 0; i < 50; i++) {
				
        		Link link = (Link)targetObjectAccessor.find(i);
        		
        		if(link!=null) {
        			System.out.println("\n\n" + link + "\n");

        			Document doc = LinkTreeWalker.genXML(link);
        			ApplyXPath.i().ppxml(doc, System.out, 0);
        		}
			}
			
		} catch (Exception e) {
			e.printStackTrace();
		} 

		
	}
}
