/*
 * Copyright © 2013-2018, The SeedStack authors <http://seedstack.org>
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/.
 */
package org.seedstack.spring.internal;

import com.google.inject.AbstractModule;
import com.google.inject.matcher.Matchers;
import javax.persistence.EntityManager;
import org.seedstack.seed.Application;
import org.seedstack.seed.core.internal.transaction.TransactionalProxy;

class SpringJpaModule extends AbstractModule {
    private Application application;

    SpringJpaModule(Application application) {
        this.application = application;
    }

    @Override
    protected void configure() {
        bind(EntityManager.class).toInstance(TransactionalProxy.create(EntityManager.class, null));
        bindListener(Matchers.any(), new SpringEntityManagerTypeListener(application));
    }
}
