/**
 * Copyright (c) 2013-2015, The SeedStack authors <http://seedstack.org>
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/.
 */

package org.seedstack.spring.internal;

import org.seedstack.seed.ErrorCode;

enum SpringErrorCode implements ErrorCode{

	UNABLE_TO_INJECT_SPRING_ENTITYMANAGER,
	NO_JPA_UNIT_FOUND,
	MORE_THAN_ONE_UNIT_FOUND
}
