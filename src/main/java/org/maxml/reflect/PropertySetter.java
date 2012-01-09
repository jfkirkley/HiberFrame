package org.maxml.reflect;

import java.awt.Component;

public interface PropertySetter {
	
	public Component getGUIComponent();
	public Object getValue();
    
    public String getDeclCode();
    public String getInitCode();
    public String getSetCode(String compName, String propName);
    public String getGetCode(String compName, String propName);
    public String getGenCode(String compName, String propName);

}
