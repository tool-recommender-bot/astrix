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
package se.avanzabank.asterix.context;

import java.io.Serializable;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

/**
 * 
 * @author Elias Lindholm (elilin)
 *
 */
public final class AsterixServiceProperties implements Serializable {
	
	private static final long serialVersionUID = 1L;
	
	private final Map<String, String> properties = new HashMap<>();
	
	public AsterixServiceProperties(Map<String, String> serviceProperties) {
		this.properties.putAll(serviceProperties);
	}
	
	public AsterixServiceProperties() {
	}

	public String getProperty(String name) {
		return this.properties.get(name);
	}
	
	public Map<String, String> getProperties() {
		return properties;
	}
	
	public void setProperty(String name, String value) {
		this.properties.put(name, value);
	}
	
	public void setApi(Class<?> api) {
		setProperty("_api", api.getName());
	}
	
	public Class<?> getApi() {
		try {
			return Class.forName(getProperty("_api"));
		} catch (ClassNotFoundException e) {
			throw new RuntimeException(e);
		}
				
	}
	
	public void setQualifier(String qualifier) {
		setProperty("_qualifier", qualifier);
	}
	
	public String getQualifier() {
		return getProperty("_qualifier");
	}
	
	@Override
	public String toString() {
		return properties.toString();
	}

	public String getComponent() {
		return getProperty("_component");
	}
	
	public void setComponent(String component) {
		setProperty("_component", component);
	}

	@Override
	public int hashCode() {
		return Objects.hash(this.properties);
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		AsterixServiceProperties other = (AsterixServiceProperties) obj;
		return Objects.equals(this.properties, other.properties);
	}

}