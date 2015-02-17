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
package com.avanza.astrix.beans.factory;


/**
 * The bean aware interface allows "Astrix-plugin-instances" to depend on
 * Astrix-managed-beans, ie api-elements hooked into Astrix. <p> 
 * 
 * 
 * @author Elias Lindholm (elilin)
 *
 */
public interface AstrixBeansAware {
	
	// TODO: Is this really part of the beans-factory? Its placed here to avoid circular dependency. Maybee rename this interface and create new abstractions for AstrixBeans used in this purpose
	
	void setAstrixBeans(AstrixBeans beans);

}