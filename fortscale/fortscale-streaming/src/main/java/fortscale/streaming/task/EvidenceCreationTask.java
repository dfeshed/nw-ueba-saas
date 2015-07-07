package fortscale.streaming.task;

import com.fasterxml.jackson.databind.ObjectMapper;
import fortscale.domain.core.EntityType;
import fortscale.domain.core.Evidence;
import fortscale.services.impl.EvidencesService;
import fortscale.streaming.exceptions.KafkaPublisherException;
import fortscale.streaming.exceptions.StreamMessageNotContainFieldException;
import fortscale.streaming.service.SpringService;
import fortscale.utils.TimestampUtils;
import net.minidev.json.JSONObject;
import net.minidev.json.JSONValue;
import org.apache.samza.config.Config;
import org.apache.samza.storage.kv.Entry;
import org.apache.samza.storage.kv.KeyValueIterator;
import org.apache.samza.storage.kv.KeyValueStore;
import org.apache.samza.system.IncomingMessageEnvelope;
import org.apache.samza.system.OutgoingMessageEnvelope;
import org.apache.samza.system.SystemStream;
import org.apache.samza.task.MessageCollector;
import org.apache.samza.task.TaskContext;
import org.apache.samza.task.TaskCoordinator;
import org.springframework.dao.DuplicateKeyException;
import parquet.org.slf4j.Logger;
import parquet.org.slf4j.LoggerFactory;

import java.util.*;

import static com.google.common.base.Preconditions.checkNotNull;
import static fortscale.streaming.ConfigUtils.getConfigString;
import static fortscale.streaming.ConfigUtils.getConfigStringList;
import static fortscale.utils.ConversionUtils.*;

/**
 * Samza task that creates evidences in MongoDB
 *
 * Date: 6/23/2015.
 */
public class EvidenceCreationTask extends AbstractStreamTask {

	/**
	 * The level DB store name
	 */
	private static final String storeName = "evidences";

	/**
	 * Logger
	 */
	private static Logger logger = LoggerFactory.getLogger(EvidenceCreationTask.class);

	/**
	 * The level DB store: ID to evidence
	 */
	protected KeyValueStore<String, Evidence> store;

	/**
	 * Evidences service (for Mongo export)
	 */
	protected EvidencesService evidencesService;

	/**
	 * The output topic for the evidences
	 */
	protected String outputTopic;

	/**
	 * The time field in the input event
	 */
	protected String timestampField;

	/**
	 * Threshold for creating evidences
	 */
	protected int scoreThreshold;

	/**
	 * Map between the input topic and the relevant data-source
	 */
	protected Map<String, DataSourceConfiguration> topicToDataSourceMap = new HashMap<>();

	/**
	 * JSON serializer
	 */
	protected ObjectMapper mapper = new ObjectMapper();




	@Override
	protected void wrappedInit(Config config, TaskContext context) throws Exception {

		// Get the levelDB store
		store = (KeyValueStore<String, Evidence>) context.getStore(storeName);

		// Get the user service (for Mongo) from spring
		evidencesService = SpringService.getInstance().resolve(EvidencesService.class);

		// Get the output topic
		outputTopic = getConfigString(config, "fortscale.output.topic");

		// get the timestamp field
		timestampField = getConfigString(config, "fortscale.timestamp.field");

		// get the threshold for creating evidences
		scoreThreshold = config.getInt("fortscale.score.threshold");

		// Fill the map between the input topic and the data source
		Config fieldsSubset = config.subset("fortscale.events.input.topic.");
		for (String dataSource : fieldsSubset.keySet()) {
			String inputTopic = getConfigString(config, String.format("fortscale.events.input.topic.%s", dataSource));
			String classifier = getConfigString(config, String.format("fortscale.events.classifier.%s", dataSource));
			List<String> scoreFields = getConfigStringList(config, String.format("fortscale.events.score.fields.%s", dataSource));
			List<String> scoreFieldValues = getConfigStringList(config, String.format("fortscale.events.score.fields.values.%s", dataSource));
			List<String> scoreFieldTypes = getConfigStringList(config, String.format("fortscale.events.score.fields.types.%s", dataSource));
			String usernameField = getConfigString(config, String.format("fortscale.events.normalizedusername.field.%s", dataSource));
			String partitionField = getConfigString(config, String.format("fortscale.events.partition.field.%s", dataSource));
			topicToDataSourceMap.put(inputTopic,
					new DataSourceConfiguration(usernameField, scoreFields, scoreFieldValues, scoreFieldTypes, partitionField, classifier));
			logger.info("Finished loading configuration for data source {}", dataSource);
		}

	}

