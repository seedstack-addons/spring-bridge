/*
 * Copyright © 2013-2018, The SeedStack authors <http://seedstack.org>
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/.
 */
package org.seedstack.spring.batch;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Fail.fail;

import com.google.inject.ConfigurationException;
import com.google.inject.Injector;
import com.google.inject.Key;
import com.google.inject.name.Names;
import javax.inject.Inject;
import javax.inject.Named;
import javax.persistence.EntityManager;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.seedstack.business.domain.Repository;
import org.seedstack.jpa.Jpa;
import org.seedstack.seed.cli.WithCliCommand;
import org.seedstack.seed.testing.Arguments;
import org.seedstack.seed.testing.junit4.SeedITRunner;
import org.seedstack.spring.WithApplicationContexts;
import org.seedstack.spring.batch.sample.domain.User;
import org.springframework.batch.core.repository.JobRepository;

@RunWith(SeedITRunner.class)
public class SpringBatchCommandHandlerIT {
    @Inject
    @Named("jobRepository")
    private JobRepository jobRepository;
    @Inject
    @Jpa
    private Repository<User, Long> contactRepository;
    @Inject
    private EntityManager entityManager;
    @Inject
    private Injector injector;
    private boolean passedBefore = false;

    @Before
    public void before() {
        assertThat(passedBefore).isFalse();
        passedBefore = true;
    }

    @Test(expected = ConfigurationException.class)
    @WithCliCommand(command = "run-job")
    @Arguments({"--job", "flatFileJob", "-Pfile=fileTest.csv"})
    @WithApplicationContexts({"META-INF/spring/flatFileJob-context.xml", "META-INF/spring/simple-job-launcher-context" +
            ".xml"})
    public void execute_batch_with_partial_spring_context() {
        assertThat(jobRepository).isNotNull();
        injector.getInstance(Key.get(Object.class, Names.named("optionalBean")));
        fail("should have failed retrieving optionalBean spring bean");
    }

    @Test
    @WithCliCommand(command = "run-job", expectedStatusCode = 0)
    @Arguments({"--job", "flatFileJob", "-Pfile=fileTest.csv"})
    public void execute_batch_without_error() {
        assertThat(passedBefore).isTrue();
        assertThat(jobRepository).isNotNull();
        assertThat(injector.getInstance(Key.get(Object.class, Names.named("optionalBean")))).isNotNull();
    }

    @Test
    @WithCliCommand(command = "run-job", expectedStatusCode = 1)
    @Arguments({"--job", "flatFileJob"})
    public void execute_batch_with_error() {
        assertThat(passedBefore).isTrue();
        assertThat(jobRepository).isNotNull();
    }

    @Test
    @WithCliCommand(command = "run-job", expectedStatusCode = 0)
    @Arguments({"-j", "flatFileJob", "-Pfile2=fileTest1", "--jobParameter", "file=fileTest.csv", "-P file3=fileTest2"})
    public void execute_batch_with_multiple_parameters() {
        assertThat(passedBefore).isTrue();
        assertThat(jobRepository).isNotNull();
    }

    @Test
    @WithCliCommand(command = "run-job", expectedStatusCode = 0)
    @Arguments({"--job", "fileUploadJob"})
    @WithApplicationContexts({"META-INF/spring/spring-context-batch.xml", "META-INF/spring/spring-context-orm.xml",
            "META-INF/spring/spring-context.xml"})
    public void execute_batch_with_spring_transactionmanager() {
        assertThat(passedBefore).isTrue();
        assertThat(contactRepository).isNotNull();
        assertThat(entityManager).isNotNull();
    }

    @Test
    @WithCliCommand(command = "run-job", expectedStatusCode = 0)
    @Arguments({"--job", "fileUploadJobWith2Manager"})
    @WithApplicationContexts({"META-INF/spring/spring-context-batch.xml", "META-INF/spring/spring-context-orm.xml",
            "META-INF/spring/spring-context.xml"})
    public void execute_threaded_batch_with_2_spring_transactionmanager() {
        assertThat(passedBefore).isTrue();
        assertThat(contactRepository).isNotNull();
        assertThat(entityManager).isNotNull();
    }
}
