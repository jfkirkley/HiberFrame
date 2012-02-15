/*
Author: John Kirkley
Copyright: Copyright 2011 John Kirkley.  All rights reserved.
License: MIT License.
Version: 1.0

 */
package org.hiberframe;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;

import org.hibernate.Criteria;
import org.hibernate.HibernateException;
import org.hibernate.Query;
import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.hibernate.Transaction;
import org.hibernate.cfg.Configuration;
import org.hibernate.criterion.Projection;
import org.hibernate.criterion.Order;
import org.hibernate.criterion.Criterion;
import org.hibernate.mapping.PersistentClass;
import org.hibernate.mapping.Property;
import org.hibernate.mapping.Table;

import org.maxml.db.DBException;
import org.maxml.reflect.ReflectCache;



public class HibUtils {
    public static String     QUERYALIAS       = "obj";

    private static final SessionFactory sessionFactory;
    private static final Configuration config;
    

    static {
        try {
            // Create the SessionFactory from hibernate.cfg.xml
			config = new Configuration();
            sessionFactory = config.configure().buildSessionFactory();
            //buildClass2TypeMap();
            
        } catch (Throwable ex) {
            // Make sure you log the exception, as it might be swallowed
            System.err.println("Initial SessionFactory creation failed." + ex);
            throw new ExceptionInInitializerError(ex);
        }
    }
    
    public static void buildClass2TypeMap() throws DBException {
        ArrayList<String> classNames = new ArrayList<String>(); 
        for (Iterator iter = config.getClassMappings(); iter.hasNext();) {
            PersistentClass persistentClass = (PersistentClass) iter.next();
            classNames.add(persistentClass.getClassName());
        }
        HibernateClassNameRegistry.getInstance().init(classNames);
    }
    
    public static SessionFactory getSessionFactory() {
        return sessionFactory;
    }
    
    public static Session getSession() {
        return getSessionFactory().openSession();
    }

	public static boolean isPersistentClass( Object obj ) {
		return isPersistentClass(obj.getClass());
	}

    public static boolean isPersistentClass( String className ) {
		try {
            return findPersistentClass(Class.forName(className)) != null;
        } catch (ClassNotFoundException e) {
        }
        return false;
	}

    public static boolean isPersistentClass( Class<?> clazz ) {
        return findPersistentClass(clazz) != null;
    }
    
    public static PersistentClass findPersistentClass( Class<?> clazz ) {
        return findPersistentClass(clazz, false);
    }
    
    public static PersistentClass findPersistentClass( Class<?> clazz, boolean throwError ) {
        PersistentClass persistentClass = config.getClassMapping( clazz.getName() );
        Class<?> tmpClass = clazz;
        while( persistentClass == null ) {
            if( tmpClass.getSuperclass() == null ) {
                break;
            }
            tmpClass = tmpClass.getSuperclass();
            persistentClass = config.getClassMapping( tmpClass.getName() );
        }
        if( persistentClass == null && throwError ) {
            throw new Error( "The class, '" + clazz.getName() + "', is not persistent\n");
        }
        return persistentClass;
    }
    
    public static String getIdPropertyName( Class<?> clazz ) {
        PersistentClass persistentClass = findPersistentClass(clazz, true);
        Property property = persistentClass.getIdentifierProperty();
        return property.getName();
    }

    public static String getIdPropertyNameIfPersistent( Class<?> clazz) {
        PersistentClass persistentClass = findPersistentClass(clazz, false);
        if( persistentClass == null) return null;
        Property property = persistentClass.getIdentifierProperty();
        return property.getName();
    }

	public static Iterator getMapIterator() {return config.getClassMappings();}
	

	public static void writeObj( Object obj, Session session, boolean doTransaction ) {
		try {

			if( session == null ) session = getSessionFactory().getCurrentSession();
            Transaction tx = null;
            if( doTransaction ) 
                tx = session.beginTransaction();

			ReflectCache.i().saveObject( obj, session );

			if( doTransaction ) 
				tx.commit();

		} catch ( HibernateException he ) {
			he.printStackTrace();
		}
	}

