package org.maxml.dispatch;

public class cAND implements Criteria {

    private Criteria lhs;
    private Criteria rhs;
    
    public cAND( Criteria lhs, Criteria rhs ) {
        this.lhs = lhs;
        this.rhs = rhs;
    }
    
    public boolean matches( Object [] values ) throws CriteriaException {
        return lhs.matches(values) && rhs.matches(values);
    }
    
    public static Criteria and(Criteria lhs, Criteria rhs ) {
        return new cAND(lhs,rhs);
    }
    
}
