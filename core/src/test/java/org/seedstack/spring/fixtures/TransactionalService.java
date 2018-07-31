/*
 * Copyright © 2013-2018, The SeedStack authors <http://seedstack.org>
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/.
 */
package org.seedstack.spring.fixtures;

import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

public class TransactionalService {
    private EmService emService;

    @Transactional(value = "contact", propagation = Propagation.REQUIRES_NEW)
    public void doSomethingWithNestedTransactions() {
        emService.testContactTransaction();
        emService.nestedUserTransaction();
        emService.testContactTransaction();
    }

    public EmService getEmService() {
        return emService;
    }

    public void setEmService(EmService emService) {
        this.emService = emService;
    }
}
