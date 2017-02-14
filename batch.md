---
title: "Batch"
name: "Spring bridge"
repo: "https://github.com/seedstack/spring-bridge-addon"
tags:
    - "batch"
    - "spring"
    - "job"
    - "bridge"
zones:
    - Addons
menu:
    AddonSpringBridge:
        weight: 30
---

A specific Spring-Batch integration is also provided. Spring-Batch is a comprehensive solution to implement full-featured
batch jobs in Java. More information about Spring Batch on [http://docs.spring.io/spring-batch/](http://docs.spring.io/spring-batch/).

{{< dependency g="org.seedstack.addons.spring" a="spring-bridge-batch" >}}

All Spring context XML files **must be** in under the `META-INF/spring` classpath location.

# Running jobs

The `org.seedstack.seed.cli.SeedRunner` class contains a `main()` method. Its role is to launch a named command directly
from the operating system command line. SeedStack Spring Batch support provides the `run-job` command to start a Spring
Batch job: 
 
    java [java-options] org.seedstack.seed.cli.SeedRunner run-job [run-job-options]
    
This will start the Spring Batch job named `job`.    
 
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

```sh
java -cp "..." -Dsysprop=sysvalue org.seedstack.seed.cli.SeedRunner run-job --job myJob --jobParameter param1=value1 --jobParameter param2=value2
```

This will execute a Spring Batch job with the following characteristics:

* The classpath will be defined by the -cp option of the JVM,
* A system property `sysprop` with the value `sysvalue` will be set,
* The job named `myJob` will be executed,
* Two parameters will be passed to the job: `param1` with value `value1` and `param2` with value `value2`.

## Executable Über-JAR

To run the batch from a unique JAR, you can build the project with the Apache Maven Shade Plugin. This plugin will package 
the artifact as an über JAR with all necessary dependencies. For more information please refer to the plugin 
[documentation](http://maven.apache.org/plugins/maven-shade-plugin/examples/executable-jar.html). You can find a typical
configuration of the plugin below:

    <build>
        <plugins>
            <plugin>
                <groupId>org.apache.maven.plugins</groupId>
                <artifactId>maven-shade-plugin</artifactId>
                <version>...</version>
                <configuration>
                    <transformers>
                        <transformer
                            implementation="org.apache.maven.plugins.shade.resource.AppendingTransformer">
                            <resource>META-INF/spring.handlers</resource>
                        </transformer>
    
                        <transformer
                            implementation="org.apache.maven.plugins.shade.resource.AppendingTransformer">
                            <resource>META-INF/spring.schemas</resource>
                        </transformer>
    
                        <transformer
                            implementation="org.apache.maven.plugins.shade.resource.ManifestResourceTransformer">
                            <manifestEntries>
                                <Main-Class>org.seedstack.seed.cli.SeedRunner</Main-Class>
                            </manifestEntries>
                        </transformer>
    
                        <transformer
                            implementation="org.apache.maven.plugins.shade.resource.ServicesResourceTransformer" />
                    </transformers>
                </configuration>
                <executions>
                    <execution>
                        <phase>package</phase>
                        <goals>
                            <goal>shade</goal>
                        </goals>
                    </execution>
                </executions>
            </plugin>
        </plugins>
    </build>

# Testing

To be able to do SeedStack integration testing, add the Seed integration test support to your project. Check the 
documentation [here]({{< ref "docs/seed/manual/testing.md" >}}). The following example checks that the batch returns 
with the exit code 0 and subsequently that injection works.

    import org.seedstack.seed.it.AbstractSeedIT;

    public class RunnerBatchIT extends AbstractSeedIT {
        @Inject
        MessageService messageService;
     
        @Test
        @WithCommandLine(
            command = "run-job", 
            args = {"--job", "mySimpleJob"}, 
            expectedExitCode = 0
        )
        public void testBatch() {
            assertThat(messageService).isNotNull();
        }
    }
    
We could easily use the service (or any injectable class) to check for the batch results. The `@WithCommandLine` annotation
simulates the running of a command from the operating system command line. All the arguments of the `run-job` command
can be used in the `args` attribute. Look [here](#running-jobs) for information about these arguments. 

{{% callout info %}}
Note that the test method is called **after** the job is completed. `@Before` annotated methods are executed after Kernel
startup (so you can use injection in them) but before job execution (so you can prepare a dataset if needed).
{{% /callout %}}

# Full example

The goal of this section is to create your first batch. This one-step job will just print “My Simple Job”.

## Add Maven dependencies

This example requires `business-core`:

        <dependency>
            <groupId>org.seedstack.seed</groupId>
            <artifactId>spring-batch</artifactId>
        </dependency>
        <dependency>
           <groupId>org.seedstack.business</groupId>
           <artifactId>business-core</artifactId>
       </dependency>


## Create the application context

We need to set up a Spring Batch environment. Spring files must be in the `META-INF/spring` classpath location and end
with `-context.xml` to be automatically detected. The `application-context.xml` file:

    <beans xmlns="http://www.springframework.org/schema/beans"
           xmlns:batch="http://www.springframework.org/schema/batch"
           xmlns:xsi="http://www.w3.org/2001/XMLSchema-instance"
           xmlns:seed="http://www.seedstack.org/xml-schemas/spring-support"
           xsi:schemaLocation="http://www.springframework.org/schema/batch
            http://www.springframework.org/schema/batch/spring-batch-2.2.xsd
            http://www.springframework.org/schema/beans
            http://www.springframework.org/schema/beans/spring-beans-3.2.xsd
            http://www.seedstack.org/xml-schemas/spring-support
            http://www.seedstack.org/xml-schemas/spring-support/spring-support-1.1.xsd">
     
        <bean id="jobRepository" class="org.springframework.batch.core.repository.support.MapJobRepositoryFactoryBean">
            <property name="transactionManager" ref="transactionManager" />
        </bean>
    
        <bean id="transactionManager" class="org.springframework.batch.support.transaction.ResourcelessTransactionManager" />
    
        <bean id="jobLauncher" class="org.springframework.batch.core.launch.support.SimpleJobLauncher">
            <property name="jobRepository" ref="jobRepository" />
        </bean>
    </beans>

Description of the beans:

- `JobRepository` : responsible for persistence of batch meta-data information.
- `JobLauncher` : responsible for launching the batch job.
- `TransactionManager` : As this example won’t be dealing with transactional data, we are using `ResourcelessTransactionManager`
 which is mainly used for testing purpose. **Don't use in production**.

## Create the service

We will create a service that will be injected directly in a Spring Batch tasklet. The service interface: 

    package org.myorg.myapp.domain.services;

    import org.seedstack.seed.business.api.Service;

    @Service
    public interface MessageService {
        public String getMessage();
    }

The service implementation:

    package org.myorg.myapp.infrastructure.services;

    import org.myorg.myapp.domain.services.MessageService;

    public class MessageServiceImpl implements MessageService {

        public String getMessage() {
            return "--- My Simple Job ----";
        }

    }

## Create the Tasklet

A tasklet is a Class containing custom logic to be ran as a part of a job. `PrintTasklet` is our custom tasklet which
implements `Tasklet` interface and overrides the `execute()` method which prints the message from `MessageService`.

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

## Define the job Configuration

In this section we will configure the Spring Batch job context to use our Tasklet and inject the `MessageService` Service.
The `job-context.xml` file:

    <beans xmlns="http://www.springframework.org/schema/beans"
           xmlns:batch="http://www.springframework.org/schema/batch"
           xmlns:xsi="http://www.w3.org/2001/XMLSchema-instance"
           xmlns:seed="http://www.seedstack.org/xml-schemas/spring-support"
           xsi:schemaLocation="http://www.springframework.org/schema/batch
            http://www.springframework.org/schema/batch/spring-batch-2.2.xsd
            http://www.springframework.org/schema/beans
            http://www.springframework.org/schema/beans/spring-beans-3.2.xsd
            http://www.seedstack.org/xml-schemas/spring-support
            http://www.seedstack.org/xml-schemas/spring-support/spring-support-1.1.xsd">
     
        <import resource="application-context.xml"/>
        
        <batch:job id="mySimpleJob">        
            <batch:step id="printStep" >
                <batch:tasklet>
                    <bean class="org.seedstack.seed.batch.tasklet.PrintTasklet">
                        <property name="messageService">
                            <seed:instance class="org.seedstack.seed.service.MessageService"/>
                        </property>                        
                    </bean>
                </batch:tasklet>
            </batch:step>        
        </batch:job>
    </beans>

The example above illustrates the basic structure of a job. A job (`<batch:joby>` tag) is made of steps (`<batch:step>` 
tag) with a Tasklet (`<batch:tasklet>` tag) and irelated beans to be injected. Steps are executed one by one following 
their declared order. For more information, please read the [Spring Batch documentation](http://docs.spring.io/spring-batch/reference/html/index.html).
