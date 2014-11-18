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

import java.util.Collection;
import java.util.Objects;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.ThreadFactory;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicInteger;

import javax.annotation.PreDestroy;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Periodically polls all AstrixMetricsCollectorPlugins and sends the data they provide to an
 * AstrixMetricLoggerPlugin.
 * 
 * @author Kristoffer Erlandsson (krierl)
 */
public class MetricsPoller implements AstrixPluginsAware, AstrixSettingsAware {

	private static final Integer DEFAULT_DELAY = 5000;
	private AstrixPlugins plugins;
	private Collection<AstrixMetricsLoggerPlugin> loggers;
	private Collection<AstrixMetricsCollectorPlugin> collectors;
	ScheduledExecutorService executor;

	private static final Logger log = LoggerFactory.getLogger(MetricsPoller.class);
	private AstrixSettingsReader settings;

	public void start() {
		initializeFromPlugins();
		long delayTime = getDelayTimeFromJndiOrFallback();
		log.info("Starting metrics poller using logger {} and collectors {}", loggers, collectors);
		executor = Executors.newScheduledThreadPool(1, new NamedThreadFactory("MetricsPoller"));
		executor.scheduleAtFixedRate(new MetricPollerTask(loggers, collectors), 0, delayTime, TimeUnit.MILLISECONDS);
	}

	private void initializeFromPlugins() {
		loggers = plugins.getPlugins(AstrixMetricsLoggerPlugin.class);
		collectors = plugins.getPlugins(AstrixMetricsCollectorPlugin.class);
	}

	private long getDelayTimeFromJndiOrFallback() {
		return settings.getLong("HYSTRIX_GRAPHITE_DELAY_TIME", DEFAULT_DELAY);
	}

	/**
	 * Stops the scheduled poller. Does not wait for anything to terminate.
	 */
	@PreDestroy
	public void stop() {
		log.info("Stopping metrics poller");
		if (executor != null) {
			executor.shutdown();
		}
	}

	@Override
	public void setPlugins(AstrixPlugins plugins) {
		this.plugins = plugins;
	}

	private static class MetricPollerTask implements Runnable {

		private Collection<AstrixMetricsLoggerPlugin> loggers;
		private Collection<AstrixMetricsCollectorPlugin> collectors;

		public MetricPollerTask(Collection<AstrixMetricsLoggerPlugin> loggers, Collection<AstrixMetricsCollectorPlugin> collectors) {
			this.loggers = loggers;
			this.collectors = collectors;
		}

		@Override
		public void run() {
			for (AstrixMetricsCollectorPlugin collector : collectors) {
				for (AstrixMetricsLoggerPlugin logger : loggers) {
					logger.logMetrics(collector.getMetrics());
				}
			}
		}

	}

	@Override
	public void setSettings(AstrixSettingsReader settings) {
		this.settings = settings;
	}
	
	/**
	 * Thread factory for creating named threads. Threads are daemon threads per default.
	 * 
	 * @author Kristoffer Erlandsson (krierl), kristoffer.erlandsson@avanzabank.se
	 */
	private static class NamedThreadFactory implements ThreadFactory {

		private final ThreadGroup group;
		private final AtomicInteger threadNumber = new AtomicInteger(1);
		private final String namePrefix;
		private boolean daemon;

		/**
		 * Creates a daemon thread with the specified name prefix. Thread names will be namePrefix-<threadId>. Thread ID is
		 * incremented each time a thread is created using this factory.
		 * 
		 * @param namePrefix
		 *            not null.
		 */
		public NamedThreadFactory(String namePrefix) {
			this.namePrefix = Objects.requireNonNull(namePrefix);
			group = getThreadGroup();
			daemon = true;
		}

		private ThreadGroup getThreadGroup() {
			// Done in the same way as java.util.concurrent.Executors.DefaultThreadFactory.
			SecurityManager s = System.getSecurityManager();
			return (s != null) ? s.getThreadGroup() :
					Thread.currentThread().getThreadGroup();
		}

		@Override
		public Thread newThread(Runnable r) {
			Thread t = new Thread(group, r, namePrefix + "-" + threadNumber.getAndIncrement());
			t.setDaemon(daemon);
			return t;
		}
	}

}
