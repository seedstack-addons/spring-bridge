/**
 * Copyright (c) 2013-2016, The SeedStack authors <http://seedstack.org>
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/.
 */
package org.seedstack.spring.batch.internal;

import com.google.inject.Injector;
import com.google.inject.Key;
import com.google.inject.name.Names;
import org.apache.commons.lang.StringUtils;
import org.seedstack.seed.cli.CliCommand;
import org.seedstack.seed.cli.CliOption;
import org.seedstack.seed.cli.CommandLineHandler;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.batch.core.BatchStatus;
import org.springframework.batch.core.Job;
import org.springframework.batch.core.JobExecution;
import org.springframework.batch.core.JobExecutionException;
import org.springframework.batch.core.JobParameter;
import org.springframework.batch.core.JobParameters;
import org.springframework.batch.core.job.AbstractJob;
import org.springframework.batch.core.launch.JobLauncher;

import javax.inject.Inject;
import java.util.HashMap;
import java.util.Map;

/**
 * This {@link CommandLineHandler} is launched with the run-job command. It support the following arguments:
 *
 * <ul>
 * <li>-j job-name</li>
 * <li>-l job-launcher-name</li>
 * <li>-P job-parameter-name=job-parameter-value</li>
 * </ul>
 *
 * @author epo.jemba@ext.mpsa.com
 * @author adrien.lauer@mpsa.com
 */
@CliCommand(value = "run-job", description = "Launch Spring Batch jobs")
public class SpringBatchCommandLineHandler implements CommandLineHandler {

    private static final String DEFAULT_JOB_LAUNCHER_NAME = "jobLauncher";

    private static final String DEFAULT_JOB_NAME = "job";

    private static final Logger LOGGER = LoggerFactory.getLogger(SpringBatchCommandLineHandler.class);

    @CliOption(name = "l", longName = "jobLauncher", valueCount = 1, description = "Job launcher name to use")
    String optionJobLauncherName;

    @CliOption(name = "j", longName = "job", valueCount = 1, description = "Job name to launch")
    String optionJobName;

    @CliOption(name = "P", longName = "jobParameter", valueCount = -1, valueSeparator = '=', description = "A job parameter")
    Map<String, String> optionJobParameters;

    @Inject
    Injector injector;

    @Override
    public Integer call() throws JobExecutionException {
        Integer batchExitStatus = 0;

        JobLauncher jobLauncher = getJobLauncher();

        Job job = getJob();

        JobParameters jobParameters = getJobParameters();

        JobExecution jobExecution = jobLauncher.run(job, jobParameters);

        BatchStatus batchStatus = jobExecution.getStatus();

        if (!batchStatus.equals(BatchStatus.COMPLETED)) {
            batchExitStatus = 1;
        }

        LOGGER.info("Exit with status : " + batchStatus);
        return batchExitStatus;
    }

    private JobParameters getJobParameters() {
        if (optionJobParameters != null && !optionJobParameters.isEmpty()) {
            Map<String, JobParameter> mapJobParameter = new HashMap<String, JobParameter>();

            for (Map.Entry<String, String> stringJobParameterEntry : optionJobParameters.entrySet()) {
                mapJobParameter.put(stringJobParameterEntry.getKey(), new JobParameter(stringJobParameterEntry.getValue()));
            }

            return new JobParameters(mapJobParameter);
        } else {
            return new JobParameters();
        }
    }

    private Job getJob() {

        String jobName = option(optionJobName, DEFAULT_JOB_NAME);

        return injector.getInstance(Key.get(AbstractJob.class, Names.named(jobName)));
    }

    private JobLauncher getJobLauncher() {

        String jln = option(optionJobLauncherName, DEFAULT_JOB_LAUNCHER_NAME);
        return injector.getInstance(Key.get(JobLauncher.class, Names.named(jln)));
    }

    private String option(String option, String defaultName) {
        String ret = defaultName;

        if (!StringUtils.isBlank(option)) {
            ret = option;
        }

        return ret;
    }
}
