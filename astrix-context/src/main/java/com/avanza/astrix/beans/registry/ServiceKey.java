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
package com.avanza.astrix.beans.registry;

import java.io.Serializable;
import java.util.Objects;
/**
 * 
 * @author Elias Lindholm (elilin)
 *
 */
public class ServiceKey implements Serializable {
	
	private static final long serialVersionUID = 1L;
	private String apiClassName;
	private String qualifier;

	public ServiceKey(String apiClassName, String qualifier) {
		this.apiClassName = Objects.requireNonNull(apiClassName);
		if (qualifier == null) {
			this.qualifier = "-";
		} else {
			this.qualifier = qualifier;
		}
	}
	
	public ServiceKey(String apiClassName) {
		this(apiClassName, "-");
	}
	
	public String getApiClassName() {
		return apiClassName;
	}
	
	public String getQualifier() {
		return qualifier;
	}

	@Override
	public int hashCode() {
		return Objects.hash(apiClassName, qualifier);
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		ServiceKey other = (ServiceKey) obj;
		return Objects.equals(apiClassName, other.apiClassName) 
				&& Objects.equals(qualifier, other.qualifier);
	}
	
	@Override
	public String toString() {
		return apiClassName + "|" + qualifier;
	}
	
	
	
	

}
