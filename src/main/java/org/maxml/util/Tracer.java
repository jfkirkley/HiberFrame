package org.maxml.util;

import java.io.*;
import java.util.*;



public class Tracer {

    public static boolean on = false;
    public static final String CONSTRUCTORS = "__CONSTRUCTORS__";
    public static final String CONSTRUCTOR = "!Constructor!";

    static long    lastModTime = -1;
    static String  traceOutFileName = /*Config.getInstance().getTempDir() +*/ "trace.out";
    static TreeSet myTraceClasses = new TreeSet();
    static HashMap myTraceMethods = new HashMap();


    public static void handleTraceParams( String params ) {

	on = true;
	myTraceClasses = new TreeSet();
	myTraceMethods = new HashMap();

	StringTokenizer st = new StringTokenizer( params, ":" );
	while( st.hasMoreElements() ) {
	    String param = (String) st.nextElement();

	    if( param.indexOf( "@" ) > -1 ) {
		StringTokenizer st2 = new StringTokenizer( param, "@" );
		String methodName = (String)st2.nextElement();
		String className = (String)st2.nextElement();
		setTraceOnClassMethod( className, methodName );
	    } else {
		setTraceOn( param );
	    }
	}
    }

    public static void setTraceOn( String className ) {
	myTraceClasses.add( className );
    }

    public static void setTraceOnClassMethod( String className, String methodName ) {
	if( !myTraceClasses.contains( className ) ) myTraceClasses.add( className );

	TreeSet methodSet = (TreeSet)myTraceMethods.get( className );
	if( methodSet == null ) {
	    methodSet = new TreeSet();
	    myTraceMethods.put( className, methodSet );
	}
	if( !methodSet.contains( methodName ) ) methodSet.add( methodName );
    }

    public static void refresh() {

	try { 
	    File tf = new File( traceOutFileName );

	    long modt = tf.lastModified();

	    if( lastModTime == modt ) return;

	    lastModTime = modt;
	    
	    FileInputStream fis = new FileInputStream( tf );
	    
	    StringBuffer sb = new StringBuffer();

	    int c = 0;
	    while( ( c = fis.read() ) != -1 ) {
		char ch = (char) c;
		if( ch == '\n' ) ch = ':';
		sb.append( ch );
	    }
	    fis.close();
	    handleTraceParams( sb.toString() );

	} catch( Exception e ) {
	    e.printStackTrace();
	}
    }

    public static boolean on( Object classRef, String methodName ) {

	refresh();
	String className = classRef.getClass().getName();

 	//System.out.println( "trace? -> " + className );
 	//System.out.println( "trace? -> " + myTraceClasses );
	if( !myTraceClasses.contains( className ) ) return false;
 	//System.out.println( "trace? -> " + methodName );

	TreeSet methodSet = (TreeSet)myTraceMethods.get( className );

	// if no methods set, we assume all methods for the given class are traced
	if( methodSet == null ) return true;

	boolean f = methodSet.contains( methodName );

	return f;
    }

    public static boolean onConstructors( Object classRef ) {

	refresh();
	String className = classRef.getClass().getName();

 	//System.out.println( "trace? -> " + className );
 	//System.out.println( "trace? -> " + myTraceClasses );
	if( !myTraceClasses.contains( className ) ) return false;
 	//System.out.println( "trace? -> " + methodName );

	TreeSet methodSet = (TreeSet)myTraceMethods.get( className );

	// if no methods set, we assume all methods for the given class are traced
	if( methodSet == null ) return true;

	boolean f = methodSet.contains( Tracer.CONSTRUCTORS );

	return f;
    }

    public static boolean on( String className, String methodName ) {

	refresh();

	if( className.equals( "_CLASSNAME_" ) ) 
	    throw new Error( "Illegal classname use in tracing call" );

//  	System.out.println( "trace? -> " + className );
//  	System.out.println( "trace? -> " + myTraceClasses );
	if( !myTraceClasses.contains( className ) ) return false;
//  	System.out.println( "trace? -> " + methodName );

	TreeSet methodSet = (TreeSet)myTraceMethods.get( className );

	// if no methods set, we assume all methods for the given class are traced
	if( methodSet == null ) return true;

	boolean f = methodSet.contains( methodName );

	return f;
	//return methodSet.contains( methodName );
    }

