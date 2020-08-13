/*
 * Copyright © 2013-2020, The SeedStack authors <http://seedstack.org>
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/.
 */
package org.seedstack.spring;

import javax.inject.Inject;
import javax.inject.Named;
import org.assertj.core.api.Assertions;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.seedstack.seed.testing.junit4.SeedITRunner;
import org.seedstack.spring.fixtures.Service;
import org.seedstack.spring.fixtures.SpringRepository;

@RunWith(SeedITRunner.class)
public class SpringProviderIT {
    @Inject
    @Named("service")
    Service service;

    @Inject
    @Named("springRepository")
    SpringRepository springRepository;

    @Inject
    @Named("configBean")
    String configValue;

    @Test
    public void can_get_spring_bean_by_interface_type() {
        Assertions.assertThat(service).isNotNull();
        Assertions.assertThat(service.getFrom()).isEqualTo("spring");
    }

    @Test
    public void scanned_components_are_detected() {
        Assertions.assertThat(springRepository).isNotNull();
        Assertions.assertThat(springRepository.getId()).isEqualTo(1L);
    }

    @Test
    public void bean_can_be_identified() throws Exception {
        Assertions.assertThat(configValue).isEqualTo("titi");
    }
}
