package org.maxml.reflect;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.maxml.util.ClassUtils;
import org.maxml.util.Regex;

public class PropertyName {
    
    private boolean indexed = false;
    private boolean parameterized = false;
    private boolean typed = false;

    private String index;
    private String parameter;
    private String type;

    private String propertyName;
    
    private static final Pattern indexedPropPattern = Pattern.compile("(\\w+)\\[([^\\]]*)\\]");
    private static final Pattern parameterizedPropPattern = Pattern.compile("(\\w+)\\(([^\\)]*)\\)");
    private static final Pattern indexedAndTypedPropPattern = Pattern.compile("(\\w+)\\$([^\\$]*)\\$\\[([^\\]]*)\\]");
    private static final Pattern parameterizedAndTypePropPattern = Pattern.compile("(\\w+)\\$([^\\$]*)\\$\\(([^\\)]*)\\)");
    
    public PropertyName(String name) {
    	
    	if( Regex.isRegexDelimitted2(name) ) {
            propertyName = name;
    	}
    	else if( name.indexOf('[') != -1 ) {
            
            Matcher indexedMatcher = this.indexedPropPattern.matcher(name);
            if( indexedMatcher.matches() ) {
                propertyName = indexedMatcher.group(1);
                String indexStr = indexedMatcher.group(2);
                this.index = indexStr;
                this.indexed = true;
            }
            else {
                Matcher indexedAndTypedMatcher = this.indexedAndTypedPropPattern.matcher(name);
                if( indexedAndTypedMatcher.matches() ) {
                    propertyName = indexedAndTypedMatcher.group(1);
                    this.type = indexedAndTypedMatcher.group(2).replace('_','.');;
                    this.index = indexedAndTypedMatcher.group(3);
                    this.indexed = true;
                    this.typed = true;
                }                
            }
            
        } else if( name.indexOf('(') != -1 ) {
            
            Matcher paramerterizedMatcher = this.parameterizedPropPattern.matcher(name);
            if( paramerterizedMatcher.matches() ) {
                propertyName = paramerterizedMatcher.group(1);
                this.parameter = paramerterizedMatcher.group(2);
                this.parameterized = true;
            } 
            else {
                Matcher paramerterizedAndTypedMatcher = this.parameterizedAndTypePropPattern.matcher(name);
                if( paramerterizedAndTypedMatcher.matches() ) {
                    propertyName = paramerterizedAndTypedMatcher.group(1);
                    this.type = paramerterizedAndTypedMatcher.group(2).replace('_','.');
                    this.parameter = paramerterizedAndTypedMatcher.group(3);
                    this.parameterized = true;
                    this.typed = true;
                }                
            }
            
        } else {
            propertyName = name;
        }
    }

    public boolean isIndexed() {
        return indexed;
    }

    public boolean isParameterized() {
        return parameterized;
    }

    public int getIndex() {
        return Integer.parseInt(index);
    }
    
    public String getIndexStr() {
        return index;
    }

    public String getParameter() {
        return parameter;
    }

    public String getPropertyName() {
        return propertyName;
    }

    public String toString () {
        return propertyName;
    }

    public boolean isTyped() {
        return typed;
    }

    public void setTyped(boolean typed) {
        this.typed = typed;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }
    
    public Object createObjectOfType() {
        return ClassUtils.i().createNewObjectOfType(this.type);
    }
}
