package fortscale.streaming.task.enrichment;


import fortscale.services.UserService;
import fortscale.streaming.exceptions.StreamMessageNotContainFieldException;
import fortscale.streaming.service.SpringService;
import fortscale.streaming.task.AbstractStreamTask;
import fortscale.utils.TimestampUtils;
import net.minidev.json.JSONValue;
import org.apache.commons.lang3.tuple.Pair;
import org.apache.samza.config.Config;
import org.apache.samza.storage.kv.Entry;
import org.apache.samza.storage.kv.KeyValueIterator;
import org.apache.samza.storage.kv.KeyValueStore;
import org.apache.samza.system.IncomingMessageEnvelope;
import org.apache.samza.task.MessageCollector;
import org.apache.samza.task.TaskContext;
import org.apache.samza.task.TaskCoordinator;
import parquet.org.slf4j.Logger;
import parquet.org.slf4j.LoggerFactory;

import java.util.*;

import static fortscale.streaming.ConfigUtils.getConfigString;
import static fortscale.utils.ConversionUtils.convertToLong;
import static fortscale.utils.ConversionUtils.convertToString;

/**
 * Update User's last activity:
 * 1. In level DB (state) during process
 * 2. In Mongo from time to time
 * Date: 1/11/2015.
 */
public class UserLastActivityTask extends AbstractStreamTask {

	/**
	 * The level DB store name
	 */
	private static final String storeName = "user-last-activity";

	/**
	 * Logger
	 */
	private static Logger logger = LoggerFactory.getLogger(UserLastActivityTask.class);

	/**
	 * The level DB store: username to last activity (map of data-source to time)
	 */
	protected KeyValueStore<String, Map<String, Long>> store;

	/**
	 * The time field in the input event
	 */
	protected String timestampField;

	/**
	 * The username field in the input event
	 */
	protected String usernameField;

	/**
	 * user service (for Mongo)
	 */
	protected UserService userService;

	/**
	 * Map between the input topic and the relevant data-source
	 */
	protected Map<String, dataSourceConfiguration> topicToDataSourceMap = new HashMap<>();


	/**
	 * Init task after spring context is up
	 * @param config	Samza task configuration
	 * @param context	Samza task context
	 * @throws Exception
	 */
	@Override
	protected void wrappedInit(Config config, TaskContext context) throws Exception {

		// Get the levelDB store
		store = (KeyValueStore<String, Map<String, Long>>) context.getStore(storeName);

		// Get field names
		timestampField = getConfigString(config, "fortscale.timestamp.field");
		usernameField = getConfigString(config, "fortscale.username.field");

		// Get the user service (for Mongo) from spring
		userService = SpringService.getInstance().resolve(UserService.class);

		// Fill the map between the input topic and the data source
		Config fieldsSubset = config.subset("fortscale.data-source.input.topic.");
		for (String dataSource : fieldsSubset.keySet()) {
			String inputTopic = getConfigString(config, String.format("fortscale.data-source.input.topic.%s", dataSource));
			String classifier = getConfigString(config, String.format("fortscale.data-source.classifier.%s", dataSource));
			String successfulLoginField = getConfigString(config, String.format("fortscale.data-source.success.field.%s", dataSource));
			String successfulLoginValue = getConfigString(config, String.format("fortscale.data-source.success.value.%s", dataSource));
			topicToDataSourceMap.put(inputTopic, new dataSourceConfiguration(classifier, successfulLoginField, successfulLoginValue));
		}

	}



