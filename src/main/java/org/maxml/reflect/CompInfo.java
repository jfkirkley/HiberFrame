package org.maxml.reflect;

import java.awt.Component;


public class CompInfo {

    private Component component;
    private Object constraint;

    
    public CompInfo(Component component, Object constraint) {
        super();
        this.component = component;
        this.constraint = constraint;
    }

    public Component getComponent() {
        return component;
    }

    public void setComponent(Component component) {
        this.component = component;
    }

    public Object getConstraint() {
        return constraint;
    }

    public void setConstraint(Object constraint) {
        this.constraint = constraint;
    }

    public String toString() {
        return component.getName();
    }
}
