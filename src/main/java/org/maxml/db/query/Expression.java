package org.maxml.db.query;

public class Expression {
    
    private Operator operator = null;
    private Expression leftSide = null;
    private Expression rightSide = null;

    private Object value = null;
    private String alias = null;
    private String paramName = null;
    private boolean useBracket = false;

    public Expression() {
    }
    
    public Expression( Expression leftSide, Expression rightSide, Operator operator) {
        this.leftSide = leftSide;
        this.rightSide = rightSide;
        this.operator = operator;
    }
    
    public Expression( String alias, Operator operator) {
        this.alias = alias;
        this.operator = operator;
    }
    
    public Expression( String alias, String paramName ) {
        this.alias = alias;
        this.paramName = paramName;
    }

    public Expression( Object value ) {
        this.value = value;
    }

    public Expression( String paramName, boolean useBracket ) {
        this.paramName = paramName; 
        this.useBracket = useBracket;
    }

    public Expression( String paramName ) {
        this.paramName = paramName;
    }
        
    public String toString(){
        if( leftSide != null && operator != null && rightSide != null) {
            return " " + leftSide + " " + operator + " " + rightSide + " ";
        } else if( value != null ) {
            return " '" + value + "' ";
        } else if( alias != null && paramName != null ) {
            return " " + alias + "." + paramName + " " ;
        } else if( paramName != null ) {
            if(useBracket) {
                return " (:" + paramName + ") " ;
            } else {
                return " :" + paramName + " " ;
            }
        }
        return "";
    }

    public static Expression getOrExpression( Expression lhs, Expression rhs) {
        return new Expression(lhs, rhs, Operator.OR);
    }

    public static Expression getAndExpression( Expression lhs, Expression rhs) {
        return new Expression(lhs, rhs, Operator.AND);
    }
    
    public static Expression getExpression( String lhAlias, String lhParamName, Object value, Operator operator) {
        return new Expression(new Expression(lhAlias, lhParamName), new Expression(value), operator);
    }

    public static Expression getExpression( String lhAlias, String lhParamName, String rhAlias, String rhParamName, Operator operator) {
        return new Expression(new Expression(lhAlias, lhParamName), new Expression(rhAlias, rhParamName), operator);
    }

    public static Expression getExpression( String lhAlias, String lhParamName, String rhParamName, Operator operator) {
        return new Expression(new Expression(lhAlias, lhParamName), new Expression(rhParamName), operator);
    }

    public static Expression getEqualsExpression( String lhAlias, String lhParamName, Object value) {
        return getExpression( lhAlias, lhParamName, value, Operator.EQUALS);
    }
    
    public static Expression getEqualsExpression( String lhAlias, String lhParamName, String rhParamName) {
        return getExpression( lhAlias, lhParamName, rhParamName, Operator.EQUALS);
    }

    public static Expression getEqualsExpression( String lhAlias, String lhParamName, String rhAlias, String rhParamName) {
        return getExpression( lhAlias, lhParamName, rhAlias, rhParamName, Operator.EQUALS);
    }
        
    public static Expression getGTExpression( String lhAlias, String lhParamName, Object value) {
        return getExpression( lhAlias, lhParamName, value, Operator.GT);
    }
    
    public static Expression getGTExpression( String lhAlias, String lhParamName, String rhParamName) {
        return getExpression( lhAlias, lhParamName, rhParamName, Operator.GT);
    }

    public static Expression getGTExpression( String lhAlias, String lhParamName, String rhAlias, String rhParamName) {
        return getExpression( lhAlias, lhParamName, rhAlias, rhParamName, Operator.GT);
    }

    public static Expression getGTEExpression( String lhAlias, String lhParamName, Object value) {
        return getExpression( lhAlias, lhParamName, value, Operator.GTE);
    }
    
    public static Expression getGTEExpression( String lhAlias, String lhParamName, String rhParamName) {
        return getExpression( lhAlias, lhParamName, rhParamName, Operator.GTE);
    }

    public static Expression getGTEExpression( String lhAlias, String lhParamName, String rhAlias, String rhParamName) {
        return getExpression( lhAlias, lhParamName, rhAlias, rhParamName, Operator.GTE);
    }

    public static Expression getLTExpression( String lhAlias, String lhParamName, Object value) {
        return getExpression( lhAlias, lhParamName, value, Operator.LT);
    }
    
    public static Expression getLTExpression( String lhAlias, String lhParamName, String rhParamName) {
        return getExpression( lhAlias, lhParamName, rhParamName, Operator.LT);
    }

    public static Expression getLTExpression( String lhAlias, String lhParamName, String rhAlias, String rhParamName) {
        return getExpression( lhAlias, lhParamName, rhAlias, rhParamName, Operator.LT);
    }

    public static Expression getLTEExpression( String lhAlias, String lhParamName, Object value) {
        return getExpression( lhAlias, lhParamName, value, Operator.LTE);
    }
    
    public static Expression getLTEExpression( String lhAlias, String lhParamName, String rhParamName) {
        return getExpression( lhAlias, lhParamName, rhParamName, Operator.LTE);
    }

    public static Expression getLTEExpression( String lhAlias, String lhParamName, String rhAlias, String rhParamName) {
        return getExpression( lhAlias, lhParamName, rhAlias, rhParamName, Operator.LTE);
    }
    
    public static Expression getINExpression(String lhAlias, String lhParamName, String listAlias) {
        return new Expression( new Expression(lhAlias, lhParamName), new Expression(listAlias, true), Operator.IN);
    }

}
