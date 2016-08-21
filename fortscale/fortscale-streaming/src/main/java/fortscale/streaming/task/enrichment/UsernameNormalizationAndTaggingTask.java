
package fortscale.streaming.task.enrichment;

import fortscale.services.CachingService;
import fortscale.services.impl.SpringService;
import fortscale.streaming.cache.KeyValueDbBasedCache;
import fortscale.streaming.exceptions.KafkaPublisherException;
import fortscale.streaming.exceptions.StreamMessageNotContainFieldException;
import fortscale.streaming.service.FortscaleValueResolver;
import fortscale.streaming.service.config.StreamingTaskDataSourceConfigKey;
import fortscale.streaming.service.usernameNormalization.UsernameNormalizationConfig;
import fortscale.streaming.service.usernameNormalization.UsernameNormalizationService;
import fortscale.streaming.task.AbstractStreamTask;
import fortscale.streaming.task.enrichment.metrics.UsernameNormalizationAndTaggingTaskMetrics;
import fortscale.streaming.task.monitor.MonitorMessaages;
import fortscale.utils.logging.Logger;
import net.minidev.json.JSONObject;
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

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import java.util.Map.Entry;

import static com.google.common.base.Preconditions.checkNotNull;
import static fortscale.streaming.ConfigUtils.getConfigString;
import static fortscale.utils.ConversionUtils.convertToString;

/**
 * This task normalizes the username
 * UPDATE - IT NO LONGER TAGS THE EVENT
 *
 * Since we are using the users in mongo when creating the notifications we must create the users in mongo after the normalization and not as part of regular updates in UserMongoUpdateTask.
 */
public class UsernameNormalizationAndTaggingTask extends AbstractStreamTask implements InitableTask {

	private static String topicConfigKeyFormat = "fortscale.%s.service.cache.topic";
	private static String storeConfigKeyFormat = "fortscale.%s.service.cache.store";

	private static String usernameKey = "username";
	private static String samAccountKey = "samAccountName";

	/**
	 * Logger
	 */
	private static Logger logger = Logger.getLogger(UsernameNormalizationAndTaggingTask.class);

	/**
	 * Map of configuration: from the data-source and state to an entry of normalization service and output topic
	 */
	protected Map<StreamingTaskDataSourceConfigKey, UsernameNormalizationConfig> dataSourceToConfigurationMap = new HashMap<>();

	// Streaming task metrics. Note some fields are update by this class and some by the derived classes
	protected UsernameNormalizationAndTaggingTaskMetrics taskMetrics;

	/**
	 * Map between (update) input topic name and relevant caching service
	 * Uses for updates arriving from kafka update topic
	 */
	//
	protected Map<String, CachingService> topicToServiceMap = new HashMap<>();

