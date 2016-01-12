/**
 * Copyright (c) 2013-2015, The SeedStack authors <http://seedstack.org>
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/.
 */
package org.seedstack.spring.batch.sample.listeners;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.batch.core.listener.ItemListenerSupport;
import org.springframework.stereotype.Component;

@SuppressWarnings("rawtypes")
@Component("failureLoggerListener")
public class ContactFailureLoggerListener extends ItemListenerSupport {
	private static final Logger logger = LoggerFactory.getLogger(ContactFailureLoggerListener.class);

	public void onReadError(final Exception ex) {
		logger.error("Encountered error on read.", ex);
	}

	public void onWriteError(final Exception ex, final Object item) {
		logger.error("Encountered error on write.", ex);
	}
}
