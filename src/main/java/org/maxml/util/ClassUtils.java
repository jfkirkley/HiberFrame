package org.maxml.util;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.Collection;

public class ClassUtils {

	public static final String[]	primTypes	= { "int", "long", "short", "boolean", "char", "byte", "float",
			"double", "void"					};

	public static final String[]	simpleTypes	= { "java.lang.Integer", "java.lang.String", "java.lang.Byte",
			"java.lang.Float", "java.lang.Long", "java.lang.Double", "java.lang.Short", "java.lang.Void",
			"java.lang.Character"
												// "java.lang.",
												// "java.lang.", "java.lang.",
												// "java.lang.", "java.lang.",
												};

	private static ClassUtils		instance	= null;

	public static ClassUtils i() {
		if (instance == null) {
			instance = new ClassUtils();
		}
		return instance;
	}

	public boolean isPrimitive(String rep) {
		return Util.i().isInArray(rep, primTypes);
	}

	public boolean isJavaLang(Class c) {
		Package package1 = c.getPackage();
		String javaPackage = package1.getName();
		return javaPackage.startsWith("java.") || javaPackage.startsWith("javax.");
	}

	public boolean isSimple(Class c) {
		String className = c.getName();
		return Util.i().isInArray(className, simpleTypes);
	}

	public Object getSingletonInstance(Object instance, Class singletonClass) {
		if (instance == null) {
			if (singletonClass == null) {
				throw new Error("singletonClass is null");
			}
			instance = createNewObjectOfType(singletonClass);
			if (instance == null) {
				throw new Error("No such class: " + singletonClass.getName());
			}

			// System.err.println( "CREATED: " + singletonClass.getName() + "
			// ---> " + instance);
			// System.err.println( "CREATED: " + singletonClass.getName() + "
			// ---> " + instance);
		}
		return instance;
	}

	public Object invokeMethod(Object target, String methodName, Object[] params) {
		try {

			Class clazz = target.getClass();
			Method m = clazz.getMethod(methodName, null);
			return m.invoke(target, params);

		} catch (SecurityException e) {
			e.printStackTrace();
		} catch (IllegalArgumentException e) {
			e.printStackTrace();
		} catch (IllegalAccessException e) {
			e.printStackTrace();
		} catch (NoSuchMethodException e) {
			e.printStackTrace();
		} catch (InvocationTargetException e) {
			e.printStackTrace();
		}
		return null;
	}

	public Object createNewObjectOfType(Object objectOfType) {

		return createNewObjectOfType(objectOfType.getClass());
	}

	public Object createNewObjectOfType(String typeName) {
		try {
			Class claz = Class.forName(typeName);
			return createNewObjectOfType(claz);
		} catch (ClassNotFoundException cnfe) {
			cnfe.printStackTrace();
		}

		return null;
	}

	public Object createNewObjectOfType(Class claz) {
		try {
			return claz.newInstance();
		} catch (IllegalAccessException iae) {
			iae.printStackTrace();
		} catch (InstantiationException ie) {
			ie.printStackTrace();
		}
		return null;
	}

	public Object createNewObjectOfType(Object objectOfType, String arg) {

		return createNewObjectOfType(objectOfType.getClass(), arg);
	}

	public Object createNewObjectOfType(String typeName, String arg) {
		try {
			Class claz = Class.forName(typeName);
			return createNewObjectOfType(claz, arg);
		} catch (ClassNotFoundException cnfe) {
			cnfe.printStackTrace();
		}

		return null;
	}

	public Object createNewObjectOfType(Class claz, String arg) {

		try {
			return claz.getConstructor(new Class[] { String.class }).newInstance(new Object[] { arg });
		} catch (IllegalArgumentException e) {
			e.printStackTrace();
		} catch (SecurityException e) {
			e.printStackTrace();
		} catch (InstantiationException e) {
			e.printStackTrace();
		} catch (IllegalAccessException e) {
			e.printStackTrace();
		} catch (InvocationTargetException e) {
			e.printStackTrace();
		} catch (NoSuchMethodException e) {
			e.printStackTrace();
		}
		return null;
	}

	public Object createNewObjectOfType(Class claz, Class[] constructorTypes, Object[] args) {

		try {
			return claz.getConstructor(constructorTypes).newInstance(args);
		} catch (IllegalArgumentException e) {
			e.printStackTrace();
		} catch (SecurityException e) {
			e.printStackTrace();
		} catch (InstantiationException e) {
			e.printStackTrace();
		} catch (IllegalAccessException e) {
			e.printStackTrace();
		} catch (InvocationTargetException e) {
			e.printStackTrace();
		} catch (NoSuchMethodException e) {
			e.printStackTrace();
		}
		return null;
	}

