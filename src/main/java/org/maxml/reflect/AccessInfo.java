package org.maxml.reflect;

import java.util.Stack;

public class AccessInfo {

	public static class AccessObjInfo {
		private Object accessedObj;
		private String reflectSpec;

		public AccessObjInfo(Object accessedObj, String reflectSpec ) {
			this.accessedObj = accessedObj;
			this.reflectSpec = reflectSpec;
		}

		public Object getAccessedObj() {
			return accessedObj;
		}

		public void setAccessedObj(Object accessedObj) {
			this.accessedObj = accessedObj;
		}

		public String getReflectSpec() {
			return reflectSpec;
		}

		public void setReflectSpec(String reflectSpec) {
			this.reflectSpec = reflectSpec;
		}
	}
	
    private Stack accessStack;
    
    private Object target;

    public AccessInfo() {
        this.accessStack = new Stack();
    }
    
    public void pushAccessObj(Object accessedObj, String reflectSpec) {
        this.accessStack.push(new AccessObjInfo(accessedObj, reflectSpec));
    }

    public Stack getAccessStack() {
        return accessStack;
    }

    public Object getTarget() {
        return target;
    }

    public void setTarget(Object target) {
        this.target = target;
    }

}
