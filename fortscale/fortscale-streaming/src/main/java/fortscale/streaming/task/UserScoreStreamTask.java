package fortscale.streaming.task;

import static fortscale.streaming.ConfigUtils.getConfigString;
import static fortscale.utils.ConversionUtils.convertToDouble;
import static fortscale.utils.ConversionUtils.convertToLong;
import static fortscale.utils.ConversionUtils.convertToString;

import java.util.HashMap;
import java.util.Map;

import org.apache.commons.lang.StringUtils;
import org.apache.samza.config.Config;
import org.apache.samza.metrics.Counter;
import org.apache.samza.storage.kv.KeyValueStore;
import org.apache.samza.system.IncomingMessageEnvelope;
import org.apache.samza.task.ClosableTask;
import org.apache.samza.task.InitableTask;
import org.apache.samza.task.MessageCollector;
import org.apache.samza.task.TaskContext;
import org.apache.samza.task.TaskCoordinator;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.google.common.collect.Iterables;

import fortscale.streaming.FortscaleStreamingProperties;
import fortscale.streaming.exceptions.StreamMessageNotContainFieldException;
import fortscale.streaming.model.UserEventTypePair;
import fortscale.streaming.model.UserTopEvents;
import fortscale.services.impl.SpringService;
import fortscale.streaming.service.UserScoreStreamingService;
import fortscale.utils.StringPredicates;
import fortscale.utils.time.TimestampUtils;
import net.minidev.json.JSONObject;
import net.minidev.json.JSONValue;

public class UserScoreStreamTask  extends AbstractStreamTask  implements InitableTask, ClosableTask{

	private static Logger logger = LoggerFactory.getLogger(UserScoreStreamTask.class);

	// magic message to cleanup data source scores, contains a fixed text and data source name
	private static String cleanupSignalEvent = "CLEANUP-";
	private static String DATA_SOURCE_PREFIX_CONFIGURATION = "fortscale.data.source.";

	private Map<String, DataSourceConfiguration> dataSourceToConfigurationMap = new HashMap<>();
	private Map<String, String> topicToDataSourceMap = new HashMap<>();
	private UserScoreStreamingService userScoreStreamingService;
	private String dataSourceFieldName;

	
	@SuppressWarnings("unchecked")
	@Override
	protected void wrappedInit(Config config, TaskContext context) throws Exception {
		
		FortscaleStreamingProperties fortscaleStreamingProperties = SpringService.getInstance().resolve(FortscaleStreamingProperties.class);
		dataSourceFieldName = fortscaleStreamingProperties.getDataSourceFieldName();

		// get task configuration parameters
		boolean isUseLatestEventTimeAsCurrentTime = config.getBoolean("fortscale.use.latest.event.time.as.current.time", false);
		
		// get the store that holds user top events
		String storeName = getConfigString(config, "fortscale.store.name");
		KeyValueStore<UserEventTypePair, UserTopEvents> store = (KeyValueStore<UserEventTypePair, UserTopEvents>)context.getStore(storeName);
		
		//get the user score streaming service and set it with the store and the classifier id.
		userScoreStreamingService = SpringService.getInstance().resolve(UserScoreStreamingService.class);
		userScoreStreamingService.setStore(store);
		userScoreStreamingService.setUseLatestEventTimeAsCurrentTime(isUseLatestEventTimeAsCurrentTime);

		// build a map that converts topic names to data sources and register metrics for each input topic
		Config fieldsSubset = config.subset(DATA_SOURCE_PREFIX_CONFIGURATION);
		for (String fieldConfigKey : Iterables.filter(fieldsSubset.keySet(), StringPredicates.endsWith(".classifier"))) {
			String dataSource = fieldConfigKey.substring(0, fieldConfigKey.indexOf(".classifier"));
			String usernameField = getConfigString(config, String.format("%s%s.username.field", DATA_SOURCE_PREFIX_CONFIGURATION,dataSource));
			String timestampField = getConfigString(config, String.format("%s%s.timestamp.field", DATA_SOURCE_PREFIX_CONFIGURATION,dataSource));
			String eventScoreField = getConfigString(config, String.format("%s%s.event.score.field", DATA_SOURCE_PREFIX_CONFIGURATION,dataSource));
			String classifier = getConfigString(config, String.format("%s%s.classifier", DATA_SOURCE_PREFIX_CONFIGURATION,dataSource));
			Counter latestEventCounter = context.getMetricsRegistry().newCounter(getClass().getName(), String.format("%s-epochtime", dataSource));
			// register data source configuration
			dataSourceToConfigurationMap.put(dataSource, new DataSourceConfiguration(usernameField, timestampField, eventScoreField, classifier, latestEventCounter));
			// for cases that data source has its own topic.
			String topic = config.get(String.format("%s%s.input.topic", DATA_SOURCE_PREFIX_CONFIGURATION,dataSource));
			if(!StringUtils.isBlank(topic)){
				topicToDataSourceMap.put(topic, dataSource);
			}
		}
		
	}
		
