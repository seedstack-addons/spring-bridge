/**
 * Copyright (c) 2013-2015, The SeedStack authors <http://seedstack.org>
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/.
 */
package org.seedstack.spring.internal;

import java.util.Map;

import javax.persistence.EntityManagerFactory;

import org.apache.commons.configuration.Configuration;
import org.assertj.core.api.Assertions;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.runners.MockitoJUnitRunner;
import org.powermock.reflect.Whitebox;
import org.seedstack.seed.SeedException;
import org.seedstack.seed.core.spi.configuration.ConfigurationProvider;

@RunWith(MockitoJUnitRunner.class)
public class SpringEntityManagerLinkTest {

	private SpringEntityManagerLink SpringEntityManagerLink;
	
	@Mock
	private ConfigurationProvider configurationProvider;
	
	@Mock
	private Configuration configuration;
	
	@Mock
	private EntityManagerFactory entityManagerFactory;
	
	@Before
	public void up(){
		SpringEntityManagerLink = new SpringEntityManagerLink(configurationProvider, this.getClass());
	}
	
	@Test
	public void no_entityManagerfactory_found() {
		try {
			Map<String, EntityManagerFactory> map = new java.util.HashMap<String, EntityManagerFactory>();
			Whitebox.invokeMethod(SpringEntityManagerLink, "getEntityManager",map);
		} catch (Exception e) {
			Assertions.assertThat(e.getMessage()).isEqualTo(SeedException.createNew(SpringErrorCode.UNABLE_TO_INJECT_SPRING_ENTITYMANAGER).getMessage());
		}
	}
	
	@Test
	public void two_entityManagerfactory_found() {
		try {
			Map<String, EntityManagerFactory> map = new java.util.HashMap<String, EntityManagerFactory>();
			map.put("1",entityManagerFactory);
			map.put("2",entityManagerFactory);	
			
			Mockito.when(configurationProvider.getConfiguration(this.getClass())).thenReturn(configuration);
			Mockito.when(configuration.getString(SpringEntityManagerLink.JPA_UNIT_PROPERTY)).thenReturn(null);
			Whitebox.invokeMethod(SpringEntityManagerLink, "getEntityManager",map);
		} catch (Exception e) {
			Assertions.assertThat(e.getMessage()).isEqualTo(SeedException.createNew(SpringErrorCode.MORE_THAN_ONE_UNIT_FOUND).getMessage());
		}
	}

}
