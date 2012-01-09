package org.maxml.dispatch;

public interface Criteria {
    
    public boolean matches( Object [] values ) throws CriteriaException;

}
