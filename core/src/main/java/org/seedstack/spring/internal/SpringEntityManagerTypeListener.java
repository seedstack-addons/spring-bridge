/**
 * Copyright (c) 2013-2016, The SeedStack authors <http://seedstack.org>
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/.
 */
package org.seedstack.spring.internal;

import com.google.inject.TypeLiteral;
import com.google.inject.spi.TypeEncounter;
import com.google.inject.spi.TypeListener;
import org.seedstack.seed.Application;

import javax.inject.Inject;
import javax.persistence.EntityManager;
import java.lang.reflect.Field;

class SpringEntityManagerTypeListener implements TypeListener {
    private Application application;

    SpringEntityManagerTypeListener(Application application) {
        this.application = application;
    }

    @Override
    public <I> void hear(TypeLiteral<I> type, TypeEncounter<I> encounter) {
        for (Class<?> c = type.getRawType(); c != Object.class; c = c.getSuperclass()) {
            for (Field field : c.getDeclaredFields()) {
                if (EntityManager.class.isAssignableFrom(field.getType())
                        && field.getAnnotation(Inject.class) != null) {
                    encounter.register(new SpringEntityManagerMembersInjector<>(field, application, type.getRawType()));
                }
            }
        }
    }

}