package org.maxml.db.types;

public class RootNodeRef {
    
    private Integer id;
    private String name;
    private Node rootNode;
    
    public RootNodeRef() {}
    
    public RootNodeRef(Node rootNode, String name) {
        setRootNode(rootNode);
        setName(name);
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
    public Node getRootNode() {
        return rootNode;
    }
    public void setRootNode(Node rootNode) {
        this.rootNode = rootNode;
    }

}
