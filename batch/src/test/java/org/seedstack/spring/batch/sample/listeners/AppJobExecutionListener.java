/*
 * Copyright © 2013-2020, The SeedStack authors <http://seedstack.org>
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/.
 */
package org.seedstack.spring.batch.sample.listeners;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.batch.core.BatchStatus;
import org.springframework.batch.core.JobExecution;
import org.springframework.batch.core.JobExecutionListener;
import org.springframework.stereotype.Component;

@Component("jobExecutionListener")
public class AppJobExecutionListener implements JobExecutionListener {

	private static final Logger logger = LoggerFactory.getLogger(AppJobExecutionListener.class);

	public void afterJob(JobExecution jobExecution) {
		if (jobExecution.getStatus() == BatchStatus.COMPLETED) {
			logger.debug("Job completed with JobId {} ", jobExecution.getJobId());
		} else if (jobExecution.getStatus() == BatchStatus.FAILED) {
			logger.debug("Job failed with JobId {} ", jobExecution.getJobId());
		}
	}

	public void beforeJob(JobExecution jobExecution) {
		if (jobExecution.getStatus() == BatchStatus.COMPLETED) {
			logger.debug("Job completed with JobId {} ", jobExecution.getJobId());
		} else if (jobExecution.getStatus() == BatchStatus.FAILED) {
			logger.debug("Job failed with JobId {} ", jobExecution.getJobId());
		}
	}
}