    public static void internalOut( Object p1 ) {
	out( "    " + p1 );
    }

    public static void pr( Object p1 ) {
	out( "returns --> " + p1 );
    }

    public static void pr( int p1 ) {
	out( "returns --> " + p1 );
    }

    public static void pr( float p1 ) {
	out( "returns --> " + p1 );
    }

    public static void pr( double p1 ) {
	out( "returns --> " + p1 );
    }

    public static void pr( char p1 ) {
	out( "returns --> " + p1 );
    }

    public static void pr( boolean p1 ) {
	out( "returns --> " + p1 );
    }


    public static void prefix( Object classRef, String methodName ) {
	if( classRef instanceof String ) // static method tracing
	    out( "Trace: " + methodName + "@" + classRef );
	else
	    out( "Trace: " + methodName + "@" + classRef.getClass().getName() );
    }

    public static void prefix( String className, String methodName ) {

    }

    public static void pp( Object classRef, String methodName ) {
	prefix( classRef, methodName );
    }

    public static void pp( Object classRef, String methodName, 
			   Object p1, String s1 ) {
	prefix( classRef, methodName );

	out( "    " +s1 + ": " + p1 );
    }

    public static void pp( Object classRef, String methodName, 
			   Object p1, String s1, Object p2, String s2 ) {
	prefix( classRef, methodName );

	out( "    " +s1 + ": " + p1 );
	out( "    " +s2 + ": " + p2 );
    }

