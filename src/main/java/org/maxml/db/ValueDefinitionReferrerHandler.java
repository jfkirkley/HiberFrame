package org.maxml.db;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import org.maxml.db.query.Expression;
import org.maxml.db.query.GenericQuery;
import org.maxml.util.ClassUtils;

public class ValueDefinitionReferrerHandler {

    private DBObjectAccessorFactory         objectAccessorFactory = DBObjectAccessorFactory.i();

    private Class                           valueClass;
    private Class                           definitionClass;
    private Class                           referrerClass;
    private Class                           valueDefinitionReferrerAccessorClass;
    private ValueDefinitionReferrerAccessor valueDefinitionReferrerAccessor;

    public ValueDefinitionReferrerHandler(Class valueClass, Class definitionClass, Class referrerClass,
            Class valueDefinitionReferrerAccessorClass) {
        this.valueClass = valueClass;
        this.definitionClass = definitionClass;
        this.referrerClass = referrerClass;
        this.valueDefinitionReferrerAccessorClass = valueDefinitionReferrerAccessorClass;
    }

    public void update(Object valueInfo, Object referrerInfo) throws DBException {

        Object definition = valueDefinitionReferrerAccessor.getDefinition(valueInfo);

        if (definition != null) {
            Object value = valueDefinitionReferrerAccessor.getValue(definition, valueInfo);

            if (value == null) {

                // definitions exists, but value does not
                value = valueDefinitionReferrerAccessor.getNewValue(definition, valueInfo);
                getAccessor(valueClass).save(value);

                Object referrer = valueDefinitionReferrerAccessor.getNewReferrer(value, referrerInfo);
                getAccessor(referrerClass).save(referrer);

            } else {

                if (!valueDefinitionReferrerAccessor.referrerExists(value, referrerInfo)) {

                    Object referrer = valueDefinitionReferrerAccessor.getNewReferrer(value, referrerInfo);
                    getAccessor(referrerClass).save(referrer);
                }
            }

        } else {

            // no definition, must add it and value
            definition = valueDefinitionReferrerAccessor.getNewDefinition(valueInfo);
            Object value = valueDefinitionReferrerAccessor.getNewValue(definition, valueInfo);
            Object referrer = valueDefinitionReferrerAccessor.getNewReferrer(value, referrerInfo);

            getAccessor(definitionClass).save(definition);
            getAccessor(valueClass).save(value);
            getAccessor(referrerClass).save(referrer);
        }

    }

    public void update(List rawObjectList, Object referrerInfo) throws DBException {
        update(rawObjectList, referrerInfo, true);
    }

    public void update(List rawObjectList, Object referrerInfo, boolean flush) throws DBException {

        resetValueDefinitionReferrerAccessor();
        List valueInfoList = convertToInfoList(rawObjectList);

        if (valueInfoList != null) {
            for (Iterator iterator = valueInfoList.iterator(); iterator.hasNext();) {
                update(iterator.next(), referrerInfo);
            }
        }
        if (flush) {
            DBSessionInterfaceFactory.getInstance().endSession();
        }
    }

    public List getReferrersMatchingAll(List rawObjectList) throws DBException {

        resetValueDefinitionReferrerAccessor();
        List valueInfoList = convertToInfoList(rawObjectList);

        List aliasValuePairList = new ArrayList();
        GenericQuery genericQuery = objectAccessorFactory.getGenericQuery();
        Expression expression = null;
        int cnt = 0;

        String valuePropertyName = valueDefinitionReferrerAccessor.getValuePropertyName();
        String referrerIdPropertyName = valueDefinitionReferrerAccessor.getReferrerIdPropertyName();

        for (Iterator iter = valueInfoList.iterator(); iter.hasNext();) {
            Object valueInfo = iter.next();
            Object definition = valueDefinitionReferrerAccessor.getDefinition(valueInfo);
            Object value = valueDefinitionReferrerAccessor.getValue(definition, valueInfo);

            if (value == null) {
                continue;
            }

            // r1.av = v1 && r2.v = v2 && r1.rId = r2.rId && ... rN.av = vN &&
            // rN-1.rId = rvN.rId
            expression = (expression == null) ? Expression.getEqualsExpression("r" + cnt, valuePropertyName, "v" + cnt)
                    : Expression.getAndExpression(expression, Expression.getAndExpression(
                            Expression.getEqualsExpression("r" + cnt, valuePropertyName, "v" + cnt),
                            Expression.getEqualsExpression("r" + (cnt - 1), referrerIdPropertyName, "r" + cnt,
                                    referrerIdPropertyName)));

            aliasValuePairList.add("v" + cnt);
            aliasValuePairList.add(value);
            genericQuery.addEntityClass(referrerClass, "r" + cnt);

            ++cnt;
        }

        genericQuery.setSelectParameters(new String[] { "r0", referrerIdPropertyName });
        genericQuery.setParameterExpression(expression);
        genericQuery.setParameterValues(aliasValuePairList);

        System.out.println(genericQuery.getQueryAsString());

        return genericQuery.list();
    }

    public DBObjectAccessor getAccessor(Class objectClass) {
        return objectAccessorFactory.getAccessor(objectClass);
    }

    public ValueDefinitionReferrerAccessor getValueDefinitionReferrerAccessor() {
        return valueDefinitionReferrerAccessor;
    }

    public void setValueDefinitionReferrerAccessor(ValueDefinitionReferrerAccessor valueDefinitionReferrerAccessor) {
        this.valueDefinitionReferrerAccessor = valueDefinitionReferrerAccessor;
    }

    public void resetValueDefinitionReferrerAccessor() {
        valueDefinitionReferrerAccessor = (ValueDefinitionReferrerAccessor) ClassUtils.i().createNewObjectOfType(valueDefinitionReferrerAccessorClass);
    }

    protected List convertToInfoList(List rawObjectList) throws DBException {
        // by default there is no conversion
        return rawObjectList;
    }
}
