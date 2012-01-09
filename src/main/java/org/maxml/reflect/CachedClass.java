package org.maxml.reflect;

import org.hibernate.Session;

import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;

import java.io.PrintStream;
import java.lang.Class;
import java.lang.Short;
import java.lang.Character;
import java.lang.Boolean;
import java.lang.Byte;
import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.lang.reflect.Modifier;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Date;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.TreeMap;

import org.maxml.db.types.links.Link;
import org.hiberframe.HibUtils;
import org.maxml.util.ClassUtils;
import org.maxml.util.Util;

import org.maxml.xpath.ApplyXPath;

public class CachedClass {

    TreeMap                           methodMap;
    TreeMap                           getMethods;
    TreeMap                           setMethods;

    private ReflectionProviderFactory reflectionProviderFactory;

    ReflectCache                      reflectCache;
    private Class                     thisClass;

    public Class getThisClass() {
        return thisClass;
    }

    public Package getPackage() {
        return thisClass.getPackage();
    }

    public String getPackageName() {
        return thisClass.getPackage().getName();
    }

    public String getShortName() {
        return thisClass.getName().substring(thisClass.getName().lastIndexOf(".") + 1);
    }

    public static String getShortName(Class c) {
        return c.getName().substring(c.getName().lastIndexOf(".") + 1);
    }

    public CachedClass(String className) throws ClassNotFoundException {
        this(Class.forName(className), false);
    }

    public CachedClass(Class clazz) {
        this(clazz, false);
    }

    public CachedClass(Class clazz, boolean useOnlyDeclared) {
        thisClass = clazz;
        methodMap = new TreeMap();
        getMethods = new TreeMap();
        setMethods = new TreeMap();

        reflectCache = ReflectCache.i();

        Method[] methods = (useOnlyDeclared) ? clazz.getDeclaredMethods() : clazz.getMethods();
        for (int i = 0; i < methods.length; ++i) {
        	
            String mName = methods[i].getName();
            if (mName.equals("getClass"))
                continue;
            if (mName.startsWith("get")) {
//            	if( methods[i].getParameterTypes().length > 0) {
//            		continue;
//            	}
                getMethods.put(Util.i().lowerCaseFirstLetter(mName.substring(3)), methods[i]);
            } else if (mName.startsWith("set")) {
//            	if( methods[i].getParameterTypes().length != 1) {
//            		continue;
//            	}
                setMethods.put(Util.i().lowerCaseFirstLetter(mName.substring(3)), methods[i]);
            } else if (mName.startsWith("is")) {
//            	if( methods[i].getParameterTypes().length > 0) {
//            		continue;
//            	}
                getMethods.put(Util.i().lowerCaseFirstLetter(mName.substring(2)), methods[i]);
            }

            methodMap.put(mName, methods[i]);
        }
    }

    public boolean propertyIsBean(String propertyName) {
        Class propertyType = getPropertyType(propertyName);
        if (propertyType != null) {

            // TODO obviously useless outside org.maxml
            if (propertyType.getName().startsWith("org.maxml")) {
                return true;
            }
        }
        return false;
    }

    public static boolean typeIsBean(Object typeObj) {
        if (typeObj != null) {
            return typeIsBean(typeObj.getClass());
        }
        return false;
    }

    public static boolean typeIsBean(Class type) {
        if (type != null) {
            // TODO obviously useless outside org.maxml
            if (type.getName().startsWith("org.maxml")) {
                return true;
            }
        }
        return false;
    }

    public Class getPropertyType(String propertyName) {
        Method method = getGetMethod(propertyName);
        return (method != null) ? method.getReturnType() : null;
    }

    public boolean propertyIsTransient(String propertyName) {
        try {
            Field field = thisClass.getDeclaredField(propertyName);
            return Modifier.isTransient(field.getModifiers());
        } catch (SecurityException e) {
            e.printStackTrace();
        } catch (NoSuchFieldException e) {
            e.printStackTrace();
        }
        return false;
    }

    private ReflectionProviderFactory getReflectionProviderFactory() {
        if (reflectionProviderFactory == null) {
            reflectionProviderFactory = new ReflectionProviderFactory();
        }
        return reflectionProviderFactory;
    }

    public HashMap getGetMethodsWithGivenReturnType(Set returnTypes) {
        return getMethodsWithGivenReturnType(returnTypes, getMethods);
    }

    public HashMap getMethodsWithGivenReturnType(Set returnTypes, TreeMap<String,Method> prop2methodMap) {
        HashMap methods = new HashMap();
        for ( String property: prop2methodMap.keySet() ) {
            Method method = (Method) prop2methodMap.get(property);
            if (returnTypes.contains(method.getReturnType())) {
                methods.put(property, method);
            }
        }
        return methods;
    }

    public Method getGetMethod(String propertyName) {
        return (Method) getMethods.get(propertyName);
    }

