package org.hiberframe;

import java.util.Collection;
import java.util.Iterator;
import java.util.List;

import org.hibernate.HibernateException;
import org.hibernate.Query;

import org.maxml.db.DBException;
import org.maxml.db.DBObjectAccessor;
import org.maxml.db.DBSessionInterface;
import org.maxml.db.DBUtils;
import org.maxml.db.query.GenericQuery;
import org.maxml.reflect.CachedClass;
import org.maxml.reflect.ReflectCache;

public class HibernateObjectAccessor implements DBObjectAccessor {
    HibernateSessionInterface sessionInterface = null;
    Integer                   typeId           = null;
    Class                     objectClass;
    String                    idPropertyName;
    CachedClass               cachedClass;

    public HibernateObjectAccessor(Class objectClass) {
        this.objectClass = objectClass;
        idPropertyName = HibUtils.getIdPropertyName(objectClass);
        cachedClass = ReflectCache.i().getClassCache(objectClass);
    }

    private Integer getId(Object object) {
        return (Integer) cachedClass.invokeGetMethod(object, idPropertyName);
    }

    public Integer getTypeId() {
        if (typeId == null) {
            typeId = HibernateClassNameRegistry.getInstance().getTypeIdForClassName(objectClass.getName());
        }
        return typeId;
    }

    private String getIdWhereClause(String id) {
        return " where " + HibUtils.QUERYALIAS + "." + idPropertyName + " = " + id;
    }

    private String getFromClause() {
        return " from " + objectClass.getName() + " ";
    }

    private String getAlias() {
        return " " + HibUtils.QUERYALIAS + " ";
    }

    private String addQuotes(String s) {
        return "'" + s + "'";
    }

    private String getFromClauseWithAlias() {
        return " from " + objectClass.getName() + getAlias();
    }

    private String getSelectClauseForProperty(String property) {
        return "select " + HibUtils.QUERYALIAS + "." + property + getFromClauseWithAlias();
    }

    public Collection findAll() throws DBException {
        try {
            return sessionInterface.getSession().createQuery("from " + objectClass.getName()).list();
        } catch (HibernateException e) {
            throw new DBException(e);
        } finally {
            sessionInterface.finalizeSession();
        }
    }

    public List findAll(Object[] tableAliasPairs, Object[] parameterNames, Object[] parameterValues, String extraQuery)
            throws DBException {
        try {
            // Criteria criterion = HibUtils.makeCriteria(criteria,
            // sessionInterface.getSession(), objectClass);
            // return criterion.list();
            if (extraQuery == null)
                extraQuery = "";
            Query query = sessionInterface.getSession().createQuery(
                    HibUtils.buildFromClause(tableAliasPairs) + HibUtils.buildWhereClauseWithAliases(parameterNames)
                            + extraQuery);

            HibUtils.setParameters(query, parameterValues);

            return query.list();

        } catch (HibernateException e) {
            throw new DBException(e);
        } finally {
            sessionInterface.finalizeSession();
        }
    }

    public Collection findAll(Object[] criteria) throws DBException {
        try {
            // Criteria criterion = HibUtils.makeCriteria(criteria,
            // sessionInterface.getSession(), objectClass);
            // return criterion.list();

            Query query = sessionInterface.getSession().createQuery(
                    getFromClauseWithAlias() + HibUtils.buildWhereClause(criteria, HibUtils.QUERYALIAS));

            HibUtils.setParameters(query, criteria);

            return query.list();

        } catch (HibernateException e) {
            throw new DBException(e);
        } finally {
            sessionInterface.finalizeSession();
        }
    }

    public Collection findAll(String query) throws DBException {
        try {
            return sessionInterface.getSession().createQuery(query).list();
        } catch (HibernateException e) {
            throw new DBException(e);
        } finally {
            sessionInterface.finalizeSession();
        }
    }

    public Object find(int id) throws DBException {
        try {
            return sessionInterface.getSession().createQuery(getFromClause() + getAlias() + getIdWhereClause(id + "")).uniqueResult();
        } catch (HibernateException e) {
            throw new DBException(e);
        } finally {
            sessionInterface.finalizeSession();
        }
    }

