/*
 * Copyright © 2013-2020, The SeedStack authors <http://seedstack.org>
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/.
 */
package org.seedstack.spring.internal;

import java.util.Map;
import javax.persistence.EntityManagerFactory;
import org.assertj.core.api.Assertions;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.runners.MockitoJUnitRunner;
import org.powermock.reflect.Whitebox;
import org.seedstack.seed.Application;
import org.seedstack.seed.ClassConfiguration;
import org.seedstack.seed.SeedException;

@RunWith(MockitoJUnitRunner.class)
public class SpringEntityManagerLinkTest {

    private SpringEntityManagerLink springEntityManagerLink;

    @Mock
    private Application application;

    @Mock
    private ClassConfiguration<SpringEntityManagerLinkTest> classConfiguration;

    @Mock
    private EntityManagerFactory entityManagerFactory;

    @Before
    public void up() {
        springEntityManagerLink = new SpringEntityManagerLink(application, this.getClass());
    }

    @Test
    public void no_entityManagerfactory_found() {
        try {
            Map<String, EntityManagerFactory> map = new java.util.HashMap<>();
            Whitebox.invokeMethod(springEntityManagerLink, "getEntityManager", map);
        } catch (Exception e) {
            Assertions.assertThat(e.getMessage()).isEqualTo(SeedException.createNew(SpringErrorCode.NO_SPRING_ENTITYMANAGER).getMessage());
        }
    }

    @Test
    public void two_entityManagerfactory_found() {
        try {
            Map<String, EntityManagerFactory> map = new java.util.HashMap<>();
            map.put("1", entityManagerFactory);
            map.put("2", entityManagerFactory);

            Mockito.when(application.getConfiguration(SpringEntityManagerLinkTest.class)).thenReturn(classConfiguration);
            Mockito.when(classConfiguration.get(SpringEntityManagerLink.JPA_UNIT_PROPERTY)).thenReturn(null);
            Whitebox.invokeMethod(springEntityManagerLink, "getEntityManager", map);
        } catch (Exception e) {
            Assertions.assertThat(e.getMessage()).isEqualTo(SeedException.createNew(SpringErrorCode.AMBIGUOUS_SPRING_ENTITYMANAGER).getMessage());
        }
    }

}
