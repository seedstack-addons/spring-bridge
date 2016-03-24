/**
 * Copyright (c) 2013-2016, The SeedStack authors <http://seedstack.org>
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/.
 */
package org.seedstack.spring.internal;

import com.google.inject.MembersInjector;
import org.seedstack.seed.SeedException;
import org.seedstack.seed.core.spi.configuration.ConfigurationProvider;
import org.seedstack.seed.transaction.spi.TransactionalProxy;

import javax.persistence.EntityManager;
import java.lang.reflect.Field;

class SpringEntityManagerMembersInjector<T> implements MembersInjector<T> {

    private Field field;
    private ConfigurationProvider configurationProvider;
    private Class<?> currentClass;

    SpringEntityManagerMembersInjector(Field field, ConfigurationProvider configurationProvider, Class<?> currentClass) {
        this.field = field;
        this.configurationProvider = configurationProvider;
        this.currentClass = currentClass;

    }

    @Override
    public void injectMembers(T instance) {
        try {
            this.field.setAccessible(true);
            field.set(instance, TransactionalProxy.create(EntityManager.class, new SpringEntityManagerLink(configurationProvider, currentClass)));
        } catch (IllegalAccessException e) {
            throw SeedException.wrap(e, SpringErrorCode.NO_SPRING_ENTITYMANAGER).put("class", currentClass);
        }
    }

}