
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

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import static com.google.common.base.Preconditions.checkNotNull;
import static fortscale.streaming.ConfigUtils.getConfigString;
import static fortscale.utils.ConversionUtils.convertToString;

/**
 * Several enrichment regarding the user:
 * 1. Username normalization
 * 2. User tagging
 *
 * Since we are using the users in mongo when creating the notifications we must create the users in mongo after the normalization and not as part of regular updates in UserMongoUpdateTask.
 */
public class UsernameNormalizationAndTaggingTask extends AbstractStreamTask implements InitableTask {

	private static final String DATA_SOURCE_FIELD = "dataSource";

	private static String topicConfigKeyFormat = "fortscale.%s.service.cache.topic";
	private static String storeConfigKeyFormat = "fortscale.%s.service.cache.store";

	private static String usernameKey = "username";
	private static String userTagsKey = "user-tag";
	private static String samAccountKey = "samAccountName";

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
		LevelDbBasedCache<String, ArrayList> samAccountNameStore = new LevelDbBasedCache<String, ArrayList>((KeyValueStore<String, ArrayList>) context.getStore(getConfigString(config, String.format(storeConfigKeyFormat, samAccountKey))), ArrayList.class);
		CachingService usernameService = null;
		CachingService samAccountNameService = null;

		// get task configuration
		for (Entry<String,String> ConfigField : config.subset("fortscale.events.input.topic.").entrySet()) {
			String dataSource = ConfigField.getKey();
			String inputTopic = ConfigField.getValue();
			String outputTopic = getConfigString(config, String.format("fortscale.events.output.topic.%s", dataSource));
			String usernameField = getConfigString(config, String.format("fortscale.events.username.field.%s",dataSource));
			String domainField = getConfigString(config, String.format("fortscale.events.domain.field.%s",
					dataSource));
			String fakeDomain = domainField.equals("fake") ? getConfigString(config, String.format("fortscale.events"
							+ ".domain.fake.%s", dataSource)) : "";
			String normalizedUsernameField = getConfigString(config, String.format("fortscale.events"
					+ ".normalizedusername.field.%s",dataSource));
			String partitionKey = getConfigString(config, String.format("fortscale.events.output.topic.%s",dataSource));
			String serviceName = getConfigString(config, String.format("fortscale.events.normalization.service.%s",dataSource));
			Boolean updateOnlyFlag = config.getBoolean(String.format("fortscale.events.updateOnly.%s", dataSource));
			String classifier = getConfigString(config, String.format("fortscale.events.classifier.%s", dataSource));
			UsernameNormalizationService service = (UsernameNormalizationService)SpringService.getInstance().resolve(serviceName);
			// update the same caching service, since it it identical (joined) between all data sources
			usernameService = service.getUsernameNormalizer().getUsernameService();
			usernameService.setCache(usernameStore);
			samAccountNameService = service.getUsernameNormalizer().getSamAccountNameService();
			samAccountNameService.setCache(samAccountNameStore);
			inputTopicToConfiguration.put(inputTopic, new UsernameNormalizationConfig(inputTopic, outputTopic,
					usernameField, domainField, fakeDomain, normalizedUsernameField, partitionKey, updateOnlyFlag,
					classifier, service));
		}

		// add the usernameService to update input topics map
		if (usernameService != null) {
			topicToServiceMap.put(getConfigString(config,  String.format(topicConfigKeyFormat, usernameKey)), usernameService);
		}

		//add the samAccountNameService to the update input topic map
		if(samAccountNameService != null)
		{
			topicToServiceMap.put(getConfigString(config,  String.format(topicConfigKeyFormat, samAccountKey)), samAccountNameService);
		}

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
			userService.setCache(new LevelDbBasedCache<String, List>((KeyValueStore<String, List>) context.getStore(getConfigString(config, String.format(storeConfigKeyFormat, userTagsKey))), List.class));
			topicToServiceMap.put(getConfigString(config,  String.format(topicConfigKeyFormat, userTagsKey)), userService);
		}
	}

	
	@Override
	protected void wrappedProcess(IncomingMessageEnvelope envelope, MessageCollector collector, TaskCoordinator coordinator) throws Exception {
		// parse the message into json 
		String messageText = (String)envelope.getMessage();

		JSONObject message = (JSONObject) JSONValue.parseWithException(messageText);

		String dataSource = convertToString(message.get(DATA_SOURCE_FIELD));

		if (dataSource == null) {
			logger.error("Could not find dataSource field. Skipping message: " + messageText);

			return;
		}

		// Get the input topic
		String inputTopic = envelope.getSystemStreamPartition().getSystemStream().getStream();

		if (topicToServiceMap.containsKey(inputTopic)) {
			CachingService cachingService = topicToServiceMap.get(inputTopic);
			cachingService.handleNewValue((String) envelope.getKey(), (String) envelope.getMessage());
		} else {
			// Get configuration for data source
			UsernameNormalizationConfig configuration = inputTopicToConfiguration.get(inputTopic);
			if (configuration == null) {
				logger.error("No configuration found for input topic {}. Dropping Record", inputTopic);
				return;
			}

			// Normalized username

			// get the normalized username from input record
			String normalizedUsername = convertToString(message.get(configuration.getNormalizedUsernameField()));
			if (StringUtils.isEmpty(normalizedUsername)) {

				// get username
				String username = convertToString(message.get(configuration.getUsernameField()));
				if (StringUtils.isEmpty(username)) {
					logger.error("message {} does not contains username in field {}", messageText, configuration.getUsernameField());
					throw new StreamMessageNotContainFieldException(messageText, configuration.getUsernameField());
				}

				// get domain
				String domain;
				//if domain field value is fake, then take the fake.domain field's value
				if (configuration.getDomainField().equals("fake")) {
					domain = configuration.getFakeDomain();
				} else {
					domain = convertToString(message.get(configuration.getDomainField()));
				}

				UsernameNormalizationService normalizationService = configuration.getUsernameNormalizationService();
				// checks in memory-cache and mongo if the user exists
				normalizedUsername = normalizationService.normalizeUsername(username, domain, configuration);
				// check if we should drop the record (user doesn't exist)
				if (normalizationService.shouldDropRecord(username, normalizedUsername)) {
					if (logger.isDebugEnabled()) {
						logger.debug("Failed to normalized username {}. Dropping record {}", username, messageText);
					}
					// drop record
					return;
				}
				message.put(configuration.getNormalizedUsernameField(), normalizedUsername);

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
