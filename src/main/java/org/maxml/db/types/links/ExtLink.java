package org.maxml.db.types.links;

public class ExtLink extends Link{

//    public final static Integer              EXT_LINK_ID = -1; 
    
    public ExtLink() {
        super(EXT);
    }
    
    public static boolean isExtLink( Link link ) {
        return link.getType() == EXT;
    }
    

}
