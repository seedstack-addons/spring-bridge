/*
 * Copyright © 2013-2020, The SeedStack authors <http://seedstack.org>
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/.
 */
package org.seedstack.spring.internal;

import com.google.common.base.Strings;
import org.seedstack.seed.Application;
import org.seedstack.seed.SeedException;
import org.seedstack.seed.transaction.spi.TransactionalLink;
import org.springframework.orm.jpa.EntityManagerHolder;
import org.springframework.transaction.support.TransactionSynchronizationManager;

import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;
import java.util.HashMap;
import java.util.Map;

class SpringEntityManagerLink implements TransactionalLink<EntityManager> {
    private static final String GENERIC_UNIT_NAME_PROPERTY = "seedstack.jpaUnitName";
    private static final String HIBERNATE_UNIT_NAME_PROPERTY = "hibernate.ejb.persistenceUnitName";
    static final String JPA_UNIT_PROPERTY = "jpaUnit";

    private final Application application;
    private final Class<?> currentClass;

    SpringEntityManagerLink(Application application, Class<?> currentClass) {
        this.application = application;
        this.currentClass = currentClass;
    }

    @Override
    public EntityManager get() {
        Map<String, EntityManagerFactory> mapEntityManagerFactoryByUnit = new HashMap<>();
        for (Map.Entry<Object, Object> entry : TransactionSynchronizationManager.getResourceMap().entrySet()) {
            if (entry.getKey() instanceof EntityManagerFactory) {
                EntityManagerFactory emf = (EntityManagerFactory) entry.getKey();
                mapEntityManagerFactoryByUnit.put(getUnitName(emf), emf);
            }
        }
        return getEntityManager(mapEntityManagerFactoryByUnit);
    }

    private EntityManager getEntityManager(Map<String, EntityManagerFactory> mapEntityFactories) {
        if (mapEntityFactories.isEmpty()) {
            throw SeedException.createNew(SpringErrorCode.NO_SPRING_ENTITYMANAGER).put("class", currentClass);
        }

        EntityManager entityManager;
        String unitFromClass = application.getConfiguration(currentClass).get(JPA_UNIT_PROPERTY);
        if (Strings.isNullOrEmpty(unitFromClass)) {
            if (mapEntityFactories.size() > 1) {
                throw SeedException
                        .createNew(SpringErrorCode.AMBIGUOUS_SPRING_ENTITYMANAGER)
                        .put("class", currentClass.getName())
                        .put("currentTransaction", TransactionSynchronizationManager.getCurrentTransactionName());
            } else {
                EntityManagerHolder entityManagerHolder = (EntityManagerHolder) TransactionSynchronizationManager.getResource
                        (mapEntityFactories.entrySet()
                                .iterator()
                                .next()
                                .getValue());
                if (entityManagerHolder != null) {
                    entityManager = entityManagerHolder.getEntityManager();
                } else {
                    throw SeedException
                            .createNew(SpringErrorCode.UNABLE_TO_ACCESS_SPRING_ENTITYMANAGER)
                            .put("class", currentClass.getName())
                            .put("currentTransaction", TransactionSynchronizationManager.getCurrentTransactionName());
                }
            }
        } else {
            EntityManagerFactory entityManagerFactory = mapEntityFactories.get(unitFromClass);
            if (entityManagerFactory == null) {
                throw SeedException
                        .createNew(SpringErrorCode.UNKNOWN_SPRING_ENTITYMANAGER)
                        .put("class", currentClass)
                        .put("unit", unitFromClass)
                        .put("currentTransaction", TransactionSynchronizationManager.getCurrentTransactionName());
            }
            EntityManagerHolder entityManagerHolder = (EntityManagerHolder) TransactionSynchronizationManager.getResource(entityManagerFactory);
            if (entityManagerHolder != null) {
                entityManager = entityManagerHolder.getEntityManager();
            } else {
                throw SeedException
                        .createNew(SpringErrorCode.UNABLE_TO_ACCESS_SPRING_ENTITYMANAGER)
                        .put("class", currentClass.getName())
                        .put("currentTransaction", TransactionSynchronizationManager.getCurrentTransactionName());
            }
        }

        return entityManager;
    }

    private String getUnitName(EntityManagerFactory emf) {
        Map<String, Object> properties = emf.getProperties();

        String unitName = String.valueOf(properties.get(HIBERNATE_UNIT_NAME_PROPERTY));
        if (Strings.isNullOrEmpty(unitName)) {
            unitName = String.valueOf(properties.get(GENERIC_UNIT_NAME_PROPERTY));
        }
        if (Strings.isNullOrEmpty(unitName)) {
            throw SeedException.createNew(SpringErrorCode.UNABLE_TO_RESOLVE_JPA_UNIT)
                    .put("entityManagerFactory", emf.toString());
        }

        return unitName;
    }
}