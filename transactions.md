---
title: "Spring transactions"
parent: "Spring bridge"
weight: -1
repo: "https://github.com/seedstack/spring-bridge-addon"
tags:
    - transactions
zones:
    - Addons
menu:
    AddonSpringBridge:
        weight: 20
---

When using Spring framework along SeedStack, you might need to trigger transactions accross framework boundaries. The Spring bridge add-on provides a solution that goes both ways and lets you choose beteween either:

* Managing Spring transactions from Seed code,
* Managing Seed transactions from Spring code.


# Seed-managed transactions

You can specify a Spring-based transaction handler in your Seed transaction demarcation by adding the
`@SpringTransactionManager` annotation besides the `@Transactional` one.

You can define any valid Spring transaction manager in any Spring context known by Seed Spring support. Example:
		
	<bean id="transactionManager" class="org.springframework.orm.hibernate3.HibernateTransactionManager">
		<property name="sessionFactory" ref="sessionFactory" />
	</bean>

The value of the `@SpringTransactionManager` annotation is used to choose the right transaction manager. Its default
value is `transactionManager`. Example of use in an integration test:

	@RunWith(SeedITRunner.class)
	public class SpringTransactionHandlerIT {
	
		@Inject
		@Named("customerDao")
		CustomerDao customerDao;
	
		@Test
		@Transactional
		@SpringTransactionManager("myTransactionManager")
		public void testTransactional() {
			Assertions.assertThat(customerDao).isNotNull();
			Customer customer = new Customer("john", "doe", "john.doe@gmail.com",
					null);
			customerDao.save(customer);
			customerDao.delete(customer);
			Assertions.assertThat(customer).isNotNull();
		}

	}
	
# Spring-managed transactions

Seed has the ability to inject a Spring-configured **JPA EntityManger**  in your Seed components. In that case, Spring will be managing all the JPA transactions. Seed code will be executed whithin Spring transactions. This feature can be very useful in batch jobs, when you need to let Spring manage transactions for performance reasons.

## Spring configuration
As stated above Spring will be the one that will manage all JPA features (mapping, transaction ...). As such, your Spring context files need to be configured explicitly with JPA context (JPA Datasources, TransactionManagers, EntityManagerFactories).

## Seed configuration
Seed JPA add-on can be removed if possible or at least left unconfigured. Add the following configuration for Spring bridge add-on instead:

```ini
[org.seedstack.spring]
manage-transactions = true
```
