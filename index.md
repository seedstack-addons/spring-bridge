---
title: "Core"
addon: "Spring bridge"
repo: "https://github.com/seedstack/spring-bridge-addon"
author: Adrien LAUER
description: "Integrates various Spring framework technologies with SeedStack."
tags:
    - injection
zones:
    - Addons
menu:
    Spring bridge:
        parent: "contents"
        weight: 10
---

SeedStack Spring bridge add-on is a bi-directional injection bridge between SeedStack (Guice) and Spring. It allows to
inject Spring beans with Guice and vice-versa.<!--more-->

## Dependency

{{< dependency g="org.seedstack.addons.spring" a="spring-core" >}}

## Configuration

{{% config p="spring" %}}
```yaml
spring:
  # If true auto-detection of XML files in META-INF/spring ending with *-context.xml is enabled (defaults to true)
  autodetect: (boolean) 
  # Classpath locations of Spring XML files to load explicitly (in addition to autodetected ones if any) 
  contexts: (List<String>)
  # If true, Spring-managed JPA EntityManager will be injected instead of the SeedStack one (defaults to true)
  manageJpa: (boolean)
```
{{% /config %}}    

## Usage

{{% callout info %}}
By default, the add-on detects all XML files named `*-context.xml` located in the `META-INF/spring` classpath location 
and aggregates them into a global Spring context. This behavior can be changed with the following configuration:
{{% /callout %}}

### Inject Spring beans in Guice instances

The add-on scans the Spring context built from detected and/or explicitly listed XML files and makes every bean injectable
through Guice with:

* The bean class and the bean name,
* The bean parent class (except Object) and the bean name,
* Any bean directly implemented interface and the bean name.

Consider the following example:

```java
public class SomeClass {
    @Inject 
    @Named("theBeanId") 
    private SomeBeanClass bean1;
    
    @Inject 
    @Named("theBeanId") 
    private SomeBeanParentClass bean2;
    
    @Inject 
    @Named("theBeanId") 
    private SomeBeanImplementedInterface bean3;
}
```

{{% callout info %}}
Note that you always need to qualify your injection with the bean identifier (`@Named("theBeanId")`).
{{% /callout %}}

### Create Guice instances in Spring context

To use Guice instances in the Spring context, you need to add the Seed namespace to your Spring files and use the 
`<seed:instance>` element:

```xml
<?xml version="1.0" encoding="UTF-8"?>
<beans xmlns="http://www.springframework.org/schema/beans" 
       xmlns:xsi="http://www.w3.org/2001/XMLSchema-instance"
       xmlns:seed="http://www.seedstack.org/xml-schemas/spring-support"
       
       xsi:schemaLocation="
        http://www.springframework.org/schema/beans http://www.springframework.org/schema/beans/spring-beans-3.2.xsd
        http://www.seedstack.org/xml-schemas/spring-support http://seedstack.org/xml-schemas/spring-support/spring-support-1.2.xsd">
        
    <seed:instance id="bean1" class="org.myorganization.myproject.SomeClass"/>
    
    <seed:instance id="bean2" class="org.myorganization.myproject.SomeClass" qualifier="someQualifier"/>
</beans>
```

The `bean1` declaration is equivalent to the following Guice injection:
 
```java
public class SomeClass {
    @Inject 
    private SomeClass bean1;
}
``` 

The `bean2` declaration is equivalent to the following Guice injection:
 
```java
public class SomeClass {
    @Inject 
    @Named("someQualifier")
    private SomeClass bean1;
}
``` 

{{% callout warning %}}
Since Guice can inject Spring beans and Spring can inject Guice instances, circular dependencies can occur. To break it,
Guice instances are by default proxied for lazy initialization. If you need to disable this behavior, specify `proxy="false"`:

```xml
<seed:instance id="bean2" 
               class="org.myorganization.myproject.SomeClass" 
               qualifier="someQualifier" 
               proxy="false"/>
```
{{% /callout %}}

### Create configuration values in Spring context

SeedStack configuration values can be created in Spring context: 

```xml
    <bean id="..." class="...">
        <property name="someValue">
            <seed:configuration key="myProject.someValue"/>
        </property>
    </bean>
```
    
This will inject the SeedStack `myProject.someValue` configuration value into the `someValue` property of the bean.
A default value can be specified:

```xml
<seed:configuration key="myProject.someValue" default="someDefaultValue"/>
```

You can make a configuration value optional by specifying `mandatory="false"`:

```xml
<seed:configuration key="myProject.someValue" mandatory="false"/>
```

An identifier can be given to the configuration value to be further referenced:

```xml
<beans>
    <seed:configuration id="someConfigProperty" key="myProject.someValue"/>
    <bean id="..." class="...">
        <property name="someValue" ref="someConfigProperty"/>
    </bean>
</beans>
```

### JDBC data sources in Spring context
  
You can create a JDBC data source bean from a SeedStack configured datasource. The following `datasource1` JDBC datasource:
 
```yaml
jdbc:
  datasources:
    datasource1:
      url: jdbc:hsqldb:mem:testdb1
```

Can be retrieved as a bean as below:

```xml
<seed:datasource id="someDatasource" name="datasource1"/>
```

