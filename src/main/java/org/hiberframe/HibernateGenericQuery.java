package org.hiberframe;

import java.util.HashMap;
import java.util.Iterator;
import java.util.List;

import org.hibernate.Query;

import org.maxml.db.DBException;
import org.maxml.db.query.Expression;
import org.maxml.db.query.GenericQuery;

public class HibernateGenericQuery implements GenericQuery {

    private HashMap<String,Class<?>>                   aliasToEntityClassMap = new HashMap<String,Class<?>>();
    private HibernateSessionInterface hibernateSessionInterface;
    private Query                     query                 = null;
    private Expression                parameterExpression   = null;
    private String                    queryString           = null;
    private String [] selectAliasParamArray = null;
    private List selectAliasParamList = null;

    public HibernateGenericQuery(HibernateSessionInterface hibernateSessionInterface) {
        this.hibernateSessionInterface = hibernateSessionInterface;
    }

    public void addEntityClass(Class entityClass, String alias) {
        aliasToEntityClassMap.put(alias, entityClass);
    }

    public void setParameterExpression(Expression expression) {
        parameterExpression = expression;
    }

    public void setParameterValues(Object[] parameterValuePairs) {
        Query q = buildQuery();
        HibUtils.setParameters(q, parameterValuePairs);
    }
    
    public void setParameterValues(List parameterValuePairs) {
        Query q = buildQuery();
        HibUtils.setParameters(q, parameterValuePairs);
    }

    public String getQueryAsString() {
        return buildQueryString();
    }

    public Object getQuery() {
        return buildQuery();
    }

    private String buildQueryString() {
        if (queryString == null) {
            queryString = "";
            
            if( selectAliasParamArray != null) {
                queryString = HibUtils.buildSelectClause(selectAliasParamArray);
            }
            else if( selectAliasParamList != null) {
                queryString = HibUtils.buildSelectClause(selectAliasParamList);
            }
            queryString += " from ";
            String comma = "";
            for ( String alias: aliasToEntityClassMap.keySet() ) {
                Class<?> entityClass = aliasToEntityClassMap.get(alias);
                queryString += comma + entityClass.getName() + " " + alias;
                comma = ", ";
            }
            if (parameterExpression != null) {
                queryString += " where " + parameterExpression;
            }
        }
        return queryString;
    }

    private Query buildQuery() {
        if (query == null) {
            query = hibernateSessionInterface.getSession().createQuery(buildQueryString());
        }
        return query;
    }

    public Object uniqueResult() {//throws DBException {
        return buildQuery().uniqueResult();  
    }
    
    public List list() { //throws DBException {
        return buildQuery().list();
    }

    public void setSelectParameters( String[] selectAliasParameterPairs ) {
        selectAliasParamArray = selectAliasParameterPairs;
    }
    
    public void setSelectParameters( List selectAliasParameterPairs ) {
        selectAliasParamList = selectAliasParameterPairs;
    }
    
}
