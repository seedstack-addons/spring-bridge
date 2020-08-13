/*
 * Copyright © 2013-2020, The SeedStack authors <http://seedstack.org>
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/.
 */
package org.seedstack.spring.internal;

import org.seedstack.shed.reflect.StandardAnnotationResolver;
import org.seedstack.spring.SpringTransactionManager;

import java.lang.reflect.Method;

class SpringTransactionManagerResolver extends StandardAnnotationResolver<Method, SpringTransactionManager> {
    static SpringTransactionManagerResolver INSTANCE = new SpringTransactionManagerResolver();

    private SpringTransactionManagerResolver() {
        // no external instantiation allowed
    }
}
