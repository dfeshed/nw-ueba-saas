package fortscale.streaming.task;

import com.espertech.esper.client.Configuration;
import com.espertech.esper.client.EPServiceProvider;
import com.espertech.esper.client.EPServiceProviderManager;
import com.espertech.esper.client.EPStatement;
import com.fasterxml.jackson.databind.ObjectMapper;
import fortscale.services.impl.SpringService;
import fortscale.services.impl.UserTagsCacheServiceImpl;
import fortscale.streaming.alert.event.wrappers.EventWrapper;
import fortscale.streaming.alert.rule.RuleConfig;
import fortscale.streaming.alert.statement.decorators.DummyDecorator;
import fortscale.streaming.alert.statement.decorators.StatementDecorator;
import fortscale.streaming.alert.subscribers.AbstractSubscriber;
import net.minidev.json.JSONObject;
import net.minidev.json.JSONValue;
import org.apache.samza.config.Config;
import org.apache.samza.config.MapConfig;
import org.apache.samza.metrics.Counter;
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
import java.io.FileInputStream;
import java.io.IOException;
import java.util.*;

import static fortscale.streaming.ConfigUtils.*;
import static fortscale.utils.ConversionUtils.convertToLong;

/**
 * Created by danal on 16/06/2015.
 */
public class AlertGeneratorTask extends AbstractStreamTask
{
	private static Logger logger = LoggerFactory.getLogger(AlertGeneratorTask.class);

    private static String topicConfigKeyFormat = "fortscale.%s.service.cache.topic";

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

	private Counter lastTimestampCount;

	@Override protected void wrappedInit(Config config, TaskContext context) throws Exception{

		// creating the esper configuration
		Configuration esperConfig = new Configuration();
		//The EsperConfig.xml file that initialized Esper
		String confFileName = getConfigString(config,"fortscale.esper.config.file.path");
		//The properties file with all Esper rules
		String rulesFileName = getConfigString(config,"fortscale.esper.rules.file.path");
		esperConfig.configure(new File(confFileName));
		// Added for prohibiting from logging of " Spin wait timeout exceeded in". This thing is better for performence.
		esperConfig.getEngineDefaults().getThreading().setInsertIntoDispatchPreserveOrder(false);

        //used for debug Esper
		//after enabling this part, add before each rule you want to debug the prefix: '@Name("Esper_rule_name") @Audit '
		/*esperConfig.getEngineDefaults().getLogging().setAuditPattern("[%u] [%s] EsperMessage %m");
		esperConfig.getEngineDefaults().getLogging().setEnableExecutionDebug(true);
		esperConfig.getEngineDefaults().getLogging().setEnableTimerDebug(false);
		esperConfig.getEngineDefaults().getLogging().setEnableQueryPlan(true);*/

		// creating the Esper service
		epService = EPServiceProviderManager.getDefaultProvider(esperConfig);
		createEsperConfiguration(rulesFileName);
		createInputTopicMapping(config, context);
		updateEsperFromCache();

		lastTimestampCount = context.getMetricsRegistry().newCounter(getClass().getName(),
				String.format("%s-last-message-epochtime", config.get("job.name")));
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

				String messageText = (String) envelope.getMessage();
				try {
					if (inputTopicMapping.get(inputTopic).getTimeStampField()!=null) {
						// parse the message into json
						JSONObject message = (JSONObject) JSONValue.parse(messageText);
						Long endTimestampSeconds = convertToLong(message.get(inputTopicMapping.get(inputTopic).getTimeStampField()));
						lastTimestampCount.set(endTimestampSeconds);
                    }
				} catch (Exception ex) {
					logger.error("Failed to extract timestamp from message - {}, error is - {}", messageText, ex);
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

	/**
	 * initializing esper rules, variables, subscribers from properties file
	 * 1. We first read the file from ~/fortscale/streaming/config/Esper/esper-rules.properties
	 * 2. Then we load it into Properties class
	 * 3. Then we load it into Config class of Samza
	 * 4. The Config class is very useful for functions like subset() etc.
	 * @param rulesFilePath
	 */
	private void createEsperConfiguration(String rulesFilePath) throws IOException {
		Properties properties = new Properties();
		FileInputStream fileInputStream = new FileInputStream(new File(rulesFilePath));
		properties.load(fileInputStream);
		Map<String,String> propMap = new HashMap<>();
		for(Object key: properties.keySet()){
			String keyStr = (String) key;
			propMap.put(keyStr, properties.getProperty(keyStr));
		}
		Config config = new MapConfig(propMap);

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

			List<String> inputTopics = getConfigStringList(config, String.format("fortscale.input.info.topic.%s", inputInfo));
			Class clazz = null;
			String timeStampField = null;
			EventWrapper eventWrapper = null;
			KeyValueStore keyValueStore = null;
			List<String> dynamicStatements = null;
			if (isConfigContainKey(config, String.format("fortscale.input.info.class.%s", inputInfo))) {

				String className = getConfigString(config, String.format("fortscale.input.info.class.%s", inputInfo));
				try {
					clazz = Class.forName(className);
				} catch (ClassNotFoundException e) {
					logger.error("can't find class " + className + " for input topics " + inputTopics);
				}
			}
			if (isConfigContainKey(config, String.format("fortscale.input.info.event-wrapper.%s", inputInfo))) {
				String eventWrapperClassName = getConfigString(config, String.format("fortscale.input.info.event-wrapper.%s", inputInfo));
				try{
					eventWrapper = (EventWrapper) SpringService.getInstance().resolve(eventWrapperClassName);
				}
				catch (Exception e){
					e.printStackTrace();
					logger.error("can't find EventConverter " + eventWrapperClassName + " for input topics " + inputTopics);
				}
			}
			if (isConfigContainKey(config, String.format("fortscale.input.info.cache-name.%s", inputInfo))) {
				String cacheName = getConfigString(config, String.format("fortscale.input.info.cache-name.%s", inputInfo));
				keyValueStore = (KeyValueStore) context.getStore(cacheName);
			}
			if (isConfigContainKey(config, String.format("fortscale.input.info.dynamic-statements.%s", inputInfo))) {
				dynamicStatements = getConfigStringList(config, String.format("fortscale.input.info.dynamic-statements.%s", inputInfo));
			}
			if (isConfigContainKey(config, String.format("fortscale.input.info.timestampfield.%s", inputInfo))) {
				timeStampField = getConfigString(config, String.format("fortscale.input.info.timestampfield.%s", inputInfo));
			}
			TopicConfiguration topicConfiguration = new TopicConfiguration(clazz, eventWrapper, keyValueStore, dynamicStatements, timeStampField);
			for(String inputTopic: inputTopics) {
				inputTopicMapping.put(inputTopic, topicConfiguration);
			}
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
		try {
			epStatement = epService.getEPAdministrator().createEPL(ruleConfig.getStatement());
		}
		catch (Exception ex) {
			return;
		}
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

		private String timeStampField;

		public TopicConfiguration(Class clazz, EventWrapper eventWrapper, KeyValueStore keyValueStore,
								  List<String> dynamicStatements, String timeStampField) {
			this.clazz = clazz;
			this.eventWrapper = eventWrapper;
			this.keyValueStore = keyValueStore;
			this.dynamicStatements = dynamicStatements;
			this.timeStampField = timeStampField;
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

		public String getTimeStampField() {
			return timeStampField;
		}

		public void setTimeStampField(String timeStampField) {
			this.timeStampField = timeStampField;
		}
	}
}
