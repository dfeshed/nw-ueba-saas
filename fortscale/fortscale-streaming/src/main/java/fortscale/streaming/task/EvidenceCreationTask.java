package fortscale.streaming.task;

import com.fasterxml.jackson.databind.ObjectMapper;
import fortscale.domain.core.*;
import fortscale.services.EvidencesService;
import fortscale.common.dataentity.DataEntitiesConfig;
import fortscale.common.dataentity.DataEntity;
import fortscale.common.dataentity.DataEntityField;
import fortscale.streaming.exceptions.KafkaPublisherException;
import fortscale.streaming.exceptions.StreamMessageNotContainFieldException;
import fortscale.streaming.service.BDPService;
import fortscale.services.impl.SpringService;
import fortscale.streaming.service.config.StreamingTaskDataSourceConfigKey;
import fortscale.utils.time.TimestampUtils;
import net.minidev.json.JSONObject;
import net.minidev.json.JSONValue;
import org.apache.samza.config.Config;
import org.apache.samza.metrics.Counter;
import org.apache.samza.system.IncomingMessageEnvelope;
import org.apache.samza.system.OutgoingMessageEnvelope;
import org.apache.samza.system.SystemStream;
import org.apache.samza.task.MessageCollector;
import org.apache.samza.task.TaskContext;
import org.apache.samza.task.TaskCoordinator;
import org.joda.time.Duration;
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

	private static final int HOURS_IN_DAY = 24;
	private static final int HOURS_IN_WEEK = HOURS_IN_DAY * 7;
	private static final int HOURS_IN_MONTH = HOURS_IN_DAY * 30;
	private static final int ONE_HOUR = 1;

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

	private String supportingInformationField;

	private BDPService bdpService;

	private Counter lastTimestampCount;

	@Override
	protected void wrappedInit(Config config, TaskContext context) throws Exception {

		// Get the user service (for Mongo) from spring
		evidencesService = SpringService.getInstance().resolve(EvidencesService.class);

		// Configuration for data sources (relevant for top-3-events)
		dataEntitiesConfig = SpringService.getInstance().resolve(DataEntitiesConfig.class);

		// Get the output topic
		outputTopic = getConfigString(config, "fortscale.output.topic");

		supportingInformationField = getConfigString(config, "fortscale.evidence.supporting.information.field");

		// First, manage configuration by data source.
		for (Map.Entry<String,String> ConfigField : config.subset("fortscale.events.entry.name.").entrySet()) {
			String configKey = ConfigField.getValue();
			createConfig(config, configKey);
		}
		//manage configuration by input topics (those which don't have data-source : Notifications, aggregated)
		Config inputTopics = config.subset("fortscale.events.entry.input.topic.");
			for(String topic: inputTopics.keySet() ){
			if( !isConfigContainKey(config,String.format("fortscale.events.entry.name.%s",topic))){ // if no data source available
				//create configuration by input
				createConfig(config, topic);
			}
		}

		lastTimestampCount = context.getMetricsRegistry().newCounter(getClass().getName(),
				String.format("%s-last-message-epochtime", config.get("job.name")));

		bdpService = new BDPService();

	}

	private void createConfig(Config config, String configKey) {
			List<String> anomalyFields = null;
		String scoreField = null;
		String  anomalyValueField = null;
		String anomalyTypeField = null;
		String preProcessClassField = null;
		String postProcessClassField = null;
		String lastState=null;
		String dataSource=null;
		String key = null;

		if (isConfigContainKey(config, String.format("fortscale.events.entry.%s.last.state", configKey))&&
				isConfigContainKey(config, String.format("fortscale.events.entry.%s.data.source", configKey))) {
			lastState = getConfigString(config, String.format("fortscale.events.entry.%s.last.state", configKey));
			dataSource = getConfigString(config, String.format("fortscale.events.entry.%s.data.source", configKey));
			key= new StreamingTaskDataSourceConfigKey(dataSource,lastState).getConfigKeyStr();

		}
		else{
			key = getConfigString(config, String.format("fortscale.events.entry.input.topic.%s", configKey));
		}

		int scoreThreshold = Integer.parseInt(getConfigString(config, String.format("fortscale.events.entry.%s.score.threshold", configKey)));

		if (isConfigContainKey(config, String.format("fortscale.events.entry.%s.anomalyFields", configKey))) {
			anomalyFields = getConfigStringList(config, String.format("fortscale.events.entry.%s.anomalyFields", configKey));
		}

		if (isConfigContainKey(config, String.format("fortscale.events.entry.%s.scoreField", configKey))) {
			scoreField = getConfigString(config, String.format("fortscale.events.entry.%s.scoreField", configKey));
		}

		if (isConfigContainKey(config, String.format("fortscale.events.entry.%s.anomalyValueField", configKey))) {
			anomalyValueField = getConfigString(config, String.format("fortscale.events.entry.%s.anomalyValueField", configKey));
		}

		if (isConfigContainKey(config, String.format("fortscale.events.entry.%s.anomalyTypeField", configKey))) {
			anomalyTypeField = getConfigString(config, String.format("fortscale.events.entry.%s.anomalyTypeField", configKey));
		}

		if (isConfigContainKey(config, String.format("fortscale.events.entry.%s.preprocess.class", configKey))) {
			preProcessClassField = getConfigString(config, String.format("fortscale.events.entry.%s.preprocess.class", configKey));
		}

		if (isConfigContainKey(config, String.format("fortscale.events.entry.%s.postprocess.class", configKey))) {
			postProcessClassField = getConfigString(config, String.format("fortscale.events.entry.%s.postprocess.class", configKey));
		}
		EntityType entityType = EntityType.valueOf(getConfigString(config, String.format("fortscale.events.entry.%s.entityType", configKey)));
		String entityNameField = getConfigString(config, String.format("fortscale.events.entry.%s.entityName.field", configKey));
		String startTimestampField = getConfigString(config, String.format("fortscale.events.entry.%s.startTimestamp.field", configKey));
		String endTimestampField = getConfigString(config, String.format("fortscale.events.entry.%s.endTimestamp.field", configKey));
		String partitionField = getConfigString(config, String.format("fortscale.events.entry.%s.partition.field", configKey));
		EvidenceType evidenceType = EvidenceType.valueOf(getConfigString(config, String.format("fortscale.events.entry.%s.evidence.type", configKey)));
		List<String> dataEntitiesIds = null;
		String dataEntitiesIdsField = null;
		String totalFieldPath = null;
		String entitySupportingInformationPopulatorClass = null;
		//if dataEntitiesIds is a field name and not a value
		if (isConfigContainKey(config, String.format("fortscale.events.entry.%s.dataEntitiesIds.field", configKey))) {
			dataEntitiesIdsField = getConfigString(config, String.format("fortscale.events.entry.%s.dataEntitiesIds.field", configKey));
		} else if (isConfigContainKey(config, String.format("fortscale.events.entry.%s.dataEntitiesIds", configKey))) {
			dataEntitiesIds = getConfigStringList(config, String.format("fortscale.events.entry.%s.dataEntitiesIds", configKey));
		}
		List<String> defaultFields = null;
		if (isConfigContainKey(config, String.format("fortscale.events.entry.%s.defaultFields", configKey))) {
			defaultFields = getConfigStringList(config, String.format("fortscale.events.entry.%s.addDefaultFields", configKey));
		}

		if (isConfigContainKey(config, String.format("fortscale.events.entry.%s.total.field.path", configKey))) {
			totalFieldPath = getConfigString(config, String.format("fortscale.events.entry.%s.total.field.path", configKey));
		}
		if (isConfigContainKey(config, String.format("fortscale.events.entry.%s.supportinginformation.populator", configKey))) {
			entitySupportingInformationPopulatorClass = getConfigString(config,
					String.format("fortscale.events.entry.%s.supportinginformation.populator", configKey));
		}
		topicToDataSourceMap.put(key, new DataSourceConfiguration(evidenceType,dataSource,lastState,scoreThreshold,
				dataEntitiesIds, dataEntitiesIdsField, startTimestampField, endTimestampField, entityType,
				entityNameField, partitionField, anomalyFields, scoreField, anomalyValueField, anomalyTypeField,
				preProcessClassField, postProcessClassField, defaultFields, totalFieldPath,
				entitySupportingInformationPopulatorClass));

		logger.info("Finished loading configuration for data source {}", configKey);
	}

	@Override
	protected void wrappedProcess(IncomingMessageEnvelope envelope, MessageCollector collector,
			TaskCoordinator coordinator) throws Exception {

		// parse the message into json
		String messageText = (String) envelope.getMessage();
		JSONObject message = (JSONObject) JSONValue.parseWithException(messageText);
		String inputTopic = envelope.getSystemStreamPartition().getSystemStream().getStream();
		StreamingTaskDataSourceConfigKey configKey = extractDataSourceConfigKeySafe(message);
		DataSourceConfiguration dataSourceConfiguration = getDataSourceConfiguration(configKey, inputTopic);
		if (dataSourceConfiguration == null)
			return;
        //Get the total events amount if exist
        Integer totalAmountOfEvents = null;

		if(dataSourceConfiguration.totalFieldPath != null)
			totalAmountOfEvents = convertToInteger(validateFieldExistsAndGetValue(message, dataSourceConfiguration.totalFieldPath,false));


		// Go over anomaly fields, check each one of them for anomaly according to threshold
		List<String> dataEntitiesIds = null;
		//if datEntitiesIds exists (static for the topic)
		if (dataSourceConfiguration.dataEntitiesIds != null) {
			dataEntitiesIds = dataSourceConfiguration.dataEntitiesIds;
			// if datEntitiesIds doesn't exists and instead we have dataEntitiesIdsField, need to get the dataEntitiesIds from that field in the message
		} else if (dataSourceConfiguration.dataEntitiesIdsField != null) {
			dataEntitiesIds = (List) validateFieldExistsAndGetValue(message, dataSourceConfiguration.dataEntitiesIdsField,true);
		}
		DataEntity dataEntity = dataEntitiesConfig.getEntityFromOverAllCache(dataEntitiesIds.get(0));
		if (dataSourceConfiguration.anomalyFields != null) {
			for (String anomalyField : dataSourceConfiguration.anomalyFields) {
				DataEntityField dataEntityField = dataEntity.getField(anomalyField);
				String scoreField = dataEntitiesConfig.getFieldColumn(dataEntitiesIds.get(0), dataEntityField.getScoreField());
				createEvidence(dataSourceConfiguration, collector, inputTopic, message, dataEntitiesIds, scoreField, dataEntitiesConfig.getFieldColumn(dataEntitiesIds.get(0), anomalyField), anomalyField,totalAmountOfEvents);
			}
		} else {
			String anomalyField = convertToString(validateFieldExistsAndGetValue(message, dataSourceConfiguration.anomalyTypeField,true));
			createEvidence(dataSourceConfiguration, collector, inputTopic, message, dataEntitiesIds, dataSourceConfiguration.scoreField, dataSourceConfiguration.anomalyValueField, anomalyField,totalAmountOfEvents);
		}

		Long startTimestampSeconds = convertToLong(validateFieldExistsAndGetValue(message, dataSourceConfiguration.endTimestampField, true));
		lastTimestampCount.set(startTimestampSeconds);

	}

	/**
	 * try to get configuration from the data source. if data source doesn't exist - get it from the input topic.
	 * @param configKey
	 * @param inputTopic
	 * @return
	 */
	private DataSourceConfiguration getDataSourceConfiguration(StreamingTaskDataSourceConfigKey configKey, String inputTopic) {
		DataSourceConfiguration dataSourceConfiguration = null;
		if(configKey != null) {
			dataSourceConfiguration = topicToDataSourceMap.get(configKey.getConfigKeyStr());
		}
		if(dataSourceConfiguration == null) {
			// Get relevant data source according to topic
			dataSourceConfiguration = topicToDataSourceMap.get(inputTopic);
			if (dataSourceConfiguration == null) {
				logger.error("No configuration is defined for data source {} or input topic {} ",configKey, inputTopic);
				return null;
			}
		}
		return dataSourceConfiguration;
	}

	private void createEvidence(DataSourceConfiguration dataSourceConfiguration, MessageCollector collector, String inputTopic, JSONObject message, List<String> dataEntitiesIds, String scoreField, String anomalyValueField, String anomalyTypeField,Integer totalAmountOfEvents) throws Exception{

		// check score
		Double score = convertToDouble(validateFieldExistsAndGetValue(message, scoreField, true));
		if (score >= dataSourceConfiguration.scoreThreshold) {
			// create evidence

			// Pre process
			if (dataSourceConfiguration.preProcessClassField != null && !dataSourceConfiguration.preProcessClassField.isEmpty()) {

				EvidenceProcessor preProcess = (EvidenceProcessor) SpringService.getInstance().resolve(Class.forName(dataSourceConfiguration.preProcessClassField));

				preProcess.run(message, dataSourceConfiguration);
			}

			// get the start timestamp from the event
			Long startTimestampSeconds = convertToLong(validateFieldExistsAndGetValue(message, dataSourceConfiguration.startTimestampField,true));
			Long startTimestamp = TimestampUtils.convertToMilliSeconds(startTimestampSeconds);

			// get the end timestamp from the event
			Long endTimestampSeconds = convertToLong(validateFieldExistsAndGetValue(message, dataSourceConfiguration.endTimestampField,true));
			Long endTimestamp = TimestampUtils.convertToMilliSeconds(endTimestampSeconds);

			// get the username from the event
			String entityName = convertToString(validateFieldExistsAndGetValue(message, dataSourceConfiguration.entityNameField, true));

			// Get the value in the field which is the anomaly
			String anomalyValue = convertToString(message.get(anomalyValueField));

			EvidenceTimeframe evidenceTimeframe = calculateEvidenceTimeframe(dataSourceConfiguration.evidenceType, startTimestampSeconds, endTimestampSeconds);

			// Create evidence from event
			Evidence evidence = evidencesService.createTransientEvidence(dataSourceConfiguration.entityType, dataSourceConfiguration.entityNameField, entityName, dataSourceConfiguration.evidenceType, new Date(startTimestamp), new Date(endTimestamp), dataEntitiesIds, score, anomalyValue, anomalyTypeField,totalAmountOfEvents, evidenceTimeframe);

			if (evidence != null && dataSourceConfiguration.entitySupportingInformationPopulatorClass != null) {
				String entitySupportingInformationPopulatorClass = dataSourceConfiguration.entitySupportingInformationPopulatorClass;
				String supportingInformation = convertToString(validateFieldExistsAndGetValue(message,supportingInformationField, false));
				if (supportingInformation != null) {
					EntitySupportingInformationPopulator entitySupportingInformationPopulator =
							(EntitySupportingInformationPopulator)SpringService.getInstance().resolve(Class.forName(entitySupportingInformationPopulatorClass));
					EntitySupportingInformation entitySupportingInformation = entitySupportingInformationPopulator.
							populate(evidence, supportingInformation, bdpService.isBDPRunning());
					if (entitySupportingInformation != null) {
						evidence.setSupportingInformation(entitySupportingInformation);
					}
				}
				else {
					logger.info("Could not populate supporting information field. Evidence ID = {} # entitySupportingInformationPopulatorClass = ", evidence.getId(), entitySupportingInformationPopulatorClass);
				}
			}

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

			if (!bdpService.isBDPRunning()) {
				// Send evidence to output topic
				try {
					collector.send(new OutgoingMessageEnvelope(new SystemStream("kafka", outputTopic),
							getPartitionKey(dataSourceConfiguration.partitionField, message),
							mapper.writeValueAsString(evidence)));
				} catch (Exception exception) {
					throw new KafkaPublisherException(String.format("failed to send event from input topic %s, output topic %s after evidence creation for evidence",
							inputTopic, outputTopic, mapper.writeValueAsString(evidence)), exception);
				}
			}

		}
	}

	public static EvidenceTimeframe calculateEvidenceTimeframe(EvidenceType evidenceType, Long eventStartTimestampInSeconds, Long eventEndTimestampInSeconds) {
		if (evidenceType == EvidenceType.AnomalyAggregatedEvent) { // timeframe is relevant only to aggregated events
			// aggregation timeframe in seconds = (end time - start time) + 1
			// ==> need to add 1 second to the end time to get the timeframe, e.g. one hour / one day (in seconds)
			// TODO logic is quite fragile and coupled to the aggregation framework - need to be changed in the future
			Long roundedEndTime = eventEndTimestampInSeconds + 1;
			long timeframeInSeconds = roundedEndTime - eventStartTimestampInSeconds;

			Duration duration = new Duration(TimestampUtils.convertToMilliSeconds(timeframeInSeconds));
			int timeframeInHours = (int) duration.getStandardHours();

			switch (timeframeInHours) {
				case ONE_HOUR:
					return EvidenceTimeframe.Hourly;
				case HOURS_IN_DAY:
					return EvidenceTimeframe.Daily;
				case HOURS_IN_WEEK:
					return EvidenceTimeframe.Weekly;
				case HOURS_IN_MONTH:
					return EvidenceTimeframe.Monthly;
				default:
					logger.warn("Could not map aggregated event time to evidence timeframe. Start time = " + eventStartTimestampInSeconds + " # End time = " + eventEndTimestampInSeconds);
					break;
			}
		}

		return null;
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
	private Object validateFieldExistsAndGetValue(JSONObject message, String field, boolean throwException) throws Exception {
		String[] fieldHierarchy = field.split("\\.");
		Object value = message;
		for(String fieldPart : fieldHierarchy){
			value = ((JSONObject) value).get(fieldPart);

		}

		if (value == null && throwException) {
			logger.error("message {} does not contains value in field {}", mapper.writeValueAsString(message), field);

			throw new StreamMessageNotContainFieldException(mapper.writeValueAsString(message), field);
		}
		else if (value == null){
			logger.error("message {} does not contains value in field {}", mapper.writeValueAsString(message), field);
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
	public static class DataSourceConfiguration {

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
		public String preProcessClassField;
		public String postProcessClassField;
		public List<String> defaultFields;
		public String totalFieldPath;
		public String entitySupportingInformationPopulatorClass;
		public String dataSource;
		public String lastState;


		public DataSourceConfiguration(EvidenceType evidenceType,String dataSource, String lastState , int scoreThreshold, List<String> dataEntitiesIds,
									   String dataEntitiesIdsField, String startTimestampField,
									   String endTimestampField, EntityType entityType, String entityNameField,
									   String partitionField, List<String> anomalyFields, String scoreField,
									   String anomalyValueField, String anomalyTypeField ,String preProcessClassField,
									   String postProcessClassField, List<String> defaultFields,String totalFieldPath,
									   String entitySupportingInformationPopulatorClass) {
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
			this.preProcessClassField = preProcessClassField;
			this.postProcessClassField = postProcessClassField;
			this.defaultFields = defaultFields;
			this.totalFieldPath = totalFieldPath;
			this.entitySupportingInformationPopulatorClass = entitySupportingInformationPopulatorClass;
			this.dataSource = dataSource;
			this.lastState = lastState;

		}

	}

}
