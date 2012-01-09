package org.maxml.db.types.templates;

import java.util.Collection;
import java.util.Iterator;

import org.w3c.dom.Document;

import org.maxml.db.DBException;
import org.maxml.db.DBObjectAccessorFactory;
import org.maxml.db.types.links.LinkGraph;
import org.maxml.db.types.links.LinkHandler;

public class Template {
    private static Integer profileTypeId = null;
    
    private Integer id;
    private LinkGraph graph;
    private String name;
    private Document dbXpathDoc = null;
        
    public Template(){
    }
    
    public Template(LinkGraph linkGraph){
        this.graph = linkGraph;
    }
    
    public Integer getId() {
        return id;
    }
    
    public void setId(Integer id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }
    
    public static Integer getTypeId() {
        if( profileTypeId == null ) {
            profileTypeId = DBObjectAccessorFactory.i().getTypeIdForClass(Template.class.getName());
        }
        return profileTypeId;
    }
    
    public Document getDbXpathDoc() throws DBException {
        if( dbXpathDoc == null ) {
            dbXpathDoc = LinkHandler.getInstance().getDOMLinkGraph(graph.getRootLink());
        }
        return dbXpathDoc;
    }

	public LinkGraph getGraph() {
		return graph;
	}

	public void setGraph(LinkGraph profileGraph) {
		this.graph = profileGraph;
	}

}
