# Asterix
Asterix is a framework designed to simplify development and maintenance of microservices. It is used by service providers to publish provided services, and by service consumers to bind to published services. Most applications do both, they provide services as well as consume other services.

Some of the features provided:
- service publishing/discovery
- service binding
- service versioning
- fault tolerance

It's designed to support an organization where many teams develop different services and make those services available for other teams using Asterix.

## Service Registry
A core component in the framework is the service registry. It’s an application that allows service-providers to register all services hey provide. The service-registry is also used by service-consumers to discover providers of a consumed service.


## Service Binding
One of the main responsibilities for Asterix is service binding. 

Service binding is done in three steps: 
1. Asterix discovers a provider of a given service, typically using the service-registry
2. Asterix uses information retrieved from discovery to identifies what mechanism, (`ServiceComponent`), to use to bind to the given service provider 
3. Asterix uses the ServiceCompnonent and to bind to the given provider

It's also possible to locate providers without using the service-registry, for instance using configuration.

If the service is discovered using the service-registry, then a lease-manager thread will run for the given service in the background. The lease-manager will periodically ask the service-registry for information about where the given service is located, and if the service has moved the lease-manager will rebind the to the new provider.

## Service Versioning
A key goal of Asterix is to allow for an independent release cycle of different microservices. To support that Asterix has built in support for data-format versioning.

## Fault tolerance
Asterix uses a fault-tolerance layer built using Hystrix. Depending on the type of service consumed, Asterix will decide what isolation mechanism to use and protect each service-invocation using the given mechanism.

## Spring Integration
Asterix is well integrated with spring.


## Generic module layout
Typical module-layout when creating a service using Asterix: 
* API
* API provider
* Server

## Example - The LunchApi

Modules
* lunch-api
* lunch-api-provider
* lunch-server 


### API (lunch-api) 
```java
interface LunchService {
	@AsterixBroadcast(reducer = LunchSuggestionReducer.class)
	LunchRestaurant suggestRandomLunchRestaurant(String foodType);
}

interface LunchRestaurantAdministrator {
	void addLunchRestaurant(LunchRestaurant restaurant);
}

interface LunchRestaurantGrader {
	void grade(@Routing String restaurantName, int grade);
	double getAvarageGrade(@Routing String restaurantName);
}
```

### API descriptor (lunch-api-provider)

```java
// The API is versioned.
@AsterixVersioned(
	apiMigrations = {
		LunchApiV1Migration.class
	},	
	version = 2,
	objectMapperConfigurer = LunchApiObjectMapperConfigurer.class
)
// The service is exported to the service-registry. Service is bound by Asterix at runtime using service-registry
@AsterixServiceRegistryApi(
	exportedApis = {
		LunchService.class,
		LunchAdministrator.class,
		LunchRestaurantGrader.class
	}
)
public class LunchApiDescriptor {
}
```

### Migration (lunch-api-provider)

```java
public interface AsterixJsonApiMigration {
	
	int fromVersion();
	
	AsterixJsonMessageMigration<?>[] getMigrations();

}

public class LunchApiV1Migration implements AsterixJsonApiMigration {

	@Override
	public int fromVersion() {
		return 1;
	}
	
	@Override
	public AsterixJsonMessageMigration<?>[] getMigrations() {
		return new AsterixJsonMessageMigration[] {
			new LunchRestaurantV1Migration()
		};
	}
	
	private static class LunchRestaurantV1Migration implements AsterixJsonMessageMigration<LunchRestaurant> {

		@Override
		public void upgrade(ObjectNode json) {
			json.put("foodType", "unknown");
		}
		
		@Override
		public void downgrade(ObjectNode json) {
			json.remove("foodType");
		}

		@Override
		public Class<LunchRestaurant> getJavaType() {
			return LunchRestaurant.class;
		}
	}
}
```

## Providing the lunch-api

### Service implementations

```java
@AsterixServiceExport({LunchService.class, InternalLunchFeeder.class})
public class LunchServiceImpl implements LunchService, InternalLunchFeeder {
}

// And other service-implementations
```

### Service Descriptor

```java
@AsterixService(
	apiDescriptors = {
		LunchApiDescriptor.class,
		LunchFeederApiDescriptor.class
	},
	subsystem = "lunch-service",
	component = AsterixServiceComponentNames.GS_REMOTING
)
public class LunchServiceDescriptor {
}
```

### pu.xml

```xml
<!-- Asterix service framework (provider and consumer) -->
<bean id="asterixFrameworkBean" class="se.avanzabank.asterix.context.AsterixFrameworkBean">
	<property name="serviceDescriptor" value="se.avanzabank.asterix.integration.tests.domain.apiruntime.LunchServiceDescriptor"/>
</bean>

<!-- The actual service implementation(s) -->
<bean id="lunchService" class="se.avanzabank.asterix.integration.tests.domain.pu.LunchServiceImpl"/>
```



## Consuming the lunch-api

### pu.xml (or an ordinary spring.xml)

```xml
<bean id="asterixFrameworkBean" class="se.avanzabank.asterix.context.AsterixFrameworkBean">
	<property name="consumedAsterixBeans">
		<list>
			<value>se.avanzabank.asterix.integration.tests.domain.api.LunchService</value>
		</list>
	</property>
</bean>
```