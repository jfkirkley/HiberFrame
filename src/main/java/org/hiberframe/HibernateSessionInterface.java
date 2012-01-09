package org.hiberframe;

import java.util.Collection;
import java.util.HashSet;
import java.util.Stack;

import org.hibernate.FlushMode;
import org.hibernate.HibernateException;
import org.hibernate.Session;
import org.hibernate.Transaction;

import org.maxml.db.DBException;
import org.maxml.db.DBSessionInterface;

public class HibernateSessionInterface implements DBSessionInterface {

    static int          no           = 0;
    private Session     session      = null;
    private Transaction transaction  = null;
    private boolean     delayActions = true;
    private Stack       dirtyObjsCollectionStack;
    private Stack       dirtyClassesCollectionStack;

    private Collection  dirtyObjs;
    private Collection  dirtyClasses;

    public HibernateSessionInterface() {
        this.dirtyObjs = new HashSet();
        this.dirtyClasses = new HashSet();
        dirtyClassesCollectionStack = new Stack();
        dirtyObjsCollectionStack = new Stack();
    }

    public HibernateSessionInterface(boolean delayActions) {
        this.delayActions = delayActions;
    }

    public void closeSession() throws DBException {
        try {
            // System.out.println("num open: " + (--no));

            getSession().close();
            transaction = null;
            session = null;

        } catch (HibernateException e) {
            throw new DBException(e);
        }
    }

    public void openSession() throws DBException {
        getSession();
    }

    public void beginTransaction() throws DBException {
        try {
            getTransaction();
        } catch (HibernateException e) {
            throw new DBException(e);
        }
    }

    public void commitTransaction() throws DBException {
        try {

            if (!delayActions) {
                getTransaction().commit();
                transaction = null;
            }
        } catch (HibernateException e) {
            throw new DBException(e);
        }
    }

    public void rollbackTransaction() throws DBException {
        try {
            getTransaction().rollback();
        } catch (HibernateException e) {
            throw new DBException(e);
        }
    }

    public boolean transactionWasCommited() {
        return (transaction != null) && getTransaction().wasCommitted();
    }

    public boolean transactionWasRolledBack() {
        return (transaction != null) && getTransaction().wasRolledBack();
    }

    public boolean transactionIsActive() {
        return (transaction != null) && getTransaction().isActive();
    }

    public boolean sessionOpen() {
        return session==null?false:session.isOpen();
    }

    public void finalizeSession() throws DBException {

        if (!isDelayActions() && transaction != null && !transactionWasCommited() && !transactionWasRolledBack()) {
            // transaction should be commited by now, we rollback
            rollbackTransaction();
        }
        if (!isDelayActions() && session != null && sessionOpen()) {
            closeSession();
        }
    }

    public void flush() {
        if (session != null) {
            session.flush();
        }
    }

    public Session getSession() {
        if (session == null) {
            // System.out.println("num open: " + (++no));
            session = HibUtils.getSession();
            session.setFlushMode(FlushMode.COMMIT);
        }
        return session;
    }

    private Transaction getTransaction() {
        if (transaction == null) {
            transaction = getSession().beginTransaction();
            clearDirty();
        }
        return transaction;
    }

    public boolean isDelayActions() {
        return delayActions;
    }

    public void setDelayActions(boolean delayActions) {
        this.delayActions = delayActions;
    }

    // public void checkEntityStatus() {
    //
    // final EventSource source = (EventSource) session;
    //
    // final Map map = source.getPersistenceContext().getEntityEntries();
    //
    // for (Iterator iter = map.entrySet().iterator(); iter.hasNext();) {
    // Map.Entry me = (Map.Entry) iter.next();
    //
    // EntityEntry entry = (EntityEntry) me.getValue();
    // Status status = entry.getStatus();
    // 
    // if(status != Status.MANAGED ) {
    // System.out.println( ">>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>> =--- >>> status: "
    // + entry.getEntityName() + " -> " + status );
    // }
    // // if (status == Status.LOADING ) {
    // //
    // // } else if( status == Status.GONE) {
    // // } else if( status == Status.GONE) {
    // // } else if( status == Status.GONE) {
    // // } else if( status == Status.GONE) {
    // //
    // // }
    // }
    // }

    public void addDirtyObj(Object id) {
        dirtyObjs.add(id);
    }

    public Collection getDirtyObjs() {
        return dirtyObjs;
    }

    public void addDirtyClass(Object typeId) {
        dirtyClasses.add(typeId);
    }

    public Collection getDirtyClasses() {
        return dirtyClasses;
    }

    public void clearDirty() {
        dirtyClasses.clear();
        dirtyObjs.clear();
        dirtyClassesCollectionStack.clear();
        dirtyObjsCollectionStack.clear();
    }

    public void startRecording() {
        if (!dirtyClassesCollectionStack.empty()) {
            if (dirtyClasses != null) {
                dirtyClassesCollectionStack.push(dirtyClasses);
                dirtyClasses = new HashSet();
            }
        }
        if (!dirtyObjsCollectionStack.empty()) {
            if (dirtyObjs != null) {
                dirtyObjsCollectionStack.push(dirtyObjs);
                dirtyObjs = new HashSet();
            }
        }
    }

    public void stopRecording() {
        if (!dirtyClassesCollectionStack.empty()) {
            HashSet oldDirtyClasses = (HashSet) dirtyClasses;
            dirtyClasses = (HashSet) dirtyClassesCollectionStack.pop();
            dirtyClasses.addAll(oldDirtyClasses);
        }
        if (!dirtyObjsCollectionStack.empty()) {
            HashSet oldDirtyObjs = (HashSet) dirtyObjs;
            dirtyObjs = (HashSet) dirtyObjsCollectionStack.pop();
            dirtyObjs.addAll(oldDirtyObjs);
        }
    }

}
