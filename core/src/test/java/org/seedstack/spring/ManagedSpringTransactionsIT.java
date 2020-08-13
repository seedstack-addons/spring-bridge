/*
 * Copyright © 2013-2020, The SeedStack authors <http://seedstack.org>
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/.
 */
/**
 *
 */

package org.seedstack.spring;

import javax.inject.Inject;
import javax.inject.Named;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.seedstack.seed.testing.junit4.SeedITRunner;
import org.seedstack.spring.fixtures.EmService;
import org.seedstack.spring.fixtures.TransactionalService;

@RunWith(SeedITRunner.class)
@WithApplicationContexts({"META-INF/spring/MultipleTransactionManagerOrm-context.xml",
        "META-INF/spring/MultipleTransactionManagerService-context.xml"})
public class ManagedSpringTransactionsIT {
    @Inject
    @Named("emService")
    private EmService emService;

    @Inject
    @Named("transactionalService")
    private TransactionalService transactionalService;

    @Test
    public void nested_transactions() {
        transactionalService.doSomethingWithNestedTransactions();
    }

    @Test
    public void simple_transaction() {
        emService.testUserTransaction();
    }
}