    public Object find(long id) throws DBException {
        try {
            return sessionInterface.getSession().createQuery(getFromClause() + getAlias() + getIdWhereClause(id + "")).uniqueResult();
        } catch (HibernateException e) {
            throw new DBException(e);
        } finally {
            sessionInterface.finalizeSession();
        }
    }

    public Object find(Object id) throws DBException {
        try {
            // System.out.println(getFromClause() + getAlias() +
            // getIdWhereClause(addQuotes(id.toString()) ));

            return sessionInterface.getSession().createQuery(
                    getFromClause() + getAlias() + getIdWhereClause(addQuotes(id.toString()))).uniqueResult();
        } catch (HibernateException e) {
            throw new DBException(e);
        } finally {
            sessionInterface.finalizeSession();
        }
    }

    public Object find(String id) throws DBException {
        try {
            return sessionInterface.getSession().createQuery(
                    getFromClause() + getAlias() + getIdWhereClause(addQuotes(id))).uniqueResult();
        } catch (HibernateException e) {
            throw new DBException(e);
        } finally {
            sessionInterface.finalizeSession();
        }
    }

    public Object find(Object[] criteria) throws DBException {
        try {
            // Criteria criterion = HibUtils.makeCriteria(criteria,
            // sessionInterface.getSession(), objectClass);
            // return criterion.uniqueResult();

            Query query = sessionInterface.getSession().createQuery(
                    getFromClauseWithAlias() + HibUtils.buildWhereClause(criteria, HibUtils.QUERYALIAS));

            HibUtils.setParameters(query, criteria);

            return query.uniqueResult();

        } catch (HibernateException e) {
            throw new DBException(e);
        } finally {
            sessionInterface.finalizeSession();
        }
    }

    public Object findByQuery(String query) throws DBException {
        try {
            return sessionInterface.getSession().createQuery(query).uniqueResult();
        } catch (HibernateException e) {
            throw new DBException(e);
        } finally {
            sessionInterface.finalizeSession();
        }
    }

    public Collection getPropertyFromAll(String property) throws DBException {
        try {
            return sessionInterface.getSession().createQuery(HibUtils.buildSelectQuery(property, objectClass.getName())).list();
        } catch (HibernateException e) {
            throw new DBException(e);
        } finally {
            sessionInterface.finalizeSession();
        }
    }

    public Collection getPropertyFromAll(String property, Object[] criteria) throws DBException {
        try {

            Query query = sessionInterface.getSession().createQuery(
                    getSelectClauseForProperty(property) + HibUtils.buildWhereClause(criteria, HibUtils.QUERYALIAS));
            HibUtils.setParameters(query, criteria);

            return query.list();

        } catch (HibernateException e) {
            throw new DBException(e);
        } finally {
            sessionInterface.finalizeSession();
        }
    }

    public Collection getPropertyFromAll(String property, String query) throws DBException {
        try {
            return sessionInterface.getSession().createQuery(getSelectClauseForProperty(property) + query).list();

        } catch (HibernateException e) {
            throw new DBException(e);
        } finally {
            sessionInterface.finalizeSession();
        }
    }

    public Object getProperty(String property, int id) throws DBException {
        try {
            return sessionInterface.getSession().createQuery(
                    getSelectClauseForProperty(property) + getIdWhereClause(id + "")).uniqueResult();
        } catch (HibernateException e) {
            throw new DBException(e);
        } finally {
            sessionInterface.finalizeSession();
        }
    }

    public Object getProperty(String property, long id) throws DBException {
        try {
            return sessionInterface.getSession().createQuery(
                    getSelectClauseForProperty(property) + getIdWhereClause(id + "")).uniqueResult();
        } catch (HibernateException e) {
            throw new DBException(e);
        } finally {
            sessionInterface.finalizeSession();
        }
    }

    public Object getProperty(String property, Object id) throws DBException {
        try {
            return sessionInterface.getSession().createQuery(
                    getSelectClauseForProperty(property) + getIdWhereClause(id + "")).uniqueResult();
        } catch (HibernateException e) {
            throw new DBException(e);
        } finally {
            sessionInterface.finalizeSession();
        }
    }

