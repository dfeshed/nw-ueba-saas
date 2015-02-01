package fortscale.streaming.task.enrichment;

import fortscale.services.CachingService;
import fortscale.streaming.cache.LevelDbBasedCache;
import fortscale.streaming.exceptions.KafkaPublisherException;
import fortscale.streaming.exceptions.StreamMessageNotContainFieldException;
import fortscale.streaming.service.SpringService;
import fortscale.streaming.service.UserTagsService;
import fortscale.streaming.service.usernameNormalization.UsernameNormalizationConfig;
import fortscale.streaming.service.usernameNormalization.UsernameNormalizationService;
import fortscale.streaming.task.AbstractStreamTask;
import net.minidev.json.JSONObject;
import net.minidev.json.JSONValue;
import org.apache.commons.lang.StringUtils;
import org.apache.samza.config.Config;
import org.apache.samza.storage.kv.KeyValueStore;
import org.apache.samza.system.IncomingMessageEnvelope;
import org.apache.samza.system.OutgoingMessageEnvelope;
import org.apache.samza.system.SystemStream;
import org.apache.samza.task.InitableTask;
import org.apache.samza.task.MessageCollector;
import org.apache.samza.task.TaskContext;
import org.apache.samza.task.TaskCoordinator;
import parquet.org.slf4j.Logger;
import parquet.org.slf4j.LoggerFactory;

import java.util.HashMap;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;

import static com.google.common.base.Preconditions.checkNotNull;
import static fortscale.streaming.ConfigUtils.getConfigString;
import static fortscale.utils.ConversionUtils.convertToString;

/**
 * Several enrichment regarding the user:
 * 1. Username normalization
 * 2. User tagging
 */
public class UsernameNormalizationAndTaggingTask extends AbstractStreamTask implements InitableTask {

	private static String topicConfigKeyFormat = "fortscale.%s.service.cache.topic";
	private static String storeConfigKeyFormat = "fortscale.%s.service.cache.store";

	private static String usernameKey = "username";
	private static String userTagsKey = "user-tag";

	/**
	 * Logger
	 */
	private static Logger logger = LoggerFactory.getLogger(UsernameNormalizationAndTaggingTask.class);

	/**
	 * Map of configuration: from the data-source input topic, to an entry of normalization service and output topic
	 */
	protected Map<String, UsernameNormalizationConfig> inputTopicToConfiguration = new HashMap<>();

	/**
	 * Map between (update) input topic name and relevant caching service
	 * Uses for updates arriving from kafka update topic
	 */
	//
	protected Map<String, CachingService> topicToServiceMap = new HashMap<>();

	/**
	 * The username field in the input event
	 */
	protected String usernameField;

	/**
	 * The normalized username field
	 */
	protected String normalizedUsernameField;

	/**
	 * Service for tagging events according to user-tag
	 */
	protected UserTagsService tagService;

