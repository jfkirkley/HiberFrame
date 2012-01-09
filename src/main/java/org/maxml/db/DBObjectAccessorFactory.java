package org.maxml.db;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

import org.maxml.db.query.GenericQuery;
import org.maxml.db.types.ClassNameEnum;
import org.maxml.db.types.ClassNameRegistry;
import org.maxml.db.types.links.lCollection;
import org.maxml.db.types.links.pCollection;
import org.maxml.db.types.links.eCollection;
import org.hiberframe.HibUtils;
import org.hiberframe.HibernateClassNameRegistry;
import org.hiberframe.HibernateGenericQuery;
import org.hiberframe.HibernateObjectAccessor;
import org.hiberframe.HibernateSessionInterface;

import org.maxml.reflect.CachedClass;
import org.maxml.reflect.ReflectCache;
import org.maxml.util.ClassUtils;
import org.maxml.util.NestedHashMap;

public class DBObjectAccessorFactory {

	private NestedHashMap		thread2AccessorMap		= new NestedHashMap();

	private static String		HibernateImplementation	= "hibernate";
    public static String       PERSISTING_PROPERTY     = "persisting";
    public static String       READPERSISTENTOBJECTS_PROPERTY = "readPersistentObjects";
    

	private String				dbImplementationName	= HibernateImplementation;
	private ClassNameRegistry	classNameRegistry;

	private static DBObjectAccessorFactory instance				= null;

	public DBObjectAccessorFactory() {
	}

	public static DBObjectAccessorFactory i() {
		if( instance == null ){
			instance = (DBObjectAccessorFactory) ClassUtils.i().getSingletonInstance(instance,
					DBObjectAccessorFactory.class);
			instance.init();
		} 
		return instance;
	}
	
	private void init() {
		(new Thread(new DeadThreadChecker())).start();
		if (dbImplementationName.equals(HibernateImplementation)) {
			classNameRegistry = HibernateClassNameRegistry.getInstance();
			((HibernateClassNameRegistry) classNameRegistry)
					.setClassNameEnumAccessor(getDBObjectAccessor(ClassNameEnum.class));
			try {
				HibUtils.buildClass2TypeMap();
			} catch (DBException e) {
				// TODOAuto-generated catch block 
				e.printStackTrace();
			}
		}
	}

	public DBObjectAccessor getDBObjectAccessor(Class objectClass) {
		return getDBObjectAccessor(objectClass, true);
	}

	public DBObjectAccessor getDBObjectAccessor(Class objectClass, boolean addSessionInterface) {

		return getDBObjectAccessor(objectClass, addSessionInterface, true);
	}

	public DBObjectAccessor getDBObjectAccessor(Class objectClass, boolean addSessionInterface, boolean delayActions) {
		DBObjectAccessor objectAccessor = null;

		if (addSessionInterface) {

			objectAccessor = getDBObjectAccessor(objectClass, DBSessionInterfaceFactory.getInstance()
					.getDBSessionInterface());
			objectAccessor.getSessionInterface().setDelayActions(delayActions);

		} else {
			objectAccessor = getDBObjectAccessorForImplementation(objectClass);
		}

		return objectAccessor;
	}

	public DBObjectAccessor getDBObjectAccessor(Class objectClass, DBSessionInterface sessionInterface) {
		DBObjectAccessor objectAccessor = getDBObjectAccessorForImplementation(objectClass);
		objectAccessor.setSessionInterface(sessionInterface);
		return objectAccessor;
	}

	private DBObjectAccessor getDBObjectAccessorForImplementation(Class objectClass) {
		DBObjectAccessor objectAccessor = null;
		if (dbImplementationName.equals(HibernateImplementation)) {
			objectAccessor = new HibernateObjectAccessor(objectClass);
		}
		return objectAccessor;
	}

	public DBObjectAccessor getAccessor(int typeId) {
		return getAccessor(getClassForTypeId(typeId));
	}

	public DBObjectAccessor getAccessor(Class objectClass) {

		DBObjectAccessor objectAccessor = (DBObjectAccessor) thread2AccessorMap
				.get(Thread.currentThread(), objectClass);

		if (objectAccessor == null) {
			objectAccessor = getDBObjectAccessor(objectClass);
			thread2AccessorMap.set(Thread.currentThread(), objectClass, objectAccessor);
		}

		return objectAccessor;
	}

	public GenericQuery getGenericQuery() {
		GenericQuery genericQuery = null;
		if (dbImplementationName.equals(HibernateImplementation)) {
			genericQuery = new HibernateGenericQuery((HibernateSessionInterface) DBSessionInterfaceFactory
					.getInstance().getDBSessionInterface());
		}
		return genericQuery;
	}

    public boolean isClassTypeId( int typeId, Class clazz) {
        return getTypeIdForClass(clazz) == typeId;
    }
    
	public Integer getTypeIdForClass(Class clazz) {
		return getTypeIdForClass(clazz.getName());
	}
	public Integer getTypeIdForClass(String className) {
		if (dbImplementationName.equals(HibernateImplementation)) {
			return classNameRegistry.getTypeIdForClassName(className);
		}
		return null;
	}

	public String getClassNameForTypeId(int typeId) {
		if (dbImplementationName.equals(HibernateImplementation)) {
			return classNameRegistry.getClassNameForTypeId(typeId);
		}
		return null;
	}

	public Class getClassForTypeId(int typeId) {
		if (dbImplementationName.equals(HibernateImplementation)) {
			return classNameRegistry.getClassForTypeId(typeId);
		}
		return null;
	}

