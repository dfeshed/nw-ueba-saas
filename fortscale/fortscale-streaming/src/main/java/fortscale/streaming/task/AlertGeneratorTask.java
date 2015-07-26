package fortscale.streaming.task;

import com.espertech.esper.client.Configuration;
import com.espertech.esper.client.EPServiceProvider;
import com.espertech.esper.client.EPServiceProviderManager;
import com.espertech.esper.client.EPStatement;
import com.fasterxml.jackson.databind.ObjectMapper;
import fortscale.domain.core.EntityTags;
import fortscale.domain.core.EntityType;
import fortscale.services.AlertsService;
import fortscale.streaming.alert.subscribers.AlertSubscriber;
import fortscale.streaming.service.SpringService;
import org.apache.samza.config.Config;
import org.apache.samza.storage.kv.Entry;
import org.apache.samza.storage.kv.KeyValueIterator;
import org.apache.samza.storage.kv.KeyValueStore;
import org.apache.samza.system.IncomingMessageEnvelope;
import org.apache.samza.task.MessageCollector;
import org.apache.samza.task.TaskContext;
import org.apache.samza.task.TaskCoordinator;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.lang.reflect.Method;
import java.util.*;

import static fortscale.streaming.ConfigUtils.getConfigString;
import static fortscale.streaming.ConfigUtils.isConfigContainKey;

/**
 * Created by danal on 16/06/2015.
 */
public class AlertGeneratorTask extends AbstractStreamTask {

	private static Logger logger = LoggerFactory.getLogger(AlertGeneratorTask.class);

	List<EPStatement> epsStatements = new ArrayList<>();

	Map<String, RuleConfig> rulesConfiguration = new HashMap<>();

	Map<String, TopicConfiguration> inputTopicMapping = new HashMap<>();
	/**
	 * Esper service provider
	 */
	private EPServiceProvider epService;
	/**
	 * JSON serializer
	 */
	protected ObjectMapper mapper = new ObjectMapper();
	/**
	 * Alerts service (for Mongo export)
	 */
	protected AlertsService alertsService;

	@Override protected void wrappedProcess(IncomingMessageEnvelope envelope, MessageCollector collector,
			TaskCoordinator coordinator) throws Exception {
		// parse the message into json
		String inputTopic = envelope.getSystemStreamPartition().getSystemStream().getStream();
		if (inputTopicMapping.containsKey(inputTopic)) {
			Object info = convertMessageToEsperRepresentationObject(envelope, inputTopic);
			//send input data to Esper and save in cache if necessary
			if (info != null) {
				epService.getEPRuntime().sendEvent(info);
				if (inputTopicMapping.get(inputTopic).getKeyValueStore() != null) {
					KeyValueStore keyValueStore = inputTopicMapping.get(inputTopic).getKeyValueStore();
					keyValueStore.put(info.toString(), info);
				}
			}
		}
	}

	/*
	 *
	 * Convert string input data into relevant class representation
	 * Either by using direct class name given by configuration
	 * Or calling a conversion method which name is given by configuration
	 * Second option is necessary in cases where the object passing in the topic are not identical to the object need to pass into Esper (as in the case of cache topics)
	 *
	 */
	private Object convertMessageToEsperRepresentationObject(IncomingMessageEnvelope envelope,String inputTopic) {
		Object info = null;
		String messageText = (String) envelope.getMessage();
		try {
			if (inputTopicMapping.get(inputTopic).getClazz() != null) {
				info = mapper.readValue(messageText, inputTopicMapping.get(inputTopic).getClazz());
			} else if (inputTopicMapping.get(inputTopic).getMethod() != null) {
				info = inputTopicMapping.get(inputTopic).getMethod().invoke(this, (String) envelope.getKey(), messageText);
			}
		} catch (Exception ex) {
			logger.error("error parsing: " + messageText, ex);
		}
		return info;
	}


	@Override protected void wrappedWindow(MessageCollector collector, TaskCoordinator coordinator) throws Exception {
	}

	@Override protected void wrappedInit(Config config, TaskContext context) {
		alertsService = SpringService.getInstance().resolve(AlertsService.class);

		// creating the esper configuration
		Configuration esperConfig = new Configuration();

		// define package for Esper event type, each new event type should be part of this package
		esperConfig.addEventTypeAutoName("fortscale.domain.core");

		// define a Esper custom view - use for filtering out of order events from calender pre defined  windows
		esperConfig.addPlugInView("fortscale", "ext_timed_batch", "fortscale.streaming.alert.plugins.ExternallyTimedBatchViewFortscaleFactory");

		// creating the Esper service
		epService = EPServiceProviderManager.getDefaultProvider(esperConfig);
		createEsperConfiguration(config);
		createInputTopicMapping(config,context);
		updateEsperFromCache();
	}


	private void createEsperConfiguration(Config config){
		//subscribe instances of Esper EPL statements
		Config fieldsSubset = config.subset("fortscale.esper.rule.name.");
		ArrayList<String> fields = new ArrayList<String>(fieldsSubset.keySet());
		Collections.sort(fields);
		for (String rule : fields) {
			String ruleName = getConfigString(config, String.format("fortscale.esper.rule.name.%s", rule));
			String statement = getConfigString(config, String.format("fortscale.esper.rule.statement.%s", rule));
			String subscriberBeanName = getConfigString(config, String.format("fortscale.esper.rule.subscriberBean.%s", rule));
			boolean autoCreate = config.getBoolean(String.format("fortscale.esper.rule.auto-create.%s", rule));
			RuleConfig ruleConfig = new RuleConfig(ruleName, statement, autoCreate, subscriberBeanName);
			rulesConfiguration.put(ruleName, ruleConfig);
			if (autoCreate) {
				createStatement(ruleConfig);
			}
		}
	}

