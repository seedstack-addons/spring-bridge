/**
 * Copyright (c) 2013-2016, The SeedStack authors <http://seedstack.org>
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/.
 */
package org.seedstack.spring.internal;

import io.nuun.kernel.api.plugin.InitState;
import io.nuun.kernel.api.plugin.context.InitContext;
import io.nuun.kernel.api.plugin.request.ClasspathScanRequest;
import io.nuun.kernel.spi.DependencyInjectionProvider;
import org.seedstack.seed.core.internal.AbstractSeedPlugin;
import org.seedstack.spring.SpringConfig;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.support.ClassPathXmlApplicationContext;

import java.util.Collection;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

/**
 * This plugin provides Spring integration, converting any Spring bean defined in configured contexts in a named
 * Guice binding.
 */
public class SpringPlugin extends AbstractSeedPlugin {
    private static final Logger LOGGER = LoggerFactory.getLogger(SpringPlugin.class);
    private static final String APPLICATION_CONTEXT_REGEX = ".*-context.xml$";
    private final Set<String> applicationContextsPaths = new HashSet<>();
    private ClassPathXmlApplicationContext globalApplicationContext;
    private SpringConfig springConfig;

    @Override
    public String name() {
        return "spring";
    }

    @Override
    public String pluginPackageRoot() {
        return "META-INF.spring";
    }

    @Override
    public Collection<ClasspathScanRequest> classpathScanRequests() {
        return classpathScanRequestBuilder().resourcesRegex(APPLICATION_CONTEXT_REGEX).build();
    }

    @Override
    public InitState initialize(InitContext initContext) {
        SeedConfigurationFactoryBean.configuration = getConfiguration();
        springConfig = getConfiguration(SpringConfig.class);

        Map<String, Collection<String>> scannedApplicationContexts = initContext.mapResourcesByRegex();
        for (String applicationContextPath : scannedApplicationContexts.get(APPLICATION_CONTEXT_REGEX)) {
            if (springConfig.isAutodetect() && applicationContextPath.startsWith("META-INF/spring")) {
                applicationContextsPaths.add(applicationContextPath);
                LOGGER.trace("Autodetected spring context at " + applicationContextPath);
            }
        }

        List<String> explicitContexts = springConfig.getContexts();
        if (!explicitContexts.isEmpty()) {
            for (String explicitContext : explicitContexts) {
                applicationContextsPaths.add(explicitContext);
                LOGGER.trace("Configured spring context at " + explicitContext);
            }
        }

        LOGGER.info("Initializing spring context(s) " + applicationContextsPaths);
        globalApplicationContext = new ClassPathXmlApplicationContext(this.applicationContextsPaths.toArray(new String[this.applicationContextsPaths.size()]));
        return InitState.INITIALIZED;
    }

    @Override
    public Object nativeUnitModule() {
        return SpringDependencyInjectionProvider.buildModuleFromSpringContext(globalApplicationContext);
    }

    @Override
    public Object nativeOverridingUnitModule() {
        if (springConfig.isManageJpa()) {
            return new SpringJpaModule(getApplication());
        } else {
            return null;
        }
    }

    @Override
    public DependencyInjectionProvider dependencyInjectionProvider() {
        return new SpringDependencyInjectionProvider();
    }

    @Override
    public void stop() {
        if (globalApplicationContext != null) {
            LOGGER.info("Closing spring context(s) " + applicationContextsPaths);
            globalApplicationContext.close();
        }
    }
}