	public static Object readObj( Class<?> clazz, Session session, boolean doTransaction ) {
		return readObj( clazz.getName(), session, doTransaction );
	}
	public static Object readObj( Object obj, Session session, boolean doTransaction ) {
		return readObj( obj.getClass().getName(), session, doTransaction );
	}
	public static Object readObj( String className, Session session, boolean doTransaction ) {
		Object returnObj = null;
		try {

			if( session == null ) session = getSessionFactory().getCurrentSession();

            Transaction tx = null;
            if( doTransaction ) 
                tx = session.beginTransaction();

            returnObj = session.createQuery( "from " + className ).uniqueResult();

			ReflectCache.i().printProperties( returnObj );

			if( doTransaction ) 
				tx.commit();

		} catch ( HibernateException he ) {
			he.printStackTrace();
		}
		return returnObj;
	}

	public static Object readObj( Class<?> clazz, Session session, boolean doTransaction, Object [] criterionObjs ) {
		Object returnObj = null;
		try {

			if( session == null ) session = getSessionFactory().getCurrentSession();

            Transaction tx = null;
            if( doTransaction ) 
                tx = session.beginTransaction();

            Criteria criteria = makeCriteria( criterionObjs, session, clazz );

			returnObj = criteria.uniqueResult();
			//ReflectCache.getInstance().printProperties( returnObj );
			
			if( doTransaction ) 
				tx.commit();

		} catch ( HibernateException he ) {
			he.printStackTrace();
		}
		return returnObj;
	}

	public static List readObjects( Class<?> clazz, Session session, boolean doTransaction ) {
		return readObjects( clazz.getName(), session, doTransaction );
	}
	public static List readObjects( Object obj, Session session, boolean doTransaction ) {
		return readObjects( obj.getClass().getName(), session, doTransaction );
	}
	public static List readObjects( String className, Session session, boolean doTransaction ) {
		List returnList = null;
		try {

			if( session == null ) session = getSessionFactory().getCurrentSession();

            Transaction tx = null;
            if( doTransaction ) 
                tx = session.beginTransaction();

            returnList = session.createQuery( "from " + className ).list();

			if( doTransaction ) 
				tx.commit();

		} catch ( HibernateException he ) {
			he.printStackTrace();
		}
		return returnList;
	}


	public static List readObjects( Class<?> clazz, Session session, boolean doTransaction, Object [] criterionObjs ) {
		List returnList = null;
		try {

			if( session == null ) session = getSessionFactory().getCurrentSession();

            Transaction tx = null;
            if( doTransaction ) 
                tx = session.beginTransaction();


            Criteria criteria = makeCriteria( criterionObjs, session, clazz );

            returnList = criteria.list();

			if( doTransaction ) 
				tx.commit();

		} catch ( HibernateException he ) {
			he.printStackTrace();
		}
		return returnList;
	}

	public static Criteria makeCriteria( Object[] criterionObjs, Session session, Class<?> claz ) {

		Criteria criteria = null;

		try {

			if( session == null ) session = getSessionFactory().getCurrentSession();

			criteria  = session.createCriteria( claz );

			for( int i = 0; i < criterionObjs.length; ++i ) {
				Object o = criterionObjs[i];
				if( o instanceof Criterion ) {
					criteria.add( (Criterion)o );
				} else if( o instanceof Projection ) {
					criteria.setProjection( (Projection)o );
				} else if( o instanceof Order ) {
					criteria.addOrder( (Order)o );
				}
			}


		} catch ( HibernateException he ) {
			he.printStackTrace();
		}
		return criteria;
	}


    public static String buildSelectQuery( String property, String className ) {
        String [] properties = new String[1];
        properties[0] = property;
        return buildSelectQuery(properties, className);
    }

