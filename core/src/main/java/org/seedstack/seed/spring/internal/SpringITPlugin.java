/**
 * Copyright (c) 2013-2015, The SeedStack authors <http://seedstack.org>
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/.
 */
package org.seedstack.seed.spring.internal;

import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import org.apache.commons.lang.StringUtils;
import org.junit.rules.MethodRule;
import org.junit.rules.TestRule;
import org.junit.runners.model.FrameworkMethod;
import org.junit.runners.model.TestClass;
import org.seedstack.seed.it.spi.ITKernelMode;
import org.seedstack.seed.it.spi.ITRunnerPlugin;
import org.seedstack.seed.spring.api.WithApplicationContexts;

import java.util.List;
import java.util.Map;

/**
 * This IT plugin enables to specify explicitly the spring contexts used for a test.
 *
 * @author adrien.lauer@mpsa.com
 */
public class SpringITPlugin implements ITRunnerPlugin {
    @Override
    public List<Class<? extends TestRule>> provideClassRulesToApply(TestClass testClass) {
        return Lists.newArrayList();
    }

    @Override
    public List<Class<? extends TestRule>> provideTestRulesToApply(TestClass testClass, Object target) {
        return Lists.newArrayList();
    }

    @Override
    public List<Class<? extends MethodRule>> provideMethodRulesToApply(TestClass testClass, Object target) {
        return Lists.newArrayList();
    }

    @Override
    public Map<String, String> provideDefaultConfiguration(TestClass testClass, FrameworkMethod frameworkMethod) {
        Map<String, String> defaultConfiguration = Maps.newHashMap();
        WithApplicationContexts annotation;

        if (frameworkMethod != null) {
            annotation = frameworkMethod.getAnnotation(WithApplicationContexts.class);
        } else {
            annotation = testClass.getJavaClass().getAnnotation(WithApplicationContexts.class);
        }

        if (annotation != null) {
            defaultConfiguration.put(SpringPlugin.SPRING_PLUGIN_CONFIGURATION_PREFIX + ".autodetect", "false");
            defaultConfiguration.put(SpringPlugin.SPRING_PLUGIN_CONFIGURATION_PREFIX + ".contexts", StringUtils.join(annotation.value(), ","));
        }

        return defaultConfiguration;
    }

    @Override
    public ITKernelMode kernelMode(TestClass testClass) {
        return ITKernelMode.ANY;
    }
}