    public void merge(Object thisObject, Object intoThis, boolean overwrite) {

    }

    public Collection getProperties() {
        return this.getMethods.keySet();
    }

    public boolean isPrimitive() {
        return thisClass.isPrimitive();
    }

    public boolean isOfType(Class type) {
        return type.isAssignableFrom(thisClass);
    }

    public static List getPropertyList(Object target, String[] properties) {
        CachedClass cachedClass = ReflectCache.i().getClassCache(target.getClass());
        return cachedClass.getPropertyList(properties, target);
    }

    public List getPropertyList(String[] properties, Object target) {
        ArrayList propertyList = new ArrayList();
        for (int i = 0; i < properties.length; i++) {
            propertyList.add(invokeNestedGetMethod(target, properties[i]));
        }
        return propertyList;
    }

    public static Map makeMapWithPropertyKey(Collection elements, String propertyKey) {
        return makeMapWithPropertyKey(propertyKey, elements, ClassUtils.i().getCollectionElementClass(elements));
    }

    public static Map makeMapWithPropertyKey(String propertyKey, Collection elements, Class type) {
        CachedClass cachedClass = ReflectCache.i().getClassCache(type);
        return cachedClass.makeMapWithPropertyKey(propertyKey, elements);
    }

    public Map makeMapWithPropertyKey(String propertyKey, Collection elements) {
        return makeMapWithPropertyKey(propertyKey, elements, new HashMap());
    }

    public Map makeMapWithPropertyKey(String propertyKey, Collection elements, Map map) {

        for ( Object element: elements ) {
            Object property = invokeGetMethod(element, propertyKey);
            map.put(property, element);
        }
        return map;
    }

    public static void setPropertyOnAll(Object propertyValue, String property, Collection elements) {
        setPropertyOnAll(property, elements, propertyValue, ClassUtils.i().getCollectionElementClass(elements));
    }

    public static void setPropertyOnAll(String property, Collection elements, Object propertyValue, Class type) {
        CachedClass cachedClass = ReflectCache.i().getClassCache(type);
        cachedClass.setPropertyOnAll(property, elements, propertyValue);
    }

    public void setPropertyOnAll(String property, Collection elements, Object propertyValue) {

        for ( Object element: elements ) {
            invokeSetMethod(element, property, propertyValue);
        }
    }

    public Method getSetMethod(String propertyName) {
        return (Method) setMethods.get(propertyName);
    }

    public void invokeNestedSetMethod(Object rootTarget, String[] nestedProps, Object value) {
        CachedClass currentCachedClass = this;
        Object currTarget = rootTarget;
        for (int i = 0; i < nestedProps.length - 1; i++) {
            currTarget = currentCachedClass.getProperty(nestedProps[i], currTarget);
            currentCachedClass = ReflectCache.i().getClassCache(currTarget.getClass());
        }
        String finalProp = nestedProps[nestedProps.length - 1];
        currentCachedClass.setProperty(finalProp, currTarget, value);
    }

    public void invokeNestedSetMethod(Object rootTarget, String propSpec, Object value) {
        getReflectionProviderFactory().setNestedProperty(propSpec, rootTarget, true, value);
    }

    public Object invokeNestedGetMethod(Object rootTarget, String propSpec) {
        return invokeNestedGetMethod(rootTarget, propSpec, true, false);
    }

    public Object invokeNestedGetMethod(Object rootTarget, String propSpec, boolean doCreate) {
        return getReflectionProviderFactory().getNestedProperty(propSpec, rootTarget, doCreate);
    }
    
    public Object invokeNestedGetMethod(Object rootTarget, String propSpec, boolean doCreate, boolean getAccessInfo) {
        return getReflectionProviderFactory().getNestedProperty(propSpec, rootTarget, doCreate, getAccessInfo?new AccessInfo():null);
    }

    public Object invokeNestedGetMethod(Object rootTarget, String[] nestedProps) {
        CachedClass currentCachedClass = this;
        Object currTarget = rootTarget;
        for (int i = 0; i < nestedProps.length; i++) {
            currTarget = currentCachedClass.getProperty(nestedProps[i], currTarget);
            currentCachedClass = ReflectCache.i().getClassCache(currTarget.getClass());
        }
        return currTarget;
    }

    public void copyNestedProperty(Object rootTarget, String targetNestedProps, Object rootSource,
            String sourceNestedProps) {
        Object o = invokeNestedGetMethod(rootSource, sourceNestedProps);
        invokeNestedSetMethod(rootTarget, targetNestedProps, o);
    }

    public void copyNestedProperty(Object rootTarget, String[] targetNestedProps, Object rootSource,
            String[] sourceNestedProps) {
        CachedClass sourceCachedClass = ReflectCache.i().getClassCache(rootSource.getClass());
        Object sourceObj = sourceCachedClass.invokeNestedGetMethod(rootSource, sourceNestedProps);
        invokeNestedSetMethod(rootTarget, targetNestedProps, sourceObj);
    }

