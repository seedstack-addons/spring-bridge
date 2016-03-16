/**
 * Copyright (c) 2013-2016, The SeedStack authors <http://seedstack.org>
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/.
 */
package org.seedstack.spring.internal;

import java.util.HashMap;
import java.util.Map;

import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;

import org.apache.commons.lang.StringUtils;
import org.seedstack.seed.SeedException;
import org.seedstack.seed.core.spi.configuration.ConfigurationProvider;
import org.seedstack.seed.transaction.spi.TransactionalLink;
import org.springframework.orm.jpa.EntityManagerHolder;
import org.springframework.transaction.support.TransactionSynchronizationManager;

/**
 * 
 * @author redouane.loulou@ext.mpsa.com
 *
 */
class SpringEntityManagerLink implements TransactionalLink<EntityManager> {

	private static final String PERSISTENCE_UNIT_NAME_PROPERTY = "PERSISTENCE_UNIT_NAME";
	private static final String HIBERNATE_EJB_PERSISTENCEUNITNAME_PROPERTY = "hibernate.ejb.persistenceUnitName";
    public static final String JPA_UNIT_PROPERTY = "jpa-unit";


	private ConfigurationProvider configurationProvider;

	private Class<?> currentClass;

	public SpringEntityManagerLink(ConfigurationProvider configuration, Class<?> currentClass) {
		this.configurationProvider = configuration;
		this.currentClass = currentClass;
	}

	@Override
	public EntityManager get() {
		//Map EntityManagerFactories By unitNames
		Map<String, EntityManagerFactory> mapEntityManagerFactoryByUnit = new HashMap<String, EntityManagerFactory>();
		for (Map.Entry<Object, Object> entry : TransactionSynchronizationManager.getResourceMap().entrySet()) {
			if (entry.getKey() instanceof EntityManagerFactory) {
				EntityManagerFactory emf = (EntityManagerFactory) entry.getKey();
				mapEntityManagerFactoryByUnit.put(getUnitName(emf.getProperties()), emf);
			}
		}
		return getEntityManager(mapEntityManagerFactoryByUnit);
	}


	private EntityManager getEntityManager(Map<String, EntityManagerFactory> mapEntityFactories){
		if(mapEntityFactories.isEmpty()){			
			throw SeedException.createNew(SpringErrorCode.UNABLE_TO_INJECT_SPRING_ENTITYMANAGER).put("class", currentClass);
		}
		EntityManager entityManager = null;
		String unitFromClass =  configurationProvider.getConfiguration(currentClass)
				.getString(JPA_UNIT_PROPERTY);
		if(StringUtils.isBlank(unitFromClass)){
			if(mapEntityFactories.size()>1){
				throw SeedException.createNew(SpringErrorCode.MORE_THAN_ONE_UNIT_FOUND).put("class",
						currentClass.getName());
			}else{
				entityManager = ((EntityManagerHolder)TransactionSynchronizationManager.getResource(mapEntityFactories.entrySet().iterator().next().getValue())).getEntityManager();
			}
		} else if(!StringUtils.isBlank(unitFromClass)){
			EntityManagerFactory entityManagerFactory = mapEntityFactories.get(unitFromClass);
			if(entityManagerFactory == null){
				throw SeedException.createNew(SpringErrorCode.UNABLE_TO_INJECT_SPRING_ENTITYMANAGER).put("class", currentClass);
			}
			entityManager = ((EntityManagerHolder)TransactionSynchronizationManager.getResource(entityManagerFactory)).getEntityManager();
		}
		return entityManager;
	}
	
	/**
	 * get unitName from Spring EntityManagerFactory properties
	 * @param properties
	 * @return
	 */
	private String getUnitName(Map<String, Object> properties) {
		String unitName = String.valueOf(properties.get(HIBERNATE_EJB_PERSISTENCEUNITNAME_PROPERTY));
		if(StringUtils.isBlank(unitName)){
			unitName = String.valueOf(properties.get(PERSISTENCE_UNIT_NAME_PROPERTY));
		}else if(StringUtils.isBlank(unitName)){
			throw SeedException.createNew(SpringErrorCode.NO_JPA_UNIT_FOUND).put("class",
					currentClass.getName());
		}
		return unitName;
	}

}