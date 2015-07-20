/**
 * Copyright (c) 2013-2015 by The SeedStack authors. All rights reserved.
 *
 * This file is part of SeedStack, An enterprise-oriented full development stack.
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/.
 */
package org.seedstack.seed.springbatch.internal;

import org.junit.Before;
import org.seedstack.seed.cli.api.WithCommandLine;
import org.seedstack.seed.it.AbstractSeedIT;
import org.junit.Test;
import org.springframework.batch.core.repository.JobRepository;

import javax.inject.Inject;
import javax.inject.Named;

import static org.assertj.core.api.Assertions.assertThat;

/**
 * @author epo.jemba@ext.mpsa.com
 */
public class SpringBatchCommandHandlerIT extends AbstractSeedIT {
    @Inject
    @Named("jobRepository")
    JobRepository jobRepository;

    private boolean passedBefore = false;

    @Before
    public void before() {
        assertThat(passedBefore).isFalse();
        passedBefore = true;
    }

    @Test
    @WithCommandLine(command = "run-job", value = {"--job", "flatFileJob", "-Pfile=fileTest.csv"}, expectedExitCode = 0)
    public void execute_batch_without_error() {
        assertThat(passedBefore).isTrue();
        assertThat(jobRepository).isNotNull();
    }

    @Test
    @WithCommandLine(command = "run-job", value = {"--job", "flatFileJob"}, expectedExitCode = 1)
    public void execute_batch_with_error() {
        assertThat(passedBefore).isTrue();
        assertThat(jobRepository).isNotNull();
    }

    @Test
    @WithCommandLine(command = "run-job", value = {"-j", "flatFileJob", "-Pfile2=fileTest1", "--jobParameter", "file=fileTest.csv", "-P file3=fileTest2"}, expectedExitCode = 0)
    public void execute_batch_with_multiple_parameters() {
        assertThat(passedBefore).isTrue();
        assertThat(jobRepository).isNotNull();
    }
}
