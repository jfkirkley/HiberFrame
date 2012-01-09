package org.maxml.db.types.profiles;

import org.w3c.dom.Document;

import org.maxml.db.DBException;
import org.maxml.db.DBObjectAccessorFactory;
import org.maxml.db.types.links.ExtLink;
import org.maxml.db.types.links.Link;
import org.maxml.db.types.links.LinkGraph;
import org.maxml.db.types.links.LinkHandler;

public class Profile {
    private static Integer profileTypeId = null;
    
    private Integer id;
    private LinkGraph profileGraph;
    private String name;
    private Document dbXpathDoc = null;
        
    public Profile(){
    }
    
    public Profile(LinkGraph linkGraph){
        this.profileGraph = linkGraph;
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
            profileTypeId = DBObjectAccessorFactory.i().getTypeIdForClass(Profile.class.getName());
        }
        return profileTypeId;
    }

    
    public Document getDbXpathDoc() throws DBException {
        if( dbXpathDoc == null ) {
            dbXpathDoc = LinkHandler.getInstance().getDOMLinkGraph(profileGraph.getRootLink());
        }
        return dbXpathDoc;
    }

	public LinkGraph getProfileGraph() {
		return profileGraph;
	}

	public void setProfileGraph(LinkGraph profileGraph) {
		this.profileGraph = profileGraph;
	}
    
    public void addThisAsSubGraph(LinkGraph linkGraph) {
        ExtLink extLink = new ExtLink();
        LinkGraph subLinkGraph = new LinkGraph(extLink);
        linkGraph.addSubLinkGraph(subLinkGraph);
        Link linkToMe = new Link(profileGraph.getRootLink(), false);
        subLinkGraph.addChildLink(linkToMe);

    }

}