	/**
	 * Init task after spring context is up
	 * @param config	Samza task configuration
	 * @param context	Samza task context
	 * @throws Exception
	 */
	@Override
	protected void wrappedInit(Config config, TaskContext context) throws Exception {
		LevelDbBasedCache<String, String> usernameStore = new LevelDbBasedCache<String, String>((KeyValueStore<String, String>) context.getStore(getConfigString(config, String.format(storeConfigKeyFormat, usernameKey))), String.class);
		CachingService usernameService = null;
		// get task configuration
		for (Entry<String,String> ConfigField : config.subset("fortscale.events.input.topic.").entrySet()) {
			String dataSource = ConfigField.getKey();
			String inputTopic = ConfigField.getValue();
			String outputTopic = getConfigString(config, String.format("fortscale.events.output.topic.%s",dataSource));
			String partitionKey = getConfigString(config, String.format("fortscale.events.output.topic.%s",dataSource));
			String serviceName = getConfigString(config, String.format("fortscale.events.normalization.service.%s",dataSource));
			UsernameNormalizationService service = (UsernameNormalizationService)SpringService.getInstance().resolve(serviceName);
			// update the same caching service, since it it identical (joined) between all data sources
			usernameService = service.getUsernameNormalizer().getUsernameService();
			usernameService.setCache(usernameStore);
			inputTopicToConfiguration.put(inputTopic, new UsernameNormalizationConfig(inputTopic, outputTopic, partitionKey, service));
		}

		// add the usernameService to update input topics map
		if (usernameService != null) {
			topicToServiceMap.put(getConfigString(config,  String.format(topicConfigKeyFormat, usernameKey)), usernameService);
		}


		// Get field names
		normalizedUsernameField = getConfigString(config, "fortscale.normalizedusername.field");
		usernameField = getConfigString(config, "fortscale.username.field");



		// construct tagging service with the tags that are required from configuration
		Map<String, String> tags = new HashMap<String, String>();
		for (Entry<String,String> tagConfigField : config.subset("fortscale.tags.").entrySet()) {
			String tagName = tagConfigField.getKey();
			String tagField = tagConfigField.getValue();
			tags.put(tagName, tagField);
		}
		tagService = new UserTagsService(tags);
		CachingService userService = tagService.getUserService();
		// add the tagService to update input topics map
		if (userService != null) {
			userService.setCache(new LevelDbBasedCache<String, Set>((KeyValueStore<String, Set>) context.getStore(getConfigString(config, String.format(storeConfigKeyFormat, userTagsKey))), Set.class));
			topicToServiceMap.put(getConfigString(config,  String.format(topicConfigKeyFormat, userTagsKey)), userService);
		}
	}

	
	@Override
	protected void wrappedProcess(IncomingMessageEnvelope envelope, MessageCollector collector, TaskCoordinator coordinator) throws Exception {
		// parse the message into json 
		String messageText = (String)envelope.getMessage();
		// Get the input topic
		String inputTopic = envelope.getSystemStreamPartition().getSystemStream().getStream();

		if (topicToServiceMap.containsKey(inputTopic)) {
			String key = (String) envelope.getKey();
			CachingService cachingService = topicToServiceMap.get(inputTopic);
			if(envelope.getMessage() == null){
				cachingService.getCache().remove(key);
			}
			else {
				cachingService.getCache().putFromString(key, (String) envelope.getMessage());
			}
		} else {
			JSONObject message = (JSONObject) JSONValue.parseWithException(messageText);
			// Get configuration for data source
			UsernameNormalizationConfig configuration = inputTopicToConfiguration.get(inputTopic);
			if (configuration == null) {
				logger.error("No configuration found for input topic {}. Dropping Record", inputTopic);
				return;
			}

			// Normalized username

			// get the normalized username from input record
			String normalizedUsername = convertToString(message.get(normalizedUsernameField));
			if (StringUtils.isEmpty(normalizedUsername)) {

				// get username
				String username = convertToString(message.get(usernameField));
				if (StringUtils.isEmpty(username)) {
					logger.error("message {} does not contains username in field {}", messageText, usernameField);
					throw new StreamMessageNotContainFieldException(messageText, usernameField);
				}

				UsernameNormalizationService normalizationService = configuration.getUsernameNormalizationService();
				// checks in memory-cache and mongo if the user exists
				normalizedUsername = normalizationService.normalizeUsername(username);
				// check if we should drop the record (user doesn't exist)
				if (normalizationService.shouldDropRecord(username, normalizedUsername)) {
					if (logger.isDebugEnabled()) {
						logger.debug("Failed to normalized username {}. Dropping record {}", username, messageText);
					}
					// drop record
					return;
				}
				if (normalizedUsername == null) {
					// normalization failed, but we keep the record and generate normalized
					normalizedUsername = normalizationService.getUsernameAsNormalizedUsername(username, message);
				}
				message.put(normalizedUsernameField, normalizedUsername);

			}

			// add the tags to the event - checks in memory-cache and mongo if the user exists with tags
			tagService.addTagsToEvent(normalizedUsername, message);

			// send the event to the output topic
			String outputTopic = configuration.getOutputTopic();
			try {
				collector.send(new OutgoingMessageEnvelope(new SystemStream("kafka", outputTopic), getPartitionKey(configuration.getPartitionField(), message), message.toJSONString()));
			} catch (Exception exception) {
				throw new KafkaPublisherException(String.format("failed to send message to topic %s after processing. Message: %s.", outputTopic, messageText), exception);
			}
		}
	}


	@Override
	protected void wrappedClose() throws Exception {
		tagService = null;
	}

	@Override
	protected void wrappedWindow(MessageCollector collector, TaskCoordinator coordinator) throws Exception {
		// Do nothing
	}

	/** Get the partition key to use for outgoing message envelope for the given event */
	private Object getPartitionKey(String partitionKeyField, JSONObject event) {
		checkNotNull(partitionKeyField);
		checkNotNull(event);
		return event.get(partitionKeyField);
	}


}
