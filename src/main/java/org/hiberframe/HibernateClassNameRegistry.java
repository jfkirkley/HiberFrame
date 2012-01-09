package org.hiberframe;

import java.util.Collection;
import java.util.HashMap;
import java.util.Iterator;

import org.maxml.db.DBException;
import org.maxml.db.DBObjectAccessor;
import org.maxml.db.types.ClassNameEnum;
import org.maxml.db.types.ClassNameRegistry;
import org.maxml.util.ClassUtils;

public class HibernateClassNameRegistry implements ClassNameRegistry {

    private HashMap<String,Integer>    class2TypeIdMap;
    private HashMap<Integer,String>    typeId2ClassNameMap;
    private HashMap<Integer,Class<?>>       typeId2ClassMap;
    private DBObjectAccessor classNameEnumAccessor;
    private static Object instance = null;
    
    public HibernateClassNameRegistry() {
        this.class2TypeIdMap = new HashMap<String,Integer>();
        this.typeId2ClassNameMap = new HashMap<Integer,String>();
        this.typeId2ClassMap = new HashMap<Integer,Class<?>>();
        
        // ClassNameEnum MUST be the first class enumerated
        putValues(ClassNameEnum.class.getName(), new Integer(0));
        
    }

    public static HibernateClassNameRegistry getInstance() {
        return (HibernateClassNameRegistry) (instance = ClassUtils.i().getSingletonInstance(instance,
                HibernateClassNameRegistry.class));
    }
    
    private void putValues(String className, Integer cid) {
        class2TypeIdMap.put(className, cid);
        typeId2ClassNameMap.put(cid, className);
        try {
            typeId2ClassMap.put(cid, Class.forName(className));
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
        }
    }
    
    private void loadClassNameEnums() throws DBException {
        Collection<ClassNameEnum> classNameEnums = this.classNameEnumAccessor.findAll();
        for( ClassNameEnum classNameEnum: classNameEnums){
        //for (Iterator iter = classNameEnums.iterator(); iter.hasNext();) {
            //ClassNameEnum classNameEnum = (ClassNameEnum) iter.next();
            Integer cid = classNameEnum.getId();
            String className = classNameEnum.getClassName();
    
            putValues( className, cid );
            System.out.println(cid + ": " + className);
        }
    }

    public void init(Collection<String> classNameList) throws DBException {
        loadClassNameEnums();
        for ( String className: classNameList ) {

            if( !class2TypeIdMap.containsKey(className) ) {

                ClassNameEnum classNameEnum = new ClassNameEnum(className);
                Integer cid = (Integer)this.classNameEnumAccessor.save(classNameEnum);
                //DBObjectAccessorFactory.getInstance().flush();
                //Integer cid = classNameEnum.getId();

                putValues( className, cid );

                System.out.println("new-->> " + cid + ": " + className);
            }
        }
    }

    public Integer getTypeIdForClassName(String className) {
        return (Integer) class2TypeIdMap.get(className);
    }

    public String getClassNameForTypeId(Integer typeId) {
        return (String) typeId2ClassNameMap.get(typeId);
    }

    public Class getClassForTypeId(Integer typeId) {
        return (Class) typeId2ClassMap.get(typeId);
    }

    public void setClassNameEnumAccessor(DBObjectAccessor classNameEnumAccessor) {
        this.classNameEnumAccessor = classNameEnumAccessor;
        
        // ClassNameEnum MUST be the first class enumerated
//        if(classNameEnumAccessor.numInstances()==0) {
//            try {
//                String className = ClassNameEnum.class.getName();
//                ClassNameEnum classNameEnum = new ClassNameEnum(className);
//                this.classNameEnumAccessor.save(classNameEnum);
//                DBObjectAccessorFactory.getInstance().flush();
//                Integer cid = classNameEnum.getId();
//                putValues( className, cid );
//            } catch (DBException e) {
//                // TODO Auto-generated catch block
//                e.printStackTrace();
//            }
//        }
    }

}
