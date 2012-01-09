package org.maxml.common;

import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

public class MultiMethodBroadcaster extends BroadCaster {
	protected Map<Method,BroadCaster> broadCasters = new HashMap<Method,BroadCaster>();

	protected BroadCaster currBroadCaster;
	public MultiMethodBroadcaster(){}

	public void addBroadCaster(BroadCaster broadCaster) {
		this.broadCasters.put(broadCaster.getMethod(), broadCaster);
	}
	
	public void addBroadCaster(Class listenerClass, String listenerMethod, Class[] listenerMethodParams) {
		this.addBroadCaster(new BroadCaster(listenerClass, listenerMethod, listenerMethodParams));
	}
	
	public void addBroadCaster(Class listenerClass, String listenerMethod, Class listenerMethodParam) {
		this.addBroadCaster(new BroadCaster(listenerClass, listenerMethod, listenerMethodParam));
	}
	
	public void addBroadCaster(Method method) {
		this.addBroadCaster(new BroadCaster(method));
	}
	
	
	
		

	public void broadcast(Method method, Object param) throws BroadcastException {
		setCurrBroadCaster(method);
		broadcast(param);
	}

	public void broadcast(Method method, Object[] params) throws BroadcastException {
		setCurrBroadCaster(method);
		broadcast(params);
	}

	public void broadcast(Object param) throws BroadcastException {
		if(currBroadCaster!=null) {
			currBroadCaster.broadcast(param);
		}
	}

	@Override
	public void broadcast(Object[] params) throws BroadcastException {
		if(currBroadCaster!=null) {
			currBroadCaster.broadcast(params);
		}
	}

	public BroadCaster getCurrBroadCaster() {
		return currBroadCaster;
	}

	public void setCurrBroadCaster(Method method) {
		this.currBroadCaster = broadCasters.get(method);
	}

	@Override
	public void addListener(Object listener) {
		for ( Method method: broadCasters.keySet() ) {
			BroadCaster broadCaster = broadCasters.get(method);
			broadCaster.addListener(listener);
		}
	}
	


}
