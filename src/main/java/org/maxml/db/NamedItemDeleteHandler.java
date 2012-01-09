package org.maxml.db;

import org.maxml.db.types.NamedItem;
import org.maxml.db.types.links.LinkHandler;

public class NamedItemDeleteHandler implements DeleteHandler {

    public void delete(Object target) throws DBException {
        // TODO Auto-generated method stub
        
    }

    public void delete(Object targetId, Class targetType, Object infoObj) {

        DBObjectAccessor targetObjectAccessor = DBObjectAccessorFactory.i().getDBObjectAccessor(targetType);
        
        try {
            NamedItem namedItem = (NamedItem)targetObjectAccessor.find(new Integer((String)targetId));
            
            LinkHandler.getInstance().deleteReferentObject(namedItem.getItemLink());
            targetObjectAccessor.delete(namedItem);
                        
        } catch (NumberFormatException e) {
            e.printStackTrace();
        } catch (DBException e) {
            e.printStackTrace();
        }

    }

}