	/** Process incoming events and update the user models stats */
	@Override
	public void wrappedProcess(IncomingMessageEnvelope envelope, MessageCollector collector, TaskCoordinator coordinator) throws Exception {
		String topicName = envelope.getSystemStreamPartition().getSystemStream().getStream();
		String messageText = (String)envelope.getMessage();

		// check if we received a magical cleanup event to delete a data source scores
		if (messageText.startsWith(cleanupSignalEvent)) {
			// get the data source classifier from the message
			String classifierId = messageText.substring(cleanupSignalEvent.length());
			userScoreStreamingService.cleanupScores(classifierId);

		} else {
			// process regular scored event message		
			JSONObject event = (JSONObject) JSONValue.parseWithException(messageText);
			DataSourceConfiguration dataSourceConfiguration = getDataSourceConfiguration(event, topicName);

			// get the username, so that we can get the model from store
			String username = getUsername(event, dataSourceConfiguration, messageText);

			// get the timestamp from the message
			Long timestamp = getTimestamp(event, dataSourceConfiguration, messageText);

			// get the event score from the message
			Double eventScore = getEventScore(event, dataSourceConfiguration, messageText);

			userScoreStreamingService.updateUserWithEventScore(username, dataSourceConfiguration.classifierId, eventScore, TimestampUtils.convertToMilliSeconds(timestamp));
			dataSourceConfiguration.latestEventCounter.set(timestamp);
		}
	}
	
	private String getUsername(JSONObject event, DataSourceConfiguration dataSourceConfiguration, String messageText) throws StreamMessageNotContainFieldException{
		String username = convertToString(event.get(dataSourceConfiguration.usernameField));
		if (StringUtils.isEmpty(username)) {
			//logger.error("message {} does not contains username in field {}", messageText, usernameField);
			throw new StreamMessageNotContainFieldException(messageText, dataSourceConfiguration.usernameField);
		}
		
		return username;
	}
	
	private Long getTimestamp(JSONObject event, DataSourceConfiguration dataSourceConfiguration, String messageText) throws StreamMessageNotContainFieldException{
		Long timestamp = convertToLong(event.get(dataSourceConfiguration.timestampField));
		if (timestamp == null) {
			//logger.error("message {} does not contains timestamp in field {}", messageText, timestampField);
			throw new StreamMessageNotContainFieldException(messageText, dataSourceConfiguration.timestampField);
		}
		
		return timestamp;
	}

	private Double getEventScore(JSONObject event, DataSourceConfiguration dataSourceConfiguration, String messageText) throws StreamMessageNotContainFieldException{
		Double eventScore = convertToDouble(event.get(dataSourceConfiguration.eventScoreField));
		if (eventScore == null) {
			//logger.error("message {} does not contains event score in field {}", messageText, eventScoreField);
			throw new StreamMessageNotContainFieldException(messageText, dataSourceConfiguration.eventScoreField);
		}
		
		return eventScore;
	}
	
	private DataSourceConfiguration getDataSourceConfiguration(JSONObject event, String topicName) throws Exception{
		String dataSource = (String) event.get(dataSourceFieldName);
		if(dataSource == null){
			// convert topic to data source name
			dataSource = topicToDataSourceMap.get(topicName);
			if(dataSource == null){
				String errMsg = String.format("received event which doesn't contains the data source field and the topic %s is not mapped to any data source", topicName);
				logger.error(errMsg);
				throw new Exception(errMsg);
			}
		}
		DataSourceConfiguration dataSourceConfiguration = dataSourceToConfigurationMap.get(dataSource);
		if (dataSourceConfiguration==null) {
			String errMsg = String.format("recieved event with data source %s which is not configured in the task", dataSource);
			logger.error(errMsg);
			throw new Exception(errMsg);
		}
		
		return dataSourceConfiguration;
	}
	
	/** periodically save the state to mongodb as a secondary backing store and update the user score in mongodb*/
	@Override public void wrappedWindow(MessageCollector collector, TaskCoordinator coordinator) {}
	
	/** save the state to mongodb when the job shutsdown */
	@Override 
	protected void wrappedClose() throws Exception {
		if (userScoreStreamingService!=null) {
			userScoreStreamingService.exportSnapshot();
		}
		userScoreStreamingService = null;
	}

	/**
	 * Configuration for each topic how to extract fields from it
	 */
	private static class DataSourceConfiguration {
		public String usernameField;
		public String timestampField;
		public String eventScoreField;
		public String classifierId;
		public Counter latestEventCounter;

		public DataSourceConfiguration(String usernameField, String timestampField, String eventScoreField, String classifierId, Counter latestEventCounter) {
			this.usernameField = usernameField;
			this.timestampField = timestampField;
			this.eventScoreField = eventScoreField;
			this.classifierId = classifierId;
			this.latestEventCounter = latestEventCounter;
		}
	}
}