    public Object getProperty(String property, String id) throws DBException {
        try {
            return sessionInterface.getSession().createQuery(
                    getSelectClauseForProperty(property) + getIdWhereClause(id)).uniqueResult();
        } catch (HibernateException e) {
            throw new DBException(e);
        } finally {
            sessionInterface.finalizeSession();
        }
    }

    public Object getProperty(String property, Object[] criteria) throws DBException {
        try {
            Query query = sessionInterface.getSession().createQuery(
                    getSelectClauseForProperty(property)
                            + HibUtils.buildWhereClause((String[]) criteria, HibUtils.QUERYALIAS));
            HibUtils.setParameters(query, criteria);

            return query.uniqueResult();
        } catch (HibernateException e) {
            throw new DBException(e);
        } finally {
            sessionInterface.finalizeSession();
        }
    }

    public Object getPropertyByQuery(String property, String query) throws DBException {
        try {
            return sessionInterface.getSession().createQuery(getSelectClauseForProperty(property) + query).uniqueResult();
        } catch (HibernateException e) {
            throw new DBException(e);
        } finally {
            sessionInterface.finalizeSession();
        }
    }

    public Collection getPropertiesFromAll(String[] properties) throws DBException {
        try {

            return sessionInterface.getSession().createQuery(
                    HibUtils.buildSelectQuery(properties, objectClass.getName())).list();

        } catch (HibernateException e) {
            throw new DBException(e);
        } finally {
            sessionInterface.finalizeSession();
        }
    }

    public Collection getPropertiesFromAll(String[] properties, Object[] criteria) throws DBException {
        try {

            Query query = sessionInterface.getSession().createQuery(
                    HibUtils.buildSelectQuery(properties, objectClass.getName())
                            + HibUtils.buildWhereClause((String[]) criteria, HibUtils.QUERYALIAS));

            HibUtils.setParameters(query, criteria);

            return query.list();
        } catch (HibernateException e) {
            throw new DBException(e);
        } finally {
            sessionInterface.finalizeSession();
        }
    }

    public Collection getPropertiesFromAll(String[] properties, String query) throws DBException {
        try {

            return sessionInterface.getSession().createQuery(
                    HibUtils.buildSelectQuery(properties, objectClass.getName()) + query).list();

        } catch (HibernateException e) {
            throw new DBException(e);
        } finally {
            sessionInterface.finalizeSession();
        }
    }

    public Object[] getProperties(String[] properties, int id) throws DBException {
        try {

            return (Object[]) sessionInterface.getSession().createQuery(
                    HibUtils.buildSelectQuery(properties, objectClass.getName()) + getIdWhereClause(id + "")).uniqueResult();

        } catch (HibernateException e) {
            throw new DBException(e);
        } finally {
            sessionInterface.finalizeSession();
        }

    }

    public Object[] getProperties(String[] properties, long id) throws DBException {
        try {

            return (Object[]) sessionInterface.getSession().createQuery(
                    HibUtils.buildSelectQuery(properties, objectClass.getName()) + getIdWhereClause(id + "")).uniqueResult();

        } catch (HibernateException e) {
            throw new DBException(e);
        } finally {
            sessionInterface.finalizeSession();
        }
    }

    public Object[] getProperties(String[] properties, Object id) throws DBException {
        try {

            return (Object[]) sessionInterface.getSession().createQuery(
                    HibUtils.buildSelectQuery(properties, objectClass.getName()) + getIdWhereClause(id + "")).uniqueResult();

        } catch (HibernateException e) {
            throw new DBException(e);
        } finally {
            sessionInterface.finalizeSession();
        }
    }

    public Object[] getProperties(String[] properties, String id) throws DBException {
        try {

            return (Object[]) sessionInterface.getSession().createQuery(
                    HibUtils.buildSelectQuery(properties, objectClass.getName()) + getIdWhereClause(id)).uniqueResult();

        } catch (HibernateException e) {
            throw new DBException(e);
        } finally {
            sessionInterface.finalizeSession();
        }
    }

