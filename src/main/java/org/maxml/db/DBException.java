package org.maxml.db;

public class DBException extends Exception{

    public DBException(){super();}
    public DBException(Throwable t){super(t);}
    public DBException(String m, Throwable t){super(m,t);}
    public DBException(String m){super(m);}
}
