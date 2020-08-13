/*
 * Copyright © 2013-2020, The SeedStack authors <http://seedstack.org>
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/.
 */
package org.seedstack.spring;

import org.seedstack.coffig.Config;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

@Config("spring")
public class SpringConfig {
    private boolean autodetect = true;
    private boolean manageJpa = true;
    private List<String> contexts = new ArrayList<>();

    public boolean isAutodetect() {
        return autodetect;
    }

    public SpringConfig setAutodetect(boolean autodetect) {
        this.autodetect = autodetect;
        return this;
    }

    public boolean isManageJpa() {
        return manageJpa;
    }

    public SpringConfig setManageJpa(boolean manageJpa) {
        this.manageJpa = manageJpa;
        return this;
    }

    public List<String> getContexts() {
        return Collections.unmodifiableList(contexts);
    }

    public SpringConfig addContext(String context) {
        this.contexts.add(context);
        return this;
    }
}
