package org.maxml.db;

import java.util.Iterator;
import java.util.List;

import org.maxml.util.ClassUtils;

public class ValueDefinitionHandler {
    protected DBObjectAccessorFactory objectAccessorFactory = DBObjectAccessorFactory.i();

    private Class                     valueClass;
    private Class                     definitionClass;
    private Class                     valueDefinitionAccessorClass;
    private ValueDefinitionAccessor   valueDefinitionAccessor;

    public ValueDefinitionHandler(Class valueClass, Class definitionClass, Class definitionValueAccessorClass) {
        this.valueClass = valueClass;
        this.definitionClass = definitionClass;
        this.valueDefinitionAccessorClass = definitionValueAccessorClass;
    }

    public void saveDefinition(Object definition) throws DBException {
        getAccessor(definitionClass).save(definition);
    }

    public void saveValue(Object value) throws DBException {
        getAccessor(valueClass).save(value);
    }

    public void addNewValuesFromDefinitions(List definitionList) throws DBException {
        for (Iterator iter = definitionList.iterator(); iter.hasNext();) {
            saveValue(valueDefinitionAccessor.getNewValue(iter.next()));
        }
    }

    public void update(Object valueInfo, Object referrerInfo) throws DBException {

        Object definition = valueDefinitionAccessor.getDefinition(valueInfo);

        if (definition != null) {
            Object value = valueDefinitionAccessor.getValue(definition, valueInfo);

            if (value == null) {

                // definitions exists, but value does not
                value = valueDefinitionAccessor.getNewValue(definition, valueInfo);
                getAccessor(valueClass).save(value);

            }

        } else {

            // no definition, must add it and value
            definition = valueDefinitionAccessor.getNewDefinition(valueInfo);
            Object value = valueDefinitionAccessor.getNewValue(definition, valueInfo);

            getAccessor(definitionClass).save(definition);
            getAccessor(valueClass).save(value);
        }

    }

    public void update(List rawObjectList, Object referrerInfo) throws DBException {
        update(rawObjectList, referrerInfo, true);
    }

    public void update(List rawObjectList, Object referrerInfo, boolean flush) throws DBException {

        resetDefinitionValueAccessor();
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

    public DBObjectAccessor getAccessor(Class objectClass) {
        return objectAccessorFactory.getAccessor(objectClass);
    }

    public void resetDefinitionValueAccessor() {
        valueDefinitionAccessor = (ValueDefinitionAccessor) ClassUtils.i().createNewObjectOfType(valueDefinitionAccessorClass);
    }

    protected List convertToInfoList(List rawObjectList) throws DBException {
        // by default there is no conversion
        return rawObjectList;
    }

    protected void flush(boolean flush) throws DBException {
        if (flush) {
            flush();
        }
    }

    protected void flush() throws DBException {
        DBSessionInterfaceFactory.getInstance().endSession(false);
    }

}
