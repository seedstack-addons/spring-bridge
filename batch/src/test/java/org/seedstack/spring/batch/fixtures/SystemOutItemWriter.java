/*
 * Copyright © 2013-2020, The SeedStack authors <http://seedstack.org>
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/.
 */
package org.seedstack.spring.batch.fixtures;

import org.springframework.batch.item.ItemWriter;

import java.util.List;

public class SystemOutItemWriter implements ItemWriter<Employee> {

    @Override
    public void write(List<? extends Employee> items) throws Exception {
        for (Employee employee : items) {
            System.out.println(employee);
        }
        System.out.println("* * *");
    }

}
