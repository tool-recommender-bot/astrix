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
package se.avanzabank.asterix.dashboard;

import java.util.Arrays;
import java.util.List;

import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/services")
public class ServiceRegistryController {

	@RequestMapping(value = "/", method = RequestMethod.GET)
	public List<Service> services() {
		Service s = new Service();
		s.setProvidedApi("se.avanzabank.trading.TradingService");
		s.setComponent("GS-REMOTING");
		s.setUrl("jini://*/*/trading-space?groups=ax2332ssw");
		
		
		Service s1 = new Service();
		s1.setProvidedApi("se.avanzabank.market.InstrumentService");
		s1.setComponent("GS-REMOTING");
		s1.setUrl("jini://*/*/market-data-space?groups=" + Math.random());
		return Arrays.<Service>asList(s, s1);
	}

	public static class Service {
		private String providedApi;
		private String component;
		private String url;

		public String getProvidedApi() {
			return providedApi;
		}

		public void setProvidedApi(String providedApi) {
			this.providedApi = providedApi;
		}

		public String getComponent() {
			return component;
		}

		public void setComponent(String component) {
			this.component = component;
		}

		public String getUrl() {
			return url;
		}

		public void setUrl(String url) {
			this.url = url;
		}

	}

}
