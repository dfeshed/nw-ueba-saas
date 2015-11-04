package fortscale.streaming.task.enrichment;


import fortscale.services.UserService;
import fortscale.streaming.exceptions.StreamMessageNotContainFieldException;
import fortscale.streaming.service.SpringService;
import fortscale.streaming.task.AbstractStreamTask;
import fortscale.utils.JksonSerilaizablePair;
import fortscale.utils.time.TimestampUtils;
import net.minidev.json.JSONObject;
import net.minidev.json.JSONValue;
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

	private static final String DATA_SOURCE_FIELD = "dataSource";

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
	protected Map<String, DataSourceConfiguration> dataSourceToConfiguration = new HashMap<>();

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

		// Get the user service (for Mongo) from spring
		userService = SpringService.getInstance().resolve(UserService.class);

		// Fill the map between the input topic and the data source
		for (Map.Entry<String,String> ConfigField : config.subset("fortscale.events.data.source.").entrySet()) {
			String dataSource = ConfigField.getKey();
			String inputTopic = getConfigString(config, String.format("fortscale.events.input.topic.%s", dataSource));
			String classifier = getConfigString(config, String.format("fortscale.events.classifier.%s", dataSource));
			String successfulLoginField = getConfigString(config, String.format("fortscale.events.success.field.%s", dataSource));
			String successfulLoginValue = getConfigString(config, String.format("fortscale.events.success.value.%s", dataSource));
			Boolean udpateOnlyFlag = config.getBoolean(String.format("fortscale.events.updateOnly.%s", dataSource));
			String logUserNameField =getConfigString(config, String.format("fortscale.events.logusername.field.%s", dataSource));
			usernameField = getConfigString(config, String.format("fortscale.events.username.field.%s", dataSource));
			dataSourceToConfiguration.put(dataSource, new DataSourceConfiguration(dataSource, inputTopic, classifier, successfulLoginField, successfulLoginValue, udpateOnlyFlag, logUserNameField));
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

		String messageText = (String)envelope.getMessage();

		JSONObject message = (JSONObject) JSONValue.parseWithException(messageText);

		String dataSource = convertToString(message.get(DATA_SOURCE_FIELD));

		if (dataSource == null) {
			logger.error("Could not find mandatory dataSource field. Skipping message: {} ", messageText);

			return;
		}


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

		// Get relevant data source according to topic
		DataSourceConfiguration dataSourceConfiguration = dataSourceToConfiguration.get(dataSource);
		if (dataSourceConfiguration == null) {
			logger.error("No configuration is defined for data source {} ", dataSource);
			return;
		}

		//get the actual username from the event - using for assigning to logusername
		String logUserNameFromEvent = convertToString(message.get(dataSourceConfiguration.getLogUserNameField()));
		if (logUserNameFromEvent == null) {
			logger.error("message {} does not contains field {} that will needed for marking the logusername ", messageText, dataSourceConfiguration.getLogUserNameField());
			return;
		}


		//in case that the success field is equal to #AnyRow# in the configuration file
		//update the last activity for any row
		// or check that the event represent successful login
		if (dataSourceConfiguration.getSuccessField().equals("#AnyRow#") || convertToString(message.get(dataSourceConfiguration.getSuccessField())).equalsIgnoreCase(dataSourceConfiguration.getSuccessValue())) {
			// Find the last activity of the user (if exist) and update it if the event is newer than the event's activity
			//update the logusername if needed
			updateUserInfoInStore(timestamp, normalizedUsername, dataSourceConfiguration.getMongoClassifierId(),logUserNameFromEvent);

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

		protected DataSourceConfiguration(String dataSource, String inputTopic, String mongoClassifierId, String successField, String successValue,
				boolean udpateOnlyFlag, String logUserNameField) {
			this.dataSource = dataSource;
			this.inputTopic = inputTopic;
			this.mongoClassifierId = mongoClassifierId;
			this.successField = successField;
			this.successValue = successValue;
			this.dataSourceUpdateOnlyFlag = udpateOnlyFlag;
			this.logUserNameField = logUserNameField;

		}

		private String dataSource;
		private String inputTopic;
		private String mongoClassifierId;
		private String successField;
		private String successValue;
		private boolean dataSourceUpdateOnlyFlag;
		private String logUserNameField;

		public String getDataSource() {
			return dataSource;
		}

		public String getInputTopic() {
			return inputTopic;
		}

		public String getMongoClassifierId() {
			return mongoClassifierId;
		}

		public String getSuccessField() {
			return successField;
		}

		public String getSuccessValue() {
			return successValue;
		}

		public boolean isDataSourceUpdateOnlyFlag() {
			return dataSourceUpdateOnlyFlag;
		}

		public String getLogUserNameField() {
			return logUserNameField;
		}
	}



}
