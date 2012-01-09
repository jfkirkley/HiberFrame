package org.maxml.db;

import java.util.Collection;
import java.util.HashMap;

import org.hiberframe.HibernateSessionInterface;
import org.maxml.util.ClassUtils;

public class DBSessionInterfaceFactory {

    private HashMap       thread2SessionInterfaceMap = new HashMap();

    private static String HibernateImplementation    = "hibernate";
    private String        dbImplementationName       = HibernateImplementation;

    private static Object instance                   = null;

    public static DBSessionInterfaceFactory getInstance() {
        return (DBSessionInterfaceFactory) (instance = ClassUtils.i().getSingletonInstance(instance,
                DBSessionInterfaceFactory.class));
    }

    public DBSessionInterface getDBSessionInterface() {
        return getDBSessionInterface(true);
    }

    public DBSessionInterface getDBSessionInterface(boolean useThreadSessions) {
        DBSessionInterface sessionInterface = null;

        if (useThreadSessions) {
            if (!thread2SessionInterfaceMap.containsKey(Thread.currentThread())) {
                thread2SessionInterfaceMap.put(Thread.currentThread(), getDBSessionInterfaceImplementation());
            }
            sessionInterface = (DBSessionInterface) thread2SessionInterfaceMap.get(Thread.currentThread());
            // System.out.println(this + " got sessionInterface: " +
            // sessionInterface);
        } else {
            sessionInterface = getDBSessionInterfaceImplementation();
        }

        return sessionInterface;
    }

    public void endSession() throws DBException {
        endSession(true);
    }

    public void endSession(boolean remove) throws DBException {

        if (thread2SessionInterfaceMap.containsKey(Thread.currentThread())) {

            DBSessionInterface sessionInterface = (DBSessionInterface) thread2SessionInterfaceMap.get(Thread.currentThread());
            // System.out.println("killing sessionInterface: " +
            // sessionInterface);

            boolean suspendDelayActions = sessionInterface.isDelayActions();
            if (suspendDelayActions) {
                sessionInterface.setDelayActions(false);// no commit will happen
                                                        // otherwise
            }
            if (sessionInterface.transactionIsActive()) {
                // System.out.println("commiting sessionInterface: " +
                // sessionInterface);
                sessionInterface.commitTransaction();
            }
            sessionInterface.closeSession();

            if (suspendDelayActions) {
                sessionInterface.setDelayActions(true);
            }

            if (remove) {
                thread2SessionInterfaceMap.remove(Thread.currentThread());
            }
        }
    }

    public void checkEndSession(boolean remove) throws DBException {

        if (thread2SessionInterfaceMap.containsKey(Thread.currentThread())) {

            DBSessionInterface sessionInterface = (DBSessionInterface) thread2SessionInterfaceMap.get(Thread.currentThread());
            // System.out.println("killing sessionInterface: " +
            // sessionInterface);

            if (sessionInterface != null) {
                // bad - possble rollback required
                boolean suspendDelayActions = sessionInterface.isDelayActions();
                if (suspendDelayActions) {
                    sessionInterface.setDelayActions(false);// no commit will
                                                            // happen otherwise
                }
                sessionInterface.finalizeSession();
                if (suspendDelayActions) {
                    sessionInterface.setDelayActions(true);
                }

                if (remove) {
                    thread2SessionInterfaceMap.remove(Thread.currentThread());
                }
            }
        }
    }

    public void flush() throws DBException {

        if (thread2SessionInterfaceMap.containsKey(Thread.currentThread())) {

            DBSessionInterface sessionInterface = (DBSessionInterface) thread2SessionInterfaceMap.get(Thread.currentThread());

            sessionInterface.flush();

        }
    }

    private DBSessionInterface getDBSessionInterfaceImplementation() {
        DBSessionInterface sessionInterface = null;

        if (dbImplementationName.equals(HibernateImplementation)) {
            sessionInterface = new HibernateSessionInterface();
        }
        return sessionInterface;
    }

    public Collection getDirtyObs() {
        return getDBSessionInterface().getDirtyObjs();
    }

    public Collection getDirtyClasses() {
        return getDBSessionInterface().getDirtyClasses();
    }

    public void clearDirty() {
        getDBSessionInterface().clearDirty();
    }

    public void startRecording() {
        getDBSessionInterface().startRecording();
    }

    public void stopRecording() {
        getDBSessionInterface().stopRecording();
    }
}
