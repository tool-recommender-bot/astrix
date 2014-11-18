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
package com.avanza.astrix.dashboard.web;

import java.util.Arrays;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import com.avanza.astrix.context.AstrixFrameworkBean;
import com.avanza.astrix.context.AstrixSettings;
import com.avanza.astrix.provider.component.AstrixServiceComponentNames;
import com.avanza.astrix.service.registry.client.AstrixServiceRegistryAdministrator;

@Configuration
public class AstrixDashboardConfig {
	
	@Bean
	public AstrixFrameworkBean Astrix() {
		AstrixFrameworkBean result = new AstrixFrameworkBean();
		result.setConsumedAstrixBeans(Arrays.<Class<?>>asList(
			AstrixServiceRegistryAdministrator.class
		));
		return result;
	}
	
	@Bean
	public AstrixSettings AstrixSettings() {
		return new AstrixSettings() {{
			set(Astrix_SERVICE_REGISTRY_URI, AstrixServiceComponentNames.GS_REMOTING + ":jini://*/*/service-registry-space?groups=elilin");
		}};
	}
	
}