    public void removeNestedElement(Object rootSource, String sourceNestedProps) {
        getReflectionProviderFactory().removeNestedElement(sourceNestedProps, rootSource, true);
    }

    public void removeNestedCollectionElement(Object rootSource, String[] sourceNestedProps) {
        CachedClass sourceCachedClass = ReflectCache.i().getClassCache(rootSource.getClass());

        // check if this is an element of a collection
        PropertyName propertyName = new PropertyName(sourceNestedProps[sourceNestedProps.length - 1]);
        if (propertyName.isIndexed()) {
            // just get the collection
            sourceNestedProps[sourceNestedProps.length - 1] = propertyName.getPropertyName();
            Collection collection = (Collection) sourceCachedClass.invokeNestedGetMethod(rootSource, sourceNestedProps);
            if (collection.size() > propertyName.getIndex()) {
                if (collection instanceof List) {
                    ((List) collection).remove(propertyName.getIndex());
                } else {
                    Object elem = Util.i().getNthElement(collection, propertyName.getIndex());
                    if (elem != null) {
                        collection.remove(elem);
                    }
                }
            }
        } else if (propertyName.isParameterized()) {
            sourceNestedProps[sourceNestedProps.length - 1] = propertyName.getPropertyName();
            Map map = (Map) sourceCachedClass.invokeNestedGetMethod(rootSource, sourceNestedProps);
            map.remove(propertyName.getParameter());
        }
    }

    public Class getNestedPropertyType(Object rootTarget, String[] nestedProps) {
        CachedClass currentCachedClass = this;
        Object currTarget = rootTarget;
        for (int i = 0; i < nestedProps.length - 1; i++) {
            currTarget = currentCachedClass.getProperty(nestedProps[i], currTarget);
            currentCachedClass = ReflectCache.i().getClassCache(currTarget.getClass());
        }
        String finalProp = nestedProps[nestedProps.length - 1];

        PropertyName propertyName = new PropertyName(finalProp);
        if (propertyName.isIndexed()) {
            currTarget = ((Object[]) currTarget)[propertyName.getIndex()];
            return currTarget.getClass();
        } else if (propertyName.isParameterized()) {
            currTarget = ((Map) currTarget).get(propertyName.getParameter());
            return currTarget.getClass();
        }

        return currentCachedClass.getPropertyType(propertyName.getPropertyName());
    }

    private void setProperty(String propertyName, Object onThisObj, Object property) {
        setProperty(new PropertyName(propertyName), onThisObj, property);
    }

    private void setProperty(PropertyName propertyName, Object onThisObj, Object property) {

        if (propertyName.isIndexed()) {
            Object target = invokeGetMethod(onThisObj, propertyName.getPropertyName());

            if (target instanceof List) {
                List list = (List) target;
                int index = propertyName.getIndex();
                list.add(index, property);

            } else if (target instanceof Collection) {

                Collection collection = (Collection) target;
                collection.add(property);

            } else {
                ((Object[]) target)[propertyName.getIndex()] = property;
            }
        } else if (propertyName.isParameterized()) {
            Object target = invokeGetMethod(onThisObj, propertyName.getPropertyName());
            ((Map) target).put(propertyName.getParameter(), property);
        } else {
            invokeSetMethod(onThisObj, propertyName.getPropertyName(), property);
        }
    }

    private Object getProperty(String propertyName, Object fromThisObj) {
        return getProperty(new PropertyName(propertyName), fromThisObj);
    }

    private Object getProperty(PropertyName propertyName, Object fromThisObj) {
        Object target = invokeGetMethod(fromThisObj, propertyName.getPropertyName());

        if (target == null && (propertyName.isIndexed() || propertyName.isParameterized())) {
            // TODO hack alert! on this block
            // preserve bean types: set target, than re-get target
            target = (propertyName.isIndexed()) ? new ArrayList()
                    : new HashMap();
            invokeSetMethod(fromThisObj, propertyName.getPropertyName(), target);
            target = invokeGetMethod(fromThisObj, propertyName.getPropertyName());
        }

        if (propertyName.isIndexed()) {

            int index = propertyName.getIndex();

            if (target instanceof List) {

                List list = (List) target;

                if (index >= 0 && index < list.size()) {
                    target = list.get(propertyName.getIndex());
                } else if (index >= 0) {
                    for (int i = list.size(); i <= index; ++i) {
                        target = propertyName.createObjectOfType();
                        list.add(target);
                    }
                }
            } else if (target instanceof Collection) {

                Collection collection = (Collection) target;

                if (index >= 0 && index < collection.size()) {
                    target = Util.i().getNthElement(collection, index);
                } else if (index >= 0) {

                    for (int i = collection.size(); i <= index; ++i) {
                        target = propertyName.createObjectOfType();
                        collection.add(target);
                    }
                }

            } else {
                target = ((Object[]) target)[propertyName.getIndex()];
            }
        } else if (propertyName.isParameterized()) {
            target = ((Map) target).get(propertyName.getParameter());
        }
        return target;
    }

