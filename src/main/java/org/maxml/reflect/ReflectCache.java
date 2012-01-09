package org.maxml.reflect;


//import org.maxml.gui.Util;
import org.maxml.util.ClassUtils;
import org.maxml.util.FileUtils;
import org.maxml.util.JavaClassFileBean;
import org.maxml.util.Util;

import java.lang.Class;
import java.lang.reflect.Constructor;
import java.lang.reflect.Method;
import java.lang.reflect.Modifier;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Set;
import java.util.TreeSet;
import java.util.jar.JarEntry;
import java.util.jar.JarInputStream;
import java.io.FileInputStream;
import java.io.File;
import java.io.FileOutputStream;

import java.io.IOException;

import org.hibernate.Session;

public class ReflectCache {

    HashMap classMap;            // <Class,
    // CachedClass>classMap;
    HashMap onlyDeclaredClassMap; // <Class,

    // CachedClass>classMap;

    public ReflectCache() {
        classMap = new HashMap();
        this.onlyDeclaredClassMap = new HashMap();
    }

    private static ReflectCache instance = null;

    public static ReflectCache i() {
        if (instance == null) {
            instance = new ReflectCache();
        }
        return instance;
    }

    public void addJar(String jarName) throws IOException, ClassNotFoundException {
        FileInputStream fis = new FileInputStream(new File(jarName));
        JarInputStream jis = new JarInputStream(fis);
        JarEntry entry;
        while ((entry = jis.getNextJarEntry()) != null) {
            if (!entry.isDirectory()) {

                String name = entry.getName();
                if (name.endsWith(".class")) {
                    name = name.substring(0, name.lastIndexOf('.'));
                    name = name.replace('/', '.');
                    // System.out.println( "name: " + name );
                    addClass(name);
                }
                // System.out.println( "entrynm: " + entry.getName() );
                // System.out.println( "entry: " + name );
            }
        }
        jis.close();
    }

    public CachedClass addClass(String className) throws ClassNotFoundException {
        return addClass(Class.forName(className));
    }

    public void addAllClassesInPackage(String packageName, String rootDir) throws IOException, ClassNotFoundException {

        String fs = System.getProperty("file.separator");
        if (!rootDir.endsWith(fs))
            rootDir += fs;
        String fullPath = rootDir + packageName.replace('.', fs.charAt(0));

        File packageDirFile = new File(fullPath);

        if (packageDirFile.isDirectory()) {
            String[] fileNames = packageDirFile.list();
            for (int i = 0; i < fileNames.length; ++i) {
                String fname = fileNames[i];
                if (fname.endsWith(".class")) {
                    addClass(packageName + "." + fname.substring(0, fname.lastIndexOf(".")));
                }
            }
        }
    }

    public CachedClass addClass(Class c) {
        CachedClass cachedClass = new CachedClass(c);
        classMap.put(c, cachedClass);
        return cachedClass;
    }

    public CachedClass addClassOnlyDeclared(Class c) {
        CachedClass cachedClass = new CachedClass(c, true);
        this.onlyDeclaredClassMap.put(c, cachedClass);
        return cachedClass;
    }

    public CachedClass getClassCache(String className) throws ClassNotFoundException {
        return getClassCache(Class.forName(className));
    }

    public CachedClass getClassCache(Object obj) {
        return getClassCache(obj.getClass());
    }


    public static final String[] instrumentedClassNames= {"EnhancerByCGLIB"};
    
    public Class normalizeClass(Class c) {
        if( Util.i().matchesPartOf(c.getName(), instrumentedClassNames)) {
            return c.getSuperclass();
        }
    	return c;
    }
    
    public CachedClass getClassCache(Class c) {
    	c = normalizeClass(c);
        CachedClass cachedClass = (CachedClass) classMap.get(c);
        if (cachedClass == null) {
            cachedClass = addClass(c);
        }
        return cachedClass;
    }

    public CachedClass getClassCacheOnlyDeclared(String className) throws ClassNotFoundException {
        return getClassCacheOnlyDeclared(Class.forName(className));
    }

    public CachedClass getClassCacheOnlyDeclared(Object obj) {
        return getClassCacheOnlyDeclared(obj.getClass());
    }

