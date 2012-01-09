package org.maxml.common;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Iterator;

public class BroadCaster {
	Class listenerClass;
	String listenerMethod;
	Class[] listenerMethodParams;
	Method method;
	protected Collection listenerers = new ArrayList();
	public BroadCaster(){}
	
	public BroadCaster(Class listenerClass, String listenerMethod, Class[] listenerMethodParams) {
		this.listenerClass = listenerClass;
		this.listenerMethod = listenerMethod;
		this.listenerMethodParams = listenerMethodParams;
	}
	public BroadCaster(Class listenerClass, String listenerMethod, Class listenerMethodParam) {
		this.listenerClass = listenerClass;
		this.listenerMethod = listenerMethod;
		this.listenerMethodParams = new Class[1];
		listenerMethodParams[0] = listenerMethodParam;
	}
	
	public BroadCaster(Method method) {
		this.listenerClass = method.getDeclaringClass();
		this.listenerMethod = method.getName();
		this.listenerMethodParams = method.getParameterTypes();
	}

	public void addListener(Object listener) {
		listenerers.add(listener);
	}
	
	public void broadcast(Object [] params) throws BroadcastException {
		for (Iterator iter = listenerers.iterator(); iter.hasNext();) {
			try {
				listenerClass.getMethod(listenerMethod, listenerMethodParams).invoke(iter.next(), params);
			} catch (IllegalArgumentException e) {
				throw new BroadcastException(e);
			} catch (SecurityException e) {
				throw new BroadcastException(e);
			} catch (IllegalAccessException e) {
				throw new BroadcastException(e);
			} catch (InvocationTargetException e) {
				throw new BroadcastException(e);
			} catch (NoSuchMethodException e) {
				throw new BroadcastException(e);
			}
		}
	}
	
	public void broadcast(Object param) throws BroadcastException {
		Object [] params = new Object[1];
		params[0] = param;
		for (Iterator iter = listenerers.iterator(); iter.hasNext();) {
			try {
				listenerClass.getMethod(listenerMethod, listenerMethodParams).invoke(iter.next(), params);
			} catch (IllegalArgumentException e) {
				throw new BroadcastException(e);
			} catch (SecurityException e) {
				throw new BroadcastException(e);
			} catch (IllegalAccessException e) {
				throw new BroadcastException(e);
			} catch (InvocationTargetException e) {
				throw new BroadcastException(e);
			} catch (NoSuchMethodException e) {
				throw new BroadcastException(e);
			}
		}
	}
	public Method getMethod() {
		if(method==null) {
			try {
				method = listenerClass.getMethod(listenerMethod, listenerMethodParams);
			} catch (SecurityException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			} catch (NoSuchMethodException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
		return method;
	}
	public void setMethod(Method method) {
		this.method = method;
	}
}