    public void invokeSetMethod(Object target, String propertyName, Object param) {

        if (param == null)
            return;

        Method m = (Method) setMethods.get(propertyName);
        if (m == null)
            return;

        Object[] params = new Object[1];
        params[0] = param;

        try {

            m.invoke(target, params);

        } catch (IllegalArgumentException e) {
            Method getMethod = (Method) getMethods.get(propertyName);
            params[0] = ClassUtils.i().createNewObjectOfType(getMethod.getReturnType(), param.toString());
            try {
                m.invoke(target, params);
            } catch (IllegalArgumentException e1) {
                e1.printStackTrace();
            } catch (IllegalAccessException e1) {
                e1.printStackTrace();
            } catch (InvocationTargetException e1) {
                e1.printStackTrace();
            }

        } catch (IllegalAccessException e) {
            e.printStackTrace();
        } catch (InvocationTargetException e) {
            e.printStackTrace();
        }
    }

    public Object invokeGetMethod(Object target, String propertyName) {
        Method m = (Method) getMethods.get(propertyName);
        if (m == null)
            return null;

        return  invokeMethod(target, m);
    }

    public Object invokeMethod(Object target, Method m) {
        try {
        	if(m.getParameterTypes().length>0){
        		// we don't want your stinking parameters! this is only bean a accessor invoker
        		return null;
        	}
            return m.invoke(target, null);
        } catch (Throwable e) {
            // TODO
            System.err.println("CachedClass.invokeMethod: \ntarget:" + target + " \nmethod: " + m.getName() 
                    + " \nerror: " + e.getMessage());
        }
        return null;
    }

    public Method getMethodWithName(String methodName) {
        Method[] methods = thisClass.getDeclaredMethods();
        for (int i = 0; i < methods.length; i++) {
            if (methods[i].getName().equals(methodName)) {
                return methods[i];
            }
        }
        return null;
    }

    public TreeMap getMethods() {
        return getMethods;
    }

    public TreeMap setMethods() {
        return setMethods;
    }

    public static Object getNestedPropOnObj(Object object, String spec) {
        
        return getNestedPropOnObj(object, spec, true);
    }

    public static Object getNestedPropOnObj(Object object, String spec, boolean doCreate) {
        CachedClass cachedClass = ReflectCache.i().getClassCache(object);
        return cachedClass.invokeNestedGetMethod(object, spec, doCreate);
    }

    public static AccessInfo getNestedPropOnObj(Object object, String spec, boolean doCreate, boolean getAccessInfo) {
        CachedClass cachedClass = ReflectCache.i().getClassCache(object);
        return (AccessInfo) cachedClass.invokeNestedGetMethod(object, spec, doCreate, getAccessInfo);
    }
    
    public static Object callFunc(Object target, String func) {
        CachedClass cachedClass = ReflectCache.i().getClassCache(target);
        try {
            Method m = cachedClass.getThisClass().getMethod(func, null);
            return cachedClass.invokeMethod(target, m);
        } catch (SecurityException e) {
            System.err.println("CachedClass.callFunc: \ntarget:" + target + " \nmethod: " + func
                    + " \nerror: " + e.getMessage());

        } catch (NoSuchMethodException e) {
            e.printStackTrace();
        }
        return null;
    }

    public static void setNestedPropOnObj(Object object, String spec, Object value) {
        CachedClass cachedClass = ReflectCache.i().getClassCache(object);
        cachedClass.invokeNestedSetMethod(object, spec, value);
    }

    public StringBuffer buildTokenAccessorCode(String prefix, String suffix) {
        return buildTokenAccessorCode(prefix, suffix, null);
    }

    public StringBuffer buildTokenAccessorCode(String prefix, String suffix, String[] exclList) {

        StringBuffer initBuf = new StringBuffer();
        StringBuffer funcBuf = new StringBuffer();

        Iterator iter = getMethods.keySet().iterator();
        while (iter.hasNext()) {
            String mName = (String) iter.next();

            if (exclList != null && Util.i().isInArray(mName, exclList)) {
                continue;
            }

            String varName = prefix + "_" + mName + suffix;
            initBuf.append("    private String " + varName + ";\n\n");

            // property getters and setters
            funcBuf.append("    public String get" + Util.i().upCaseFirstLetter(varName) + "() {\n");
            funcBuf.append("        return " + varName + ";\n");
            funcBuf.append("    }\n\n");

            funcBuf.append("    public void set" + Util.i().upCaseFirstLetter(varName) + "( String" + " value ) {\n");
            funcBuf.append("        this." + varName + " = value;\n");
            funcBuf.append("    }\n\n");

        }
        initBuf.append("\n\n");
        initBuf.append(funcBuf);
        return initBuf;
    }

