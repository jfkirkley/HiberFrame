package org.maxml.util;

import java.util.Collection;

import org.maxml.reflect.PropertySource;

public class RegisterdFactoryPropertySource extends PropertySource {

    private RegisteredFactory registeredFactory;

    public RegisterdFactoryPropertySource(RegisteredFactory registeredFactory) {
        this.registeredFactory = registeredFactory;
    }
    @Override
    public Collection getCollection(String descriptor) {
        return super.getCollection(registeredFactory.getRegisteredValue(descriptor));
    }

}