    public static String buildSelectQuery( String [] properties, String className ) {

        StringBuffer qb = new StringBuffer( "select " );
        String comma = "";
        for( int i = 0; i < properties.length; ++i ) {
            qb.append( comma + " " + QUERYALIAS + "." + properties[i] );
            comma = ",";
        }
        qb.append( " from " + className + " " + QUERYALIAS + " " );

        return qb.toString();
    }

    public static String buildSelectClause( String [] aliasParamArray ) {

        StringBuffer qb = new StringBuffer( "select " );
        String comma = "";
        for( int i = 0; i < aliasParamArray.length; i+=2 ) {
            qb.append( comma + " " + aliasParamArray[i] + "." + aliasParamArray[i+1] );
            comma = ",";
        }

        return qb.toString();
    }
    
    public static String buildSelectClause( List aliasParamList ) {

        StringBuffer qb = new StringBuffer( "select " );
        String comma = "";
        for (Iterator iter = aliasParamList.iterator(); iter.hasNext();) {
            qb.append( comma + " " + iter.next() + "." + iter.next());
            comma = " ,";
        }
        return qb.toString();
    }
    
    public static String buildWhereClause( String [] propertiesValuePairs, String alias) {


        StringBuffer qb = new StringBuffer( " where " );
        String operator = "";
        for( int i = 0; i < propertiesValuePairs.length; i+=2 ) {
            qb.append( operator + alias + "." + propertiesValuePairs[i] + "='" + propertiesValuePairs[i+1] + "' " );
            operator = " AND ";
        }

        return qb.toString();
    }    
    
    public static String buildWhereClause( Object [] propertyValuePairs, String alias) {


        StringBuffer qb = new StringBuffer( " where " );
        String operator = "";
        for( int i = 0; i < propertyValuePairs.length; i+=2 ) {
            qb.append( operator + alias + "." + propertyValuePairs[i] + "= :" + propertyValuePairs[i] + " " );
            operator = " AND ";
        }

        return qb.toString();
    }
    
    public static String buildWhereClause( Object [] propertyValuePairs) {

        StringBuffer qb = new StringBuffer( " where " );
        String operator = "";
        for( int i = 0; i < propertyValuePairs.length; i+=2 ) {
            qb.append( operator + propertyValuePairs[i] + "= :" + propertyValuePairs[i] + " " );
            operator = " AND ";
        }

        return qb.toString();
    }
    
    

    public static String buildWhereClauseWithAliases( Object [] propertiesValuePairs) {


        StringBuffer qb = new StringBuffer( " where " );
        String operator = "";
        for( int i = 0; i < propertiesValuePairs.length; i+=2 ) {
            qb.append( operator + propertiesValuePairs[i] + "." + propertiesValuePairs[i+1] + " = :" + propertiesValuePairs[i+1] + " " );
            operator = " AND ";
        }

        return qb.toString();
    }
    
    public static String buildFromClause( Object [] tableAliasPairs) {

        StringBuffer qb = new StringBuffer( " from " );
        String comma = "";
        for( int i = 0; i < tableAliasPairs.length; i+=2 ) {
            qb.append( comma + tableAliasPairs[i] + " " + tableAliasPairs[i+1] );
            if(i==0)comma = ", ";
        }

        return qb.toString();
    }    

    public static Query setParameters(Query query, Object[] parameters) {
        for( int i = 0; i < parameters.length; i+=2 ) {
            query.setParameter((String) parameters[i], parameters[i+1]);
        }
        return query;
    }

    public static Query setParameters(Query query, List parameters) {
    	for( Iterator iter = parameters.iterator(); iter.hasNext(); ){
        //for ( String alias: parameters ) {
            String alias = (String)iter.next();
            Object value = iter.next();
            if (value instanceof List) {
                List paramList = (List) value;
                query.setParameterList(alias, paramList);
            } else {
                query.setParameter(alias, value);
            }
        }
        return query;
    }
    
