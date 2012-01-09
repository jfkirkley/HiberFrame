package org.maxml.util;

import java.lang.*;





public class NumericRange {

    boolean myMinInclusive;
    boolean myMaxInclusive;
    boolean myUnbounded;
    int myMinValue;
    int myMaxValue;

    String myPrimTypeName;
    String myTypeName;

    public NumericRange(  String primTypeName ) {

// 	System.out.println( "classname: " + simpleTypeDef.getClass().getName() );
// 	System.out.println( "classname: " + simpleTypeDef.getName() );
	short definedFacets = 0;
// 	System.out.println( definedFacets + ":" + XSSimpleTypeDefinition.FACET_FRACTIONDIGITS );
// 	System.out.println( "primTypeName: " + primTypeName );
	// 				XSSimpleTypeDefinition.FACET_MININCLUSIVE + ":" +
	// 				XSSimpleTypeDefinition.FACET_MAXINCLUSIVE + ":" +
	// 				XSSimpleTypeDefinition.FACET_MINEXCLUSIVE + ":" +
	// 				XSSimpleTypeDefinition.FACET_MAXEXCLUSIVE);

	String minInclusiveStr =  null;
	String maxInclusiveStr =  null;
	String minExclusiveStr =  null;
	String maxExclusiveStr =  null;

	boolean isInteger = (primTypeName!=null&&primTypeName.equals("integer"))? true:false;

	if( minInclusiveStr != null && maxInclusiveStr != null ) {

	    myMinInclusive = true;
	    myMaxInclusive = true;
	    myUnbounded    = false;
	    myMinValue = (isInteger)? Integer.parseInt( minInclusiveStr ):(int)Double.parseDouble( minInclusiveStr );
	    myMaxValue = (isInteger)? Integer.parseInt( maxInclusiveStr ):(int)Double.parseDouble( maxInclusiveStr );

	} else if( minInclusiveStr != null && maxExclusiveStr != null ) {

	    myMinInclusive = true;
	    myMaxInclusive = false;
	    myUnbounded    = false;
	    myMinValue = (isInteger)? Integer.parseInt( minInclusiveStr ):(int)Double.parseDouble( minInclusiveStr );
	    myMaxValue = (isInteger)? Integer.parseInt( maxExclusiveStr ):(int)Double.parseDouble( maxExclusiveStr );

	} else if( minExclusiveStr != null && maxExclusiveStr != null ) {

	    myMinInclusive = false;
	    myMaxInclusive = false;
	    myUnbounded    = false;
	    myMinValue = (isInteger)? Integer.parseInt( minExclusiveStr ):(int)Double.parseDouble( minExclusiveStr );
	    myMaxValue = (isInteger)? Integer.parseInt( maxExclusiveStr ):(int)Double.parseDouble( maxExclusiveStr );

	} else if( minExclusiveStr != null && maxInclusiveStr != null ) {

	    myMinInclusive = false;
	    myMaxInclusive = true;
	    myUnbounded    = false;
	    myMinValue = (isInteger)? Integer.parseInt( minExclusiveStr ):(int)Double.parseDouble( minExclusiveStr );
	    myMaxValue = (isInteger)? Integer.parseInt( maxInclusiveStr ):(int)Double.parseDouble( maxInclusiveStr );

	} else {
	    myUnbounded = true;
	}
    }

    public String toString() {  

	String minRangeSign = (myMinInclusive)? " >= ": " > ";
	String maxRangeSign = (myMaxInclusive)? " <= ": " < ";

	return myMinValue + minRangeSign + "X" + maxRangeSign + myMaxValue; 
    }
    
    public String getPrimTypeName() { return myPrimTypeName; }
    public String getTypeName() { return myTypeName; }
    public boolean isUnBounded() { return myUnbounded; }

    public boolean isMinInclusive() { return myMinInclusive; }
    public boolean isMaxInclusive() { return myMaxInclusive; }

    public int getMinValue() { return myMinValue; }
    public int getMaxValue() { return myMaxValue; }

    public int getAbsoluteMinValue() { return (myMinInclusive)? myMinValue: myMinValue + 1; }
    public int getAbsoluteMaxValue() { return (myMaxInclusive)? myMaxValue: myMaxValue - 1; }

}
