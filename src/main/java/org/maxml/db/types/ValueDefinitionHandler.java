package org.maxml.db.types;

import java.util.HashMap;
import java.util.Iterator;
import java.util.Collection;
import java.util.Map;
import java.util.TreeMap;

import org.maxml.db.DBException;
import org.maxml.db.DBObjectAccessor;
import org.maxml.db.DBObjectAccessorFactory;
import org.maxml.util.ClassUtils;

public class ValueDefinitionHandler {
    protected DBObjectAccessorFactory objectAccessorFactory = DBObjectAccessorFactory.i();

    private Class<?>                     definitionClass;
    private Class<?>                     valueObjectClass;
    private Class<?>                     definitionCollectionClass;
    private Class<?>                     valueCollectionObjectClass;

    public ValueDefinitionHandler(Class<?> definitionClass, Class<?> valueObjectClass, Class<?> definitionCollectionClass,
            Class<?> valueObjectCollectionClass) {
        this.definitionClass = definitionClass;
        this.valueObjectClass = valueObjectClass;
        this.definitionCollectionClass = definitionCollectionClass;
        this.valueCollectionObjectClass = valueObjectCollectionClass;
    }

    public ValueObject createValueObject(String definitionCode) throws DBException {
        return createValueObject(getDefinition(definitionCode));
    }

    public ValueObject createValueObject(Definition definition) throws DBException {
        ValueObject valueObject = (ValueObject) ClassUtils.i().createNewObjectOfType(valueObjectClass);
        valueObject.setDefinition(definition);
        getAccessor(valueObjectClass).save(valueObject);
        return valueObject;
    }

    public void createValueObjects(Collection<ValueObject> valueObjects) throws DBException {
        for ( ValueObject valueObject: valueObjects ) {
            getAccessor(valueObjectClass).save(valueObject);
        }
    }

    public ValueObject createValueCollectionObject(String definitionCollectionCode) throws DBException {
        return createValueObject(getDefinitionCollection(definitionCollectionCode));
    }

    public ValueCollectionObject createValueCollectionObject(DefinitionCollection definitionCollection)
            throws DBException {
        ValueCollectionObject valueCollectionObject = (ValueCollectionObject) ClassUtils.i().createNewObjectOfType(valueCollectionObjectClass);
        valueCollectionObject.setDefinition(definitionCollection);
        getAccessor(valueObjectClass).save(valueCollectionObject);

        for ( Definition definition: definitionCollection.getDefinitionCollection() ) {
            valueCollectionObject.getValueCollection().add(createValueObject(definition));
        }
        return valueCollectionObject;
    }

    public Definition createDefinition(String name, String code, String description, ValueObject defaultValue)
            throws DBException {
        Definition definition = (Definition) ClassUtils.i().createNewObjectOfType(definitionClass);
        definition.setCode(code);
        definition.setName(name);
        definition.setDescription(description);
        definition.setDefault(defaultValue);

        getAccessor(definitionClass).save(definition);

        return definition;
    }

    public void createDefinitions(Collection<Definition> definitions) throws DBException {
        for ( Definition definition: definitions ) {
            getAccessor(definitionClass).save(definition);
        }
    }

    public DefinitionCollection createDefinitionCollection(String name, String code, String description,
            ValueObject defaultValue, Collection definitions) throws DBException {

        DefinitionCollection definitionCollection = (DefinitionCollection) ClassUtils.i().createNewObjectOfType(definitionCollectionClass);
        definitionCollection.setCode(code);

        definitionCollection.setName(name);
        definitionCollection.setDescription(description);
        
        definitionCollection.setDefault(defaultValue);
        definitionCollection.setDefinitionCollection(definitions);

        // for (Iterator iter = definitions.iterator(); iter.hasNext();) {
        // Definition definition = (Definition) iter.next();
        // definitionCollection.getDefinitionCollection().add( definition );
        // }
        getAccessor(definitionCollectionClass).save(definitionCollection);

        return definitionCollection;
    }

    public Definition getDefinition(String code) throws DBException {
        return (Definition) getObjectByCode(code, definitionClass);
    }

    public DefinitionCollection getDefinitionCollection(String code) throws DBException {
        return (DefinitionCollection) getObjectByCode(code, definitionCollectionClass);
    }

    private Object getObjectByCode(String code, Class<?> objectClass) throws DBException {
        return getAccessor(objectClass).find(new Object[] { "code", code });
    }

    public DBObjectAccessor getAccessor(Class<?> objectClass) {
        return objectAccessorFactory.getAccessor(objectClass);
    }

    public Map makeDef2ValueMap(Collection<ValueObject> valueObjects) {
        TreeMap<Definition, ValueObject> def2ValueMap = new TreeMap();
        if (valueObjects != null) {
            for ( ValueObject valueObject: valueObjects ) {

                def2ValueMap.put(valueObject.getDefinition(), valueObject);
            }
        }
        return def2ValueMap;
    }

    // public ValueObject getValueObject( String code ) throws DBException {
    // return (ValueObject)getObjectByCode(code, valueObjectClass);
    // }
    //
    // public ValueCollectionObject getValueCollectionObject( String code )
    // throws DBException {
    // return (ValueCollectionObject)getObjectByCode(code,
    // valueCollectionObjectClass);
    // }

    // GenericQuery genericQuery = objectAccessorFactory.getGenericQuery();
    //
    // createDefinition()
    //  
    // getDefinition(String code)

}
