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

import org.junit.Test;
import org.seedstack.seed.it.AbstractSeedIT;
import org.seedstack.seed.spring.api.WithApplicationContexts;
import org.seedstack.seed.spring.fixtures.Service;

import javax.inject.Inject;
import javax.inject.Named;

import static org.assertj.core.api.Assertions.assertThat;

/**
 * @author adrien.lauer@mpsa.com
 */
@WithApplicationContexts({"META-INF/spring/first-context.xml", "META-INF/spring/second-context.xml"})
public class AllSpringContextsIT extends AbstractSeedIT {
    @Inject
    @Named("serviceTest1")
    Service service1;

    @Inject
    @Named("serviceTest2")
    Service service2;

    @Test
    public void specified_spring_contexts_should_be_loaded() {
        assertThat(service1).isNotNull();
        assertThat(service1.getFrom()).isEqualTo("spring");
        assertThat(service2).isNotNull();
        assertThat(service2.getFrom()).isEqualTo("spring");
    }
}
