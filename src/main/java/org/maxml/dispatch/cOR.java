package org.maxml.dispatch;

public class cOR implements Criteria {
    private Criteria lhs;
    private Criteria rhs;
    
    public cOR( Criteria lhs, Criteria rhs ) {
        this.lhs = lhs;
        this.rhs = rhs;
    }
    
    public boolean matches( Object [] values ) throws CriteriaException {
        return lhs.matches(values) || rhs.matches(values);
    }
    
    public static Criteria or(Criteria lhs, Criteria rhs ) {
        return new cOR(lhs,rhs);
    }

}
