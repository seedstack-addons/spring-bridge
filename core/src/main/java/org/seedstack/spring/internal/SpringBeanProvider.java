/*
 * Copyright © 2013-2020, The SeedStack authors <http://seedstack.org>
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/.
 */
package org.seedstack.spring.internal;

import org.springframework.beans.factory.BeanFactory;

import javax.inject.Provider;

import static com.google.common.base.Preconditions.checkNotNull;

class SpringBeanProvider<T> implements Provider<T> {
    private final BeanFactory beanFactory;
    private final Class<T> type;
    private final String name;

    SpringBeanProvider(Class<T> type, String name, BeanFactory beanFactory) {
        this.type = checkNotNull(type, "type");
        this.name = checkNotNull(name, "name");
        this.beanFactory = beanFactory;
    }

    @Override
    public T get() {
        return type.cast(beanFactory.getBean(name));
    }
}