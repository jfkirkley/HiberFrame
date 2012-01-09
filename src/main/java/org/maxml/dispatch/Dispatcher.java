package org.maxml.dispatch;

import java.util.Arrays;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Vector;

public class Dispatcher {

    private HashMap<Criteria,Caller>criteria2CallerMap = new HashMap<Criteria,Caller>();

    public void addCaller( Criteria criteria, Caller caller) {
        criteria2CallerMap.put(criteria, caller);
    }
    
    public void dispatch(Object values[]) throws CriteriaException {
        for ( Criteria criteria: criteria2CallerMap.keySet() ) {
            if( criteria.matches(values)) {
                Caller caller = (Caller) criteria2CallerMap.get(criteria);
                caller.call( values );
            }
        }
    }
    
    public void test( Object [] p) {
        
        for (int i = 0; i < p.length; i++) {
            System.out.println( p[i]);
        }
    }
    
    /**
     * @param args
     */
    public static void main(String[] args) {

        try {
            Object [] stuff = new Object[3];
            stuff[0] = "Jack";
            stuff[1] = 39;
            stuff[2] = 393.0392;            
            Caller caller = new Caller( new Dispatcher(), "test");
            
            Dispatcher dispatcher = new Dispatcher();
            dispatcher.addCaller( cAND.and( cContains.contains("Jack",2), cOR.or(cEquals.eq(3998, 1), cEquals.eq("kfk"))), caller);
            stuff[2] = Arrays.asList(stuff);
            
            try {
                dispatcher.dispatch(stuff);
            } catch (CriteriaException e) {
                // TODO Auto-generated catch block
                e.printStackTrace();
            }
            
        } catch (NoSuchMethodException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }

    }

}
