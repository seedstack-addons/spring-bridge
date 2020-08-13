/*
 * Copyright © 2013-2020, The SeedStack authors <http://seedstack.org>
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/.
 */
package org.seedstack.spring.internal;

import org.seedstack.coffig.Coffig;
import org.springframework.beans.factory.FactoryBean;

import java.util.Optional;

class SeedConfigurationFactoryBean implements FactoryBean<Object> {
    static Coffig configuration;
    private String key;
    private String defaultValue;
    private boolean mandatory = true;

    @Override
    public Object getObject() throws Exception {
        if (key == null) {
            throw new IllegalArgumentException("Property key is required for SeedConfigurationFactoryBean");
        } else {
            Optional<String> value = configuration.getOptional(String.class, key);
            if (value.isPresent()) {
                return value.get();
            } else {
                if (defaultValue == null && mandatory) {
                    throw new IllegalArgumentException("Configuration value " + key + " is mandatory and has no value nor default value");
                }
                return defaultValue;
            }
        }
    }

    @Override
    public Class<?> getObjectType() {
        return String.class;
    }

    @Override
    public boolean isSingleton() {
        return false;
    }

    public String getKey() {
        return key;
    }

    public void setKey(String key) {
        this.key = key;
    }

    public String getDefaultValue() {
        return defaultValue;
    }

    public void setDefaultValue(String defaultValue) {
        this.defaultValue = defaultValue;
    }

    public boolean isMandatory() {
        return mandatory;
    }

    public void setMandatory(boolean mandatory) {
        this.mandatory = mandatory;
    }
}
