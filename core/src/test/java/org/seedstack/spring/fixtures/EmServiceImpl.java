/**
 * Copyright (c) 2013-2016, The SeedStack authors <http://seedstack.org>
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/.
 */
package org.seedstack.spring.fixtures;

import org.assertj.core.api.Assertions;
import org.seedstack.spring.fixtures.model.Contact;
import org.seedstack.spring.fixtures.model.User;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

public class EmServiceImpl implements EmService {
    private UserService userService;

    private ContactService contactService;

    @Override
    public void testContactTransaction() {
        Contact contact = new Contact("firstName", "lastName", "email");
        Assertions.assertThat(contactService).isNotNull();
        contactService.getEntityManager().persist(contact);
    }

    @Transactional(value = "user", propagation = Propagation.REQUIRES_NEW)
    @Override
    public void nestedUserTransaction() {
        User user = new User("otherFirstName", "otherLastName", "otherEmail");
        userService.getEntityManager().persist(user);
    }

    @Transactional("user")
    @Override
    public void testUserTransaction() {
        User user = new User("firstName", "lastName", "email");
        Assertions.assertThat(userService).isNotNull();
        userService.getEntityManager().persist(user);
    }

    public UserService getUserService() {
        return userService;
    }

    public void setUserService(UserService userService) {
        this.userService = userService;
    }

    public ContactService getContactService() {
        return contactService;
    }

    public void setContactService(ContactService contactService) {
        this.contactService = contactService;
    }


}
