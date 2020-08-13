/*
 * Copyright © 2013-2020, The SeedStack authors <http://seedstack.org>
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/.
 */
package org.seedstack.spring.internal;

import org.seedstack.shed.exception.ErrorCode;

enum SpringErrorCode implements ErrorCode {
    AMBIGUOUS_SPRING_ENTITYMANAGER,
    NO_ACTIVE_SPRING_TRANSACTION,
    NO_SPRING_ENTITYMANAGER,
    UNABLE_TO_RESOLVE_JPA_UNIT,
    UNABLE_TO_ACCESS_SPRING_ENTITYMANAGER,
    UNKNOWN_SPRING_ENTITYMANAGER
}
