/*
 * Copyright © 2013-2018, The SeedStack authors <http://seedstack.org>
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/.
 */
package org.seedstack.spring.batch.sample.mapper;

import org.seedstack.spring.batch.sample.domain.Contact;
import org.springframework.batch.item.file.mapping.FieldSetMapper;
import org.springframework.batch.item.file.transform.FieldSet;
import org.springframework.stereotype.Component;
import org.springframework.validation.BindException;

@Component("contactFieldSetMapper")
public class ContactFieldSetMapper implements FieldSetMapper<Contact> {

	private static final int FIRST_NAME = 0;
	private static final int LAST_NAME = 1;
	private static final int EMAIL = 2;

	@Override
	public Contact mapFieldSet(final FieldSet fieldSet) throws BindException {
		if (fieldSet == null) {
			throw new IllegalStateException("Exception in Field Set Mapper. Field Set Mapper must not be null...");
		}

		final Contact contact = new Contact();
		contact.setFirstName(fieldSet.readString(FIRST_NAME));
		contact.setLastName(fieldSet.readString(LAST_NAME));
		contact.setEmail(fieldSet.readString(EMAIL));

		return contact;
	}

}
