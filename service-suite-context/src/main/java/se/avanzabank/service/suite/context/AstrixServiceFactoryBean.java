/*
 * Copyright 2014-2015 Avanza Bank AB
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package se.avanzabank.service.suite.context;

import org.springframework.beans.factory.FactoryBean;
import org.springframework.beans.factory.annotation.Autowired;

public class AstrixServiceFactoryBean<T> implements FactoryBean<T> {
	
	private Class<T> type;
	private AstrixContext astrix;

	@Autowired
	public void setAstrixContext(AstrixContext astrix) {
		this.astrix = astrix;
	}
	
	public AstrixContext getAstrixContext() {
		return astrix;
	}
	
	public void setType(Class<T> type) {
		this.type = type;
	}
	
	public Class<T> getType() {
		return type;
	}

	@Override
	public T getObject() throws Exception {
		AstrixServiceFactory<T> serviceFactory = astrix.getServiceFactory(type);
		if (serviceFactory instanceof ExternalDependencyAware) {
			injetExternalDependency((ExternalDependencyAware<?>)serviceFactory);
		}
		return serviceFactory.create(astrix);
	}

	private <D extends ExternalDependencyBean> void injetExternalDependency(ExternalDependencyAware<D> externalDependencyAware) {
		D dependency = astrix.getExternalDependency(externalDependencyAware.getDependencyBeanClass());
		externalDependencyAware.setDependency(dependency);
	}

	@Override
	public Class<?> getObjectType() {
		return type;
	}

	@Override
	public boolean isSingleton() {
		return true;
	}

}