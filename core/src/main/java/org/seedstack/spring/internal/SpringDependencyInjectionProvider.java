/*
 * Copyright © 2013-2020, The SeedStack authors <http://seedstack.org>
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/.
 */
package org.seedstack.spring.internal;


import com.google.inject.Module;
import io.nuun.kernel.api.di.UnitModule;
import io.nuun.kernel.api.plugin.PluginException;
import io.nuun.kernel.core.internal.injection.ModuleEmbedded;
import io.nuun.kernel.spi.DependencyInjectionProvider;
import org.springframework.beans.factory.config.ConfigurableListableBeanFactory;
import org.springframework.context.ConfigurableApplicationContext;

class SpringDependencyInjectionProvider implements DependencyInjectionProvider {
    @Override
    public boolean canHandle(Class<?> injectionDefinition) {
        return ConfigurableListableBeanFactory.class.isAssignableFrom(injectionDefinition) || ConfigurableApplicationContext.class.isAssignableFrom(injectionDefinition);
    }

    @Override
    public UnitModule convert(Object injectionDefinition) {
        return ModuleEmbedded.wrap(buildModuleFromSpringContext(injectionDefinition));
    }

    @Override
    public Object kernelDIProvider() {
        return null;
    }

    static Module buildModuleFromSpringContext(Object injectionDefinition) {
        if (injectionDefinition instanceof ConfigurableListableBeanFactory) {
            return new SpringModule((ConfigurableListableBeanFactory) injectionDefinition);
        } else if (injectionDefinition instanceof ConfigurableApplicationContext) {
            return new SpringModule(((ConfigurableApplicationContext) injectionDefinition).getBeanFactory());
        } else {
            throw new PluginException("Only ConfigurableListableBeanFactory or ConfigurableApplicationContext types are handled");
        }
    }
}