    public static HashMap readSingleProperties( String className, Session session, boolean doTransaction, String [] properties ) {
		return readSingleProperties(className,session,doTransaction,"", properties);
	}

	public static HashMap readSingleProperties( String className, Session session, boolean doTransaction, String whereClause, String [] properties ) {

		HashMap returnHashMap = new HashMap();
		try {

			if( session == null ) session = getSessionFactory().getCurrentSession();

            Transaction tx = null;
            if( doTransaction ) 
                tx = session.beginTransaction();

			List rl = session.createQuery( buildSelectQuery( properties, className ) + whereClause).list();

			if( rl.size() != 1 ) { 
				// error 
			}
			
			Object [] objs = (Object[]) rl.get(0);

			int objectIndex = 0;
			for( int i = 0; i < properties.length; ++i ) {
				returnHashMap.put(properties[i], objs[objectIndex++] );
			}
				
			if( doTransaction ) 
				tx.commit();

		} catch ( HibernateException he ) {
			he.printStackTrace();
		}
		return returnHashMap;
	}

	public static List readProperties( String className, Session session, boolean doTransaction, String [] properties ) {
		return readProperties(className,session,doTransaction,"",properties);
	}

	public static List readProperties( String className, Session session, boolean doTransaction, String whereClause, String [] properties ) {
		ArrayList returnList = new ArrayList();
		try {

			if( session == null ) session = getSessionFactory().getCurrentSession();

            Transaction tx = null;
            if( doTransaction ) 
                tx = session.beginTransaction();


			List rl = session.createQuery( buildSelectQuery( properties, className ) + whereClause).list();
			Iterator iter = rl.iterator();

			while(iter.hasNext()) {
				Object [] objs = (Object[])iter.next();

				HashMap hm = new HashMap();

				int objectIndex = 0;
				for( int i = 0; i < properties.length; ++i ) {
					hm.put(properties[i], objs[objectIndex++] );
				}

				returnList.add(hm);
			}
										

			if( doTransaction ) 
				tx.commit();

		} catch ( HibernateException he ) {
			he.printStackTrace();
		}
		return returnList;
	}

	public static void updateProperties( String className, Session session, boolean doTransaction, String whereClause, HashMap properties ) {
		try {

			if( session == null ) session = getSessionFactory().getCurrentSession();

            Transaction tx = null;
            if( doTransaction ) 
                tx = session.beginTransaction();


			StringBuffer qb = new StringBuffer( "update " + className + " o set "  );
			String comma = "";
			Iterator iter = properties.keySet().iterator();
			while(iter.hasNext()) {
				String propName = (String)iter.next();

				qb.append( comma + " o." + propName + " = " + ":" + propName );
				comma = ",";
			}
			Query updateQuery = session.createQuery( qb + whereClause);

			iter = properties.keySet().iterator();
			while(iter.hasNext()) {
				String propName = (String)iter.next();
				Object property = properties.get( propName );

				updateQuery.setParameter( propName, property );
			}

			updateQuery.executeUpdate();

			if( doTransaction ) 
				tx.commit();

		} catch ( HibernateException he ) {
			he.printStackTrace();
		}
	}

	public static void dumpTableInfo( Class<?> c ) {
		PersistentClass pc = config.getClassMapping(c.getName());
		Table t = pc.getTable();
		System.out.println(c.getName());
		System.out.println( "t name: " + t.getName() );
		System.out.println( "t id: " + t.getPrimaryKey().getName() );
		
	}

