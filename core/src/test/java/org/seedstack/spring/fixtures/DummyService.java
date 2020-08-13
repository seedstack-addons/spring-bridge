/*
 * Copyright © 2013-2020, The SeedStack authors <http://seedstack.org>
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/.
 */
package org.seedstack.spring.fixtures;

import javax.sql.DataSource;

public class DummyService implements Service {
    private String from = "?";
    private String configurationValue = "?";
    private int anotherConfigurationValue = 0;
    private double defaultConfigurationValue;
    private DataSource dataSource;

    public void setFrom(String from) {
        this.from = from;
    }

    public String getFrom() {
        return from;
    }

    public String getConfigurationValue() {
        return configurationValue;
    }

    public void setConfigurationValue(String configurationValue) {
        this.configurationValue = configurationValue;
    }

    public int getOtherConfigurationValue() {
        return anotherConfigurationValue;
    }

    public void setOtherConfigurationValue(int otherConfigurationValue) {
        this.anotherConfigurationValue = otherConfigurationValue;
    }

    public void setDefaultConfigurationValue(double defaultConfigurationValue) {
        this.defaultConfigurationValue = defaultConfigurationValue;
    }

    public double getDefaultConfigurationValue() {
        return defaultConfigurationValue;
    }

    public DataSource getDataSource() {
        return dataSource;
    }

    public void setDataSource(DataSource dataSource) {
        this.dataSource = dataSource;
    }
}
