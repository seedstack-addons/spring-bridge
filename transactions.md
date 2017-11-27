---
title: "Transactions"
addon: "Spring bridge"
weight: -1
repo: "https://github.com/seedstack/spring-bridge-addon"
tags:
    - transactions
zones:
    - Addons
menu:
    Spring bridge:
        parent: "contents"
        weight: 20
---

The SeedStack/Spring bridge add-on allows to trigger transactions across framework boundaries.<!--more-->

You can:

* Manage Spring transactions from SeedStack code,
* Manage SeedStack transactions from Spring code.


## SeedStack-managed transactions

You can specify a Spring-based transaction handler in your Seed transaction demarcation by adding the
{{< java "org.seedstack.spring.SpringTransactionManager" "@">}} annotation next to the {{< java "org.seedstack.seed.transaction.Transactional" "@">}} one.

Consider the following Spring transaction manager:
		
```xml
<bean id="transactionManager" class="org.springframework.orm.jpa.JpaTransactionManager">
    <property name="entityManagerFactory" ref="someEmf" />
</bean>
```

You can trigger transactions on this Spring transaction manager from SeedStack as below:

```java
public class SomeClass {
    @Inject
    @Named("customerDao")
    private CustomerDao customerDao;

    @Transactional
    @SpringTransactionManager
    public void someMethod() {
        // do something transactional with the customerDao Spring bean
    }
}
```

{{% callout info %}}
The default name of the transaction manager in the {{< java "org.seedstack.spring.SpringTransactionManager" "@">}} annotation
parameter is `transactionManager` but you can specify a custom name if needed.
{{% /callout %}}
	
## Spring-managed transactions

Seed has the ability to inject a Spring-configured **JPA EntityManger**  in your SeedStack classes. In that case, Spring 
is be managing the JPA transactions. SeedStack code is executed within the Spring transaction. This feature can be very 
useful in batch jobs, when you need to let Spring batch manage transactions for performance reasons.

### Spring configuration

As stated above Spring will be the one that will manage all JPA features (mapping, transaction ...). As such, your 
Spring context files need contain a complete JPA configuration (datasource + entity manager factory + transaction manager).

### SeedStack configuration

SeedStack JPA add-on should be removed if possible or at least left un-configured. Additionally, the SeedStack/Spring 
bridge must be configured as below:

```yaml
spring:
  manageJpa: true
```
