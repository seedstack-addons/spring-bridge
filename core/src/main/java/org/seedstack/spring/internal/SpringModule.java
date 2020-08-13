/*
 * Copyright © 2013-2020, The SeedStack authors <http://seedstack.org>
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/.
 */
package org.seedstack.spring.internal;

import com.google.inject.AbstractModule;
import com.google.inject.Key;
import com.google.inject.name.Names;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.BeanFactory;
import org.springframework.beans.factory.FactoryBean;
import org.springframework.beans.factory.config.BeanDefinition;
import org.springframework.beans.factory.config.ConfigurableListableBeanFactory;
import org.springframework.transaction.support.AbstractPlatformTransactionManager;

import java.util.HashMap;
import java.util.Map;

class SpringModule extends AbstractModule {
    private static final Logger LOGGER = LoggerFactory.getLogger(SpringModule.class);
    private final ConfigurableListableBeanFactory beanFactory;
    private final Map<Class<?>, Map<String, SpringBeanDefinition>> beanDefinitions;

    SpringModule(ConfigurableListableBeanFactory beanFactory) {
        this.beanFactory = beanFactory;
        this.beanDefinitions = new HashMap<>();
    }

    @Override
    protected void configure() {
        requestStaticInjection(SeedInstanceFactoryBean.class);
        bindFromApplicationContext();
        bindTransactionHandlers();
    }

    private void bindTransactionHandlers() {
        String[] beanNamesForTransactionManager = beanFactory.getBeanNamesForType(AbstractPlatformTransactionManager.class);
        for (String beanNameForTransactionManager : beanNamesForTransactionManager) {
            SpringTransactionStatusLink transactionStatusLink = new SpringTransactionStatusLink();
            SpringTransactionHandler transactionHandler = new SpringTransactionHandler(transactionStatusLink, beanNameForTransactionManager);
            requestInjection(transactionHandler);
            bind(Key.get(SpringTransactionHandler.class, Names.named(beanNameForTransactionManager))).toInstance(transactionHandler);
        }
    }

    private void bindFromApplicationContext() {
        boolean debugEnabled = LOGGER.isDebugEnabled();

        ConfigurableListableBeanFactory currentBeanFactory = this.beanFactory;
        do {
            for (String beanName : currentBeanFactory.getBeanDefinitionNames()) {
                BeanDefinition beanDefinition = currentBeanFactory.getMergedBeanDefinition(beanName);
                if (!beanDefinition.isAbstract()) {
                    Class<?> beanClass;
                    try {
                        beanClass = Class.forName(beanDefinition.getBeanClassName());
                    } catch (ClassNotFoundException e) {
                        LOGGER.warn("Cannot bind spring bean " + beanName + " because its class " + beanDefinition.getBeanClassName() + " failed to load", e);
                        continue;
                    }

                    // FactoryBean special case: retrieve it and query for the object type it creates
                    if (FactoryBean.class.isAssignableFrom(beanClass)) {
                        beanClass = ((FactoryBean<?>) currentBeanFactory.getBean('&' + beanName)).getObjectType();
                        if (beanClass == null) {
                            LOGGER.warn("Cannot bind spring bean " + beanName + " because its factory bean cannot determine its class");
                            continue;
                        }
                    }

                    SpringBeanDefinition springBeanDefinition = new SpringBeanDefinition(beanName, currentBeanFactory);

                    // Adding bean with its base type
                    addBeanDefinition(beanClass, springBeanDefinition);

                    // Adding bean with its parent type if enabled
                    Class<?> parentClass = beanClass.getSuperclass();
                    if (parentClass != null && parentClass != Object.class) {
                        addBeanDefinition(parentClass, springBeanDefinition);
                    }

                    // Adding bean with its immediate interfaces if enabled
                    for (Class<?> i : beanClass.getInterfaces()) {
                        addBeanDefinition(i, springBeanDefinition);
                    }
                }
            }
            BeanFactory factory = currentBeanFactory.getParentBeanFactory();
            if (factory != null) {
                if (factory instanceof ConfigurableListableBeanFactory) {
                    currentBeanFactory = (ConfigurableListableBeanFactory) factory;
                } else {
                    LOGGER.info("Cannot go up further in the bean factory hierarchy, parent bean factory doesn't implement ConfigurableListableBeanFactory");
                    currentBeanFactory = null;
                }
            } else {
                currentBeanFactory = null;
            }
        } while (currentBeanFactory != null);

        for (Map.Entry<Class<?>, Map<String, SpringBeanDefinition>> entry : this.beanDefinitions.entrySet()) {
            Class<?> type = entry.getKey();
            Map<String, SpringBeanDefinition> definitions = entry.getValue();

            // Bind by name for each bean of this type and by type if there is no ambiguity
            bindBean(debugEnabled, type, definitions);
        }
    }

    private <T> void bindBean(boolean debugEnabled, Class<T> type, Map<String, SpringBeanDefinition> definitions) {
        for (SpringBeanDefinition candidate : definitions.values()) {
            if (debugEnabled) {
                LOGGER.info("Binding spring bean " + candidate.getName() + " by name and type " + type.getCanonicalName());
            }

            bind(type)
                    .annotatedWith(Names.named(candidate.getName()))
                    .toProvider(new SpringBeanProvider<>(type, candidate.getName(), candidate.getBeanFactory()));
        }
    }

    private void addBeanDefinition(Class<?> beanClass, SpringBeanDefinition springBeanDefinition) {
        Map<String, SpringBeanDefinition> beansOfType = this.beanDefinitions.computeIfAbsent(beanClass, k -> new HashMap<>());

        // If there are overriding beans, the first encountered bean wins (the lowest in the context hierarchy)
        if (!beansOfType.containsKey(springBeanDefinition.getName())) {
            beansOfType.put(springBeanDefinition.getName(), springBeanDefinition);
        }
    }

    static private class SpringBeanDefinition {
        private final String name;
        private final ConfigurableListableBeanFactory beanFactory;

        SpringBeanDefinition(String name, ConfigurableListableBeanFactory beanFactory) {
            this.name = name;
            this.beanFactory = beanFactory;
        }

        String getName() {
            return name;
        }

        ConfigurableListableBeanFactory getBeanFactory() {
            return beanFactory;
        }
    }
}
