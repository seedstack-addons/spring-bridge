/**
 * Copyright (c) 2013-2015, The SeedStack authors <http://seedstack.org>
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/.
 */
package org.seedstack.spring.internal;

import java.lang.reflect.Field;

import javax.inject.Inject;
import javax.persistence.EntityManager;

import org.seedstack.seed.core.spi.configuration.ConfigurationProvider;

import com.google.inject.TypeLiteral;
import com.google.inject.spi.TypeEncounter;
import com.google.inject.spi.TypeListener;

/**
 * 
 * @author redouane.loulou@ext.mpsa.com
 *
 */
public class SpringEntityManagerTypeListener implements TypeListener {

	private ConfigurationProvider configurationProvider;

	public SpringEntityManagerTypeListener(ConfigurationProvider configurationProvider) {
		this.configurationProvider = configurationProvider;
	}

	@Override
	public <I> void hear(TypeLiteral<I> type, TypeEncounter<I> encounter) {
		for (Class<?> c = type.getRawType(); c != Object.class; c = c.getSuperclass()) {
			for (Field field : c.getDeclaredFields()) {
				if (EntityManager.class.isAssignableFrom(field.getType())
						&& field.getAnnotation(Inject.class) != null) {
					encounter.register(new SpringEntityManagerMembersInjector<I>(field, configurationProvider, type.getRawType()));
				}
			}
		}
	}

}