package fortscale.streaming.task;

import static fortscale.streaming.ConfigUtils.getConfigString;
import static fortscale.utils.ConversionUtils.convertToDouble;
import static fortscale.utils.ConversionUtils.convertToLong;
import static fortscale.utils.ConversionUtils.convertToString;

import fortscale.streaming.model.UserEventTypePair;
import net.minidev.json.JSONObject;
import net.minidev.json.JSONValue;

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

import fortscale.streaming.exceptions.StreamMessageNotContainFieldException;
import fortscale.streaming.model.UserTopEvents;
import fortscale.streaming.service.SpringService;
import fortscale.streaming.service.UserScoreStreamingService;
import fortscale.utils.TimestampUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class UserScoreStreamTask  extends AbstractStreamTask  implements InitableTask, ClosableTask{

	private static Logger logger = LoggerFactory.getLogger(UserScoreStreamTask.class);

	// magic message to cleanup data source scores, contains a fixed text and data source name
	private static String cleanupSignalEvent = "CLEANUP-";

	private Map<String, TopicConfiguration> topicToDataSourceMap = new HashMap<>();
	private UserScoreStreamingService userScoreStreamingService;

	
	@SuppressWarnings("unchecked")
	@Override
	protected void wrappedInit(Config config, TaskContext context) throws Exception {

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
		for (String inputTopic : config.getList("task.inputs")) {
			String topic = inputTopic.substring(inputTopic.indexOf(".")+1);

			String usernameField = getConfigString(config, String.format("fortscale.topic.%s.username.field", topic));
			String timestampField = getConfigString(config, String.format("fortscale.topic.%s.timestamp.field", topic));
			String eventScoreField = getConfigString(config, String.format("fortscale.topic.%s.event.score.field", topic));
			String dataSource = getConfigString(config, String.format("fortscale.topic.%s.classifier", topic));

			// register counter for topic
			Counter latestEventCounter = context.getMetricsRegistry().newCounter(getClass().getName(), String.format("%s-epochtime", topic));

			// register data source mapping for topic
			topicToDataSourceMap.put(topic, new TopicConfiguration(usernameField, timestampField, eventScoreField, dataSource, latestEventCounter));
		}
	}
	
	/** Process incoming events and update the user models stats */
	@Override
	public void wrappedProcess(IncomingMessageEnvelope envelope, MessageCollector collector, TaskCoordinator coordinator) throws Exception {
		// get the input topic name and convert it to data source name
		String topicName = envelope.getSystemStreamPartition().getSystemStream().getStream();
		TopicConfiguration topicConfiguration = topicToDataSourceMap.get(topicName);
		if (topicConfiguration==null) {
			logger.error("received events from topic {} without mapping to data source", topicName);
			throw new Exception(String.format("received events from topic %s without mapping to data source", topicName));
		}

		// parse the message into json
		String messageText = (String)envelope.getMessage();

		// check if we received a magical cleanup event to delete a data source scores
		if (messageText.startsWith(cleanupSignalEvent)) {
			// get the data source classifier from the message
			String classifierId = messageText.substring(cleanupSignalEvent.length());
			userScoreStreamingService.cleanupScores(classifierId);

		} else {
			// process regular scored event message

			JSONObject message = (JSONObject) JSONValue.parseWithException(messageText);

			// get the username, so that we can get the model from store
			String username = convertToString(message.get(topicConfiguration.usernameField));
			if (StringUtils.isEmpty(username)) {
				//logger.error("message {} does not contains username in field {}", messageText, usernameField);
				throw new StreamMessageNotContainFieldException(messageText, topicConfiguration.usernameField);
			}

			// get the timestamp from the message
			Long timestamp = convertToLong(message.get(topicConfiguration.timestampField));
			if (timestamp == null) {
				//logger.error("message {} does not contains timestamp in field {}", messageText, timestampField);
				throw new StreamMessageNotContainFieldException(messageText, topicConfiguration.timestampField);
			}

			// get the event score from the message
			Double eventScore = convertToDouble(message.get(topicConfiguration.eventScoreField));
			if (eventScore == null) {
				//logger.error("message {} does not contains event score in field {}", messageText, eventScoreField);
				throw new StreamMessageNotContainFieldException(messageText, topicConfiguration.eventScoreField);
			}

			userScoreStreamingService.updateUserWithEventScore(username, topicConfiguration.classifierId, eventScore, TimestampUtils.convertToMilliSeconds(timestamp));
			topicConfiguration.topicCounter.set(timestamp);
		}
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
	private static class TopicConfiguration {
		public String usernameField;
		public String timestampField;
		public String eventScoreField;
		public String classifierId;
		public Counter topicCounter;

		public TopicConfiguration(String usernameField, String timestampField, String eventScoreField, String classifierId, Counter topicCounter) {
			this.usernameField = usernameField;
			this.timestampField = timestampField;
			this.eventScoreField = eventScoreField;
			this.classifierId = classifierId;
			this.topicCounter = topicCounter;
		}
	}
}