    public CachedClass getClassCacheOnlyDeclared(Class c) {
    	c = normalizeClass(c);
        CachedClass cachedClass = (CachedClass) this.onlyDeclaredClassMap.get(c);
        if (cachedClass == null) {
            cachedClass = addClassOnlyDeclared(c);
        }
        return cachedClass;
    }

    public List getClassesOfType(Class type) {
        ArrayList al = new ArrayList();
        Iterator i = classMap.values().iterator();
        while (i.hasNext()) {
            CachedClass cc = (CachedClass) i.next();
            if (type.isAssignableFrom(cc.getThisClass())) {
                al.add(cc);
            }
        }
        return al;
    }

    public List getAllClasses() {
        ArrayList al = new ArrayList();
        Iterator i = classMap.values().iterator();
        while (i.hasNext()) {
            CachedClass cc = (CachedClass) i.next();
            al.add(cc);
        }
        return al;
    }

    public void printProperties(Object source) {
        CachedClass.printProperties(source);
    }

    public void saveObject(Object source, Session session) {
        CachedClass cc = (CachedClass) classMap.get(source.getClass());
        if (cc != null)
            cc.saveObject(source, session);
    }

    public static void addPkgToList(Class c, Set l) {
        if (c.isPrimitive())
            return;
        String cname = c.getName();
        String pname = c.getPackage().getName();
        if (pname.equals("java.lang"))
            return;
        l.add(cname);
    }

    public static void showJarEntries(String jarName, String prefix) throws Exception, ClassNotFoundException {

    	Collection collection = FileUtils.i().getAllJarEntries(jarName, prefix);
    	collection = Util.i().appendPrefixToElements(collection, "jar/");
    	//SimpleNode simpleNode = new SimpleNode("jar");
    	//simpleNode.addChild( simpleNode.buildFromDelimitedPaths(collection,"","jar","/")); 
    	//simpleNode.printOut(System.out,"");
    }
    
    public static void catapultIt(String jarName, String toDir) throws IOException, ClassNotFoundException {

        FileInputStream fis = new FileInputStream(new File(jarName));
        JarInputStream jis = new JarInputStream(fis);
        JarEntry entry;
        HashMap hashMap = new HashMap();
        while ((entry = jis.getNextJarEntry()) != null) {
            if (!entry.isDirectory()) {

                String name = entry.getName();
                if (name.endsWith(".class") && name.indexOf("/form/") != -1 || name.endsWith(".class")
                        && name.indexOf("/action/") != -1) {

                    System.out.println(name);
                    name = name.substring(0, name.lastIndexOf('.'));
                    String fname = toDir + name + ".java";
                    name = name.replace('/', '.');
                    File f = new File(fname);
                    f.getParentFile().mkdirs();
                    try {
                        Class handlerClass = Class.forName(name);
                        JavaClassFileBean javaClassFileBean = new JavaClassFileBean(f);
                        CodeGenerator cg = new CodeGenerator(false);
                        // cg.generate(handlerClass,
                        // handlerClass.getPackage().getName(),
                        // handlerClass.getName(), System.out);
                        cg.generate(handlerClass, handlerClass.getPackage().getName(),
                                javaClassFileBean.getClassName(), new FileOutputStream(f));
                        // Method [] methods =
                        // handlerClass.getDeclaredMethods();
                        // for (int i = 0; i < methods.length; i++) {
                        // System.out.println( " " + methods[i]);
                        // Class [] ptypes = methods[i].getParameterTypes();
                        // addPkgToList(methods[i].getReturnType(),
                        // packageNameSet);
                        // String paramStr = "";
                        // for (int j = 0; j < ptypes.length; j++) {
                        // //System.out.println( " " + ptypes[j].getName());
                        // addPkgToList(ptypes[j], packageNameSet);
                        // //paramStr += CachedClass.getShortName(ptypes[j]) + "
                        // }
                        // String methodSig = getModifierPrefix(methods[i]) +
                        // }
                        System.out.println("\n\n\n");
                    } catch (Throwable e) {
                        // TODO Auto-generated catch block
                        hashMap.put(name, e);
                    }
                }
            }
        }
        Util.i().printMap(hashMap);
    }

