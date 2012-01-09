package org.maxml.reflect;

import java.io.IOException;
import java.io.OutputStream;
import java.io.PrintStream;
import java.lang.reflect.Method;
import java.lang.reflect.Modifier;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.TreeSet;

import javax.swing.ListSelectionModel;
import javax.swing.event.TreeSelectionEvent;

import org.maxml.util.Util;


public class CodeGenerator {

    private static String indent = "    ";
    private ArrayList     typesToImport;
    private StringBuffer  codeBuffer;
    private boolean       isInterface=false;

    public CodeGenerator() {
        typesToImport = new ArrayList();
        codeBuffer = new StringBuffer();
    }

    public CodeGenerator(boolean isInterface) {
        this();
        this.isInterface = isInterface;
    }

    public void generate(Class c, String packageName, String newName,
            PrintStream os) {
        try {
            getHeader(c, newName);
            Method[] methods = c.getDeclaredMethods();
            for (int i = 0; i < methods.length; ++i) {
                generate(methods[i]);
            }
            os.write(getImports(packageName).getBytes());
            os.write("\n\n".getBytes());
            os.write(codeBuffer.toString().getBytes());
            os.write("}\n".getBytes());

        } catch (SecurityException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        } catch (IOException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
    }

    public void generate(Class c, String packageName, String newName,
            OutputStream os) {
        try {
            getHeader(c, newName);
            Method[] methods = c.getDeclaredMethods();
            for (int i = 0; i < methods.length; ++i) {
                generate(methods[i]);
            }
            os.write(getImports(packageName).getBytes());
            os.write("\n\n".getBytes());
            os.write(codeBuffer.toString().getBytes());
            os.write("}\n".getBytes());

        } catch (SecurityException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        } catch (IOException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
    }

    public String getImports(String packageName) {
        StringBuffer ibuf = new StringBuffer();
        if (packageName != null && packageName.length() > 0) {
            ibuf.append("package " + packageName + ";\n\n");
        }
        Iterator i = typesToImport.iterator();
        while (i.hasNext()) {
            Class importTypeClass = (Class) i.next();
            if (importTypeClass.getPackage() == null)
                continue;
            String pkg = importTypeClass.getPackage().getName();
            if (!importTypeClass.isPrimitive() && !pkg.equals("java.lang")
                    && !pkg.equals(packageName)) {
                ibuf.append("import " + importTypeClass.getName() + ";\n");
            }
        }
        return ibuf.toString();
    }

    public String getModifierString(int modifiers) {
        String modStr = "";
        String access = (Modifier.isPublic(modifiers)) ? "public"
                : (Modifier.isProtected(modifiers)) ? "protected"
                        : (Modifier.isPrivate(modifiers)) ? "private" : "";
        modStr = access;
        if (Modifier.isStatic(modifiers))
            modStr = modStr + " static";
        if (Modifier.isSynchronized(modifiers))
            modStr = modStr + " synchronized";

        return modStr + " ";
    }

    public String getTypeName(Class c) {
        if (c.isPrimitive()) {

            if (c == Integer.TYPE)
                return "int";
            if (c == Long.TYPE)
                return "long";
            if (c == Boolean.TYPE)
                return "boolean";
            if (c == Character.TYPE)
                return "char";
            if (c == Byte.TYPE)
                return "byte";
            if (c == Short.TYPE)
                return "short";
            if (c == Double.TYPE)
                return "double";
            if (c == Float.TYPE)
                return "float";
            if (c == Void.TYPE)
                return "void";

        } else {
            String fullName = c.getName();
            if( c.isArray()) { 
                fullName = c.getComponentType().getName() + "[]"; 
                if (!typesToImport.contains(c.getComponentType())) {
                    typesToImport.add(c.getComponentType());
                }
            } else {
                if (!typesToImport.contains(c)) {
                    typesToImport.add(c);
                }
            }
            return fullName.substring(fullName.lastIndexOf(".") + 1);
        }
        return "";
    }

    public String getDefaultReturn(Class c) {
        if (c.isPrimitive()) {

            if (c == Integer.TYPE)
                return "return 0;";
            if (c == Long.TYPE)
                return "return 0;";
            if (c == Boolean.TYPE)
                return "return false;";
            if (c == Character.TYPE)
                return "return 'c';";
            if (c == Byte.TYPE)
                return "return (byte)0;";
            if (c == Short.TYPE)
                return "return 0;";
            if (c == Double.TYPE)
                return "return 0.0;";
            if (c == Float.TYPE)
                return "return 0.0;";
            if (c == Void.TYPE)
                return "";

        } else {
            return "return null;";
        }
        return "";
    }

    public void generate(Method m) {

        if( isInterface && Modifier.isStatic(m.getModifiers()))return;
        
        Class returnType = m.getReturnType();
        Class[] paramTypes = m.getParameterTypes();
        Class[] exceptionTypes = m.getExceptionTypes();
        int modifiers = m.getModifiers();

        codeBuffer.append(indent + getModifierString(modifiers));
        codeBuffer.append(getTypeName(returnType) + " ");
        codeBuffer.append(m.getName());
        codeBuffer.append("( ");
        String comma = "";
        TreeSet treeSet = new TreeSet();
        for (int i = 0; i < paramTypes.length; ++i) {
            String typeName = getTypeName(paramTypes[i]);
            codeBuffer.append(comma + typeName  + " ");
            String paramName = Util.i().lowerCaseFirstLetter(typeName);
            if( paramName.endsWith("[]")) {
                paramName = paramName.substring(0, paramName.length()-2) + "Arr";
            }
            if( paramName.equals(typeName)) {
                paramName += i;
            }
            if( treeSet.contains(paramName)) {
                paramName += i;
            }
            treeSet.add(paramName);
            codeBuffer.append(paramName);
            comma = ", ";
        }
        codeBuffer.append(" )");
        comma = "";
        if (exceptionTypes.length > 0) {
            codeBuffer.append("throws ");
            for (int i = 0; i < exceptionTypes.length; ++i) {
                codeBuffer.append(comma + getTypeName(exceptionTypes[i]) + " ");
                comma = ",";
            }
        }
        if (!isInterface) {
            codeBuffer.append(" {\n");
            codeBuffer.append(indent + indent + getDefaultReturn(returnType)
                    + "\n");
            codeBuffer.append(indent + "}\n\n");
        } else {
            codeBuffer.append(";\n\n");
        }
    }

    public void getHeader(Class c, String newName) {

        String h = "public class " + newName;
        Class superclass = c.getSuperclass();
        if( superclass != null ){
            String fullName = superclass.getName();
            h += " extends " + (fullName.substring(fullName.lastIndexOf(".") + 1));
            typesToImport.add(superclass);
        }
        Class [] interfaces = c.getInterfaces();
        for (int i = 0; i < interfaces.length; i++) {
            if(i==0) h += " implements ";
            String fullName = interfaces[i].getName();
            h += " " + (fullName.substring(fullName.lastIndexOf(".") + 1));
            typesToImport.add(interfaces[i]);
        }
        h += "{\n";
        
        
//                + ((c.isInterface()) ? " implements " : " extends ")
//                + getTypeName(c) + "{\n";

        codeBuffer.append(h);
    }

    public static void main(String[] a) {
        CodeGenerator cg = new CodeGenerator();
        
//        cg.generate(RulesCommonForm.class, "org.maxml.reflect", "Bob",
//                System.out);
    }
}
