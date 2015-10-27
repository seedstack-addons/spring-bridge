/**
 * Copyright (c) 2013-2015, The SeedStack authors <http://seedstack.org>
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/.
 */
/**
 *
 */
package org.seedstack.seed.spring;

import com.google.inject.ConfigurationException;
import com.google.inject.Injector;
import com.google.inject.Key;
import com.google.inject.name.Names;
import org.junit.Test;
import org.seedstack.seed.it.AbstractSeedIT;
import org.seedstack.seed.spring.api.WithApplicationContexts;
import org.seedstack.seed.spring.fixtures.Service;

import javax.inject.Inject;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.fail;

/**
 * @author adrien.lauer@mpsa.com
 */
@WithApplicationContexts("META-INF/spring/first-context.xml")
public class OnlyOneSpringContextIT extends AbstractSeedIT {
    @Inject
    Injector injector;

    @Test
    public void specified_context_should_be_loaded() {
        assertThat(injector.getInstance(Key.get(Service.class, Names.named("serviceTest1")))).isNotNull();

        try {
            injector.getInstance(Key.get(Service.class, Names.named("serviceTest2")));
        } catch(ConfigurationException e) {
            return;
        }

        fail("should have failed with a CreationException");
    }
}