    public Object[] getProperties(String[] properties, Object[] criteria) throws DBException {
        try {

            Query query = sessionInterface.getSession().createQuery(
                    HibUtils.buildSelectQuery(properties, objectClass.getName())
                            + HibUtils.buildWhereClause((String[]) criteria, HibUtils.QUERYALIAS));
            HibUtils.setParameters(query, criteria);

            return (Object[]) query.uniqueResult();

        } catch (HibernateException e) {
            throw new DBException(e);
        } finally {
            sessionInterface.finalizeSession();
        }
    }

    public Object[] getPropertiesByQuery(String[] properties, String query) throws DBException {
        try {

            return (Object[]) sessionInterface.getSession().createQuery(
                    HibUtils.buildSelectQuery(properties, objectClass.getName()) + query).uniqueResult();

        } catch (HibernateException e) {
            throw new DBException(e);
        } finally {
            sessionInterface.finalizeSession();
        }
    }

    public Object saveAll(List objects) throws DBException {
        try {
            sessionInterface.beginTransaction();
            for (Iterator iter = objects.iterator(); iter.hasNext();) {
                Object object = iter.next();
                doSave(object);
            }
            sessionInterface.commitTransaction();
        } catch (DBException e) {
            throw new DBException(e);
        } finally {
            sessionInterface.finalizeSession();
        }
        return null;
    }

    public Object saveAll(Object[] objects) throws DBException {
        try {
            sessionInterface.beginTransaction();
            for (int i = 0; i < objects.length; i++) {
                Object object = objects[i];
                doSave(object);
            }
            sessionInterface.commitTransaction();
        } catch (DBException e) {
            throw new DBException(e);
        } finally {
            sessionInterface.finalizeSession();
        }
        return null;
    }

    public Object save(Object object) throws DBException {
        try {
            sessionInterface.beginTransaction();
            Object o = doSave(object);
            sessionInterface.commitTransaction();
            return o;
        } catch (HibernateException e) {
            throw new DBException(e);
        } finally {
            sessionInterface.finalizeSession();
        }
    }

    public Object updateAll(List objects) throws DBException {
        try {
            sessionInterface.beginTransaction();
            for (Iterator iter = objects.iterator(); iter.hasNext();) {
                doUpdate(iter.next());
            }
            sessionInterface.commitTransaction();
        } catch (DBException e) {
            throw new DBException(e);
        } finally {
            sessionInterface.finalizeSession();
        }
        return null;
    }

    public Object updateAll(Object[] objects) throws DBException {
        try {
            sessionInterface.beginTransaction();
            for (int i = 0; i < objects.length; i++) {
                doUpdate(objects[i]);
            }
            sessionInterface.commitTransaction();
        } catch (DBException e) {
            throw new DBException(e);
        } finally {
            sessionInterface.finalizeSession();
        }
        return null;
    }

    public Object update(Object object) throws DBException {
        try {
            sessionInterface.beginTransaction();
            doUpdate(object);
            sessionInterface.commitTransaction();
        } catch (HibernateException e) {
            throw new DBException(e);
        } finally {
            sessionInterface.finalizeSession();
        }
        return null;
    }

    public Object deleteAll() throws DBException {
        try {
            sessionInterface.beginTransaction();
            addAllAsDirty(getPropertyFromAll(idPropertyName));
            Object o = sessionInterface.getSession().createQuery("delete " + getFromClause()).executeUpdate();

            sessionInterface.commitTransaction();
            return o;
        } catch (HibernateException e) {
            throw new DBException(e);
        } finally {
            sessionInterface.finalizeSession();
        }
    }

    public Object deleteAll(Object[] criteria) throws DBException {
        // we assume that criteria is a set of property value pairs (not
        // Criteria objects)
        try {
            sessionInterface.beginTransaction();
            addAllAsDirty(getPropertyFromAll(idPropertyName, criteria));
            Query query = sessionInterface.getSession().createQuery(
                    "delete from " + getFromClauseWithAlias()
                            + HibUtils.buildWhereClause((String[]) criteria, HibUtils.QUERYALIAS));

            HibUtils.setParameters(query, criteria);

            Object o = query.executeUpdate();

            sessionInterface.commitTransaction();
            return o;
        } catch (HibernateException e) {
            throw new DBException(e);
        } finally {
            sessionInterface.finalizeSession();
        }
    }