    public static void printProperties(Object source) {
        printProperties(source, System.out);
    }

    public static void printProperties(Object source, PrintStream out) {
        CachedClass cachedClass = ReflectCache.i().getClassCache(source.getClass());
        // cachedClass.printProperties(source, "",
        // new HashSet(), new String[]
        // {"org.maxml"},new String[] {"CGLIB", "Hibernate"}, out);
        cachedClass.printProperties(source, "", new HashSet(), null, new String[] { "CGLIB",
                "Hibernate" }, out);
    }

    private static boolean pit = true;

    private void p(String s, PrintStream out) {
        if (pit)
            out.println(s);
    }

    private void p(PrintStream out) {
        if (pit)
            out.println();
    }

    public void printProperties(Object source, String indent, HashSet printPropCheckSet, String[] packagePrefixes,
            String[] exclusionStrs, PrintStream out) {

        String cname = source.getClass().getName();
        p(out);
        p(indent + cname + ":", out);

        Iterator iter = getMethods.keySet().iterator();
        while (iter.hasNext()) {
            String mName = (String) iter.next();

            Object o = invokeGetMethod(source, mName);

            if (o == null || hasClassNameContaining(o, exclusionStrs)) {
                continue;
            }

            if (!isFromSubPackage(o, packagePrefixes)) {
                p(indent + mName + ": " + o, out);
                continue;
            }

            // stop endless recursion of caused by dag relations
            if (printPropCheckSet.contains(o))
                continue;
            printPropCheckSet.add(o);

            if (o instanceof Collection) {
                Collection c = (Collection) o;
                Iterator iter2 = c.iterator();
                while (iter2.hasNext()) {
                    Object o2 = iter2.next();
                    CachedClass cc2 = reflectCache.getClassCache(o2.getClass());
                    if (cc2 != null) {
                        cc2.printProperties(o2, indent + "  ", printPropCheckSet, packagePrefixes, exclusionStrs, out);
                    } else {
                        p(indent + o2, out);
                    }
                }
            } else {
                CachedClass cc = reflectCache.getClassCache(o.getClass());
                if (cc != null) {
                    cc.printProperties(o, indent + "  ", printPropCheckSet, packagePrefixes, exclusionStrs, out);
                } else {
                    p(indent + o, out);
                }
            }
        }
        p(out);
    }

    public boolean hasProperty(String propertyName) {
        return getMethods.containsKey(propertyName);
    }

    public boolean propertyIsBasic(String propertyName) {
        Method m = (Method) getMethods.get(propertyName);
        if (m == null)
            return false;
        Class type = m.getReturnType();
        return type.isAssignableFrom(String.class) || type.isAssignableFrom(Integer.class)
                || type.isAssignableFrom(Date.class) || type.isAssignableFrom(Timestamp.class)
                || type.isAssignableFrom(Short.class) || type.isAssignableFrom(Long.class)
                || type.isAssignableFrom(Boolean.class) || type.isAssignableFrom(Float.class)
                || type.isAssignableFrom(Double.class) || type.isAssignableFrom(Character.class)
                || type.isAssignableFrom(StringBuffer.class) || type.isAssignableFrom(Byte.class);
    }

    public boolean propertyIsOfType(String propertyName, Class type) {
        Method m = (Method) getMethods.get(propertyName);
        if (m == null)
            return false;
        return type.isAssignableFrom(m.getReturnType());
    }

    public void testPopulate(Object target) {
        testPopulate(target, null);
    }