	private void createInputTopicMapping(Config config, TaskContext context) {
		Config inputTopicSubset = config.subset("fortscale.input.info.topic.");
		for (String inputInfo : inputTopicSubset.keySet()) {

			String inputTopic = getConfigString(config, String.format("fortscale.input.info.topic.%s", inputInfo));
			Class clazz = null;
			Method method = null;
			KeyValueStore keyValueStore = null;
			if (isConfigContainKey(config, String.format("fortscale.input.info.class.%s", inputInfo))) {

				String className = getConfigString(config, String.format("fortscale.input.info.class.%s", inputInfo));
				try {
					clazz = Class.forName(className);
				} catch (ClassNotFoundException e) {
					logger.error("can't find class " + className + " for input topic " + inputTopic);
				}
			}
			if (isConfigContainKey(config, String.format("fortscale.input.info.convert-method.%s", inputInfo))) {
				String methodName = getConfigString(config, String.format("fortscale.input.info.convert-method.%s", inputInfo));
				try{
					method = this.getClass().getMethod(methodName,Object.class,String.class);
				}
				catch (NoSuchMethodException e){
					e.printStackTrace();
					logger.error("can't find class " + methodName + " for input topic " + inputTopic);
				}
			}
			if (isConfigContainKey(config, String.format("fortscale.input.info.cache-name.%s", inputInfo))) {
				String cacheName = getConfigString(config, String.format("fortscale.input.info.cache-name.%s", inputInfo));
				keyValueStore = (KeyValueStore) context.getStore(cacheName);
			}
			inputTopicMapping.put(inputTopic, new TopicConfiguration(clazz, method, keyValueStore ));
		}
	}

	/*
	 * in the case of task failure, send esper all the current information saved in the cache
	 *
	 */
	private void updateEsperFromCache() {
		for (TopicConfiguration topicConfiguration : inputTopicMapping.values()) {
			if (topicConfiguration.getKeyValueStore() != null) {
				KeyValueStore keyValueStore = topicConfiguration.getKeyValueStore();
				KeyValueIterator keyValueIterator = keyValueStore.all();
				while(keyValueIterator.hasNext()){
					Entry entry = (Entry) keyValueIterator.next();
					epService.getEPRuntime().sendEvent(entry.getValue());
				}
				keyValueIterator.close();
			}
		}
	}

	@Override protected void wrappedClose() throws Exception {
		for (EPStatement esperEventStatement : epsStatements) {
			esperEventStatement.destroy();
		}
	}

	/**
	 * create esper statement according to given rule configuration and set a subscriber to that statement
	 */
	private void createStatement(RuleConfig ruleConfig) {
		//Create the Esper alert statement object
		EPStatement esperEventStatement = epService.getEPAdministrator().createEPL(ruleConfig.getStatement());
		//Generate Subscriber from spring
		if (!ruleConfig.getSubscriberBeanName().equals("none")) {
			AlertSubscriber alertSubscriber = (AlertSubscriber) SpringService.getInstance().resolve(ruleConfig.getSubscriberBeanName());
			//inits the subscriber with the alert Mongo service and the rule name
			alertSubscriber.init(alertsService);
			//subscribe Alert creation class to Esper EPL statement
			esperEventStatement.setSubscriber(alertSubscriber);
		}
		epsStatements.add(esperEventStatement);
	}

	public EntityTags createEntityTags(String userName,String tagMessageString) throws Exception{
		List<String> tags = mapper.readValue(tagMessageString, List.class);
		return new EntityTags(EntityType.User, userName, tags);
	}

	// inner class for holding input topic configurations
	protected static class TopicConfiguration {

		private Class clazz;

		private Method method;

		private KeyValueStore keyValueStore;

		public TopicConfiguration(Class clazz, Method method, KeyValueStore keyValueStore) {
			this.clazz = clazz;
			this.method = method;
			this.keyValueStore = keyValueStore;
		}

		public Class getClazz() {
			return clazz;
		}

		public void setClazz(Class clazz) {
			this.clazz = clazz;
		}

		public Method getMethod() {
			return method;
		}

		public void setMethod(Method method) {
			this.method = method;
		}

		public KeyValueStore getKeyValueStore() {
			return keyValueStore;
		}

		public void setKeyValueStore(KeyValueStore keyValueStore) {
			this.keyValueStore = keyValueStore;
		}
	}


	// inner class for holding rule configuration
	protected static class RuleConfig {

		private String name;
		private String statement;
		private boolean autoCreate;
		private String subscriberBeanName;

		public RuleConfig(String name, String statement, boolean autoCreate, String subscriberBeanName) {
			this.name = name;
			this.statement = statement;
			this.autoCreate = autoCreate;
			this.subscriberBeanName = subscriberBeanName;
		}

		public String getName() {
			return name;
		}

		public void setName(String name) {
			this.name = name;
		}

		public String getStatement() {
			return statement;
		}

		public void setStatement(String statement) {
			this.statement = statement;
		}

		public String getSubscriberBeanName() {
			return subscriberBeanName;
		}

		public void setSubscriberBeanName(String subscriberBeanName) {
			this.subscriberBeanName = subscriberBeanName;
		}

		public boolean isAutoCreate() {
			return autoCreate;
		}

		public void setAutoCreate(boolean autoCreate) {
			this.autoCreate = autoCreate;
		}
	}
}
