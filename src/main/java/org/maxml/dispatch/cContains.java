package org.maxml.dispatch;

import java.util.Collection;

public class cContains implements Criteria {
    
    private Object target;
    private int pIndex = 0;
    
    public cContains(Object target) {
        this.target = target;
    }
    
    public cContains(Object target, int pIndex) {
        this(target);
        this.pIndex = pIndex;
    }

    
    public boolean matches(Object[] values) throws CriteriaException {
        try {
            return ((Collection)values[pIndex]).contains(target);
        } catch (RuntimeException e) {
            throw new CriteriaException(e);
        }
    }
    
    public static Criteria contains(Object target) {
        return new cContains(target);
    }

    public static Criteria contains(Object target, int pIndex) {
        return new cContains(target, pIndex);
    }
}