	public String getClassRefStrRep(Class c) {
		if (c.isPrimitive()) {
			if (c == Boolean.TYPE) {
				return "Boolean.TYPE";
			} else if (c == Character.TYPE) {
				return "Character.TYPE";
			} else if (c == Byte.TYPE) {
				return "Byte.TYPE";
			} else if (c == Short.TYPE) {
				return "Short.TYPE";
			} else if (c == Integer.TYPE) {
				return "Integer.TYPE";
			} else if (c == Long.TYPE) {
				return "Long.TYPE";
			} else if (c == Float.TYPE) {
				return "Float.TYPE";
			} else if (c == Double.TYPE) {
				return "Double.TYPE";
			} else if (c == Void.TYPE) {
				return "Void.TYPE";
			}
		}
		// else
		return c.getSimpleName() + ".class";
	}

	public Class getCollectionElementClass(Collection collection) {
		if (collection.size() > 0) {
			return collection.iterator().next().getClass();
		}
		return null;
	}

	public Method getMethod(Class clazz, String name, Class[] paramTypes) throws SecurityException,
			NoSuchMethodException {
		return clazz.getMethod(name, paramTypes);
	}

	public Method getMethod(Class clazz, String name) throws SecurityException, NoSuchMethodException {
		return getMethod(clazz, name, (Class[])null);
	}

	public Method getMethod(Class clazz, String name, Class type0) throws SecurityException, NoSuchMethodException {
		Class types[] = { type0 };
		return getMethod(clazz, name, types);
	}

	public Method getMethod(Class clazz, String name, Class type0, Class type00) throws SecurityException, NoSuchMethodException {
		Class types[] = { type0, type00 };
		return getMethod(clazz, name, types);
	}

	public Method getMethod(Class clazz, String name, Class type0, Class type00, Class type000) throws SecurityException, NoSuchMethodException {
		Class types[] = { type0, type00, type000 };
		return getMethod(clazz, name, types);
	}

	public Method getMethod(Class clazz, String name, Class type0, Class type00, Class type000, Class type0000) throws SecurityException, NoSuchMethodException {
		Class types[] = { type0, type00, type000, type0000 };
		return getMethod(clazz, name, types);
	}

	public Method getMethod(Class clazz, String name, Class type0, Class type00, Class type000, Class type0000, Class type00000) throws SecurityException, NoSuchMethodException {
		Class types[] = { type0, type00, type000, type0000, type00000 };
		return getMethod(clazz, name, types);
	}

	public Method getMethod(Class clazz, String name, Class type0, Class type00, Class type000, Class type0000, Class type00000, Class type000000) throws SecurityException, NoSuchMethodException {
		Class types[] = { type0, type00, type000, type0000, type00000, type000000 };
		return getMethod(clazz, name, types);
	}

	public Method getMethod(Class clazz, String name, Class type0, Class type00, Class type000, Class type0000, Class type00000, Class type000000, Class type0000000) throws SecurityException, NoSuchMethodException {
		Class types[] = { type0, type00, type000, type0000, type00000, type000000, type0000000 };
		return getMethod(clazz, name, types);
	}

	public Method getMethod(Class clazz, String name, Class type0, Class type00, Class type000, Class type0000, Class type00000, Class type000000, Class type0000000, Class type00000000) throws SecurityException, NoSuchMethodException {
		Class types[] = { type0, type00, type000, type0000, type00000, type000000, type0000000, type00000000 };
		return getMethod(clazz, name, types);
	}

	public Method getMethod(Class clazz, String name, Class type0, Class type00, Class type000, Class type0000, Class type00000, Class type000000, Class type0000000, Class type00000000, Class type000000000) throws SecurityException, NoSuchMethodException {
		Class types[] = { type0, type00, type000, type0000, type00000, type000000, type0000000, type00000000, type000000000 };
		return getMethod(clazz, name, types);
	}

	public Method getMethod(Class clazz, String name, Class type0, Class type00, Class type000, Class type0000, Class type00000, Class type000000, Class type0000000, Class type00000000, Class type000000000, Class type0000000000) throws SecurityException, NoSuchMethodException {
		Class types[] = { type0, type00, type000, type0000, type00000, type000000, type0000000, type00000000, type000000000, type0000000000 };
		return getMethod(clazz, name, types);
	}

	public Method getMethod(Class clazz, String name, Class type0, Class type00, Class type000, Class type0000, Class type00000, Class type000000, Class type0000000, Class type00000000, Class type000000000, Class type0000000000, Class type00000000000) throws SecurityException, NoSuchMethodException {
		Class types[] = { type0, type00, type000, type0000, type00000, type000000, type0000000, type00000000, type000000000, type0000000000, type00000000000 };
		return getMethod(clazz, name, types);
	}

	public Method getMethod(Class clazz, String name, Class type0, Class type00, Class type000, Class type0000, Class type00000, Class type000000, Class type0000000, Class type00000000, Class type000000000, Class type0000000000, Class type00000000000, Class type000000000000) throws SecurityException, NoSuchMethodException {
		Class types[] = { type0, type00, type000, type0000, type00000, type000000, type0000000, type00000000, type000000000, type0000000000, type00000000000, type000000000000 };
		return getMethod(clazz, name, types);
	}





}
