/**
 * Copyright (c) 2013-2016, The SeedStack authors <http://seedstack.org>
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/.
 */
package org.seedstack.spring.internal;

import com.google.inject.MembersInjector;
import org.seedstack.seed.Application;
import org.seedstack.seed.SeedException;
import org.seedstack.seed.core.internal.transaction.TransactionalProxy;

import javax.persistence.EntityManager;
import java.lang.reflect.Field;

class SpringEntityManagerMembersInjector<T> implements MembersInjector<T> {
    private final Field field;
    private final Application application;
    private final Class<?> currentClass;

    SpringEntityManagerMembersInjector(Field field, Application application, Class<?> currentClass) {
        this.field = field;
        this.application = application;
        this.currentClass = currentClass;

    }

    @Override
    public void injectMembers(T instance) {
        try {
            this.field.setAccessible(true);
            field.set(instance, TransactionalProxy.create(EntityManager.class, new SpringEntityManagerLink(application, currentClass)));
        } catch (IllegalAccessException e) {
            throw SeedException.wrap(e, SpringErrorCode.NO_SPRING_ENTITYMANAGER).put("class", currentClass);
        }
    }

}