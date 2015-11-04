/**
 * Copyright (c) 2013-2015, The SeedStack authors <http://seedstack.org>
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/.
 */
package org.seedstack.seed.springbatch;

import com.google.inject.ConfigurationException;
import com.google.inject.Injector;
import com.google.inject.Key;
import com.google.inject.name.Names;
import org.junit.Before;
import org.junit.Test;
import org.seedstack.seed.cli.WithCommandLine;
import org.seedstack.seed.it.AbstractSeedIT;
import org.seedstack.seed.spring.api.WithApplicationContexts;
import org.springframework.batch.core.repository.JobRepository;

import javax.inject.Inject;
import javax.inject.Named;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Fail.fail;

/**
 * @author epo.jemba@ext.mpsa.com
 * @author adrien.lauer@mpsa.com
 */
public class SpringBatchCommandHandlerIT extends AbstractSeedIT {
    @Inject
    @Named("jobRepository")
    JobRepository jobRepository;

    @Inject
    Injector injector;

    private boolean passedBefore = false;

    @Before
    public void before() {
        assertThat(passedBefore).isFalse();
        passedBefore = true;
    }

    @Test(expected = ConfigurationException.class)
    @WithCommandLine(command = "run-job", args = {"--job", "flatFileJob", "-Pfile=fileTest.csv"}, expectedExitCode = 0)
    @WithApplicationContexts({"META-INF/spring/flatFileJob-context.xml", "META-INF/spring/simple-job-launcher-context.xml"})
    public void execute_batch_with_partial_spring_context() {
        assertThat(jobRepository).isNotNull();
        injector.getInstance(Key.get(Object.class, Names.named("optionalBean")));
        fail("should have failed retrieving optionalBean spring bean");
    }

    @Test
    @WithCommandLine(command = "run-job", args = {"--job", "flatFileJob", "-Pfile=fileTest.csv"}, expectedExitCode = 0)
    public void execute_batch_without_error() {
        assertThat(passedBefore).isTrue();
        assertThat(jobRepository).isNotNull();
        assertThat(injector.getInstance(Key.get(Object.class, Names.named("optionalBean")))).isNotNull();
    }

    @Test
    @WithCommandLine(command = "run-job", args = {"--job", "flatFileJob"}, expectedExitCode = 1)
    public void execute_batch_with_error() {
        assertThat(passedBefore).isTrue();
        assertThat(jobRepository).isNotNull();
    }

    @Test
    @WithCommandLine(command = "run-job", args = {"-j", "flatFileJob", "-Pfile2=fileTest1", "--jobParameter", "file=fileTest.csv", "-P file3=fileTest2"}, expectedExitCode = 0)
    public void execute_batch_with_multiple_parameters() {
        assertThat(passedBefore).isTrue();
        assertThat(jobRepository).isNotNull();
    }
}