	public static void insertProperties( String className, 
										 Session session, 
										 boolean doTransaction, 
										 String [] propertyNames,
										 Object [] propertyValues,
										 Class<?>  [] mappedClasses,
										 String [] mapWhereClauses,
										 HashMap parameters
										 ) {
		try {

			if( session == null ) session = getSessionFactory().getCurrentSession();

            Transaction tx = null;
            if( doTransaction ) 
                tx = session.beginTransaction();


			StringBuffer qb = new StringBuffer( "insert into " + className + " ("  );

			String comma = "";
			for( int i = 0; i < propertyNames.length; ++i ) {
				qb.append( comma + propertyNames[i] );
				comma = ",";
			}
			qb.append( ") select " );

			comma = "";
			for( int i = 0; i < propertyValues.length; ++i ) {
				Class<?>  c = mappedClasses[i];
				String n = propertyNames[i];

				qb.append( comma );
				if( c != null ) {
					qb.append( "c" + i );
					qb.append( "." + n );
				} else {
					qb.append( "'" + n + "'" );
				}
				comma = ",";
			}
			qb.append( " from " );

			comma = "";
			for( int i = 0; i < mappedClasses.length; ++i ) {
				Class<?> c = mappedClasses[i];
				if( c != null ) {
					qb.append( comma + c.getName() + " " );
					qb.append( "c" + i );
					comma = ",";
				}
			}
			qb.append( " where " );

			comma = "";
			for( int i = 0; i < mapWhereClauses.length; ++i ) {
				if( mapWhereClauses[i] != null ) {
					qb.append( comma );
					qb.append( mapWhereClauses[i] );
					comma = ",";
				}
			}
			System.out.println( "qb: " + qb );
			
 			Query updateQuery = session.createQuery( qb.toString() );

 			Iterator iter = parameters.keySet().iterator();
 			while(iter.hasNext()) {
 				String paramName = (String)iter.next();
 				Object paramVal  = parameters.get( paramName );

 				updateQuery.setParameter( paramName, paramVal );
 			}

 			updateQuery.executeUpdate();

			if( doTransaction ) 
				tx.commit();

		} catch ( HibernateException he ) {
			he.printStackTrace();
		}
	}

	public static void insertProperties( String className, 
										 Session session, 
										 boolean doTransaction, 
										 String [] propertyNames,
										 Object [] propertyValues,
										 Class<?>  [] mappedClasses,
										 String [] mapWhereClauses,
										 String [] paramNames,
										 Object [] paramValues
										 ) {
		try {

			if( session == null ) session = getSessionFactory().getCurrentSession();

            Transaction tx = null;
            if( doTransaction ) 
                tx = session.beginTransaction();

			StringBuffer qb = new StringBuffer( "insert into " + className + " ("  );

			String comma = "";
			for( int i = 0; i < propertyNames.length; ++i ) {
				qb.append( comma + propertyNames[i] );
				comma = ",";
			}
			qb.append( ") select " );

			comma = "";
			for( int i = 0; i < propertyValues.length; ++i ) {
				Class<?>  c = mappedClasses[i];
				String n = propertyNames[i];

				qb.append( comma );
				if( c != null ) {
					qb.append( "c" + i );
					qb.append( "." + n );
				} else {
					qb.append( "'" + n + "'" );
				}
				comma = ",";
			}
			qb.append( " from " );

			comma = "";
			for( int i = 0; i < mappedClasses.length; ++i ) {
				Class<?> c = mappedClasses[i];
				if( c != null ) {
					qb.append( comma + c.getName() + " " );
					qb.append( "c" + i );
					comma = ",";
				}
			}
			qb.append( " where " );

			String operator = "";
			for( int i = 0; i < mapWhereClauses.length; ++i ) {
				if( mapWhereClauses[i] != null ) {
					qb.append( operator );
					qb.append( mapWhereClauses[i] );
					operator = " AND ";
				}
			}
			System.out.println( "qb: " + qb );
			
 			Query updateQuery = session.createQuery( qb.toString() );

			for( int i = 0; i < paramNames.length; ++i ) {
 				String paramName = paramNames[i];
 				Object paramVal  = paramValues[i];

 				updateQuery.setParameter( paramName, paramVal );
 			}

 			updateQuery.executeUpdate();

			if( doTransaction ) 
				tx.commit();

		} catch ( HibernateException he ) {
			he.printStackTrace();
		}
	}


    
    
}