	/**
	 * Process specific message
	 * @param envelope	The message
	 * @param collector collector in order to send the message to other topics - not used in this task
	 * @param coordinator	coordinator
	 * @throws Exception
	 */
	@Override
	protected void wrappedProcess(IncomingMessageEnvelope envelope, MessageCollector collector,
			TaskCoordinator coordinator) throws Exception {

		// parse the message into json
		String messageText = (String) envelope.getMessage();
		net.minidev.json.JSONObject message = (net.minidev.json.JSONObject) JSONValue.parseWithException(messageText);

		// get the timestamp from the event
		Long timestampSeconds = convertToLong(message.get(timestampField));
		if (timestampSeconds == null) {
			logger.error("message {} does not contains timestamp in field {}", messageText, timestampField);
			throw new StreamMessageNotContainFieldException(messageText, timestampField);
		}
		Long timestamp = TimestampUtils.convertToMilliSeconds(timestampSeconds);

		// get the username from the event
		String normalizedUsername = convertToString(message.get(usernameField));
		if (normalizedUsername == null) {
			logger.error("message {} does not contains username in field {}", messageText, usernameField);
			throw new StreamMessageNotContainFieldException(messageText, usernameField);
		}

		// Get the input topic
		String topic = envelope.getSystemStreamPartition().getSystemStream().getStream();

		// Get relevant data source according to topic
		dataSourceConfiguration dataSourceConfiguration = topicToDataSourceMap.get(topic);
		if (dataSourceConfiguration == null) {
			logger.error("No data source is defined for input topic {} ", topic);
			return;
		}

		// check that the event represent successful login
		if (convertToString(message.get(dataSourceConfiguration.successField)).equalsIgnoreCase(dataSourceConfiguration.successValue)) {
			// Find the last activity of the user (if exist) and update it if the event is newer than the event's activity
			updateLastActivityInStore(timestamp, normalizedUsername, dataSourceConfiguration.mongoClassifierId);

		}



		// No output topic -> this is the last task in the chain

	}

	/**
	 * Find the last activity of the user (if exist) and update it if the event is newer than the event's activity
	 * @param timestamp    the time of the event
	 * @param normalizedUsername    the user
	 * @param classifierId	The classifier in Mongo for this data-source
	 */
	private void updateLastActivityInStore(Long timestamp, String normalizedUsername, String classifierId) {

		Map<String, Long> dataSourceToTimestamp = store.get(normalizedUsername);

		if (dataSourceToTimestamp == null) {
			// Since the same user will be always on the same partition, no need to synchronize this
			dataSourceToTimestamp = new HashMap<>();
			store.put(normalizedUsername, dataSourceToTimestamp);
		}



		Long userLastActivity = dataSourceToTimestamp.get(classifierId);
		if(userLastActivity == null || userLastActivity < timestamp){
			// update last activity in level DB
			dataSourceToTimestamp.put(classifierId, timestamp);
			store.put(normalizedUsername, dataSourceToTimestamp);
		}
	}

	@Override
	protected void wrappedWindow(MessageCollector collector, TaskCoordinator coordinator) throws Exception {

		// copy level DB to mongo DB
		if (userService !=null) {
			copyLevelDbToMongoDB();
		}

	}

	/**
	 * The close method should be called upon streaming task shutdown
	 * @throws Exception
	 */
	@Override
	protected void wrappedClose() throws Exception {

		// copy level DB to mongo DB
		if (userService != null) {
			copyLevelDbToMongoDB();
		}
		userService = null;

	}


	/**
	 * Go over all users in the last-activity map and write them to Mongo
	 */
	private void copyLevelDbToMongoDB() {
		KeyValueIterator<String, Map<String, Long>> iter = store.all();
		List<String> usernames = new LinkedList<>();
		while (iter.hasNext()) {
			Entry<String, Map<String, Long>> user = iter.next();
			// update user in mongo
			userService.updateUsersLastActivityGeneralAndPerType(user.getKey(), user.getValue());
			usernames.add(user.getKey());
		}
		iter.close();

		// remove from store all users after they were copied to Mongo
		for (String username : usernames) {
			store.delete(username);
		}

	}

	/**
	 * Private class for saving data-source specific configuration in-memory
	 */
	protected static class dataSourceConfiguration {

		protected dataSourceConfiguration(String mongoClassifierId, String successField, String successValue) {
			this.mongoClassifierId = mongoClassifierId;
			this.successField = successField;
			this.successValue = successValue;
		}

		public String mongoClassifierId;
		public String successField;
		public String successValue;
	}



}