	/**
	 * Init task after spring context is up
	 * @param config	Samza task configuration
	 * @param context	Samza task context
	 * @throws Exception
	 */
	@Override
	protected void wrappedInit(Config config, TaskContext context) throws Exception {

		res = SpringService.getInstance().resolve(FortscaleValueResolver.class);

		KeyValueDbBasedCache<String, String> usernameStore = new KeyValueDbBasedCache<String, String>((KeyValueStore<String, String>) context.getStore(getConfigString(config, String.format(storeConfigKeyFormat, usernameKey))), String.class,
				"usernameStore", statsService);
		KeyValueDbBasedCache<String, ArrayList> samAccountNameStore = new KeyValueDbBasedCache<String, ArrayList>((KeyValueStore<String, ArrayList>) context.getStore(getConfigString(config, String.format(storeConfigKeyFormat, samAccountKey))), ArrayList.class,
				"samAccountNameStore", statsService);
		CachingService usernameService = null;
		CachingService samAccountNameService = null;

		// get task configuration
		for (Entry<String,String> configField : config.subset("fortscale.events.entry.name.").entrySet()) {
			String configKey = configField.getValue();
			String dataSource = getConfigString(config, String.format("fortscale.events.entry.%s.data.source", configKey));
			String lastState = getConfigString(config, String.format("fortscale.events.entry.%s.last.state", configKey));
			Boolean shouldBeTaged = config.getBoolean(String.format("fortscale.events.entry.%s.shouldBeTag", configKey));

			String outputTopic = getConfigString(config, String.format("fortscale.events.entry.%s.output.topic", configKey));

			String normalizationBasedField = resolveStringValue(config, String.format("fortscale.events.entry.%s.normalization.based.field",configKey),res);
			String domainField = getConfigString(config, String.format("fortscale.events.entry.%s.domain.field",
					configKey));
			String fakeDomain = domainField.equals("fake") ? getConfigString(config, String.format("fortscale.events.entry.%s"
							+ ".domain.fake", configKey)) : "";
			String normalizedUsernameField = resolveStringValue(config, String.format("fortscale.events.entry.%s"
					+ ".normalizedusername.field",configKey),res);
			String partitionKey = resolveStringValue(config, String.format("fortscale.events.entry.%s.partition.field", configKey),res);
			String serviceName = getConfigString(config, String.format("fortscale.events.entry.%s.normalization.service",configKey));
			Boolean updateOnlyFlag = config.getBoolean(String.format("fortscale.events.entry.%s.updateOnly", configKey));
			String classifier = getConfigString(config, String.format("fortscale.events.entry.%s.classifier", configKey));
			UsernameNormalizationService service = (UsernameNormalizationService)SpringService.getInstance().resolve(serviceName);
			// update the same caching service, since it it identical (joined) between all data sources
			usernameService = service.getUsernameNormalizer().getUsernameService();
			usernameService.setCache(usernameStore);
			samAccountNameService = service.getUsernameNormalizer().getSamAccountNameService();
			samAccountNameService.setCache(samAccountNameStore);
			dataSourceToConfigurationMap.put(new StreamingTaskDataSourceConfigKey(dataSource, lastState), new UsernameNormalizationConfig(outputTopic,
                    normalizationBasedField, domainField, fakeDomain, normalizedUsernameField, partitionKey, updateOnlyFlag,
					classifier, service,shouldBeTaged));
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

	}

	
	@Override
	protected void wrappedProcess(IncomingMessageEnvelope envelope, MessageCollector collector, TaskCoordinator coordinator) throws Exception {
		// Get the input topic
		String inputTopic = envelope.getSystemStreamPartition().getSystemStream().getStream();

		if (topicToServiceMap.containsKey(inputTopic)) {
			CachingService cachingService = topicToServiceMap.get(inputTopic);
			cachingService.handleNewValue((String) envelope.getKey(), (String) envelope.getMessage());
		} else {
			JSONObject message = parseJsonMessage(envelope);
			taskMetrics.parsedToJSONMessages++;

			StreamingTaskDataSourceConfigKey configKey = extractDataSourceConfigKeySafe(message);
			if (configKey == null){
				taskMetrics.unknownConfigurationKeyMessages++;
				taskMonitoringHelper.countNewFilteredEvents(super.UNKNOW_CONFIG_KEY, MonitorMessaages.BAD_CONFIG_KEY);
				return;
			}

			UsernameNormalizationConfig usernameNormalizationConfig = dataSourceToConfigurationMap.get(configKey);

			if (usernameNormalizationConfig == null){
				taskMonitoringHelper.countNewFilteredEvents(configKey, MonitorMessaages.NO_STATE_CONFIGURATION_MESSAGE);
				taskMetrics.unknownNormalizationConfigurationMessages++;
			}

			// get the normalized username from input record - if he doesnt exist  its sign that we should normalized the username field
			String normalizedUsername = convertToString(message.get(usernameNormalizationConfig.getNormalizedUsernameField()));
			if (StringUtils.isEmpty(normalizedUsername)) {
				taskMetrics.emptyNormalizedUsernameMessages++;
				String messageText = (String)envelope.getMessage();
				// get username
				String normalizationBasedField = convertToString(message.get(usernameNormalizationConfig.getNormalizationBasedField()));
				if (StringUtils.isEmpty(normalizationBasedField)) {
					logger.error("message {} does not contains username in field {}", messageText, usernameNormalizationConfig.getNormalizationBasedField());
					taskMonitoringHelper.countNewFilteredEvents(configKey,MonitorMessaages.CANNOT_EXTRACT_USER_NAME_MESSAGE);
					taskMetrics.emptyUsernameMessages++;
					throw new StreamMessageNotContainFieldException(messageText, usernameNormalizationConfig.getNormalizationBasedField());
				}

				// get domain
				String domain;
				//if domain field value is fake, then take the fake.domain field's value
				if (usernameNormalizationConfig.getDomainField().equals("fake")) {
					domain = usernameNormalizationConfig.getFakeDomain();
				} else {
					domain = convertToString(message.get(usernameNormalizationConfig.getDomainField()));
				}

				UsernameNormalizationService normalizationService = usernameNormalizationConfig.getUsernameNormalizationService();
				// checks in memory-cache and mongo if the user exists
				normalizedUsername = normalizationService.normalizeUsername(normalizationBasedField, domain, usernameNormalizationConfig);
				// check if we should drop the record (user doesn't exist)
				if (normalizationService.shouldDropRecord(normalizationBasedField, normalizedUsername)) {
					if (logger.isDebugEnabled()) {
						logger.debug("Failed to normalized username {}. Dropping record {}", normalizationBasedField, messageText);
					}
					// drop record
					taskMonitoringHelper.countNewFilteredEvents(configKey,MonitorMessaages.FAIL_TO_NORMALIZED_USERNAME);
					taskMetrics.failedToNormalizeUsernameMessages++;
					return;
				}
				message.put(usernameNormalizationConfig.getNormalizedUsernameField(), normalizedUsername);

			} else {
				taskMetrics.normalizedUsernameAlreadyExistsMessages++;
			}

			// add the default false tags to the event
			if (usernameNormalizationConfig.getShouldBeTagged()) {
				//tagService.addTagsToEvent(normalizedUsername, message);
			}

			// send the event to the output topic
			String outputTopic = usernameNormalizationConfig.getOutputTopic();
			try {
				collector.send(new OutgoingMessageEnvelope(new SystemStream("kafka", outputTopic), getPartitionKey(usernameNormalizationConfig.getPartitionField(), message), message.toJSONString()));
			} catch (Exception exception) {
				taskMetrics.failedToForwardMessage++;
				throw new KafkaPublisherException(String.format("failed to send message %s from input topic %s to output topic %s", message.toJSONString(), inputTopic, outputTopic), exception);
			}
			handleUnfilteredEvent(message,configKey);
		}
	}


	@Override
	protected void wrappedClose() throws Exception {}

	@Override
	protected String getJobLabel() {
		return "USERNAME NORMALIZED AND TAGGING";
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

	/**
	 * Create the task's specific metrics.
	 *
	 * Typically, the function is called from AbstractStreamTask.createTaskMetrics() at init()
	 */
	@Override
	protected void wrappedCreateTaskMetrics() {

		// Create the task's specific metrics
		taskMetrics = new UsernameNormalizationAndTaggingTaskMetrics(statsService);
	}

}