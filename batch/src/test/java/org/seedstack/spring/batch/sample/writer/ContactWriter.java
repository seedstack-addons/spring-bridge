/*
 * Copyright © 2013-2020, The SeedStack authors <http://seedstack.org>
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/.
 */
package org.seedstack.spring.batch.sample.writer;

import java.util.List;
import org.seedstack.spring.batch.sample.domain.Contact;
import org.seedstack.spring.batch.sample.service.ContactService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.batch.item.ItemWriter;
import org.springframework.stereotype.Component;

@Component("contactWriter")
public class ContactWriter implements ItemWriter<Contact> {
    private static final Logger logger = LoggerFactory.getLogger(ContactWriter.class);
    private ContactService contactService;

    @Override
    public void write(final List<? extends Contact> contacts) throws Exception {
        for (Contact contact : contacts) {
            logger.debug("Writing contact information FirstName: {}, LastName: {}, Email: {}",
                    contact.getFirstName(),
                    contact.getLastName(),
                    contact.getEmail());
            contactService.save(contact);
        }
    }

    public ContactService getContactService() {
        return contactService;
    }

    public void setContactService(ContactService contactService) {
        this.contactService = contactService;
    }
}
