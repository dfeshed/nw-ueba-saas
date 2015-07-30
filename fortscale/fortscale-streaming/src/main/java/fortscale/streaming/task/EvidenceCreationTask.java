package fortscale.streaming.task;

import com.fasterxml.jackson.databind.ObjectMapper;
import fortscale.domain.core.EntityType;
import fortscale.domain.core.Evidence;
import fortscale.domain.core.EvidenceType;
import fortscale.services.dataentity.DataEntitiesConfig;
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
import static fortscale.streaming.ConfigUtils.*;
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

		// Fill the map between the input topic and the data source
		Config fieldsSubset = config.subset("fortscale.events.input.topic.");
		for (String dataSource : fieldsSubset.keySet()) {
			String inputTopic = getConfigString(config, String.format("fortscale.events.input.topic.%s", dataSource));
			int scoreThreshold = Integer.parseInt(getConfigString(config, String.format("fortscale.events.score.threshold.%s", dataSource)));
			List<String> scoreFields = getConfigStringList(config, String.format("fortscale.events.score.fields.%s", dataSource));
			List<String> scoreFieldValues = getConfigStringList(config, String.format("fortscale.events.score.fields.values.%s", dataSource));
			List<String> scoreFieldTypes = null;
			List<String> scoreFieldTypesFields = null;
			if(isConfigContainKey(config, String.format("fortscale.events.score.fields.types.%s", dataSource))) {
				scoreFieldTypes = getConfigStringList(config, String.format("fortscale.events.score.fields.types.%s", dataSource));
			}
			if(isConfigContainKey(config, String.format("fortscale.events.score.fields.types.fields.%s", dataSource))) {
				scoreFieldTypesFields = getConfigStringList(config, String.format("fortscale.events.score.fields.types.fields.%s", dataSource));
			}
			EntityType entityType = EntityType.valueOf(getConfigString(config, String.format("fortscale.events.entityType.%s", dataSource)));
			String entityNameField = getConfigString(config, String.format("fortscale.events.entityName.field.%s", dataSource));
			String startTimestampField = getConfigString(config, String.format("fortscale.events.startTimestamp.field.%s", dataSource));
			String endTimestampField = getConfigString(config, String.format("fortscale.events.endTimestamp.field.%s", dataSource));
			String partitionField = getConfigString(config, String.format("fortscale.events.partition.field.%s", dataSource));
			EvidenceType evidenceType = EvidenceType.valueOf(getConfigString(config, String.format("fortscale.events.evidence.type.%s", dataSource)));
			List<String> dataEntitiesIds = null;
			String dataEntitiesIdsField = null;
			//if dataEntitiesIds is a field name and not a value
			if (isConfigContainKey(config, String.format("fortscale.events.dataEntitiesIds.field.%s", dataSource))) {
				dataEntitiesIdsField = getConfigString(config, String.format("fortscale.events.dataEntitiesIds.field.%s", dataSource));
			} else if (isConfigContainKey(config, String.format("fortscale.events.dataEntitiesIds.%s", dataSource))) {
				dataEntitiesIds = getConfigStringList(config, String.format("fortscale.events.dataEntitiesIds.%s", dataSource));
			}
			List<String> defaultFields = null;
			if (isConfigContainKey(config, String.format("fortscale.events.defaultFields.%s", dataSource))) {
				defaultFields = getConfigStringList(config, String.format("fortscale.events.defaultFields.%s", dataSource));
			}
			topicToDataSourceMap.put(inputTopic, new DataSourceConfiguration(entityType, entityNameField, scoreFields, scoreFieldValues, scoreFieldTypes, scoreFieldTypesFields, startTimestampField, endTimestampField, partitionField, dataEntitiesIdsField, dataEntitiesIds, evidenceType, scoreThreshold, defaultFields));
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
			if (score >= dataSourceConfiguration.scoreThreshold) {

				// create evidence

				// get the start timestamp from the event
				Long startTimestampSeconds = convertToLong(validateFieldExistsAndGetValue(message, messageText, dataSourceConfiguration.startTimestampField));
				Long startTimestamp = TimestampUtils.convertToMilliSeconds(startTimestampSeconds);

				// get the end timestamp from the event
				Long endTimestampSeconds = convertToLong(validateFieldExistsAndGetValue(message, messageText, dataSourceConfiguration.endTimestampField));
				Long endTimestamp = TimestampUtils.convertToMilliSeconds(endTimestampSeconds);

				// get the username from the event
				String entityName = convertToString(validateFieldExistsAndGetValue(message, messageText, dataSourceConfiguration.entityNameField));

				// Get the value in the field which is the anomaly
				String anomalyValue = convertToString(message.get(dataSourceConfiguration.scoreFieldValues.get(index)));

				// Get the type of the anomaly
				String anomalyType = null;
				if (dataSourceConfiguration.scoreFieldTypes != null){
					anomalyType = dataSourceConfiguration.scoreFieldTypes.get(index);
				}
				// if type of the anomaly doesn't exists and instead we have scoreFieldTypesFields, need to get the anomalyType from that field in the message
				else if (dataSourceConfiguration.scoreFieldTypesFields != null) {
					anomalyType = convertToString(message.get(dataSourceConfiguration.scoreFieldTypesFields.get(index)));
				}

				List<String> dataEntitiesIds = null;
				//if datEntitiesIds exists (static for the topic)
				if (dataSourceConfiguration.dataEntitiesIds != null){
					dataEntitiesIds = dataSourceConfiguration.dataEntitiesIds;
				}
				// if datEntitiesIds doesn't exists and instead we have dataEntitiesIdsField, need to get the dataEntitiesIds from that field in the message
				else if (dataSourceConfiguration.dataEntitiesIdsField != null) {
					dataEntitiesIds = (List) validateFieldExistsAndGetValue(message, messageText, dataSourceConfiguration.dataEntitiesIdsField);
				}

				// Create evidence from event
				Evidence evidence = evidencesService.createTransientEvidence(dataSourceConfiguration.entityType, entityName, dataSourceConfiguration.evidenceType, new Date(startTimestamp), new Date(endTimestamp), scoreField, dataEntitiesIds, score, anomalyValue, anomalyType);

				// Save evidence to MongoDB
				try {
					evidencesService.saveEvidenceInRepository(evidence);
				} catch (DuplicateKeyException e) {
					logger.warn("Got duplication for evidence {}. Going to drop it.", evidence.getName());
					// In case this evidence is duplicated, we don't send it to output topic and continue to next score
					continue;
				}

				// only if we have default fields for the message
				// add the event to the top events of the evidence for alerts topic only - get only the default fields
				// this is using us for Esper to query event's fields
				if (dataSourceConfiguration.defaultFields != null) {
					JSONObject newMessage = convertMessageToStandardFormat(message, dataSourceConfiguration);
					String jsonString = newMessage.toJSONString();
					evidence.setTop3events(new Map[] { mapper.readValue(jsonString, HashMap.class) });
				}

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
		for (String fieldId : dataSourceConfiguration.defaultFields) {
			Object valueOfColumn = message.get(fieldId);
			if (valueOfColumn != null) {
				newMessage.put(fieldId, valueOfColumn);
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
		String[] fieldHierarchy = field.split("\\.");
		Object value = message;
		for(String fieldPart : fieldHierarchy){
			value = ((JSONObject) value).get(fieldPart);

		}
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
		String[] fieldHierarchy = partitionKeyField.split("\\.");
		Object value = event;
		for(String fieldPart : fieldHierarchy){
			value = ((JSONObject) value).get(fieldPart);

		}
		return value;
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

		protected DataSourceConfiguration(EntityType entityType, String entityNameField, List<String> scoreFields, List<String> scoreFieldValues, List<String> scoreFieldTypes, List<String> scoreFieldTypesFields,
				String startTimestampField, String endTimestampField, String partitionField, String dataEntitiesIdsField, List<String> dataEntitiesIds,
				EvidenceType evidenceType, int scoreThreshold, List<String> defaultFields) {
			this.evidenceType = evidenceType;
			this.dataEntitiesIds = dataEntitiesIds;
			this.dataEntitiesIdsField = dataEntitiesIdsField;
			this.startTimestampField = startTimestampField;
			this.endTimestampField = endTimestampField;
			this.entityType = entityType;
			this.entityNameField = entityNameField;
			this.partitionField = partitionField;
			this.scoreFields = scoreFields;
			this.scoreFieldValues = scoreFieldValues;
			this.scoreFieldTypes = scoreFieldTypes;
			this.scoreFieldTypesFields = scoreFieldTypesFields;
			this.defaultFields = defaultFields;
			this.scoreThreshold = scoreThreshold;
		}

		public int scoreThreshold;
		public EvidenceType evidenceType;
		public List<String> dataEntitiesIds;
		public String startTimestampField;
		public String endTimestampField;
		public String dataEntitiesIdsField;
		public String entityNameField;
		public EntityType entityType;
		public String partitionField;
		public List<String> scoreFields;
		public List<String> scoreFieldValues;
		public List<String> scoreFieldTypes;
		public List<String> scoreFieldTypesFields;
		public List<String> defaultFields;
	}
}
