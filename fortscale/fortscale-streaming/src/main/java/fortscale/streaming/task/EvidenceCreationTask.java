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


	private DataEntitiesConfig dataEntitiesConfig;


	@Override
	protected void wrappedInit(Config config, TaskContext context) throws Exception {

		// Get the user service (for Mongo) from spring
		evidencesService = SpringService.getInstance().resolve(EvidencesService.class);

		// Configuration for data sources (relevant for top-3-events)
		dataEntitiesConfig = SpringService.getInstance().resolve(DataEntitiesConfig.class);

		// Get the output topic
		outputTopic = getConfigString(config, "fortscale.output.topic");

		// Fill the map between the input topic and the data source
		Config fieldsSubset = config.subset("fortscale.events.input.topic.");
		for (String dataSource : fieldsSubset.keySet()) {
			String inputTopic = getConfigString(config, String.format("fortscale.events.input.topic.%s", dataSource));
			int scoreThreshold = Integer.parseInt(getConfigString(config, String.format("fortscale.events.score.threshold.%s", dataSource)));
			List<String> anomalyFields = null;
			String scoreField = null;
			String  anomalyValueField = null;
			String anomalyTypeField = null;
			if (isConfigContainKey(config, String.format("fortscale.events.anomalyFields.%s", dataSource))) {
				anomalyFields = getConfigStringList(config, String.format("fortscale.events.anomalyFields.%s", dataSource));
			}

			if (isConfigContainKey(config, String.format("fortscale.events.scoreField.%s", dataSource))) {
				scoreField = getConfigString(config, String.format("fortscale.events.scoreField.%s", dataSource));
			}

			if (isConfigContainKey(config, String.format("fortscale.events.anomalyValueField.%s", dataSource))) {
				anomalyValueField = getConfigString(config, String.format("fortscale.events.anomalyValueField.%s", dataSource));
			}

			if (isConfigContainKey(config, String.format("fortscale.events.anomalyTypeField.%s", dataSource))) {
				anomalyTypeField = getConfigString(config, String.format("fortscale.events.anomalyTypeField.%s", dataSource));
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
				defaultFields = getConfigStringList(config, String.format("fortscale.events.addDefaultFields.%s", dataSource));
			}
			topicToDataSourceMap.put(inputTopic, new DataSourceConfiguration(
					evidenceType,scoreThreshold,dataEntitiesIds,dataEntitiesIdsField,startTimestampField,endTimestampField,entityType,entityNameField,
					partitionField,anomalyFields, scoreField, anomalyValueField,anomalyTypeField , defaultFields));


			logger.info("Finished loading configuration for data source {}", dataSource);
		}
	}

	@Override
	protected void wrappedProcess(IncomingMessageEnvelope envelope, MessageCollector collector,
			TaskCoordinator coordinator) throws Exception {

		// parse the message into json
		String messageText = (String) envelope.getMessage();
		JSONObject message = (JSONObject) JSONValue.parseWithException(messageText);

		// Get the input topic
		String inputTopic = envelope.getSystemStreamPartition().getSystemStream().getStream();

		// Get relevant data source according to topic
		DataSourceConfiguration dataSourceConfiguration = topicToDataSourceMap.get(inputTopic);
		if (dataSourceConfiguration == null) {
			logger.error("No data source is defined for input topic {} ", inputTopic);
			return;
		}

		// Go over anomaly fields, check each one of them for anomaly according to threshold
		List<String> dataEntitiesIds = null;
		//if datEntitiesIds exists (static for the topic)
		if (dataSourceConfiguration.dataEntitiesIds != null) {
			dataEntitiesIds = dataSourceConfiguration.dataEntitiesIds;
			// if datEntitiesIds doesn't exists and instead we have dataEntitiesIdsField, need to get the dataEntitiesIds from that field in the message
		} else if (dataSourceConfiguration.dataEntitiesIdsField != null) {
			dataEntitiesIds = (List) validateFieldExistsAndGetValue(message, dataSourceConfiguration.dataEntitiesIdsField);
		}
		DataEntity dataEntity = dataEntitiesConfig.getEntityFromOverAllCache(dataEntitiesIds.get(0));
		if (dataSourceConfiguration.anomalyFields != null) {
			for (String anomalyField : dataSourceConfiguration.anomalyFields) {
				DataEntityField dataEntityField = dataEntity.getField(anomalyField);
				String scoreField = dataEntitiesConfig.getFieldColumn(dataEntitiesIds.get(0), dataEntityField.getScoreField());
				createEvidence(dataSourceConfiguration, collector, inputTopic, message, dataEntitiesIds, scoreField, dataEntitiesConfig.getFieldColumn(dataEntitiesIds.get(0), anomalyField), anomalyField);
			}
		} else {
			String anomalyField = convertToString(validateFieldExistsAndGetValue(message, dataSourceConfiguration.anomalyTypeField));
			createEvidence(dataSourceConfiguration, collector, inputTopic, message, dataEntitiesIds, dataSourceConfiguration.scoreField, dataSourceConfiguration.anomalyValueField, anomalyField);
		}
	}



	private void createEvidence(DataSourceConfiguration dataSourceConfiguration, MessageCollector collector, String inputTopic, JSONObject message, List<String> dataEntitiesIds, String scoreField, String anomalyValueField, String anomalyTypeField) throws Exception{

		// check score
		Double score = convertToDouble(validateFieldExistsAndGetValue(message, scoreField));
		if (score >= dataSourceConfiguration.scoreThreshold) {

			// create evidence

			// get the start timestamp from the event
			Long startTimestampSeconds = convertToLong(validateFieldExistsAndGetValue(message, dataSourceConfiguration.startTimestampField));
			Long startTimestamp = TimestampUtils.convertToMilliSeconds(startTimestampSeconds);

			// get the end timestamp from the event
			Long endTimestampSeconds = convertToLong(validateFieldExistsAndGetValue(message, dataSourceConfiguration.endTimestampField));
			Long endTimestamp = TimestampUtils.convertToMilliSeconds(endTimestampSeconds);

			// get the username from the event
			String entityName = convertToString(validateFieldExistsAndGetValue(message, dataSourceConfiguration.entityNameField));

			// Get the value in the field which is the anomaly
			String anomalyValue = convertToString(message.get(anomalyValueField));



			// Create evidence from event
			Evidence evidence = evidencesService.createTransientEvidence(dataSourceConfiguration.entityType, dataSourceConfiguration.entityNameField, entityName, dataSourceConfiguration.evidenceType, new Date(startTimestamp), new Date(endTimestamp), dataEntitiesIds, score, anomalyValue, anomalyTypeField);

			// Save evidence to MongoDB
			try {
				evidencesService.saveEvidenceInRepository(evidence);
			} catch (DuplicateKeyException e) {
				logger.warn("Got duplication for evidence {}. Going to drop it.", evidence.toString());
				// In case this evidence is duplicated, we don't send it to output topic and continue to next score
				return;
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
				collector.send(new OutgoingMessageEnvelope(new SystemStream("kafka", outputTopic), getPartitionKey(dataSourceConfiguration.partitionField, message), mapper.writeValueAsString(evidence)));
			} catch (Exception exception) {
				throw new KafkaPublisherException(String.format("failed to send event from input topic %s, output topic %s after evidence creation for evidence", inputTopic, outputTopic, mapper.writeValueAsString(evidence)), exception);
			}
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
	 * @param field    The requested field
	 * @return The value of the field
	 * @throws StreamMessageNotContainFieldException in case the field doesn't exist in the JSON
	 */
	private Object validateFieldExistsAndGetValue(JSONObject message, String field) throws Exception {
		String[] fieldHierarchy = field.split("\\.");
		Object value = message;
		for(String fieldPart : fieldHierarchy){
			value = ((JSONObject) value).get(fieldPart);

		}
		if (value == null) {
			logger.error("message {} does not contains value in field {}", mapper.writeValueAsString(message), field);
			throw new StreamMessageNotContainFieldException(mapper.writeValueAsString(message), field);
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

		protected DataSourceConfiguration(EvidenceType evidenceType,int scoreThreshold, List<String> dataEntitiesIds, String dataEntitiesIdsField, String startTimestampField, String endTimestampField, EntityType entityType, String entityNameField,
				 String partitionField, List<String> anomalyFields, String scoreField, String anomalyValueField, String anomalyTypeField , List<String> defaultFields) {
			this.evidenceType = evidenceType;
			this.scoreThreshold = scoreThreshold;
			this.dataEntitiesIds = dataEntitiesIds;
			this.dataEntitiesIdsField = dataEntitiesIdsField;
			this.startTimestampField = startTimestampField;
			this.endTimestampField = endTimestampField;
			this.entityType = entityType;
			this.entityNameField = entityNameField;
			this.partitionField = partitionField;
			this.anomalyFields = anomalyFields;
			this.scoreField = scoreField;
			this.anomalyValueField = anomalyValueField;
			this.anomalyTypeField = anomalyTypeField;
			this.defaultFields = defaultFields;

		}


		public EvidenceType evidenceType;
		public int scoreThreshold;
		public List<String> dataEntitiesIds;
		public String dataEntitiesIdsField;
		public String startTimestampField;
		public String endTimestampField;
		public EntityType entityType;
		public String entityNameField;
		public String partitionField;
		public List<String> anomalyFields;
		public String scoreField;
		public String anomalyValueField;
		public String anomalyTypeField;
		public List<String> defaultFields;
	}
}
