package org.maxml.dispatch;

import java.lang.reflect.Method;

public class Caller {

    private static Class[] paramArray = new Class[1];
    private Object callee;
    private Method method;
    
    public Caller(Object callee, String methodName) throws NoSuchMethodException {
        this.callee = callee;
        Class calleeClass = callee.getClass();
        paramArray[0] = (new Object[1]).getClass();
        this.method = calleeClass.getMethod(methodName, paramArray);
    }
    
    public Object call(Object [] params) {
        try {
            Object [] paramWrapper = new Object[1];
            paramWrapper[0] = params;
            return method.invoke(callee, paramWrapper);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }
}