    public static String getModifierPrefix(Method method) {
        String prefix = "";
        int modifiers = method.getModifiers();
        if (Modifier.isPrivate(modifiers)) {
            prefix = "private " + prefix;
        }
        if (Modifier.isProtected(modifiers)) {
            prefix = "protected " + prefix;
        }
        if (Modifier.isPublic(modifiers)) {
            prefix = "public " + prefix;
        }
        if (Modifier.isStatic(modifiers)) {
            prefix = "static " + prefix;
        }
        if (Modifier.isFinal(modifiers)) {
            prefix = "final " + prefix;
        }
        if (Modifier.isAbstract(modifiers)) {
            prefix = "abstract " + prefix;
        }
        if (Modifier.isNative(modifiers)) {
            prefix = "native " + prefix;
        }
        if (Modifier.isSynchronized(modifiers)) {
            prefix = "synchronized " + prefix;
        }
        if (Modifier.isTransient(modifiers)) {
            prefix = "transient " + prefix;
        }
        if (Modifier.isVolatile(modifiers)) {
            prefix = "volatile " + prefix;
        }
        return prefix;
    }

    public static void genClassFactories(String baseDir, String[] pkgs) throws IOException, ClassNotFoundException {

        TreeSet packageNameSet = new TreeSet();
        StringBuffer initBuf = new StringBuffer();
        StringBuffer funcBuf = new StringBuffer();
        StringBuffer pkgBuf = new StringBuffer();

        for (int i = 0; i < pkgs.length; i++) {

            String pkg = pkgs[i];
            String pkgDir = baseDir + File.separator + pkg.replace('.', File.separatorChar);
            File pkgDirFile = new File(pkgDir);

            pkgBuf.append("package " + pkg + ";\n\n");
            pkgBuf.append("import org.maxml.instrumentors.ClassInstrumentor;\n");
            initBuf.append("\npublic class IB extends ClassInstrumentor {\n\n");

            File[] pkgFiles = pkgDirFile.listFiles();
            for (int j = 0; j < pkgFiles.length; j++) {
                if (pkgFiles[j].getAbsolutePath().endsWith(".java")) {

                    JavaClassFileBean javaClassFileBean = new JavaClassFileBean(pkgFiles[j]);
                    genNewFunc(javaClassFileBean.getFullClassName(), funcBuf, pkgBuf, packageNameSet, pkg);
                }
            }
            funcBuf.append("}\n");

            FileUtils.i().writeContents(pkgDir + File.separator + "IB.java",
                    pkgBuf.toString() + initBuf.toString() + funcBuf.toString());

            funcBuf.setLength(0);
            pkgBuf.setLength(0);
            initBuf.setLength(0);
        }

        System.out.println(pkgBuf.toString() + initBuf.toString() + funcBuf.toString());
    }

    public static void genClassFactories(String jarFilePath, String toDir, String pkg, String[] pkgPrefixes)
            throws IOException, ClassNotFoundException {

        TreeSet packageNameSet = new TreeSet();

        StringBuffer initBuf = new StringBuffer();
        StringBuffer funcBuf = new StringBuffer();
        StringBuffer pkgBuf = new StringBuffer();

        pkgBuf.append("package " + pkg + ";\n\n");
        pkgBuf.append("import org.maxml.reflect.ClassInstrumentor;\n");
        initBuf.append("\npublic class " + pkgPrefixes[0].replace('/', '_') + "IB extends ClassInstrumentor {\n\n");

        FileInputStream fis = new FileInputStream(new File(jarFilePath));
        JarInputStream jis = new JarInputStream(fis);
        JarEntry entry;

        while ((entry = jis.getNextJarEntry()) != null) {
            if (!entry.isDirectory()) {
                String name = entry.getName();
                if (name.endsWith(".class") && Util.i().matchesPrefix(name, pkgPrefixes) && name.indexOf("$") == -1) {

                    System.out.println(name);
                    name = name.substring(0, name.lastIndexOf('.'));
                    name = name.replace('/', '.');
                    genNewFunc(name, funcBuf, pkgBuf, packageNameSet, pkg);

                }
            }
        }
        funcBuf.append("}\n");
        FileUtils.i().writeContents(toDir + File.separator + pkgPrefixes[0].replace('/', '_') + "IB.java",
                pkgBuf.toString() + initBuf.toString() + funcBuf.toString());

    }

