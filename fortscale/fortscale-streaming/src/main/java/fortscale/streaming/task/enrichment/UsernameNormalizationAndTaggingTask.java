package fortscale.streaming.task.enrichment;

import fortscale.services.UserService;
import fortscale.streaming.exceptions.KafkaPublisherException;
import fortscale.streaming.exceptions.StreamMessageNotContainFieldException;
import fortscale.streaming.service.SpringService;
import fortscale.streaming.service.UserTagsService;
import fortscale.streaming.service.usernameNormalization.UsernameNormalizationService;
import fortscale.streaming.task.AbstractStreamTask;
import net.minidev.json.JSONObject;
import net.minidev.json.JSONValue;
import org.apache.commons.lang.StringUtils;
import org.apache.commons.lang3.tuple.ImmutablePair;
import org.apache.commons.lang3.tuple.Pair;
import org.apache.samza.config.Config;
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

import static fortscale.streaming.ConfigUtils.getConfigString;
import static fortscale.utils.ConversionUtils.convertToString;

/**
 * Several enrichment regarding the user:
 * 1. Username normalization
 * 2. User tagging
 */
public class UsernameNormalizationAndTaggingTask extends AbstractStreamTask implements InitableTask {


	/**
	 * Logger
	 */
	private static Logger logger = LoggerFactory.getLogger(UsernameNormalizationAndTaggingTask.class);

	/**
	 * Map of configuration: from the data-source input topic, to an entry of normalization service and output topic
	 */
	protected Map<String, Pair<String,UsernameNormalizationService>> inputTopicToConfiguration = new HashMap<>();

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

		// get task configuration
		for (Entry<String,String> ConfigField : config.subset("fortscale.events.input.topic.").entrySet()) {
			String dataSource = ConfigField.getKey();
			String inputTopic = ConfigField.getValue();
			String outputTopic = String.format(getConfigString(config, "fortscale.events.output.topic.%s"),dataSource);
			String serviceName = String.format(getConfigString(config, "fortscale.events.normalization.service.%s"),dataSource);
			UsernameNormalizationService service = (UsernameNormalizationService)SpringService.getInstance().resolve(serviceName);
			inputTopicToConfiguration.put(inputTopic, new ImmutablePair<>(outputTopic, service));
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
	}

	
	@Override
	protected void wrappedProcess(IncomingMessageEnvelope envelope, MessageCollector collector, TaskCoordinator coordinator) throws Exception {
		// parse the message into json 
		String messageText = (String)envelope.getMessage();
		JSONObject message = (JSONObject) JSONValue.parseWithException(messageText);


		// Get the input topic
		String inputTopic = envelope.getSystemStreamPartition().getSystemStream().getStream();
		// TODO handle updates topics about existing usernames (normalizationService), and user tags (tagService)


		// Get configuration for data source
		Entry<String, UsernameNormalizationService> configuration = inputTopicToConfiguration.get(inputTopic);
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

			UsernameNormalizationService normalizationService = configuration.getValue();
			// checks in memory-cache and mongo if the user exists
			normalizedUsername = normalizationService.normalizeUsername(username);
			// check if we should drop the record (user doesn't exist)
			if(normalizationService.shouldDropRecord( username, normalizedUsername)){
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
		String outputTopic = configuration.getKey();
		try{
			// TODO send to partition according to username
			collector.send(new OutgoingMessageEnvelope(new SystemStream("kafka", outputTopic), message.toJSONString()));
		} catch(Exception exception){
			throw new KafkaPublisherException(String.format("failed to send message to topic %s after processing. Message: %s.", outputTopic, messageText), exception);
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
}
