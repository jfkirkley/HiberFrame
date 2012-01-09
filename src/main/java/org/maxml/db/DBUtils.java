package org.maxml.db;

import org.maxml.propertymappers.PropertyMapper;
import org.maxml.reflect.CachedClass;
import org.maxml.util.ClassUtils;

public class DBUtils {

    private static PropertyMapper propertyMapper = new PropertyMapper();

    public static Object getAndMap(Object id, Class dbClass, Class mapToClass)
            throws DBException {
        DBSessionInterface sessionInterface = DBSessionInterfaceFactory.getInstance().getDBSessionInterface();
        return getAndMap(id, dbClass, mapToClass, sessionInterface);
    }

    public static Object getAndMap(Object id, Class dbClass, Class mapToClass,
            DBSessionInterface sessionInterface) throws DBException {
        
        DBObjectAccessor accessor = DBObjectAccessorFactory.i().getDBObjectAccessor(
                dbClass, sessionInterface);

        Object source = accessor.find(id);
        return propertyMapper.map(source, mapToClass);

    }


    public static long encodeTypeAndId(int type, int id) {
        long a = ((long)type) << 32;
        long b = (long)id;
        return a | b;
    }


}
