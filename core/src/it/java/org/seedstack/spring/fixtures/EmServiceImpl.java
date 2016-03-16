/**
 * Copyright (c) 2013-2016, The SeedStack authors <http://seedstack.org>
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/.
 */
package org.seedstack.spring.fixtures;

import org.assertj.core.api.Assertions;
import org.seedstack.spring.model.Contact;
import org.seedstack.spring.model.User;
import org.springframework.transaction.annotation.Transactional;

public class EmServiceImpl implements EmService{

	private UserService userService;
	
	private ContactService contactService;

	@Transactional("contact")
	public void testContactTransaction() {
		try{			
			Contact contact = new Contact("firstName", "lastName", "email");
			Assertions.assertThat(contactService).isNotNull();
			contactService.getEntityManager().persist(contact);
		}catch(Exception e){
			Assertions.fail(e.getMessage(), e);
		}		
	}

	@Transactional("user")
	public void testUserTransaction() {
		try{			
			User user = new User("firstName", "lastName", "email");
			Assertions.assertThat(userService).isNotNull();
			userService.getEntityManager().persist(user);
		}catch(Exception e){
			Assertions.fail(e.getMessage(), e);
		}		
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