    public void testPopulate(Object target, String[] excludeMethods) {
        Iterator iter = setMethods.values().iterator();
        while (iter.hasNext()) {
            Method m = (Method) iter.next();
            if (excludeMethods != null && Util.i().isInArray(m.getName(), excludeMethods))
                continue;

            Class[] paramTypes = m.getParameterTypes();
            Object[] paramValues = new Object[paramTypes.length];

            for (int i = 0; i < paramTypes.length; ++i) {
                Object randObj = new Object();

                Class pc = paramTypes[i];

                CachedClass cc = reflectCache.getClassCache(pc);

                if (pc == String.class) {
                    paramValues[i] = m.getName() + "_strval" + randObj.hashCode();
                } else if (pc == Integer.class || pc == Integer.TYPE) {
                    paramValues[i] = new Integer(randObj.hashCode());
                } else if (pc == Long.class || pc == Long.TYPE) {
                    paramValues[i] = new Long(randObj.hashCode());
                } else if (pc == Boolean.class || pc == Boolean.TYPE) {
                    paramValues[i] = new Boolean(i % 2 == 0);
                } else if (pc == Character.class || pc == Character.TYPE) {
                    paramValues[i] = new Character((char) (randObj.hashCode() % 256));
                } else if (pc == Byte.class || pc == Byte.TYPE) {
                    paramValues[i] = new Byte((byte) (randObj.hashCode() % 256));
                } else if (pc == Short.class || pc == Short.TYPE) {
                    paramValues[i] = new Short((short) (randObj.hashCode() % Short.MAX_VALUE));
                } else if (pc == Date.class) {
                    paramValues[i] = new Date();
                } else if (pc == Timestamp.class) {
                    paramValues[i] = new Timestamp(i);
                } else if (pc == Double.class || pc == Double.TYPE) {
                    paramValues[i] = new Double(i);
                } else if (pc == Float.class || pc == Float.TYPE) {
                    paramValues[i] = new Float(i);

                } else if (cc != null) {
                    Object pobj = ClassUtils.i().createNewObjectOfType(pc);
                    cc.testPopulate(pobj);
                    paramValues[i] = pobj;
                }
            }

            try {
                m.invoke(target, paramValues);
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    public String toString() {
        return thisClass.getName();
    }

    public Document getHibernateMappingDocument() {
        Document hibDoc = ApplyXPath.i().getNewDoc("", "hibernate-mapping",
                "-//Hibernate/Hibernate Mapping DTD 3.0//EN",
                // "http://hibernate.sourceforge.net/hibernate-mapping-3.0.dtd",
                // // real uri
                "/home/jkirkley/3p/db/hibernate-3.1/hibernate-mapping-3.0.dtd");

        Element classElem = hibDoc.createElement("class");
        classElem.setAttribute("name", thisClass.getName());
        classElem.setAttribute("table", thisClass.getName().replace('.', '_').toLowerCase() + "_tbl");

        Element idElem = hibDoc.createElement("id");
        idElem.setAttribute("name", getShortName() + "Id");
        idElem.setAttribute("column", getShortName() + "Id");
        idElem.setAttribute("type", "java.lang.Integer");

        Element genElem = hibDoc.createElement("generator");
        genElem.setAttribute("class", "native");

        idElem.appendChild(genElem);
        classElem.appendChild(idElem);

        Iterator iter = getMethods.keySet().iterator();
        while (iter.hasNext()) {
            String mName = (String) iter.next();
            Class propertyType = getPropertyType(mName);
            if (propertyType == null) {
                System.out.println(mName + " not happenin");
                continue;
            }
            if (propertyIsOfType(mName, Set.class)) {
                Element setElem = hibDoc.createElement("set");
                setElem.setAttribute("name", mName);
                setElem.setAttribute("cascade", "all");
                Element keyElem = hibDoc.createElement("key");
                keyElem.setAttribute("column", mName + "Id");

                setElem.appendChild(keyElem);
                classElem.appendChild(setElem);

            } else if (propertyIsOfType(mName, List.class)) {

                Element setElem = hibDoc.createElement("list");
                setElem.setAttribute("name", mName);
                setElem.setAttribute("cascade", "all");
                Element keyElem = hibDoc.createElement("key");
                keyElem.setAttribute("column", mName + "Id");

                setElem.appendChild(keyElem);
                classElem.appendChild(setElem);

            } else if (propertyIsOfType(mName, Map.class)) {
                Element setElem = hibDoc.createElement("map");
                setElem.setAttribute("name", mName);
                setElem.setAttribute("cascade", "all");
                Element keyElem = hibDoc.createElement("key");
                keyElem.setAttribute("column", mName + "Id");

                setElem.appendChild(keyElem);
                classElem.appendChild(setElem);
            } else if (propertyIsOfType(mName, Collection.class)) {
                Element setElem = hibDoc.createElement("bag");
                setElem.setAttribute("name", mName);
                setElem.setAttribute("cascade", "all");
                Element keyElem = hibDoc.createElement("key");
                keyElem.setAttribute("column", mName + "Id");

                setElem.appendChild(keyElem);
                classElem.appendChild(setElem);

            } else if (propertyType.isArray()) {

                Element setElem = hibDoc.createElement("array");
                setElem.setAttribute("name", mName);
                setElem.setAttribute("cascade", "all");
                setElem.setAttribute("table", propertyType.getComponentType().getName().replace('.', '_').toLowerCase()
                        + "_tbl");
                Element keyElem = hibDoc.createElement("key");
                keyElem.setAttribute("column", mName + "Id");

                setElem.appendChild(keyElem);
                classElem.appendChild(setElem);

            } else {
                Element propElem = hibDoc.createElement("property");
                propElem.setAttribute("name", mName);
                propElem.setAttribute("column", mName);
                propElem.setAttribute("type", propertyType.getName());
                classElem.appendChild(propElem);
            }
        }
        hibDoc.getDocumentElement().appendChild(classElem);
        return hibDoc;
    }

    public List getNonStaticFields() {
        Field[] fields = thisClass.getDeclaredFields();
        ArrayList al = new ArrayList();
        for (int i = 0; i < fields.length; ++i) {
            if (!Modifier.isStatic(fields[i].getModifiers())) {
                al.add(fields[i]);
            }
        }
        return al;
    }

    public void copyObject(Object target, Object source) {
        Iterator iter = getMethods.keySet().iterator();
        while (iter.hasNext()) {
            String mName = (String) iter.next();
            Object o = invokeGetMethod(source, mName);
            if (o == null)
                continue;
            invokeSetMethod(target, mName, o);
        }
    }

    public void saveObject(Object source, Session session) {

        Iterator iter = getMethods.keySet().iterator();
        while (iter.hasNext()) {
            String mName = (String) iter.next();

            Object o = invokeGetMethod(source, mName);
            if (o == null)
                continue;

            if (o instanceof Collection) {
                Collection c = (Collection) o;
                Iterator iter2 = c.iterator();
                while (iter2.hasNext()) {
                    Object o2 = iter2.next();

                    CachedClass cc2 = reflectCache.getClassCache(o2);

                    if (cc2 != null && HibUtils.isPersistentClass(o2)) {
                        cc2.saveObject(o2, session);
                    }
                }

            } else if (HibUtils.isPersistentClass(o)) {

                CachedClass cc = reflectCache.getClassCache(o);
                if (cc != null) {
                    cc.saveObject(o, session);
                }
            }
        }
        session.save(source);
    }

    public String getGetMethodName(String propertyName) {
        Method m = null;
        try {
            m = thisClass.getMethod("get" + Util.i().upCaseFirstLetter(propertyName), null);
        } catch (SecurityException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        } catch (NoSuchMethodException e) {
            try {
                m = thisClass.getMethod("is" + Util.i().upCaseFirstLetter(propertyName), null);
            } catch (SecurityException e1) {
                // TODO Auto-generated catch block
                e1.printStackTrace();
            } catch (NoSuchMethodException e1) {
                return null;
            }
        }
        return m.getName();
    }

    public Element appendXMLRep(Element parentElement) {
        Element classElement = parentElement.getOwnerDocument().createElement(getShortName());
        parentElement.appendChild(classElement);
        Iterator iter = getMethods.keySet().iterator();
        while (iter.hasNext()) {
            String mName = (String) iter.next();

            Element propertyElement = parentElement.getOwnerDocument().createElement(mName);
            classElement.appendChild(propertyElement);
        }
        return classElement;
    }

    public Node getXMLRep(Document doc, Object target) {
        return getXMLRep(doc, target, (ReflectionNavigationFilter)null);
    }

    public static Document getXMLRepresentationDocument(Object target) {
		Document document = ApplyXPath.i().getNewDoc();
        CachedClass cachedClass = ReflectCache.i().getClassCache(target.getClass());
        document.appendChild(cachedClass.getXMLRep(document, target));
        return document; 
    }
    
    public static Node getXMLRepresentation(Document doc, Object target) {
        CachedClass cachedClass = ReflectCache.i().getClassCache(target.getClass());
        return cachedClass.getXMLRep(doc, target);
    }
    
    public static Node getXMLRepresentation(Document doc, Object target, ReflectionNavigationFilter reflectionNavigationFilter) {
        CachedClass cachedClass = ReflectCache.i().getClassCache(target.getClass());
        return cachedClass.getXMLRep(doc, target, reflectionNavigationFilter);
    }
    
    public Node getXMLRep(Document doc, Object target, ReflectionNavigationFilter reflectionNavigationFilter) {
        return getXMLRep(doc, getShortName(), target, new HashSet(), reflectionNavigationFilter);
    }
    
    public Node getXMLRep(Document doc, String tagName, Object target, Set objectsSeen, ReflectionNavigationFilter reflectionNavigationFilter) {
        
        if(objectsSeen.contains(target)) return null;
        
        if(reflectionNavigationFilter!=null) {
            reflectionNavigationFilter.setOwnerType(thisClass);
        }
        
        objectsSeen.add(target);
        Element repElement = doc.createElement(tagName);
        Iterator iter = getMethods.keySet().iterator();
        while (iter.hasNext()) {
            String mName = (String) iter.next();
            

            Object value = invokeGetMethod(target, mName);
            
            if(value !=null ) {
                try {
                    Node subNode = getXMLRep(value, mName, value.getClass(), doc, objectsSeen, reflectionNavigationFilter);
                    if( subNode != null){
                        repElement.appendChild(subNode);
                    }
                } catch (Throwable e) {
                    e.printStackTrace();
                }
            }
        }
        return repElement;
    }
    
    public Node getXMLRep(Object target, String tagName, Class pc, Document doc, Set objectsSeen, ReflectionNavigationFilter reflectionNavigationFilter) {
    	
    	if( reflectionNavigationFilter != null && !reflectionNavigationFilter.include(pc, "get"+Util.i().upCaseFirstLetter(tagName))) {
    		return null;
    	}
        Element repElement = doc.createElement(tagName);

        if (pc == String.class) {
            repElement.appendChild(doc.createTextNode(ApplyXPath.i().normalizeText(target.toString())));
        } else if (pc == Integer.class || pc == Integer.TYPE) {
            repElement.appendChild(doc.createTextNode(target.toString()));
        } else if (pc == Long.class || pc == Long.TYPE) {
            repElement.appendChild(doc.createTextNode(target.toString()));
        } else if (pc == Boolean.class || pc == Boolean.TYPE) {
            repElement.appendChild(doc.createTextNode(target.toString()));
        } else if (pc == Character.class || pc == Character.TYPE) {
            repElement.appendChild(doc.createTextNode(target.toString()));
        } else if (pc == Byte.class || pc == Byte.TYPE) {
            repElement.appendChild(doc.createTextNode(target.toString()));
        } else if (pc == Short.class || pc == Short.TYPE) {
            repElement.appendChild(doc.createTextNode(target.toString()));
        } else if (pc == Date.class) {
            repElement.appendChild(doc.createTextNode(target.toString()));
        } else if (pc == Timestamp.class) {
            repElement.appendChild(doc.createTextNode(target.toString()));
        } else if (pc == Double.class || pc == Double.TYPE) {
            repElement.appendChild(doc.createTextNode(target.toString()));
        } else if (pc == Float.class || pc == Float.TYPE) {
            repElement.appendChild(doc.createTextNode(target.toString()));
        } else if (target instanceof Map) {

            Map map = (Map) target;
            
            for (Iterator iter = map.keySet().iterator(); iter.hasNext();) {
                Object key = iter.next();
                Object val = map.get(key);
                if( val == null) continue;
                if (val instanceof Collection) {
					Collection collection = (Collection) val;
					if(collection.size()==0) continue;
				}
                Element valueElement = (Element)getXMLRep(val, "value", val.getClass(), doc, objectsSeen, reflectionNavigationFilter);
                if( valueElement != null){
                    valueElement.setAttribute("key", key.toString());
                    repElement.appendChild(valueElement);
                }
            }

        } else if (target instanceof Collection) {

            Collection collection = (Collection) target;
            for (Iterator iter = collection.iterator(); iter.hasNext();) {
                Object val = iter.next();
                Node subNode = getXMLRep(val, "value", val.getClass(), doc, objectsSeen, reflectionNavigationFilter);
                if(subNode!=null)repElement.appendChild(subNode);
            }

        } else {
            return ReflectCache.i().getClassCache(pc).getXMLRep(doc, tagName, target, objectsSeen, reflectionNavigationFilter);
        }
        return repElement;
        
    }

    public static boolean isFromSubPackage(Object o, String[] packagePrefixes) {
        if (packagePrefixes == null)
            return false;

        String fullClassName = o.getClass().getName();
        return Util.i().matchesPrefix(fullClassName, packagePrefixes);
    }

    public static boolean hasClassNameContaining(Object o, String[] parts) {
        if (parts == null)
            return true;

        String fullClassName = o.getClass().getName();
        return Util.i().matchesPartOf(fullClassName, parts);
    }

    public static void deepRead(Object property) {
        
        if(property == null) return;
        
        if (property instanceof Collection) {
            Collection collection = (Collection) property;
            for (Iterator iter = collection.iterator(); iter.hasNext();) {
                deepRead(iter.next(), new HashSet());
            }

        } else {
            deepRead(property, new HashSet());
        }
    }

    public static void deepRead(Object sourceObj, Set seenAlready) {
        CachedClass cachedClass = ReflectCache.i().getClassCache(sourceObj.getClass());
        Map<String,Method> methods = cachedClass.getMethods();

        for ( String propName: methods.keySet() ) {

            if (!Util.i().hasContent(propName))
                continue;

            Object value = cachedClass.invokeGetMethod(sourceObj, propName);

            if (value != null && !seenAlready.contains(value)) {
                seenAlready.add(value);
                if (value instanceof Collection) {
                    Collection collection = (Collection) value;
                    for (Iterator iter2 = collection.iterator(); iter2.hasNext();) {
                        deepRead(iter2.next(), seenAlready);
                    }
                }

                if (CachedClass.typeIsBean(value) && cachedClass.getPropertyType(propName) != Link.class) {
                    deepRead(value, seenAlready);
                }

            }
        }
    }

    
    public static void copyInPropertysFromMap(Map propValueMap, Object target){
        CachedClass cachedClass = ReflectCache.i().getClassCache(target.getClass());
        Collection<String> properties = cachedClass.getProperties();

        for ( String property: properties ) {
            
            cachedClass.copyNestedProperty(target, property, propValueMap,property);
        }        
    }
}