    private static void genNewFunc(String fullClassName, StringBuffer funcBuf, StringBuffer pkgBuf, Set packageNameSet,
            String pkg) {
        try {
            Class handlerClass = Class.forName(fullClassName);

            if (handlerClass.isInterface() || !Modifier.isPublic(handlerClass.getModifiers())
                    || Modifier.isAbstract(handlerClass.getModifiers())) {
                return;
            }
            Constructor[] constructors = handlerClass.getConstructors();

            if (!packageNameSet.contains(fullClassName)
                    && !(fullClassName.startsWith(pkg) && fullClassName.substring(pkg.length() + 1).indexOf('.') != -1)) {
                packageNameSet.add(fullClassName);
                pkgBuf.append("import " + fullClassName + ";\n");
            }

            for (int k = 0; k < constructors.length; k++) {

                Constructor constructor = constructors[k];
                String simpleClassName = handlerClass.getSimpleName();
                String throwsClause = "";
                String paramClause = "";
                String paramCallClause = "";
                String paramArrClause = "";
                String paramClassArrClause = "";
                String comma = "";
                Class[] paramTypes = constructor.getParameterTypes();
                Class[] exceptionTypes = constructor.getExceptionTypes();

                for (int index = 0; index < exceptionTypes.length; index++) {
                    String fullName = exceptionTypes[index].getName();
                    if (!packageNameSet.contains(fullName)) {
                        packageNameSet.add(fullName);
                        pkgBuf.append("import " + fullName + ";\n");
                    }
                    throwsClause += comma + exceptionTypes[index].getSimpleName();
                    comma = ",";
                }
                comma = "";
                for (int index = 0; index < paramTypes.length; index++) {
                    String fullName = paramTypes[index].getName();
                    if (!packageNameSet.contains(fullName) && !paramTypes[index].isPrimitive()
                            && !fullName.startsWith("[")) {
                        packageNameSet.add(fullName);
                        pkgBuf.append("import " + fullName + ";\n");
                    }
                    paramClause += comma + paramTypes[index].getSimpleName() + " arg" + index;
                    paramCallClause += comma + " arg" + index;
                    if (index == 0) {
                        paramArrClause = "Object[] params = {";
                        paramClassArrClause = "Class[] paramTypes = {";
                    }
                    paramArrClause += comma + "arg" + index;
                    paramClassArrClause += comma + ClassUtils.i().getClassRefStrRep(paramTypes[index]);
                    if (index == paramTypes.length - 1) {
                        paramArrClause += "};\n";
                        paramClassArrClause += "};\n";
                    }
                    comma = ",";
                }
                funcBuf.append("    public static " + simpleClassName + " new" + simpleClassName + "(");
                funcBuf.append(paramClause);
                funcBuf.append(")");
                if (exceptionTypes.length > 0) {
                    funcBuf.append(" throws " + throwsClause);
                }
                funcBuf.append("{\n");
                funcBuf.append("        if(instrumentationEnabled && needsInstrumentation(" + simpleClassName
                        + ".class)) {\n");
                if (paramTypes.length > 0) {
                    funcBuf.append("            " + paramArrClause);
                    funcBuf.append("            " + paramClassArrClause);
                    funcBuf.append("            return (" + simpleClassName + ") instrumentInstance(" + simpleClassName
                            + ".class , params, paramTypes);\n");
                } else {
                    funcBuf.append("            return (" + simpleClassName + ") instrumentInstance(" + simpleClassName
                            + ".class);\n");
                }
                funcBuf.append("        } else {\n");
                funcBuf.append("            return new " + simpleClassName + "(" + paramCallClause + ");\n");
                funcBuf.append("        }\n");
                funcBuf.append("    }\n\n");
            }

        } catch (Throwable e) {

        }

    }

