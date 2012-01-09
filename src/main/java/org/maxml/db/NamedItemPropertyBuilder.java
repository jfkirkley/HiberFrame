package org.maxml.db;

import org.maxml.db.types.NamedItem;
import org.maxml.reflect.SpecialPropertyBuilder;
import org.maxml.util.Util;

public class NamedItemPropertyBuilder implements SpecialPropertyBuilder {

    public Object readProperty(Object target, Object otherObj) {
        return null;
    }

    public Object updateProperty(Object target, Object otherObj) {
        
//        MaxmlForm maxmlForm = (MaxmlForm) otherObj;
//
//        String editMode = (String) maxmlForm.getB().get("editMode");
//
//        if (Util.i().notNullAndEqual(editMode, "add")) {
//            String typeName = (String) maxmlForm.getB().get("namedItemTypeId");
//            ObjectIdParam objectIdParam = new ObjectIdParam(typeName);
//            NamedItem namedItem = (NamedItem)target;
//            try {
//                namedItem.setItem( objectIdParam.getObject(null));
//            } catch (Exception e) {
//                e.printStackTrace();
//            }
//        }
//        
        return null;
    }

}
