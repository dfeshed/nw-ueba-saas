package fortscale.streaming.task;

import com.fasterxml.jackson.databind.ObjectMapper;
import fortscale.domain.core.EntityType;
import fortscale.domain.core.Evidence;
import fortscale.domain.core.EvidenceType;
import fortscale.services.dataentity.DataEntitiesConfig;
import fortscale.services.dataentity.DataEntity;
import fortscale.services.dataentity.DataEntityField;
import fortscale.services.impl.EvidencesService;
import fortscale.streaming.exceptions.KafkaPublisherException;
import fortscale.streaming.exceptions.StreamMessageNotContainFieldException;
import fortscale.streaming.service.SpringService;
import fortscale.utils.TimestampUtils;
import net.minidev.json.JSONObject;
import net.minidev.json.JSONValue;
import org.apache.samza.config.Config;
import org.apache.samza.system.IncomingMessageEnvelope;
import org.apache.samza.system.OutgoingMessageEnvelope;
import org.apache.samza.system.SystemStream;
import org.apache.samza.task.MessageCollector;
import org.apache.samza.task.TaskContext;
import org.apache.samza.task.TaskCoordinator;
import org.springframework.dao.DuplicateKeyException;
import parquet.org.slf4j.Logger;
import parquet.org.slf4j.LoggerFactory;

import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

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
	 * Logger
	 */
	private static Logger logger = LoggerFactory.getLogger(EvidenceCreationTask.class);

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

		// Get the user service (for Mongo) from spring
		evidencesService = SpringService.getInstance().resolve(EvidencesService.class);

		// Configuration for data sources (relevant for top-3-events)
		DataEntitiesConfig dataEntitiesConfig = SpringService.getInstance().resolve(DataEntitiesConfig.class);

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
			String dataEntityId = getConfigString(config, String.format("fortscale.events.dataEntityId.%s", dataSource));
			List<String> scoreFields = getConfigStringList(config, String.format("fortscale.events.score.fields.%s", dataSource));
			List<String> scoreFieldValues = getConfigStringList(config, String.format("fortscale.events.score.fields.values.%s", dataSource));
			List<String> scoreFieldTypes = getConfigStringList(config, String.format("fortscale.events.score.fields.types.%s", dataSource));
			String usernameField = getConfigString(config, String.format("fortscale.events.normalizedusername.field.%s", dataSource));
			String partitionField = getConfigString(config, String.format("fortscale.events.partition.field.%s", dataSource));



			// get the default fields for the data source, to be used later for top-3-events
			DataEntity dataEntity = dataEntitiesConfig.getEntityFromOverAllCache(dataEntityId);
			HashMap<String, String> fieldColumnToFieldId = new HashMap<>(); // Mapping: field-name-in-DB -> field-id
			if (dataEntity==null) {
				logger.error("Could not get metadata for entity {} . Top events won't be available", dataSource);
			} else {
				for (DataEntityField field : dataEntity.getFields()) {
					if (field.getIsDefaultEnabled() && !field.isLogicalOnly() &&  (field.getAttributes() == null || !field.getAttributes().contains("internal"))) {
						String fieldColumn = dataEntitiesConfig.getFieldColumn(dataEntity.getId(), field.getId());
						fieldColumnToFieldId.put(fieldColumn, field.getId());
					}
				}
			}

			topicToDataSourceMap.put(inputTopic,
					new DataSourceConfiguration(usernameField, scoreFields, scoreFieldValues, scoreFieldTypes, partitionField, dataEntityId, fieldColumnToFieldId));


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
						new Date(timestamp), scoreField, dataSourceConfiguration.dataEntityId, score, anomalyValue, anomalyType);

				// add the event to the top events
				JSONObject newMessage = convertMessageToStandardFormat(message, dataSourceConfiguration);
				String jsonString = newMessage.toJSONString();
				evidence.setNumOfEvents(1);
				evidence.setEvidenceType(EvidenceType.AnomalySingleEvent);

				// Save evidence to MongoDB
				try {
					evidencesService.saveEvidenceInRepository(evidence);
				} catch (DuplicateKeyException e) {
					logger.warn("Got duplication for evidence {}. Going to drop it.", evidence.getName());
					// In case this evidence is duplicated, we don't send it to output topic and continue to next score
					continue;
				}

				// add the map of events to the evidence instead of the string - for alerts topic only!
				evidence.setTop3eventsJsonStr(null); // for performance
				evidence.setTop3events(new Map[] { mapper.readValue(jsonString, HashMap.class) }); // for Esper to query event's fields

				// Send evidence to output topic
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
	 * Convert the event JSON to thin event with only the required fields, and with field-id from entities.properties
	 * @param message    The original event
	 * @param dataSourceConfiguration    The configuration of the specific data source
	 * @return	New message
	 */
	private JSONObject convertMessageToStandardFormat(JSONObject message,
			DataSourceConfiguration dataSourceConfiguration) {
		JSONObject newMessage = new JSONObject();
		for (Map.Entry<String, String> columnToId : dataSourceConfiguration.fieldColumnToFieldId.entrySet()) {
			Object valueOfColumn = message.get(columnToId.getKey());
			if (valueOfColumn != null) {
				newMessage.put(columnToId.getValue(), valueOfColumn);
			}
		}
		return newMessage;
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
		// nothing
	}

	/**
	 * The close method should be called upon streaming task shutdown
	 * @throws Exception
	 */
	@Override
	protected void wrappedClose() throws Exception {
		// nothing
	}

	/**
	 * Private class for saving data-source specific configuration in-memory
	 */
	protected static class DataSourceConfiguration {

		protected DataSourceConfiguration(String userNameField, List<String> scoreFields, List<String> scoreFieldValues,
				List<String> scoreFieldTypes, String partitionField, String dataEntityId,
				HashMap<String, String> fieldColumnToFieldId) {
			this.dataEntityId = dataEntityId;
			this.userNameField = userNameField;
			this.partitionField = partitionField;
			this.scoreFields = scoreFields;
			this.scoreFieldValues = scoreFieldValues;
			this.scoreFieldTypes = scoreFieldTypes;
			this.fieldColumnToFieldId = fieldColumnToFieldId;

		}

		public String dataEntityId;
		public String userNameField;
		public String partitionField;
		public List<String> scoreFields;
		public List<String> scoreFieldValues;
		public List<String> scoreFieldTypes;
		public Map<String, String> fieldColumnToFieldId;
	}
}
