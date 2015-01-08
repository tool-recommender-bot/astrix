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
package com.avanza.astrix.context;

import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Method;
import java.lang.reflect.Proxy;
import java.util.Objects;

import com.avanza.astrix.config.DynamicBooleanProperty;
import com.avanza.astrix.provider.versioning.ServiceVersioningContext;

/**
 * 
 * @author Elias Lindholm (elilin)
 *
 * @param <T>
 */
public class AstrixServiceFactory<T> implements AstrixFactoryBeanPlugin<T> {
	
	private final AstrixBeanKey<T> beanKey;
	private final AstrixServiceComponents serviceComponents;
	private final AstrixServiceLookup serviceLookup;
	private final String subsystem;
	private final DynamicBooleanProperty enforceSubsystemBoundaries;
	private final AstrixServiceLeaseManager leaseManager;
	private final ServiceVersioningContext versioningContext;

	public AstrixServiceFactory(ServiceVersioningContext versioningContext, 
								AstrixBeanKey<T> beanType, 
								AstrixServiceLookup serviceLookup, 
								AstrixServiceComponents serviceComponents, 
								AstrixServiceLeaseManager leaseManager,
								AstrixSettingsReader settings) {
		this.versioningContext = Objects.requireNonNull(versioningContext);
		this.beanKey = Objects.requireNonNull(beanType);
		this.serviceLookup = Objects.requireNonNull(serviceLookup);
		this.serviceComponents = Objects.requireNonNull(serviceComponents);
		this.leaseManager = Objects.requireNonNull(leaseManager);
		this.subsystem = Objects.requireNonNull(settings.getString(AstrixSettings.SUBSYSTEM_NAME));
		this.enforceSubsystemBoundaries = settings.getBooleanProperty(AstrixSettings.ENFORCE_SUBSYSTEM_BOUNDARIES, true);
	}

	@Override
	public T create() {
		AstrixServiceProperties serviceProperties = serviceLookup.lookup(beanKey.getBeanType(), beanKey.getQualifier());
		if (serviceProperties == null) {
			throw new RuntimeException(String.format("No service-provider found: bean=%s serviceLookup=%s", beanKey, serviceLookup));
		}
		String providerSubsystem = serviceProperties.getProperty(AstrixServiceProperties.SUBSYSTEM);
		if (!isAllowedToInvokeService(providerSubsystem)) {
			return createIllegalSubsystemProxy(providerSubsystem);
		}
		T service = create(serviceProperties);
		return leaseManager.startManageLease(service, serviceProperties, this, serviceLookup);
	}
	
	@Override
	public AstrixBeanKey<T> getBeanKey() {
		return beanKey;
	}
	
	@Override
	public boolean isStateful() {
		return true;
	}

	private boolean isAllowedToInvokeService(String providerSubsystem) {
		if (versioningContext.isVersioned()) {
			return true;
		}
		if (!enforceSubsystemBoundaries.get()) {
			return true;
		}
		return this.subsystem.equals(providerSubsystem);
	}
	
	private T createIllegalSubsystemProxy(String providerSubsystem) {
		return beanKey.getBeanType().cast(
				Proxy.newProxyInstance(beanKey.getBeanType().getClassLoader(), 
									   new Class[]{beanKey.getBeanType()}, 
									   new IllegalSubsystemProxy(subsystem, providerSubsystem, beanKey.getBeanType())));
	}

	public T create(AstrixServiceProperties serviceProperties) {
		if (serviceProperties == null) {
			throw new RuntimeException(String.format("Misssing entry in service-registry beanKey=%s", beanKey));
		}
		AstrixServiceComponent serviceComponent = getServiceComponent(serviceProperties);
		return serviceComponent.createService(versioningContext, beanKey.getBeanType(), serviceProperties);
	}
	
	private AstrixServiceComponent getServiceComponent(AstrixServiceProperties serviceProperties) {
		String componentName = serviceProperties.getComponent();
		if (componentName == null) {
			throw new IllegalArgumentException("Expected a componentName to be set on serviceProperties: " + serviceProperties);
		}
		return serviceComponents.getComponent(componentName);
	}
	
	private class IllegalSubsystemProxy implements InvocationHandler {
		
		private String currentSubsystem;
		private String providerSubsystem;
		private Class<?> beanType;
		

		public IllegalSubsystemProxy(String currentSubsystem, String providerSubsystem, Class<?> beanType) {
			this.currentSubsystem = currentSubsystem;
			this.providerSubsystem = providerSubsystem;
			this.beanType = beanType;
		}

		@Override
		public Object invoke(Object proxy, Method method, Object[] args) throws Throwable {
			throw new IllegalSubsystemException(currentSubsystem, providerSubsystem, beanType);
		}
	}
	
}
