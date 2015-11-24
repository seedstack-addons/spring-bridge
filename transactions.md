---
title: "Transactions"
addon: "Spring bridge"
menu:
    AddonSpringBridge:
        weight: 20
---

SEED implements its own transaction engine and sometimes it could be troublesome depending on your needs. Spring bridge provides a solution that goes both ways and lets you choose beteween either :
* a transaction managed by Spring (for compatibility with Seed internal JPA features)
* a transaction managed by Seed (for compatibility with Spring ORM features)


# Spring transaction managed by Seed

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
	
# Seed JPA Transaction managed by Spring

Seed has the ability to inject and use an ongoing Spring configured **JPA EntityManger**  in your Seed component, by doing so Spring will be the one who manage all the transactions.
This feature can be very useful when you need to let Spring be the one who manage transactions at the batch configuration level.

## Spring requirement :
As stated above Spring will be the one that will need all your JPA features (mapping, transaction ...), as so your Spring context files need to be configured explicitly with JPA context (JPA Datasources, TransactionManagers, EntityManagerFactories).

## Seed requirement :
Seed props configuration should be JPALess, no JPA configuration is needed here.
Seed only need to know that Spring will be the one doing all the JPA part by adding a `manage-transactions` property, as followed :

```ini
[org.seedstack.spring]
manage-transactions = true