    public static void main(String[] a) {

        try {

            if (false) {
                ReflectCache.genClassFactories("/home/jkirkley/3p/java/jdk1.6.0/jre/lib/rt.jar",
                        "/home/jkirkley/jrp/setfer/src/org.maxml", "org.maxml", new String[] { "java/awt" });

                ReflectCache.genClassFactories("/home/jkirkley/3p/java/jdk1.6.0/jre/lib/rt.jar",
                        "/home/jkirkley/jrp/setfer/src/org.maxml", "org.maxml", new String[] { "java/io" });

                ReflectCache.genClassFactories("/home/jkirkley/3p/java/jdk1.6.0/jre/lib/rt.jar",
                        "/home/jkirkley/jrp/setfer/src/org.maxml", "org.maxml", new String[] { "java/lang" });

                ReflectCache.genClassFactories("/home/jkirkley/3p/java/jdk1.6.0/jre/lib/rt.jar",
                        "/home/jkirkley/jrp/setfer/src/org.maxml", "org.maxml", new String[] { "java/net" });

                ReflectCache.genClassFactories("/home/jkirkley/3p/java/jdk1.6.0/jre/lib/rt.jar",
                        "/home/jkirkley/jrp/setfer/src/org.maxml", "org.maxml", new String[] { "java/nio/channels" });

                ReflectCache.genClassFactories("/home/jkirkley/3p/java/jdk1.6.0/jre/lib/rt.jar",
                        "/home/jkirkley/jrp/setfer/src/org.maxml", "org.maxml", new String[] { "java/rmi/server" });

                ReflectCache.genClassFactories("/home/jkirkley/3p/java/jdk1.6.0/jre/lib/rt.jar",
                        "/home/jkirkley/jrp/setfer/src/org.maxml", "org.maxml", new String[] { "java/security" });

                ReflectCache.genClassFactories("/home/jkirkley/3p/java/jdk1.6.0/jre/lib/rt.jar",
                        "/home/jkirkley/jrp/setfer/src/org.maxml", "org.maxml", new String[] { "java/sql" });

                ReflectCache.genClassFactories("/home/jkirkley/3p/java/jdk1.6.0/jre/lib/rt.jar",
                        "/home/jkirkley/jrp/setfer/src/org.maxml", "org.maxml", new String[] { "java/util" });

                ReflectCache.genClassFactories("/home/jkirkley/3p/java/jdk1.6.0/jre/lib/rt.jar",
                        "/home/jkirkley/jrp/setfer/src/org.maxml", "org.maxml", new String[] { "javax/servlet" });

                ReflectCache.genClassFactories("/home/jkirkley/3p/java/jdk1.6.0/jre/lib/rt.jar",
                        "/home/jkirkley/jrp/setfer/src/org.maxml", "org.maxml", new String[] { "javax/swing" });

                ReflectCache.genClassFactories("/home/jkirkley/3p/java/jdk1.6.0/jre/lib/rt.jar",
                        "/home/jkirkley/jrp/setfer/src/org.maxml", "org.maxml", new String[] { "javax/xml" });
            }

            if (false) {
                ReflectCache.genClassFactories("/home/jkirkley/jrp/setfer/lib/axis.jar",
                        "/home/jkirkley/jrp/setfer/src/org.maxml", "org.maxml", new String[] { "org/apache/axis" });

                ReflectCache.genClassFactories("/home/jkirkley/jrp/setfer/lib/xerces-2.6.2.jar",
                        "/home/jkirkley/jrp/setfer/src/org.maxml", "org.maxml",
                        new String[] { "org/apache/xerces/parsers" });

                ReflectCache.genClassFactories("/home/jkirkley/jrp/setfer/lib/hibernate3.jar",
                        "/home/jkirkley/jrp/setfer/src/org.maxml", "org.maxml", new String[] { "org/hibernate" });

                ReflectCache.genClassFactories("/home/jkirkley/jrp/goodoldlib/xalan.jar",
                        "/home/jkirkley/jrp/setfer/src/org.maxml", "org.maxml", new String[] { "org/apache/xalan" });

                ReflectCache.genClassFactories("/home/jkirkley/jrp/setfer/oldlib/struts.jar",
                        "/home/jkirkley/jrp/setfer/src/org.maxml", "org.maxml", new String[] { "org/apache/struts" });

                // ReflectCache.genClassFactories("",
                // "/home/jkirkley/jrp/setfer/src/org.maxml", "org.maxml", new
                // String[] { "" });
            }

//            if (false) {
//                ReflectCache.genClassFactories(Util.RD, new String[] { "org.maxml.reflect",
//                        "org.maxml.reflect.transfer", "org.maxml.config", "org.maxml.web", "org.maxml.web.components",
//                        "org.maxml.web.transfer", "org.maxml.web.struts", "org.maxml.web.struts.forms",
//                        "org.maxml.web.struts.actions", "org.maxml.web.struts.util", "org.maxml.xpath", "org.maxml.iw",
//                        "org.maxml.common", "org.maxml.hibernate", "org.maxml.hibernate.gui", "org.maxml.links",
//                        "org.maxml.app", "org.maxml.app.fei", "org.maxml.app.fei.obj",
//                        "org.maxml.app.fei.obj.schedule", "org.maxml.app.fei.obj.tasks",
//                        "org.maxml.app.fei.obj.components", "org.maxml.app.fei.obj.dmd",
//                        "org.maxml.app.fei.obj.connectivity", "org.maxml.app.fei.obj.profiles",
//                        "org.maxml.app.fei.obj.metadata", "org.maxml.app.fei.web", "org.maxml.app.fei.web.dmd",
//                        "org.maxml.util", "org.maxml.rules", "org.maxml.rules.simpleDecisionTree",
//                        "org.maxml.rules.impl", "org.maxml.rules.core", "org.maxml.rules.gui", "org.maxml.gui",
//                        "org.maxml.gui.laf", "org.maxml.gui.layout", "org.maxml.gui.templates", "org.maxml.gui.dtd",
//                        "org.maxml.gui.nav", "org.maxml.gui.font", "org.maxml.gui.builders",
//                        "org.maxml.gui.builders.containers", "org.maxml.gui.builders.components",
//                        "org.maxml.gui.builders.ext", "org.maxml.gui.builders.menu", "org.maxml.gui.builders.model",
//                        "org.maxml.xml", "org.maxml.xml.mapping", "org.maxml.dispatch", "org.maxml.propertymappers",
//                        "org.maxml.db", "org.maxml.db.types", "org.maxml.db.types.profiles",
//                        "org.maxml.db.types.links", "org.maxml.db.types.templates", "org.maxml.db.gui",
//                        "org.maxml.db.query" });
//            }
//
//            if (false) {
//                ReflectCache.genClassFactories(Util.RD, new String[] { "org.maxml.gui", "org.maxml.gui.laf",
//                        "org.maxml.gui.layout", "org.maxml.gui.templates", "org.maxml.gui.dtd", "org.maxml.gui.nav",
//                        "org.maxml.gui.font", "org.maxml.gui.builders", "org.maxml.gui.builders.containers",
//                        "org.maxml.gui.builders.components", "org.maxml.gui.builders.ext",
//                        "org.maxml.gui.builders.menu", "org.maxml.gui.builders.model" });
//            }
//
//            if (false) {
//                ReflectCache.genClassFactories(Util.RD, new String[] { "org.maxml.db.types.links" });
//
//            }

            if (false) {
                ReflectCache.genClassFactories("/home/jkirkley/jrp/setfer/oldlib/dtdparser121.jar",
                        "/home/jkirkley/jrp/setfer/src/org.maxml", "org.maxml", new String[] { "com/wutka" });
            }

            if (true) {
//                ReflectCache.genClassFactories(Util.RD, new String[] { "org.maxml.web.pg" } );
//                ReflectCache.genClassFactories(Util.RD, new String[] { "org.maxml.web.pg.assemblers" } );
//                ReflectCache.genClassFactories(Util.RD, new String[] { "org.maxml.web.pg.gui" } );
                
//                ReflectCache.genClassFactories(Util.RD, new String[] { "org.maxml.xml" } );
            }
            
            //ReflectCache.showJarEntries("/home/jkirkley/3p/java/jdk1.6.0/jre/lib/rt.jar","java/util");
            //ReflectCache.showJarEntries("/home/jkirkley/jrp/setfer/oldlib/struts.jar",null);
            
//            ReflectCache.genClassFactories(Util.RD + "/lib/
//                    "/home/jkirkley/jrp/setfer/src/org.maxml", "org.maxml", new String[] { "org/apache/struts" });
            
        } catch (Exception e) {
            e.printStackTrace();
        } 
    }

}
