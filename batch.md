---
title: "Spring batch"
parent: "Spring bridge"
weight: -1
repo: "https://github.com/seedstack/spring-bridge-addon"
zones:
    - Addons
menu:
    AddonSpringBridge:
        weight: 30
---

The SeedStack/Spring bridge add-on provides a Spring Batch specific integration. 
Spring-Batch is a comprehensive solution to implement full-featured batch jobs in Java.<!--more-->
 
More information about Spring Batch can be found [here](http://docs.spring.io/spring-batch/reference/html/index.html).

# Dependency 

{{< dependency g="org.seedstack.addons.spring" a="spring-bridge-batch" >}}

# Running jobs

Spring Batch jobs are run from a specific command-line handler providing the `run-job` command. To run a Spring Batch job
packaged in a Capsule, execute the following command:

```bash
java -jar batch-capsule.jar run-job --job someJob
```

This will run the `someJob` Spring Batch job.
   
## Options
        
A number of options are available for customizing the `run-job` command behavior:

<table class="table table-striped">
<tbody>
<tr>
<th>Short option</th>
<th>Long option</th>
<th>Description</th>
</tr>
<tr>
<td>-j</td>
<td>--job jobName</td>
<td>Specify the job to launch by name (<code>job</code> by default).</td>
</tr>
<tr>
<td>-l</td>
<td>--jobLauncher jobLauncherName</td>
<td>Specify the job launcher by name (<code>jobLauncher</code> by default).</td>
</tr>
<tr>
<td>-P</td>
<td>--jobParameters paramName=paramValue</td>
<td>Specify a job parameter. Can be used multiple times.</td>
</tr>
</tbody>
</table>

## Example

Consider the following command:

```bash
java -jar batch-capsule.jar run-job --job someJob --jobParameter param1=value1 --jobParameter param2=value2
```

This will execute a Spring Batch job named `someJob` with `param1` set to `value1` and `param2` set to `value2`.

# Testing

To learn how to do integration testing with SeedStack, check the [testing documentation]({{< ref "docs/seed/manual/testing.md" >}}). 
The following example checks that the batch returns with the exit code 0 and subsequently that the batch result is functionally
ok:

```java
public class RunnerBatchIT extends AbstractSeedIT {
    @Inject
    private SomeRepository someRepository;
 
    @Test
    @WithCommandLine(
        command = "run-job", 
        args = {"--job", "someJob"}, 
        expectedExitCode = 0
    )
    public void testBatch() {
        assertThat(someRepository.isResultOk()).isTrue();
    }
}
```
    
The {{< java "org.seedstack.seed.cli.WithCommandLine" "@">}} annotation simulates the running of a command from the 
operating system command line. All the arguments of the `run-job` command can be used in the `args` attribute. 

{{% callout info %}}
Note that the test method is called **after** the job is completed. `@Before` annotated methods are executed after Kernel
startup (so you can use injection in them) but before job execution (so you can prepare a dataset if needed).
{{% /callout %}}

# Example

The goal of this section is to create your first batch. This one-step job will just print “My Simple Job”.

## Create a service

We will create [a business framework service]({{< ref "docs/business/manual/services.md" >}}) that will be 
injected directly in a Spring Batch tasklet. The service interface: 

```java
package org.myorg.myapp.domain.services;

import org.seedstack.seed.business.Service;

@Service
public interface MessageService {
    String getMessage();
}
```

The service implementation:

```java
package org.myorg.myapp.domain.services;

import org.myorg.myapp.domain.services.MessageService;

public class MessageServiceImpl implements MessageService {
    public String getMessage() {
        return "--- My Simple Job ---";
    }
}
```

## Create the Tasklet

A tasklet is a Class containing custom logic to be ran as a part of a job. `PrintTasklet` is our custom tasklet which
implements `Tasklet` interface and overrides the `execute()` method which prints the message from `MessageService`.

```java
package org.seedstack.seed.batch.tasklet;

import org.springframework.batch.core.StepContribution;
import org.springframework.batch.core.scope.context.ChunkContext;
import org.springframework.batch.core.step.tasklet.Tasklet;
import org.springframework.batch.repeat.RepeatStatus;

import org.seedstack.seed.service.MessageService;

public class PrintTasklet implements Tasklet {
    private MessageService messageService;
    private Logger logger = LoggerFactory.getLogger(PrintTasklet.class);

    public RepeatStatus execute(StepContribution contribution,
                                ChunkContext chunkContext) throws Exception {
        logger.info(messageService.getMessage());
        return RepeatStatus.FINISHED;
    }

    public MessageService getMessageService() {
        return messageService;
    }

    public void setMessageService(MessageService messageService) {
        this.messageService = messageService;
    }
}
```

## Create the job context

In this section we will configure the Spring Batch job context to use our Tasklet and inject the `MessageService` Service.
The `job-context.xml` file:

```xml
<beans xmlns="http://www.springframework.org/schema/beans"
       xmlns:batch="http://www.springframework.org/schema/batch"
       xmlns:xsi="http://www.w3.org/2001/XMLSchema-instance"
       xmlns:seed="http://www.seedstack.org/xml-schemas/spring-support"
       xsi:schemaLocation="http://www.springframework.org/schema/batch
        http://www.springframework.org/schema/batch/spring-batch-3.0.xsd
        http://www.springframework.org/schema/beans
        http://www.springframework.org/schema/beans/spring-beans-3.2.xsd
        http://www.seedstack.org/xml-schemas/spring-support
        http://www.seedstack.org/xml-schemas/spring-support/spring-support-1.2.xsd">
 
    <bean id="transactionManager" class="org.springframework.batch.support.transaction.ResourcelessTransactionManager" />

    <bean id="jobRepository" class="org.springframework.batch.core.repository.support.MapJobRepositoryFactoryBean">
        <property name="transactionManager" ref="transactionManager" />
    </bean>

    <bean id="jobLauncher" class="org.springframework.batch.core.launch.support.SimpleJobLauncher">
        <property name="jobRepository" ref="jobRepository" />
    </bean>

    <batch:job id="mySimpleJob">        
        <batch:step id="printStep" >
            <batch:tasklet>
                <bean class="org.seedstack.seed.batch.tasklet.PrintTasklet">
                    <property name="messageService">
                        <seed:instance class="org.myorg.myapp.domain.services.MessageService"/>
                    </property>                        
                </bean>
            </batch:tasklet>
        </batch:step>        
    </batch:job>
</beans>
```

Description of the beans:

- `JobRepository` : responsible for persistence of batch meta-data information.
- `JobLauncher` : responsible for launching the batch job.
- `TransactionManager` : As this example won’t be dealing with transactional data, we are using `ResourcelessTransactionManager`
 which is mainly used for testing purpose. **Don't use it in production**.

{{% callout info %}}
The example above illustrates the basic structure of a job. A job (`<batch:job>` tag) is made of steps (`<batch:step>` 
tag) with a Tasklet (`<batch:tasklet>` tag). Steps are executed one by one following 
their declared order. For more information, please read the 
[Spring Batch documentation](http://docs.spring.io/spring-batch/reference/html/index.html).
{{% /callout %}}
