/*
 * Copyright © 2013-2020, The SeedStack authors <http://seedstack.org>
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/.
 */
package org.seedstack.spring.internal;

import org.springframework.beans.factory.support.BeanDefinitionBuilder;
import org.springframework.beans.factory.xml.AbstractSingleBeanDefinitionParser;
import org.springframework.beans.factory.xml.NamespaceHandlerSupport;
import org.springframework.util.StringUtils;
import org.w3c.dom.Element;

/**
 * This class handles the SeedStack Spring namespace.
 */
public class SeedNamespaceHandler extends NamespaceHandlerSupport {
    private static final String INSTANCE_LOCAL_NAME = "instance";
    private static final String CONFIGURATION_LOCAL_NAME = "configuration";
    private static final String DATA_SOURCE_LOCAL_NAME = "datasource";

    @Override
    public void init() {
        registerBeanDefinitionParser(INSTANCE_LOCAL_NAME, new SeedInstanceDefinitionParser());
        registerBeanDefinitionParser(CONFIGURATION_LOCAL_NAME, new SeedConfigurationDefinitionParser());
        registerBeanDefinitionParser(DATA_SOURCE_LOCAL_NAME, new SeedDataSourceDefinitionParser());
    }

    private static class SeedInstanceDefinitionParser extends AbstractSingleBeanDefinitionParser {
        @Override
        protected Class<?> getBeanClass(Element element) {
            return SeedInstanceFactoryBean.class;
        }

        @Override
        protected void doParse(Element element, BeanDefinitionBuilder builder) {
            builder.getRawBeanDefinition().setBeanClass(SeedInstanceFactoryBean.class);
            builder.getRawBeanDefinition().setAttribute("id", element.getAttribute("id"));

            String className = element.getAttribute("class");
            if (StringUtils.hasText(className)) {
                builder.addPropertyValue("classname", className);
            }

            String qualifier = element.getAttribute("qualifier");
            if (StringUtils.hasText(qualifier)) {
                builder.addPropertyValue("qualifier", qualifier);
            }

            String proxy = element.getAttribute("proxy");
            if (StringUtils.hasText(proxy)) {
                builder.addPropertyValue("proxy", Boolean.parseBoolean(proxy));
            }
        }
    }

    private static class SeedConfigurationDefinitionParser extends AbstractSingleBeanDefinitionParser {
        @Override
        protected Class<?> getBeanClass(Element element) {
            return SeedConfigurationFactoryBean.class;
        }

        @Override
        protected void doParse(Element element, BeanDefinitionBuilder builder) {
            builder.getRawBeanDefinition().setBeanClass(SeedConfigurationFactoryBean.class);
            builder.getRawBeanDefinition().setAttribute("id", element.getAttribute("id"));

            String key = element.getAttribute("key");
            if (StringUtils.hasText(key)) {
                builder.addPropertyValue("key", key);
            }

            String defaultValue = element.getAttribute("default");
            if (StringUtils.hasText(defaultValue)) {
                builder.addPropertyValue("defaultValue", defaultValue);
            }

            String mandatory = element.getAttribute("mandatory");
            if (StringUtils.hasText(mandatory)) {
                builder.addPropertyValue("mandatory", Boolean.parseBoolean(mandatory));
            }
        }
    }

    private static class SeedDataSourceDefinitionParser extends AbstractSingleBeanDefinitionParser {
        @Override
        protected Class<?> getBeanClass(Element element) {
            return SeedDataSourceFactoryBean.class;
        }

        @Override
        protected void doParse(Element element, BeanDefinitionBuilder builder) {
            builder.getRawBeanDefinition().setBeanClass(SeedDataSourceFactoryBean.class);
            builder.getRawBeanDefinition().setAttribute("id", element.getAttribute("id"));

            String name = element.getAttribute("name");
            if (StringUtils.hasText(name)) {
                builder.addPropertyValue("name", name);
            }
        }
    }
}