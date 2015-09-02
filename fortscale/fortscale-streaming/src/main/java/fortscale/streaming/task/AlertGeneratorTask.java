package fortscale.streaming.task;

import com.espertech.esper.client.Configuration;
import com.espertech.esper.client.EPServiceProvider;
import com.espertech.esper.client.EPServiceProviderManager;
import com.espertech.esper.client.EPStatement;
import com.fasterxml.jackson.databind.ObjectMapper;
import fortscale.streaming.alert.event.wrappers.EventWrapper;
import fortscale.streaming.alert.rule.RuleConfig;
import fortscale.streaming.alert.statement.decorators.DummyDecorator;
import fortscale.streaming.alert.statement.decorators.StatementDecorator;
import fortscale.streaming.alert.subscribers.AbstractSubscriber;
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

import java.io.File;
import java.util.*;

import static fortscale.streaming.ConfigUtils.*;

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

	@Override protected void wrappedInit(Config config, TaskContext context) {

		// creating the esper configuration
		Configuration esperConfig = new Configuration();
		String confFileName = getConfigString(config,"fortscale.esper.config.file.path");
		esperConfig.configure(new File(confFileName));
		// creating the Esper service
		epService = EPServiceProviderManager.getDefaultProvider(esperConfig);
		createEsperConfiguration(config);
		createInputTopicMapping(config,context);
		updateEsperFromCache();
	}


	@Override protected void wrappedProcess(IncomingMessageEnvelope envelope, MessageCollector collector,
			TaskCoordinator coordinator) throws Exception {
		// parse the message into json
		String inputTopic = envelope.getSystemStreamPartition().getSystemStream().getStream();
		if (inputTopicMapping.containsKey(inputTopic)) {
			Object info = convertMessageToEsperRepresentationObject(envelope, inputTopic);
			if (info != null) {

				createDynamicStatements(inputTopic, info);
				//send input data to Esper
				epService.getEPRuntime().sendEvent(info);

				//save input data in cache if necessary
				if (inputTopicMapping.get(inputTopic).getKeyValueStore() != null) {
					KeyValueStore keyValueStore = inputTopicMapping.get(inputTopic).getKeyValueStore();
					keyValueStore.put(info.toString(), info);
				}
			}
		}
		else{
			logger.warn("Can't handle events arriving from topic " + inputTopic + ", Doesn't have TopicConfiguration");
		}
	}


	@Override protected void wrappedWindow(MessageCollector collector, TaskCoordinator coordinator) throws Exception {
	}


	@Override protected void wrappedClose() throws Exception {
		for (EPStatement esperEventStatement : epsStatements) {
			try {
				esperEventStatement.destroy();
			}
			catch (Exception e){
				logger.error("",e);
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
			} else if (inputTopicMapping.get(inputTopic).getEventWrapper() != null) {
				info = inputTopicMapping.get(inputTopic).getEventWrapper().convertEvent(inputTopic, (String) envelope.getKey(), messageText);
			}
		} catch (Exception ex) {
			logger.error("error parsing: " + messageText + " from topic " + inputTopic, ex);
		}
		return info;
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
			rulesConfiguration.put(rule, ruleConfig);
			if (autoCreate) {
				createStatement(ruleConfig,new DummyDecorator());
			}
		}
	}

	private void createInputTopicMapping(Config config, TaskContext context) {
		Config inputTopicSubset = config.subset("fortscale.input.info.topic.");
		for (String inputInfo : inputTopicSubset.keySet()) {

			String inputTopic = getConfigString(config, String.format("fortscale.input.info.topic.%s", inputInfo));
			Class clazz = null;
			EventWrapper eventWrapper = null;
			KeyValueStore keyValueStore = null;
			List<String> dynamicStatements = null;
			if (isConfigContainKey(config, String.format("fortscale.input.info.class.%s", inputInfo))) {

				String className = getConfigString(config, String.format("fortscale.input.info.class.%s", inputInfo));
				try {
					clazz = Class.forName(className);
				} catch (ClassNotFoundException e) {
					logger.error("can't find class " + className + " for input topic " + inputTopic);
				}
			}
			if (isConfigContainKey(config, String.format("fortscale.input.info.event-wrapper.%s", inputInfo))) {
				String eventWrapperClassName = getConfigString(config, String.format("fortscale.input.info.event-wrapper.%s", inputInfo));
				try{
					eventWrapper = (EventWrapper) SpringService.getInstance().resolve(eventWrapperClassName);
				}
				catch (Exception e){
					e.printStackTrace();
					logger.error("can't find EventConverter " + eventWrapperClassName + " for input topic " + inputTopic);
				}
			}
			if (isConfigContainKey(config, String.format("fortscale.input.info.cache-name.%s", inputInfo))) {
				String cacheName = getConfigString(config, String.format("fortscale.input.info.cache-name.%s", inputInfo));
				keyValueStore = (KeyValueStore) context.getStore(cacheName);
			}
			if (isConfigContainKey(config, String.format("fortscale.input.info.dynamic-statements.%s", inputInfo))) {
				dynamicStatements = getConfigStringList(config, String.format("fortscale.input.info.dynamic-statements.%s", inputInfo));
			}
			inputTopicMapping.put(inputTopic, new TopicConfiguration(clazz, eventWrapper, keyValueStore, dynamicStatements));
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

	/**
	 * create esper statement according to given rule configuration and set a subscriber to that statement
	 */
	private void createStatement(RuleConfig ruleConfig, StatementDecorator statementDecorator, Object... decoratorParams) {
		//Create the Esper alert statement object
		ruleConfig = statementDecorator.prepareStatement(ruleConfig,decoratorParams);
		EPStatement epStatement;
		epStatement = epService.getEPAdministrator().createEPL(ruleConfig.getStatement());

		//Generate Subscriber from spring
		if (!ruleConfig.getSubscriberBeanName().equals("none")) {
			AbstractSubscriber alertSubscriber = (AbstractSubscriber) SpringService.getInstance().resolve(ruleConfig.getSubscriberBeanName());
			//inits the subscriber with the alert Mongo service and the rule name
			alertSubscriber.setEsperStatement(epStatement);
			//subscribe Alert creation class to Esper EPL statement
			epStatement.setSubscriber(alertSubscriber);
		}
		epsStatements.add(epStatement);
	}


	//create dynamic statements if necessary
	private  void createDynamicStatements(String inputTopic, Object info) throws Exception {
		if (inputTopicMapping.get(inputTopic).getDynamicStatements() != null && inputTopicMapping.get(inputTopic).getEventWrapper() != null) {
			EventWrapper eventWrapper = inputTopicMapping.get(inputTopic).getEventWrapper();
			if (eventWrapper.shouldCreateDynamicStatements(info)) {
				List<String> dynamicStatements = inputTopicMapping.get(inputTopic).getDynamicStatements();
				for (String dynamicStatement : dynamicStatements) {
					RuleConfig ruleConfig = rulesConfiguration.get(dynamicStatement);
					createStatement(ruleConfig, eventWrapper.getStatementDecorator(), eventWrapper.getDecoratorParams(info));
				}
			}
		}
	}




	// inner class for holding input topic configurations
	protected static class TopicConfiguration {

		private Class clazz;

		private EventWrapper eventWrapper;

		private KeyValueStore keyValueStore;

		private List<String> dynamicStatements;

		public TopicConfiguration(Class clazz, EventWrapper eventWrapper, KeyValueStore keyValueStore, List<String> dynamicStatements) {
			this.clazz = clazz;
			this.eventWrapper = eventWrapper;
			this.keyValueStore = keyValueStore;
			this.dynamicStatements = dynamicStatements;
		}

		public Class getClazz() {
			return clazz;
		}

		public void setClazz(Class clazz) {
			this.clazz = clazz;
		}

		public EventWrapper getEventWrapper() {
			return eventWrapper;
		}

		public void setEventWrapper(EventWrapper eventWrapper) {
			this.eventWrapper = eventWrapper;
		}

		public KeyValueStore getKeyValueStore() {
			return keyValueStore;
		}

		public void setKeyValueStore(KeyValueStore keyValueStore) {
			this.keyValueStore = keyValueStore;
		}

		public List<String> getDynamicStatements() {
			return dynamicStatements;
		}

		public void setDynamicStatements(List<String> dynamicStatements) {
			this.dynamicStatements = dynamicStatements;
		}
	}

}
