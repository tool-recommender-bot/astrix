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
package com.avanza.astrix.context;


import com.avanza.astrix.versioning.core.AstrixObjectSerializer;
import com.avanza.astrix.versioning.core.AstrixObjectSerializerConfigurer;
import com.avanza.astrix.versioning.core.ObjectSerializerDefinition;
import com.avanza.astrix.versioning.core.ObjectSerializerFactoryPlugin;

public class JavaSerializationSerializerPlugin implements ObjectSerializerFactoryPlugin {
	
	@Override
	public AstrixObjectSerializer create(
			ObjectSerializerDefinition serializerDefinition) {
		return new JavaSerializationSerializer(serializerDefinition.version());
	}

	@Override
	public Class<? extends AstrixObjectSerializerConfigurer> getConfigurerType() {
		return JavaSerializationConfigurer.class;
	}
	
	public interface JavaSerializationConfigurer extends AstrixObjectSerializerConfigurer {
	}
	
}