    public static void pp( Object classRef, String methodName, 
			   Object p1, String s1, Object p2, String s2, Object p3, String s3 ) {
	prefix( classRef, methodName );

	out( "    " +s1 + ": " + p1 );
	out( "    " +s2 + ": " + p2 );
	out( "    " +s3 + ": " + p3 );
    }
    public static void pp( Object classRef, String methodName, 
			   Object p1, String s1, Object p2, String s2, Object p3, String s3, Object p4, String s4 ) {
	prefix( classRef, methodName );

	out( "    " +s1 + ": " + p1 );
	out( "    " +s2 + ": " + p2 );
	out( "    " +s3 + ": " + p3 );
	out( "    " +s4 + ": " + p4 );
    }
    public static void pp( Object classRef, String methodName, 
			   Object p1, String s1, Object p2, String s2, Object p3, String s3, Object p4, String s4, 
			   Object p5, String s5 ) {
	prefix( classRef, methodName );

	out( "    " +s1 + ": " + p1 );
	out( "    " +s2 + ": " + p2 );
	out( "    " +s3 + ": " + p3 );
	out( "    " +s4 + ": " + p4 );
	out( "    " +s5 + ": " + p5 );
    }
    public static void pp( Object classRef, String methodName, 
			   Object p1, String s1, Object p2, String s2, Object p3, String s3, Object p4, String s4, 
			   Object p5, String s5, Object p6, String s6 ) {
	prefix( classRef, methodName );

	out( "    " +s1 + ": " + p1 );
	out( "    " +s2 + ": " + p2 );
	out( "    " +s3 + ": " + p3 );
	out( "    " +s4 + ": " + p4 );
	out( "    " +s5 + ": " + p5 );
	out( "    " +s6 + ": " + p6 );

    }
    public static void pp( Object classRef, String methodName, 
			   Object p1, String s1, Object p2, String s2, Object p3, String s3, Object p4, String s4, 
			   Object p5, String s5, Object p6, String s6, Object p7, String s7 ) {
	prefix( classRef, methodName );

	out( "    " +s1 + ": " + p1 );
	out( "    " +s2 + ": " + p2 );
	out( "    " +s3 + ": " + p3 );
	out( "    " +s4 + ": " + p4 );
	out( "    " +s5 + ": " + p5 );
	out( "    " +s6 + ": " + p6 );
	out( "    " +s7 + ": " + p7 );
    }
    public static void pp( Object classRef, String methodName, 
			   Object p1, String s1, Object p2, String s2, Object p3, String s3, Object p4, String s4, 
			   Object p5, String s5, Object p6, String s6, Object p7, String s7, Object p8, String s8 ) {
	prefix( classRef, methodName );

	out( "    " +s1 + ": " + p1 );
	out( "    " +s2 + ": " + p2 );
	out( "    " +s3 + ": " + p3 );
	out( "    " +s4 + ": " + p4 );
	out( "    " +s5 + ": " + p5 );
	out( "    " +s6 + ": " + p6 );
	out( "    " +s7 + ": " + p7 );
	out( "    " +s8 + ": " + p8 );

    }
    public static void pp( Object classRef, String methodName, 
			   Object p1, String s1, Object p2, String s2, Object p3, String s3, Object p4, String s4, 
			   Object p5, String s5, Object p6, String s6, Object p7, String s7, Object p8, String s8, 
			   Object p9, String s9 ) {
	prefix( classRef, methodName );

	out( "    " +s1 + ": " + p1 );
	out( "    " +s2 + ": " + p2 );
	out( "    " +s3 + ": " + p3 );
	out( "    " +s4 + ": " + p4 );
	out( "    " +s5 + ": " + p5 );
	out( "    " +s6 + ": " + p6 );
	out( "    " +s7 + ": " + p7 );
	out( "    " +s8 + ": " + p8 );
	out( "    " +s9 + ": " + p9 );

    }
    public static void pp( Object classRef, String methodName, 
			   Object p1, String s1, Object p2, String s2, Object p3, String s3, Object p4, String s4, 
			   Object p5, String s5, Object p6, String s6, Object p7, String s7, Object p8, String s8, 
			   Object p9, String s9, Object p10, String s10 ) {
	prefix( classRef, methodName );

	out( "    " +s1 + ": " + p1 );
	out( "    " +s2 + ": " + p2 );
	out( "    " +s3 + ": " + p3 );
	out( "    " +s4 + ": " + p4 );
	out( "    " +s5 + ": " + p5 );
	out( "    " +s6 + ": " + p6 );
	out( "    " +s7 + ": " + p7 );
	out( "    " +s8 + ": " + p8 );
	out( "    " +s9 + ": " + p9 );
	out( "    " +s10 + ": " + p10 );
    }
    public static void pp( Object classRef, String methodName, 
			   Object p1, String s1, Object p2, String s2, Object p3, String s3, Object p4, String s4, 
			   Object p5, String s5, Object p6, String s6, Object p7, String s7, Object p8, String s8, 
			   Object p9, String s9, Object p10, String s10, Object p11, String s11 ) {
	prefix( classRef, methodName );

	out( "    " +s1 + ": " + p1 );
	out( "    " +s2 + ": " + p2 );
	out( "    " +s3 + ": " + p3 );
	out( "    " +s4 + ": " + p4 );
	out( "    " +s5 + ": " + p5 );
	out( "    " +s6 + ": " + p6 );
	out( "    " +s7 + ": " + p7 );
	out( "    " +s8 + ": " + p8 );
	out( "    " +s9 + ": " + p9 );
	out( "    " +s10 + ": " + p10 );
	out( "    " +s11 + ": " + p11 );

    }
    public static void pp( Object classRef, String methodName, 
			   Object p1, String s1, Object p2, String s2, Object p3, String s3, Object p4, String s4, 
			   Object p5, String s5, Object p6, String s6, Object p7, String s7, Object p8, String s8, 
			   Object p9, String s9, Object p10, String s10, Object p11, String s11, Object p12, String s12 ) {
	prefix( classRef, methodName );

	out( "    " +s1 + ": " + p1 );
	out( "    " +s2 + ": " + p2 );
	out( "    " +s3 + ": " + p3 );
	out( "    " +s4 + ": " + p4 );
	out( "    " +s5 + ": " + p5 );
	out( "    " +s6 + ": " + p6 );
	out( "    " +s7 + ": " + p7 );
	out( "    " +s8 + ": " + p8 );
	out( "    " +s9 + ": " + p9 );
	out( "    " +s10 + ": " + p10 );
	out( "    " +s11 + ": " + p11 );
	out( "    " +s12 + ": " + p12 );
    }



