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

import org.seedstack.seed.transaction.spi.TransactionalLink;
import org.springframework.transaction.TransactionStatus;

import java.util.ArrayDeque;
import java.util.Deque;

/**
 * SpringTransactionStatusLink
 */
class SpringTransactionStatusLink implements TransactionalLink<TransactionStatus> {

    private final ThreadLocal<Deque<TransactionStatus>> transactionStatusThreadLocal;

    SpringTransactionStatusLink() {
        transactionStatusThreadLocal = new ThreadLocal<Deque<TransactionStatus>>() {
            @Override
            protected Deque<TransactionStatus> initialValue() {
                return new ArrayDeque<TransactionStatus>();
            }
        };
    }


    @Override
    public TransactionStatus get() {
        TransactionStatus peek = transactionStatusThreadLocal.get().peek();
        if (peek == null) {
            throw new IllegalStateException("Attempt to get a Spring TransactionStatus without a transaction");
        }

        return peek;
    }


    void push(TransactionStatus transactionStatus) {
        transactionStatusThreadLocal.get().push(transactionStatus);
    }

    TransactionStatus pop() {
        return transactionStatusThreadLocal.get().pop();
    }

    TransactionStatus getCurrentTransaction() {
        return transactionStatusThreadLocal.get().peek();
    }
}
