/**
 * Copyright (c) 2013-2015, The SeedStack authors <http://seedstack.org>
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/.
 */
package org.seedstack.seed.springbatch.sample.service;

import javax.inject.Inject;

import org.seedstack.seed.springbatch.sample.domain.User;
import org.seedstack.seed.springbatch.sample.domain.UserDao;


public class UserServiceImpl implements UserService {

	@Inject
	private UserDao userDao;

	@Override
	public User save(User entity) {
		return userDao.save(entity);
	}

}