    public static void pc( Object classRef ) {
	out( "Trace: " + CONSTRUCTOR + "@" + classRef.getClass().getName() );
    }

    public static void pc( Object classRef, 
			   Object p1, String s1 ) {
	out( "Trace: " + CONSTRUCTOR + "@" + classRef.getClass().getName() );
	out( "    " +s1 + ": " + p1 );
    }

    public static void pc( Object classRef, 
			   Object p1, String s1, Object p2, String s2 ) {
	out( "Trace: " + CONSTRUCTOR + "@" + classRef.getClass().getName() );

	out( "    " +s1 + ": " + p1 );
	out( "    " +s2 + ": " + p2 );
    }

    public static void pc( Object classRef, 
			   Object p1, String s1, Object p2, String s2, Object p3, String s3 ) {
	out( "Trace: " + CONSTRUCTOR + "@" + classRef.getClass().getName() );

	out( "    " +s1 + ": " + p1 );
	out( "    " +s2 + ": " + p2 );
	out( "    " +s3 + ": " + p3 );
    }
    public static void pc( Object classRef, 
			   Object p1, String s1, Object p2, String s2, Object p3, String s3, Object p4, String s4 ) {
	out( "Trace: " + CONSTRUCTOR + "@" + classRef.getClass().getName() );

	out( "    " +s1 + ": " + p1 );
	out( "    " +s2 + ": " + p2 );
	out( "    " +s3 + ": " + p3 );
	out( "    " +s4 + ": " + p4 );
    }
    public static void pc( Object classRef, 
			   Object p1, String s1, Object p2, String s2, Object p3, String s3, Object p4, String s4, 
			   Object p5, String s5 ) {
	out( "Trace: " + CONSTRUCTOR + "@" + classRef.getClass().getName() );

	out( "    " +s1 + ": " + p1 );
	out( "    " +s2 + ": " + p2 );
	out( "    " +s3 + ": " + p3 );
	out( "    " +s4 + ": " + p4 );
	out( "    " +s5 + ": " + p5 );
    }
    public static void pc( Object classRef, 
			   Object p1, String s1, Object p2, String s2, Object p3, String s3, Object p4, String s4, 
			   Object p5, String s5, Object p6, String s6 ) {
	out( "Trace: " + CONSTRUCTOR + "@" + classRef.getClass().getName() );

	out( "    " +s1 + ": " + p1 );
	out( "    " +s2 + ": " + p2 );
	out( "    " +s3 + ": " + p3 );
	out( "    " +s4 + ": " + p4 );
	out( "    " +s5 + ": " + p5 );
	out( "    " +s6 + ": " + p6 );

    }
    public static void pc( Object classRef, 
			   Object p1, String s1, Object p2, String s2, Object p3, String s3, Object p4, String s4, 
			   Object p5, String s5, Object p6, String s6, Object p7, String s7 ) {
	out( "Trace: " + CONSTRUCTOR + "@" + classRef.getClass().getName() );

	out( "    " +s1 + ": " + p1 );
	out( "    " +s2 + ": " + p2 );
	out( "    " +s3 + ": " + p3 );
	out( "    " +s4 + ": " + p4 );
	out( "    " +s5 + ": " + p5 );
	out( "    " +s6 + ": " + p6 );
	out( "    " +s7 + ": " + p7 );
    }
    public static void pc( Object classRef, 
			   Object p1, String s1, Object p2, String s2, Object p3, String s3, Object p4, String s4, 
			   Object p5, String s5, Object p6, String s6, Object p7, String s7, Object p8, String s8 ) {
	out( "Trace: " + CONSTRUCTOR + "@" + classRef.getClass().getName() );

	out( "    " +s1 + ": " + p1 );
	out( "    " +s2 + ": " + p2 );
	out( "    " +s3 + ": " + p3 );
	out( "    " +s4 + ": " + p4 );
	out( "    " +s5 + ": " + p5 );
	out( "    " +s6 + ": " + p6 );
	out( "    " +s7 + ": " + p7 );
	out( "    " +s8 + ": " + p8 );

    }
    public static void pc( Object classRef, 
			   Object p1, String s1, Object p2, String s2, Object p3, String s3, Object p4, String s4, 
			   Object p5, String s5, Object p6, String s6, Object p7, String s7, Object p8, String s8, 
			   Object p9, String s9 ) {
	out( "Trace: " + CONSTRUCTOR + "@" + classRef.getClass().getName() );

	out( "    " +s1 + ": " + p1 );
	out( "    " +s2 + ": " + p2 );
	out( "    " +s3 + ": " + p3 );
	out( "    " +s4 + ": " + p4 );
	out( "    " +s5 + ": " + p5 );
	out( "    " +s6 + ": " + p6 );
	out( "    " +s7 + ": " + p7 );
	out( "    " +s8 + ": " + p8 );
	out( "    " +s9 + ": " + p9 );

    }
    public static void pc( Object classRef, 
			   Object p1, String s1, Object p2, String s2, Object p3, String s3, Object p4, String s4, 
			   Object p5, String s5, Object p6, String s6, Object p7, String s7, Object p8, String s8, 
			   Object p9, String s9, Object p10, String s10 ) {
	out( "Trace: " + CONSTRUCTOR + "@" + classRef.getClass().getName() );

	out( "    " +s1 + ": " + p1 );
	out( "    " +s2 + ": " + p2 );
	out( "    " +s3 + ": " + p3 );
	out( "    " +s4 + ": " + p4 );
	out( "    " +s5 + ": " + p5 );
	out( "    " +s6 + ": " + p6 );
	out( "    " +s7 + ": " + p7 );
	out( "    " +s8 + ": " + p8 );
	out( "    " +s9 + ": " + p9 );
	out( "    " +s10 + ": " + p10 );
    }
    public static void pc( Object classRef, 
			   Object p1, String s1, Object p2, String s2, Object p3, String s3, Object p4, String s4, 
			   Object p5, String s5, Object p6, String s6, Object p7, String s7, Object p8, String s8, 
			   Object p9, String s9, Object p10, String s10, Object p11, String s11 ) {
	out( "Trace: " + CONSTRUCTOR + "@" + classRef.getClass().getName() );

	out( "    " +s1 + ": " + p1 );
	out( "    " +s2 + ": " + p2 );
	out( "    " +s3 + ": " + p3 );
	out( "    " +s4 + ": " + p4 );
	out( "    " +s5 + ": " + p5 );
	out( "    " +s6 + ": " + p6 );
	out( "    " +s7 + ": " + p7 );
	out( "    " +s8 + ": " + p8 );
	out( "    " +s9 + ": " + p9 );
	out( "    " +s10 + ": " + p10 );
	out( "    " +s11 + ": " + p11 );

    }
    public static void pc( Object classRef, 
			   Object p1, String s1, Object p2, String s2, Object p3, String s3, Object p4, String s4, 
			   Object p5, String s5, Object p6, String s6, Object p7, String s7, Object p8, String s8, 
			   Object p9, String s9, Object p10, String s10, Object p11, String s11, Object p12, String s12 ) {
	out( "Trace: " + CONSTRUCTOR + "@" + classRef.getClass().getName() );

	out( "    " +s1 + ": " + p1 );
	out( "    " +s2 + ": " + p2 );
	out( "    " +s3 + ": " + p3 );
	out( "    " +s4 + ": " + p4 );
	out( "    " +s5 + ": " + p5 );
	out( "    " +s6 + ": " + p6 );
	out( "    " +s7 + ": " + p7 );
	out( "    " +s8 + ": " + p8 );
	out( "    " +s9 + ": " + p9 );
	out( "    " +s10 + ": " + p10 );
	out( "    " +s11 + ": " + p11 );
	out( "    " +s12 + ": " + p12 );
    }


    public static void out( String traceStr ) {
	System.out.println( traceStr );
    }

}
