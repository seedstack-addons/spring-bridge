---
title: "Spring bridge"
repo: "https://github.com/seedstack/spring-bridge-addon"
author: Adrien LAUER
description: "Integrates various Spring framework technologies with SeedStack to enable composing hybrid applications."
tags:
    - injection
zones:
    - Addons
menu:
    AddonSpringBridge:
        weight: 10
---

Seed Spring support is a bi-directional injection bridge between Seed managed instances and Spring beans. It allows to
inject Spring beans in Seed instances and Seed instances as Spring beans.

Additionally, this support fills in a gap between Seed and Spring code allowing for instance to initiate a Spring-based 
transaction from Seed code. Tt also provides a Spring namespace handler to make its features as easy to use as possible.

{{< dependency g="org.seedstack.addons.spring" a="spring-core" >}}

# Spring to Seed

Any Spring context located in the `META-INF/spring` classpath directory and named with the pattern `*-context.xml` will
be autodetected by Seed. You can turn off auto detection with the following configuration property:
 
    org.seedstack.spring.autodetect = false
    
You can add custom contexts located anywhere in the classpath with the following configuration property:
    
    org.seedstack.spring.contexts = /resource/path/to/context1.xml, /resource/path/to/context2.xml

You can inject any Spring bean from contexts detected by Seed in any Seed injectable component. You can inject using the 
bean Class and the bean name: 

    @Inject @Named("theBeanId") BeanClass bean;

You can inject using the bean parent's Class (if not Object) and the bean name: 
    
    @Inject @Named("theBeanId") BeanParentClass bean;
    
You can inject using any directly implemented Interface and the bean name: 
    
    @Inject @Named("theBeanId") BeanImplementedInterface bean;

Note that you always need to qualify your injection with the bean identifier (`@Named("theBeanId")`)

# Seed to Spring

To use Seed instances in Spring contexts, you need to add the Seed namespace to your Spring files:

    <?xml version="1.0" encoding="UTF-8"?>
    <beans xmlns="http://www.springframework.org/schema/beans" 
           xmlns:xsi="http://www.w3.org/2001/XMLSchema-instance"
           xmlns:seed="http://www.seedstack.org/xml-schemas/spring-support"
           
           xsi:schemaLocation="
            http://www.springframework.org/schema/beans http://www.springframework.org/schema/beans/spring-beans-2.0.xsd
            http://www.seedstack.org/xml-schemas/spring-support http://www.seedstack.org/xml-schemas/spring-support/spring-support-1.1.xsd">
    
        ...
        
    </beans>


You can then create a spring bean from any Seed instance bound with a class name:

    <seed:instance id="myService" class="org.myorganization.myproject.MyService"/>
    
It is equivalent to this Seed injection:

    @Inject
    org.myorganization.myproject.MyService myService;
    
Named Seed bindings (bound with a `@Named` qualifier) are also supported:

    <seed:instance id="myService" class="org.myorganization.myproject.MyService" qualifier="myQualifier"/>

It is equivalent to this Seed injection:

    @Inject
    @Named("myQualifier")
    org.myorganization.myproject.MyService myService;
    
Since Seed can inject Spring beans and Spring can inject Seed instances, there is a circular dependency between the two
injectors. To alleviate this problem, Seed instances are by default proxied for lazy initialization. It allows Spring to 
initialize its context without needing the Seed injector to be initialized too. You can explicitly disable this proxy:

    <seed:instance id="myService" class="org.myorganization.myproject.MyService" qualifier="myQualifier" proxy="false"/>

You can also inject configuration values directly:

    <bean id="..." class="...">
        <property name="configurationValue">
            <seed:configuration key="org.myorganization.myproject.my-configuration-value"/>
        </property>
    </bean>
    
It is equivalent to this Seed configuration injection:

    @Configuration("org.myorganization.myproject.my-configuration-value")
    String configurationValue;
    
Configuration values don't require Seed injector to be initialized and are all available at context initialization. You 
can specify a default value:

    <seed:configuration key="org.myorganization.myproject.my-configuration-value" default="myDefaultValue"/>
            
It is equivalent to this Seed configuration injection:

    @Configuration(value = "org.myorganization.myproject.my-configuration-value", defaultValue="myDefaultValue")
    String configurationValue;
    
You can control if a property is mandatory with the mandatory attribute (true by default):
    
    <seed:configuration key="org.myorganization.myproject.my-configuration-value" mandatory="false"/>
    
If no configuration value nor default value is available and the injection is not mandatory, `null` will be used. 
    