    public Object deleteAll(String query) throws DBException {
        try {
            sessionInterface.beginTransaction();
            addAllAsDirty(getPropertyFromAll(idPropertyName, query));
            Object o = sessionInterface.getSession().createQuery("delete " + getFromClause() + query).executeUpdate();
            sessionInterface.commitTransaction();
            return o;
        } catch (HibernateException e) {
            throw new DBException(e);
        } finally {
            sessionInterface.finalizeSession();
        }

    }

    public Object delete(int id) throws DBException {
        try {
            addDirtyObj(id);
            sessionInterface.beginTransaction();
            Object o = sessionInterface.getSession().createQuery(
                    "delete " + objectClass.getName() + " where " + idPropertyName + " = " + id).executeUpdate();
            sessionInterface.commitTransaction();
            return o;
        } catch (HibernateException e) {
            throw new DBException(e);
        } finally {
            sessionInterface.finalizeSession();
        }
    }

    public Object delete(long id) throws DBException {
        try {
            addDirtyObj(id);
            sessionInterface.beginTransaction();
            Object o = sessionInterface.getSession().createQuery(
                    "delete " + objectClass.getName() + " where " + idPropertyName + " = " + id).executeUpdate();

            sessionInterface.commitTransaction();
            return o;
        } catch (HibernateException e) {
            throw new DBException(e);
        } finally {
            sessionInterface.finalizeSession();
        }
    }

    public void delete(Object object) throws DBException {
        try {
            addDirtyObj(getObjectId(object));
            sessionInterface.beginTransaction();
            sessionInterface.getSession().delete(object);
            sessionInterface.commitTransaction();
        } catch (HibernateException e) {
            throw new DBException(e);
        } finally {
            sessionInterface.finalizeSession();
        }
    }

    public Object delete(String id) throws DBException {
        try {
            addDirtyObj(id);
            sessionInterface.beginTransaction();
            Object o = sessionInterface.getSession().createQuery(
                    "delete " + objectClass.getName() + " where " + idPropertyName + " = " + addQuotes(id)).executeUpdate();

            sessionInterface.commitTransaction();
            return o;
        } catch (HibernateException e) {
            throw new DBException(e);
        } finally {
            sessionInterface.finalizeSession();
        }
    }

    public Object delete(Object[] criteria) throws DBException {
        try {
            sessionInterface.beginTransaction();
            Query query = sessionInterface.getSession().createQuery(
                    "delete " + objectClass.getName() + HibUtils.buildWhereClause(criteria));

            HibUtils.setParameters(query, criteria);
            Object o = query.executeUpdate();
            sessionInterface.commitTransaction();
            return o;
        } catch (HibernateException e) {
            throw new DBException(e);
        } finally {
            sessionInterface.finalizeSession();
        }

    }

    public boolean exists(int id) {
        Integer cnt = (Integer) sessionInterface.getSession().createQuery(
                "select count(" + HibUtils.QUERYALIAS + "." + idPropertyName + ")" + getFromClauseWithAlias()
                        + getIdWhereClause(id + "")).uniqueResult();

        return cnt.intValue() > 0;
    }

    public boolean exists(long id) {
        Integer cnt = (Integer) sessionInterface.getSession().createQuery(
                "select count(" + HibUtils.QUERYALIAS + "." + idPropertyName + ")" + getFromClauseWithAlias()
                        + getIdWhereClause(id + "")).uniqueResult();

        return cnt.intValue() > 0;
    }

    public boolean exists(Object id) {
        Integer cnt = (Integer) sessionInterface.getSession().createQuery(
                "select count(" + HibUtils.QUERYALIAS + "." + idPropertyName + ")" + getFromClauseWithAlias()
                        + getIdWhereClause(addQuotes(id.toString()))).uniqueResult();

        return cnt.intValue() > 0;
    }

