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
package org.seedstack.spring.spi;

import org.seedstack.seed.transaction.spi.ExceptionHandler;
import org.seedstack.seed.transaction.spi.TransactionMetadata;
import org.springframework.transaction.TransactionStatus;

/**
 * Exception handler for Spring transactions.
 *
 * *
 */
public interface SpringExceptionHandler extends ExceptionHandler<TransactionStatus> {

    @Override
    boolean handleException(Exception exception, TransactionMetadata associatedTransactionMetadata, TransactionStatus associatedTransaction);

}
