/*
 * Copyright © 2013-2018, The SeedStack authors <http://seedstack.org>
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/.
 */
/**
 *
 */

package org.seedstack.spring;

import static org.assertj.core.api.Assertions.assertThat;

import javax.inject.Inject;
import javax.inject.Named;
import org.assertj.core.api.Assertions;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.seedstack.seed.testing.junit4.SeedITRunner;
import org.seedstack.spring.fixtures.Service;

@RunWith(SeedITRunner.class)
@WithApplicationContexts({"META-INF/spring/first-context.xml", "META-INF/spring/second-context.xml"})
public class AllSpringContextsIT {
    @Inject
    @Named("serviceTest1")
    Service service1;

    @Inject
    @Named("serviceTest2")
    Service service2;

    @Test
    public void specified_spring_contexts_should_be_loaded() {
        Assertions.assertThat(service1).isNotNull();
        Assertions.assertThat(service1.getFrom()).isEqualTo("spring");
        Assertions.assertThat(service2).isNotNull();
        Assertions.assertThat(service2.getFrom()).isEqualTo("spring");
    }
}
