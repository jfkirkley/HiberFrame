package org.maxml.common;

public class BroadcastException extends Exception {

    public BroadcastException(){super();}
    public BroadcastException(Throwable t){super(t);}
    public BroadcastException(String m, Throwable t){super(m,t);}
    public BroadcastException(String m){super(m);}

}
