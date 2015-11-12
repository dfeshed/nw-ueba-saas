package fortscale.streaming.task.enrichment;


import fortscale.services.UserService;
import fortscale.streaming.exceptions.StreamMessageNotContainFieldException;
import fortscale.streaming.service.SpringService;
import fortscale.streaming.service.config.StreamingTaskDataSourceConfigKey;
import fortscale.streaming.task.AbstractStreamTask;
import fortscale.utils.JksonSerilaizablePair;
import fortscale.utils.time.TimestampUtils;
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

import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

import static fortscale.streaming.ConfigUtils.getConfigString;
import static fortscale.utils.ConversionUtils.convertToLong;
import static fortscale.utils.ConversionUtils.convertToString;

/**
 * Update User's info to mongo :
 * 	a. last activity
 * 	b. create user if need (based on configuration) -
 * 		New addition - Since we are using the users in mongo when creating the notifications we must create the users in mongo after the normalization and not here as part of updates.
 * 		You can see writes to mongo also from UsernameNormalizationAndTaggingTask
 * 	c. update the logusername
 *
 * 1. In level DB (state) during process
 * 2. In Mongo from time to time
 *
 *
 * Date: 1/11/2015.
 */
public class UserMongoUpdateTask extends AbstractStreamTask {

	/**
	 * The level DB store name
	 */
	private static final String storeName = "user-mongo-update";
	public static final String NO_LOG_USERNAME_IN_MESSAGE_LABEL = "No log username in message";
	public static final String NO_CONFIGURATION_IN_MESSAGE_LABEL = "No configuration in message";
	public static final String NO_USERNAME_FIELD_IN_MESSAGE_LABEL = "No username field  in message";
	public static final String NO_TIMESTAMP_FIELD_IN_MESSAGE_LABEL = "No timestamp field in message";

	/**
	 * Logger
	 */
	private static Logger logger = LoggerFactory.getLogger(UserMongoUpdateTask.class);

	/**
	 * The level DB store: username to last activity (map of data-source to time)
	 */
	protected KeyValueStore<String, UserInfoForUpdate> store;

	/**
	 * The time field in the input event
	 */
	protected String timestampField;

	/**
	 * The username field in the input event that we will use in streaming
	 */
	protected String usernameField;

	/**
	 * user service (for Mongo)
	 */
	protected UserService userService;

	/**
	 * Map between the input topic and the relevant data-source
	 */
	protected Map<StreamingTaskDataSourceConfigKey, DataSourceConfiguration> dataSourceConfigs = new HashMap<>();

	/**
	 * Map between classifier id to updateonly flag
	 */

	protected Map<String,Boolean> updateOnlyPerClassifire = new HashMap<>();


