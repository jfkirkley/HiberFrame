package org.maxml.db;

import java.util.Collection;

public interface DBSessionInterface {
    

    
    public void closeSession() throws DBException;
    public void openSession() throws DBException;
    
    public void beginTransaction() throws DBException;
    public void commitTransaction() throws DBException;
    public void rollbackTransaction() throws DBException;
    public boolean transactionWasCommited();
    public boolean transactionWasRolledBack();
    public boolean transactionIsActive();

    public boolean sessionOpen();
    public void flush();
    public boolean isDelayActions();
    public void setDelayActions(boolean delayActions);

//    // CRUD ops
//    public void create(Object object) throws DBException;
//    public void update(Object object) throws DBException;
//    public void delete(Object object) throws DBException;

    public void addDirtyObj(Object id);
    public Collection getDirtyObjs();
    public void addDirtyClass(Object typeId);
    public Collection getDirtyClasses();
    
    public void clearDirty();
    
    public void startRecording();
    public void stopRecording();
    
    public void finalizeSession() throws DBException;

}
