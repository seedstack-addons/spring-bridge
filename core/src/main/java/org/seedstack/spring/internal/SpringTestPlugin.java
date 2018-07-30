/*
 * Copyright © 2013-2018, The SeedStack authors <http://seedstack.org>
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/.
 */
package org.seedstack.spring.internal;

import com.google.common.collect.Maps;
import java.util.Map;
import org.seedstack.seed.testing.spi.TestContext;
import org.seedstack.seed.testing.spi.TestPlugin;
import org.seedstack.spring.WithApplicationContexts;

/**
 * This IT plugin enables to specify explicitly the spring contexts used for a test.
 */
public class SpringTestPlugin implements TestPlugin {
    @Override
    public boolean enabled(TestContext testContext) {
        return true;
    }

    @Override
    public Map<String, String> configurationProperties(TestContext testContext) {
        Map<String, String> defaultConfiguration = Maps.newHashMap();
        WithApplicationContexts annotation = getWithApplicationContexts(testContext);
        if (annotation != null) {
            defaultConfiguration.put("spring.autodetect", "false");
            defaultConfiguration.put("spring.contexts", String.join(",", annotation.value()));
        }
        return defaultConfiguration;
    }

    private WithApplicationContexts getWithApplicationContexts(TestContext testContext) {
        return testContext.testMethod()
                .map(m -> m.getAnnotation(WithApplicationContexts.class))
                .orElse(testContext.testClass().getAnnotation(WithApplicationContexts.class));
    }
}
