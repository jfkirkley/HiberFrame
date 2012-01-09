package org.maxml.dispatch;

public class cEquals implements Criteria {

    private Object target;
    private int pIndex = 0;
    
    public cEquals(Object target) {
        this.target = target;
    }
    
    public cEquals(Object target, int pIndex) {
        this(target);
        this.pIndex = pIndex;
    }

    
    public boolean matches(Object[] values) throws CriteriaException {
        try {
            return values[pIndex].equals(target);
        } catch (RuntimeException e) {
            throw new CriteriaException(e);
        }
    }
    
    public static Criteria eq(Object target) {
        return new cEquals(target);
    }

    public static Criteria eq(Object target, int pIndex) {
        return new cEquals(target, pIndex);
    }

}
