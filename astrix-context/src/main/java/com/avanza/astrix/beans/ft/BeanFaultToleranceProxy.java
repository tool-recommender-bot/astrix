/*
 * Copyright 2014 Avanza Bank AB
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
package com.avanza.astrix.beans.ft;

import java.util.function.Supplier;

import com.avanza.astrix.beans.config.BeanConfiguration;
import com.avanza.astrix.beans.core.AstrixBeanSettings;
import com.avanza.astrix.beans.core.AstrixSettings;
import com.avanza.astrix.beans.core.BeanProxy;
import com.avanza.astrix.beans.core.BeanProxyNames;
import com.avanza.astrix.config.DynamicBooleanProperty;
import com.avanza.astrix.config.DynamicConfig;
import com.avanza.astrix.core.function.CheckedCommand;

import rx.Observable;
/**
 * 
 * @author Elias Lindholm (elilin)
 *
 */
public final class BeanFaultToleranceProxy implements BeanProxy {
	
	private final DynamicBooleanProperty faultToleranceEnabledForBean;
	private final DynamicBooleanProperty faultToleranceEnabled;
	private final BeanFaultTolerance beanFaultTolerance;
	
	BeanFaultToleranceProxy(BeanConfiguration beanConfiguration, DynamicConfig config, BeanFaultTolerance beanFaultToleranceSpi) {
		this.beanFaultTolerance = beanFaultToleranceSpi;
		this.faultToleranceEnabledForBean = beanConfiguration.get(AstrixBeanSettings.FAULT_TOLERANCE_ENABLED);
		this.faultToleranceEnabled = AstrixSettings.ENABLE_FAULT_TOLERANCE.getFrom(config);
	}
	
	@Override
	public <T> CheckedCommand<T> proxyInvocation(final CheckedCommand<T> command) {
		return () -> beanFaultTolerance.execute(command);
	}

	@Override
	public <T> Supplier<Observable<T>> proxyReactiveInvocation(final Supplier<Observable<T>> command) {
		return () -> beanFaultTolerance.observe(command);
	}
	
	@Override
	public String name() {
		return BeanProxyNames.FAULT_TOLERANCE;
	}
	
	@Override
	public boolean isEnabled() {
		return faultToleranceEnabled.get() && faultToleranceEnabledForBean.get();
	}
	
}
