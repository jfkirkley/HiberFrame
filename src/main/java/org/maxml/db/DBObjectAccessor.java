package org.maxml.db;

import java.util.Collection;
import java.util.List;

import org.maxml.db.query.GenericQuery;

public interface DBObjectAccessor {
    
    public Collection findAll() throws DBException;
    public Collection findAll( Object[] criteria ) throws DBException;
    public Collection findAll( String query ) throws DBException;
    public Object find( int id ) throws DBException;
    public Object find( long id ) throws DBException;
    public Object find( Object id ) throws DBException;
    public Object find( String id ) throws DBException;
    public Object find( Object[] criteria ) throws DBException;
    public Object findByQuery( String query ) throws DBException;
    
    public Collection getPropertyFromAll(String property) throws DBException;
    public Collection getPropertyFromAll( String property, Object[] criteria) throws DBException;
    public Collection getPropertyFromAll( String property, String query ) throws DBException;
    public Object getProperty( String property, int id) throws DBException;
    public Object getProperty( String property, long id) throws DBException;
    public Object getProperty( String property, Object id) throws DBException;
    public Object getProperty( String property, String id) throws DBException;
    public Object getProperty( String property, Object[] criteria) throws DBException;
    public Object getPropertyByQuery( String property, String query) throws DBException;
    
    public Collection getPropertiesFromAll(String[] properties) throws DBException;
    public Collection getPropertiesFromAll( String[] properties, Object[] criteria) throws DBException;
    public Collection getPropertiesFromAll( String[] properties, String query) throws DBException;
    public Object[] getProperties( String[] properties, int id) throws DBException;
    public Object[] getProperties( String[] properties, long id) throws DBException;
    public Object[] getProperties( String[] properties, Object id) throws DBException;
    public Object[] getProperties( String[] properties, String id) throws DBException;
    public Object[] getProperties( String[] properties, Object[] criteria) throws DBException;
    public Object[] getPropertiesByQuery( String[] properties, String query) throws DBException;
    

    public Object saveAll( List objects ) throws DBException;
    public Object saveAll( Object[] object ) throws DBException;
    public Object save( Object object ) throws DBException;

    public Object updateAll( List objects ) throws DBException;
    public Object updateAll( Object[] object ) throws DBException;
    public Object update( Object object ) throws DBException;

    public Object deleteAll() throws DBException;
    public Object deleteAll( Object[] criteria ) throws DBException;
    public Object deleteAll( String query ) throws DBException;
    public Object delete( int id ) throws DBException;
    public Object delete( long id ) throws DBException;
    public void   delete( Object object ) throws DBException;
    public Object delete( String id ) throws DBException;
    public Object delete( Object[] criteria ) throws DBException;

    public boolean exists( int id );
    public boolean exists( long id );
    public boolean exists( Object id );
    public boolean exists( String id );
    public boolean exists( Object[] criteria );
    public boolean existsByQuery( String query );

    public int numInstances();
    public int numInstances( Object[] criteria );
    public int numInstances( String query );
    
    public void refresh(Object obj) throws DBException;
    public boolean isAttached(Object obj);

    public Integer getObjectId(Object o);
    
    public DBObjectAccessor setSessionInterface(DBSessionInterface sessionInterface); 
    public DBSessionInterface getSessionInterface(); 
    
    public GenericQuery createGenericQuery();
}
