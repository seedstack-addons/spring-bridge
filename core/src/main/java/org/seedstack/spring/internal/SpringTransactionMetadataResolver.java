/*
 * Copyright © 2013-2020, The SeedStack authors <http://seedstack.org>
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/.
 */
package org.seedstack.spring.internal;

import org.aopalliance.intercept.MethodInvocation;
import org.seedstack.seed.transaction.spi.TransactionMetadata;
import org.seedstack.seed.transaction.spi.TransactionMetadataResolver;
import org.seedstack.spring.SpringTransactionManager;

import java.util.Optional;

/**
 * This {@link org.seedstack.seed.transaction.spi.TransactionMetadataResolver} resolves metadata for transactions marked
 * with {@link SpringTransactionManager}.
 */
class SpringTransactionMetadataResolver implements TransactionMetadataResolver {
    @Override
    public TransactionMetadata resolve(MethodInvocation methodInvocation, TransactionMetadata defaults) {
        Optional<SpringTransactionManager> springTransactionManager = SpringTransactionManagerResolver.INSTANCE.apply(methodInvocation.getMethod());

        if (springTransactionManager.isPresent()) {
            TransactionMetadata result = new TransactionMetadata();
            result.setHandler(SpringTransactionHandler.class);
            result.setResource(springTransactionManager.get().value());
            return result;
        }

        return null;
    }
}