	/**
	 * Init task after spring context is up
	 * @param config	Samza task configuration
	 * @param context	Samza task context
	 * @throws Exception
	 */
	@Override
	protected void wrappedInit(Config config, TaskContext context) throws Exception {

		// Get the levelDB store
		store = (KeyValueStore<String, UserInfoForUpdate>) context.getStore(storeName);

		// Get field names
		timestampField = getConfigString(config, "fortscale.timestamp.field");
		//usernameField = getConfigString(config, "fortscale.username.field");

		// Get the user service (for Mongo) from spring
		userService = SpringService.getInstance().resolve(UserService.class);

		// Fill the map between the input topic and the data source
		Config fieldsSubset = config.subset("fortscale.events.entry");
		for (String dsSettings : fieldsSubset.keySet()) {

			String datasource = getConfigString(config, String.format("fortscale.events.entry.%s.data.source", dsSettings));
			String lastState = getConfigString(config, String.format("fortscale.events.entry.%s.last.state", dsSettings));
			StreamingTaskDataSourceConfigKey configKey = new StreamingTaskDataSourceConfigKey(datasource,lastState);
			String classifier = getConfigString(config, String.format("fortscale.events.entry.%s.classifier", dsSettings));
			String successfulLoginField = getConfigString(config, String.format("fortscale.events.entry.%s.success.field", dsSettings));
			String successfulLoginValue = getConfigString(config, String.format("fortscale.events.entry.%s.success.value", dsSettings));
			Boolean udpateOnlyFlag = config.getBoolean(String.format("fortscale.events.entry.updateOnly.%s", dsSettings));
			String logUserNameField =getConfigString(config, String.format("fortscale.events.entry.logusername.field.%s", dsSettings));
			usernameField  =getConfigString(config, String.format("fortscale.events.entry.username.field.%s", dsSettings));

			dataSourceConfigs.put(configKey, new DataSourceConfiguration(classifier, successfulLoginField, successfulLoginValue, udpateOnlyFlag, logUserNameField));
			updateOnlyPerClassifire.put(classifier,udpateOnlyFlag);
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
	protected void wrappedProcess(IncomingMessageEnvelope envelope, MessageCollector collector,TaskCoordinator coordinator) throws Exception {

		// parse the message into json
		String messageText = (String) envelope.getMessage();
		net.minidev.json.JSONObject message = (net.minidev.json.JSONObject) parseJsonMessage(envelope);

		// get the timestamp from the event
		Long timestampSeconds = convertToLong(message.get(timestampField));
		if (timestampSeconds == null) {
			taskMonitoringHelper.countNewFilteredEvents(getDataSource(message), NO_TIMESTAMP_FIELD_IN_MESSAGE_LABEL);
			logger.error("message {} does not contains timestamp in field {}", messageText, timestampField);
			throw new StreamMessageNotContainFieldException(messageText, timestampField);
		}
		Long timestamp = TimestampUtils.convertToMilliSeconds(timestampSeconds);

		// get the username from the event
		String normalizedUsername = convertToString(message.get(usernameField));
		if (normalizedUsername == null) {
			taskMonitoringHelper.countNewFilteredEvents(getDataSource(message), NO_USERNAME_FIELD_IN_MESSAGE_LABEL);
			logger.error("message {} does not contains username in field {}", messageText, usernameField);
			throw new StreamMessageNotContainFieldException(messageText, usernameField);
		}



		// Get the input topic
		String topic = envelope.getSystemStreamPartition().getSystemStream().getStream();

		// Get relevant data source according to topic
		DataSourceConfiguration dataSourceConfiguration = dataSourceConfigs.get(topic);
		if (dataSourceConfiguration == null) {
			taskMonitoringHelper.countNewFilteredEvents(getDataSource(message), NO_CONFIGURATION_IN_MESSAGE_LABEL);
			logger.error("No data source is defined for input topic {} ", topic);
			return;
		}

		//get the actual username from the event - using for assigning to logusername
		String logUserNameFromEvent = convertToString(message.get(dataSourceConfiguration.logUserNameField));
		if (logUserNameFromEvent == null) {
			taskMonitoringHelper.countNewFilteredEvents(getDataSource(message), NO_LOG_USERNAME_IN_MESSAGE_LABEL);
			logger.error("message {} does not contains field {} that will needed for marking the logusername ", messageText, dataSourceConfiguration.logUserNameField);
			return;
		}


		handleUnfilteredEvent(message);
		//in case that the success field is equal to #AnyRow# in the configuration file
		//update the last activity for any row
		// or check that the event represent successful login
		if (dataSourceConfiguration.successField.equals("#AnyRow#") || convertToString(message.get(dataSourceConfiguration.successField)).equalsIgnoreCase(dataSourceConfiguration.successValue)) {
			// Find the last activity of the user (if exist) and update it if the event is newer than the event's activity
			//update the logusername if needed
			updateUserInfoInStore(timestamp, normalizedUsername, dataSourceConfiguration.mongoClassifierId,logUserNameFromEvent);

		}


		// No output topic -> this is the last task in the chain

	}

	/**
	 * Find the last activity of the user (if exist) and update it if the event is newer than the event's activity
	 * update the logusername if needed
	 * @param timestamp    the time of the event
	 * @param normalizedUsername    the user
	 * @param classifierId	The classifier in Mongo for this data-source
	 */
	private void updateUserInfoInStore(Long timestamp, String normalizedUsername, String classifierId,String logUserNameFromEvent) {

		UserInfoForUpdate dataSourceToUserInfo = store.get(normalizedUsername);


		//in case that the user doesnt exist in the LevelDB
		if (dataSourceToUserInfo == null) {
			dataSourceToUserInfo = new UserInfoForUpdate();
			// Since the same user will be always on the same partition, no need to synchronize this
			Map<String,JksonSerilaizablePair<Long,String>> dataSourceToUserInfoHashMap  = new HashMap<>();
			dataSourceToUserInfoHashMap.put(classifierId,new JksonSerilaizablePair<Long, String>(null,logUserNameFromEvent));

			dataSourceToUserInfo.setUserInfo(dataSourceToUserInfoHashMap);

			store.put(normalizedUsername, dataSourceToUserInfo);
		}

		//in case the user have no entry for the current data source
		if (dataSourceToUserInfo.getUserInfo().get(classifierId) == null)
		{

			dataSourceToUserInfo.getUserInfo().put(classifierId,new JksonSerilaizablePair<Long, String>(null,logUserNameFromEvent));
			store.put(normalizedUsername, dataSourceToUserInfo);

		}


		Long userLastActivity = dataSourceToUserInfo.getUserInfo().get(classifierId).getKey();


		//update in case that last activity need to be update
		if(userLastActivity == null || userLastActivity < timestamp){
			// update last activity and logusername  in level DB
			dataSourceToUserInfo.getUserInfo().put(classifierId, new JksonSerilaizablePair<Long, String>(timestamp, logUserNameFromEvent));
			store.put(normalizedUsername, dataSourceToUserInfo);
		}

	}

	@Override
	protected String getJobLabel() {
		return "UserMongoUpdateTask";
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

		KeyValueIterator<String, UserInfoForUpdate> iter = store.all();

		List<String> usernames = new LinkedList<>();
		while (iter.hasNext()) {
			Entry<String, UserInfoForUpdate> user = iter.next();
			// update user in mongo
			userService.updateUsersInfo(user.getKey(), user.getValue().getUserInfo(),updateOnlyPerClassifire );
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
	protected static class DataSourceConfiguration {

		protected DataSourceConfiguration(String mongoClassifierId, String successField, String successValue,
				boolean udpateOnlyFlag, String logUserNameField) {
			this.mongoClassifierId = mongoClassifierId;
			this.successField = successField;
			this.successValue = successValue;
			this.dataSourceUpdateOnlyFlag = udpateOnlyFlag;
			this.logUserNameField = logUserNameField;

		}

		public String mongoClassifierId;
		public String successField;
		public String successValue;
		public boolean dataSourceUpdateOnlyFlag;
		public String logUserNameField;
	}



}
