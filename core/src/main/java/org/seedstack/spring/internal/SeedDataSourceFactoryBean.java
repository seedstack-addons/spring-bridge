/*
 * Copyright © 2013-2018, The SeedStack authors <http://seedstack.org>
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/.
 */
package org.seedstack.spring.internal;

import org.seedstack.jdbc.spi.JdbcProvider;
import org.springframework.beans.factory.FactoryBean;

import javax.sql.DataSource;

class SeedDataSourceFactoryBean implements FactoryBean<DataSource> {
    static JdbcProvider jdbcProvider;
    private String name;

    @Override
    public DataSource getObject() throws Exception {
        if (name == null) {
            throw new IllegalArgumentException("Property name is required for SeedDataSourceFactoryBean");
        } else {
            DataSource dataSource = jdbcProvider.getDataSource(name);
            if (dataSource == null) {
                throw new IllegalStateException("Unable to find SeedStack data source named " + name);
            }
            return dataSource;
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

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }
}