	public String getTypeIdClassName(Integer typeId) {
		if (dbImplementationName.equals(HibernateImplementation)) {
			return classNameRegistry.getClassNameForTypeId(typeId);
		}
		return null;
	}

	// should be used only when SURE object is persistent
    public String getTypeIdPropertyName(Class type) {
        if (dbImplementationName.equals(HibernateImplementation)) {
            return HibUtils.getIdPropertyName(type);
        }
        return null;
    }
    
    public Map getId2ObjectMap(Collection persitentObjs) {
    	Map map = new HashMap();
    	String typeProperty = null;
    	CachedClass cachedClass = null;
    	for (Iterator iter = persitentObjs.iterator(); iter.hasNext();) {
			Object pobj = iter.next();
			if(typeProperty == null) {
				typeProperty = getTypeIdPropertyName(pobj.getClass());
				cachedClass = ReflectCache.i().getClassCacheOnlyDeclared(pobj.getClass());
			}
			Object id = cachedClass.invokeGetMethod(pobj, typeProperty);
			map.put(id,pobj);
		}
    	return map;
    }

    public String getIdPropertyName(Class type) {
        if (dbImplementationName.equals(HibernateImplementation)) {
            return HibUtils.getIdPropertyNameIfPersistent(type);
        }
        return null;
    }
    
    public Object getObjectId(Object object) {
        return  CachedClass.getNestedPropOnObj(object, getIdPropertyName(object.getClass()));
    }

    public void endSession() throws DBException {
        DBSessionInterfaceFactory.getInstance().endSession();
		thread2AccessorMap.removeNestedMap(Thread.currentThread());
	}
    public void openSession() throws DBException {
        DBSessionInterfaceFactory.getInstance().getDBSessionInterface().openSession();
    }
    public boolean sessionOpen() throws DBException {
        return DBSessionInterfaceFactory.getInstance().getDBSessionInterface().sessionOpen();
    }
	public void checkEndSession() throws DBException {
		DBSessionInterfaceFactory.getInstance().checkEndSession(true);
		thread2AccessorMap.removeNestedMap(Thread.currentThread());
	}

	public void flush() throws DBException {
		DBSessionInterfaceFactory.getInstance().flush();
	}

    public void delete(Object object)throws DBException {
        getDBObjectAccessor(object.getClass()).delete(object);
    }
    
    public boolean isAttached(Object object) {
        return getDBObjectAccessor(object.getClass()).isAttached(object);
    }

    public void refresh(Object object) throws DBException {
        getDBObjectAccessor(object.getClass()).refresh(object);
    }
    
	public void persist(Object object) throws DBException {
		persist(object, false);
	}

	public void persist(Object object, boolean endSession) throws DBException {
		// check for a persisting flag and set to true if present
		CachedClass cachedClass = ReflectCache.i().getClassCacheOnlyDeclared(object.getClass());
        if (cachedClass.hasProperty(PERSISTING_PROPERTY)) {
            cachedClass.invokeSetMethod(object, PERSISTING_PROPERTY, new Boolean("true"));
		}
        getDBObjectAccessor(object.getClass()).save(object);
        if (cachedClass.hasProperty(READPERSISTENTOBJECTS_PROPERTY)) {
            Collection collection = (Collection)cachedClass.invokeGetMethod(object, READPERSISTENTOBJECTS_PROPERTY);
            for (Iterator iter = collection.iterator(); iter.hasNext();) {
                Object readPersistentObject = iter.next();
                if (readPersistentObject instanceof lCollection) {
                    lCollection pcoll = (lCollection) readPersistentObject;
                    Collection readObjs = pcoll.getReadObjects();
                    for (Iterator iterator = readObjs.iterator(); iterator.hasNext();) {
                        persist(iterator.next(), false);
                    }
                } else if (readPersistentObject instanceof pCollection) {
                    pCollection pcoll = (pCollection) readPersistentObject;
                    Collection readObjs = pcoll.getReadObjects();
                    for (Iterator iterator = readObjs.iterator(); iterator.hasNext();) {
                        persist(iterator.next(), false);
                    }
                
                } else if (readPersistentObject instanceof eCollection) {
                    eCollection eColl = (eCollection) readPersistentObject;
                    Collection readObjs = eColl.getReadObjects();
                    for (Iterator iterator = readObjs.iterator(); iterator.hasNext();) {
                        persist(iterator.next(), false);
                    }
                
                } else {
                    persist(readPersistentObject, false);
                }
            }
        }
        
		if (endSession) {
			endSession();
		}
	}

	public Object getObject(String objectClassName, Object id) throws DBException, ClassNotFoundException {
		return getObject(Class.forName(objectClassName), id);
	}

	public Object getObject(Class objectType, Object id) throws DBException {
		return getDBObjectAccessor(objectType).find(id);
	}

	class DeadThreadChecker implements Runnable {

		public void run() {

			while (true) {

				try {
					Thread.sleep(1000);

					//DBObjectAccessorFactory.getInstance().persist(new Period(), true);

//				} catch (DBException e) {
//					// TODO Auto-generated catch block

//					e.printStackTrace();
				} catch (InterruptedException e) {
					e.printStackTrace();
				}
				// System.out.println("free the walrus");
				// get dead threads
				ArrayList<Thread> deadThreadList = new ArrayList<Thread>();
				HashMap<Thread,?> threadMap = thread2AccessorMap.getRootMap();
				for ( Thread thread: threadMap.keySet() ) {
					if (!thread.isAlive()) {
						System.out.println("free the walrus" + thread);
						deadThreadList.add(thread);
					}
				}

				// delete the dead wood (threads)
				for ( Thread thread: deadThreadList ) {
					threadMap.remove(thread);
				}
			}
		}
	}
}
