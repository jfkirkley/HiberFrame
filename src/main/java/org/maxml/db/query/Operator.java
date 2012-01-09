package org.maxml.db.query;

public class Operator {
    private String rep;

    public static Operator AND = new Operator("AND");
    public static Operator OR = new Operator("OR");
    public static Operator EQUALS = new Operator("=");
    public static Operator GT = new Operator(">");
    public static Operator LT = new Operator("<");
    public static Operator GTE = new Operator(">=");
    public static Operator LTE = new Operator("<=");
    public static Operator IN = new Operator("IN");
    
    public Operator(String rep) {
        this.rep = rep;
    }
    
    public String toString() { return " " + rep + " "; }

}