    public boolean exists(String id) {
        Integer cnt = (Integer) sessionInterface.getSession().createQuery(
                "select count(" + HibUtils.QUERYALIAS + "." + idPropertyName + ")" + getFromClauseWithAlias()
                        + getIdWhereClause(addQuotes(id))).uniqueResult();

        return cnt.intValue() > 0;
    }

    public boolean exists(Object[] criteria) {
        Query query = sessionInterface.getSession().createQuery(
                "select count(" + HibUtils.QUERYALIAS + "." + idPropertyName + ")" + getFromClauseWithAlias()
                        + HibUtils.buildWhereClause(criteria, HibUtils.QUERYALIAS));

        HibUtils.setParameters(query, criteria);

        Integer cnt = (Integer) query.uniqueResult();
        return cnt.intValue() > 0;

    }

    public boolean existsByQuery(String query) {
        Integer cnt = (Integer) sessionInterface.getSession().createQuery(
                "select count(" + HibUtils.QUERYALIAS + "." + idPropertyName + ")" + query).uniqueResult();

        return cnt.intValue() > 0;
    }

    public int numInstances() {
        Integer cnt = (Integer) sessionInterface.getSession().createQuery(
                "select count(" + HibUtils.QUERYALIAS + "." + idPropertyName + ")" + getFromClauseWithAlias()).uniqueResult();

        return cnt.intValue();
    }

    public int numInstances(Object[] criteria) {
        Query query = sessionInterface.getSession().createQuery(
                "select count(" + HibUtils.QUERYALIAS + "." + idPropertyName + ")" + getFromClauseWithAlias()
                        + HibUtils.buildWhereClause(criteria, HibUtils.QUERYALIAS));

        HibUtils.setParameters(query, criteria);
        Integer cnt = (Integer) query.uniqueResult();

        return cnt.intValue();
    }

    public int numInstances(String query) {
        Integer cnt = (Integer) sessionInterface.getSession().createQuery(
                "select count(" + HibUtils.QUERYALIAS + "." + idPropertyName + ")" + query).uniqueResult();

        return cnt.intValue();
    }

    public void refresh(Object obj) throws DBException {
        try {
            sessionInterface.getSession().refresh(obj);
        } catch (HibernateException e) {
            throw new DBException(e);
        }
    }

    public boolean isAttached(Object obj) {
        return sessionInterface.getSession().contains(obj);
    }

    public DBObjectAccessor setSessionInterface(DBSessionInterface sessionInterface) {
        this.sessionInterface = (HibernateSessionInterface) sessionInterface;
        return this;
    }

    public DBSessionInterface getSessionInterface() {
        return sessionInterface;
    }

    public GenericQuery createGenericQuery() {
        return new HibernateGenericQuery(sessionInterface);
    }

    private Object doSave(Object object) {

        Integer id = getObjectId(object);
        if (id == null) {
            id = (Integer) sessionInterface.getSession().save(object);
        } else {
            sessionInterface.getSession().update(object);
        }
        addDirtyObj(id);
        return id;
    }

    private void doUpdate(Object object) {
        addDirtyObj(getId(object));
        sessionInterface.getSession().update(object);
    }

    private void addDirtyObj(Integer id) {
        sessionInterface.addDirtyClass(getTypeId());
        sessionInterface.addDirtyObj(DBUtils.encodeTypeAndId(id, getTypeId()));
    }

    private void addDirtyObj(Object id) {
        sessionInterface.addDirtyClass(getTypeId());
        sessionInterface.addDirtyObj(DBUtils.encodeTypeAndId(new Integer(id.toString()),
                getTypeId()));
    }

    private void addAllAsDirty(Collection ids) {
        sessionInterface.addDirtyClass(getTypeId());
        for (Iterator iter = ids.iterator(); iter.hasNext();) {
            addDirtyObj(iter.next());
        }
    }

    public Integer getObjectId(Object o) {
        Integer id = null;
        try {
            id = (Integer) sessionInterface.getSession().getIdentifier(o);
        } catch (HibernateException e) {
            // its okay not to have id - object still transient
        }
        return id;
    }

}
