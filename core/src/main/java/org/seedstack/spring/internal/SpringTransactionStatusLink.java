/**
 * Copyright (c) 2013-2016, The SeedStack authors <http://seedstack.org>
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/.
 */
/**
 *
 */
package org.seedstack.spring.internal;

import org.seedstack.seed.SeedException;
import org.seedstack.seed.transaction.spi.TransactionalLink;
import org.springframework.transaction.TransactionStatus;

import java.util.ArrayDeque;
import java.util.Deque;

class SpringTransactionStatusLink implements TransactionalLink<TransactionStatus> {
    private final ThreadLocal<Deque<TransactionStatus>> perThreadObjectContainer = new ThreadLocal<Deque<TransactionStatus>>() {
        @Override
        protected Deque<TransactionStatus> initialValue() {
            return new ArrayDeque<TransactionStatus>();
        }
    };

    @Override
    public TransactionStatus get() {
        TransactionStatus peek = perThreadObjectContainer.get().peek();
        if (peek == null) {
            throw SeedException.createNew(SpringErrorCode.NO_ACTIVE_SPRING_TRANSACTION);
        }

        return peek;
    }


    TransactionStatus getCurrentTransaction() {
        return perThreadObjectContainer.get().peek();
    }

    void push(TransactionStatus transactionStatus) {
        perThreadObjectContainer.get().push(transactionStatus);
    }

    TransactionStatus pop() {
        Deque<TransactionStatus> transactionStatuses = perThreadObjectContainer.get();
        TransactionStatus transactionStatus = transactionStatuses.pop();
        if (transactionStatuses.isEmpty()) {
            perThreadObjectContainer.remove();
        }
        return transactionStatus;
    }
}