	@Override
	protected void wrappedProcess(IncomingMessageEnvelope envelope, MessageCollector collector,
			TaskCoordinator coordinator) throws Exception {

		// parse the message into json
		String messageText = (String) envelope.getMessage();
		net.minidev.json.JSONObject message = (net.minidev.json.JSONObject) JSONValue.parseWithException(messageText);

		// Get the input topic
		String inputTopic = envelope.getSystemStreamPartition().getSystemStream().getStream();

		// Get relevant data source according to topic
		DataSourceConfiguration dataSourceConfiguration = topicToDataSourceMap.get(inputTopic);
		if (dataSourceConfiguration == null) {
			logger.error("No data source is defined for input topic {} ", inputTopic);
			return;
		}


		// Go over score fields, check each one of them for anomaly
		int index = 0;
		for (String scoreField : dataSourceConfiguration.scoreFields) {

			// check score
			Double score = convertToDouble(validateFieldExistsAndGetValue(message, messageText, scoreField));
			if (score >= scoreThreshold) {

				// create evidence

				// get the timestamp from the event
				Long timestampSeconds = convertToLong(validateFieldExistsAndGetValue(message, messageText, timestampField));
				Long timestamp = TimestampUtils.convertToMilliSeconds(timestampSeconds);

				// get the username from the event
				String normalizedUsername = convertToString(validateFieldExistsAndGetValue(message, messageText, dataSourceConfiguration.userNameField));

				// Get the value in the field which is the anomaly
				String anomalyValue = convertToString(message.get(dataSourceConfiguration.scoreFieldValues.get(index)));

				// Get the type of the anomaly
				String anomalyType = dataSourceConfiguration.scoreFieldTypes.get(index);

				// Create evidence from event
				Evidence evidence = evidencesService.createTransientEvidence(EntityType.User, normalizedUsername,
						new Date(timestamp), scoreField, dataSourceConfiguration.classifier, score, anomalyValue, anomalyType);

				// add the event to the top event os the supporting information
				evidence.setTop3eventsJsonStr("[" + messageText + "]");
				evidence.setNumOfEvents(1);

				// Save evidence to levelDB
				store.put(evidence.getId(), evidence);

				// Send evidence to output inputTopic

				try {
					collector.send(
							new OutgoingMessageEnvelope(
									new SystemStream("kafka", outputTopic),
									getPartitionKey(dataSourceConfiguration.partitionField, message),
									mapper.writeValueAsString(evidence)));
				} catch (Exception exception) {
					throw new KafkaPublisherException(
							String.format("failed to send event from input topic %s, output topic %s after evidence creation for score %s", inputTopic, outputTopic, scoreField),
							exception);
				}
			}

			index++;
		}


	}

	/**
	 * Validate that the expected field has value in the message JSON and return the value
	 *
	 * @param message        The message JSON
	 * @param messageText    The message JSON as string
	 * @param field    The requested field
	 * @return The value of the field
	 * @throws StreamMessageNotContainFieldException in case the field doesn't exist in the JSON
	 */
	private Object validateFieldExistsAndGetValue(JSONObject message, String messageText, String field) throws StreamMessageNotContainFieldException {
		Object value = message.get(field);
		if (value == null) {
			logger.error("message {} does not contains value in field {}", messageText, field);
			throw new StreamMessageNotContainFieldException(messageText, field);
		}
		return value;
	}

	/**
	 * Get the partition key to use for outgoing message envelope for the given event
	 */
	private Object getPartitionKey(String partitionKeyField, JSONObject event) {
		checkNotNull(partitionKeyField);
		checkNotNull(event);
		return event.get(partitionKeyField);
	}


	@Override
	protected void wrappedWindow(MessageCollector collector, TaskCoordinator coordinator) throws Exception {

		// copy level DB to mongo DB
		if (evidencesService !=null) {
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
		if (evidencesService != null) {
			copyLevelDbToMongoDB();
		}
		evidencesService = null;

	}


	/**
	 * Go over all users in the last-activity map and write them to Mongo
	 */
	private void copyLevelDbToMongoDB() {

		KeyValueIterator<String, Evidence> iter = store.all();

		List<String> evidences = new LinkedList<>();
		while (iter.hasNext()) {
			Entry<String, Evidence> evidence = iter.next();
			// update evidence in mongo
			try {
				evidencesService.saveEvidenceInRepository(evidence.getValue());
			} catch (DuplicateKeyException e) {
				logger.warn("Got duplication for evidence {}. Going to drop it.", evidence.getValue().getName());
			}
			evidences.add(evidence.getKey());
		}
		iter.close();

		// remove from store all users after they were copied to Mongo
		for (String evidence : evidences) {
			store.delete(evidence);
		}

	}


	/**
	 * Private class for saving data-source specific configuration in-memory
	 */
	protected static class DataSourceConfiguration {

		protected DataSourceConfiguration(String userNameField, List<String> scoreFields, List<String> scoreFieldValues,
				List<String> scoreFieldTypes, String partitionField, String classifier) {
			this.classifier = classifier;
			this.userNameField = userNameField;
			this.partitionField = partitionField;
			this.scoreFields = scoreFields;
			this.scoreFieldValues = scoreFieldValues;
			this.scoreFieldTypes = scoreFieldTypes;

		}

		public String classifier;
		public String userNameField;
		public String partitionField;
		public List<String> scoreFields;
		public List<String> scoreFieldValues;
		public List<String> scoreFieldTypes;
	}
}
