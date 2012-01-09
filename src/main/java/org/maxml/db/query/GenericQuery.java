package org.maxml.db.query;

import java.util.List;


public interface GenericQuery {

    public void addEntityClass( Class entityClass, String alias );
    public void setParameterExpression( Expression expression );
    public void setSelectParameters( String[] selectAliasParameterPairs );
    public void setSelectParameters( List selectAliasParameterPairs );
    public void setParameterValues( Object[] parameterValuePairs );
    public void setParameterValues( List parameterValuePairs );
    public String getQueryAsString();
    public Object getQuery();
    public Object uniqueResult();// throws DBException;
    public List list();// throws DBException;
}
