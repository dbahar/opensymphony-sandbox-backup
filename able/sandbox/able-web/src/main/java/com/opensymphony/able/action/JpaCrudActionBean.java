/**
 *
 * Licensed to the Apache Software Foundation (ASF) under one or more
 * contributor license agreements.  See the NOTICE file distributed with
 * this work for additional information regarding copyright ownership.
 * The ASF licenses this file to You under the Apache License, Version 2.0
 * (the "License"); you may not use this file except in compliance with
 * the License.  You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.opensymphony.able.action;

import com.opensymphony.able.service.CrudService;
import com.opensymphony.able.service.JpaCrudService;
import net.sourceforge.stripes.integration.spring.SpringBean;
import org.springframework.orm.jpa.JpaTemplate;

import javax.persistence.PersistenceContext;

/**
 * @version $Revision$
 */
public abstract class JpaCrudActionBean<E> extends DefaultCrudActionBean<E> {
    private Class<E> entityClass;
    @PersistenceContext
    @SpringBean
    private JpaTemplate jpaTemplate;

    public JpaCrudActionBean(Class<E> entityClass) {
        this.entityClass = entityClass;
    }

    protected JpaCrudActionBean(Class<E> entityClass, JpaTemplate jpaTemplate) {
        this(entityClass);
        this.jpaTemplate = jpaTemplate;
    }

    @Override
    protected CrudService<E> createService() {
        return new JpaCrudService<E>(entityClass, jpaTemplate);
    }
}