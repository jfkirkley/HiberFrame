package org.maxml.db.types;

import org.maxml.db.DBException;
import org.maxml.db.DBObjectAccessorFactory;
import org.maxml.db.UpdateHandler;

public class DefaultUpdateHandler implements UpdateHandler {

    public void update(Object target) throws DBException {
        DBObjectAccessorFactory.i().persist(target);
    }

}
