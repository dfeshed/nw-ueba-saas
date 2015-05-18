package fortscale.streaming.service;

import static fortscale.streaming.ConfigUtils.getConfigString;
import static fortscale.streaming.ConfigUtils.getConfigStringList;
import static fortscale.utils.ConversionUtils.convertToLong;
import static fortscale.utils.ConversionUtils.convertToString;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import net.minidev.json.JSONObject;
import net.minidev.json.JSONValue;

import org.apache.commons.lang.StringUtils;
import org.apache.samza.config.Config;
import org.apache.samza.metrics.Counter;
import org.apache.samza.storage.kv.KeyValueStore;
import org.apache.samza.system.IncomingMessageEnvelope;
import org.apache.samza.task.MessageCollector;
import org.apache.samza.task.TaskContext;
import org.apache.samza.task.TaskCoordinator;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.google.common.collect.Iterables;

import fortscale.ml.model.prevalance.PrevalanceModel;
import fortscale.ml.model.prevalance.PrevalanceModelBuilderImpl;
import fortscale.ml.model.prevalance.UserTimeBarrier;
import fortscale.ml.service.ModelService;
import fortscale.streaming.exceptions.StreamMessageNotContainFieldException;
import fortscale.streaming.feature.extractor.FeatureExtractionService;
import fortscale.utils.StringPredicates;


public class EventsPrevalenceModelStreamTaskService {

	private static final Logger logger = LoggerFactory.getLogger(EventsPrevalenceModelStreamTaskService.class);
	private static final String GLOBAL_MODEL_NAME = "global";

	private Map<String,PrevalanceModelStreamingService> prevalanceModelStreamingServiceMap;
	private Map<String,List<String>> modelToContextFieldNameMap;
	private List<String> modelsNamesOrder;

	private String timestampField;
	private String sourceType;
	private String entityType;
	
	private FeatureExtractionService featureExtractionService;
	
	private Counter processedMessageCount;
	private Counter skippedMessageCount;
	private Counter lastTimestampCount;
	private List<String> discriminatorsFields;

	private GlobalModelStreamTaskService globalModelStreamTaskService;

	public EventsPrevalenceModelStreamTaskService(Config config, TaskContext context) throws Exception {
		// get model task configuration parameters
		sourceType = getConfigString(config, "fortscale.source.type");
		entityType = getConfigString(config, "fortscale.entity.type");
		timestampField = getConfigString(config, "fortscale.timestamp.field");
		discriminatorsFields = getConfigStringList(config, "fortscale.discriminator.fields");
		
		createPrevalanceModelStreamingServices(config, context);
		featureExtractionService = new FeatureExtractionService(config);
		
		// create counter metric for processed messages
		processedMessageCount = context.getMetricsRegistry().newCounter(getClass().getName(), String.format("%s-%s-model-message-count", sourceType,entityType));
		skippedMessageCount = context.getMetricsRegistry().newCounter(getClass().getName(), String.format("%s-%s-model-skip-count", sourceType,entityType));
		lastTimestampCount = context.getMetricsRegistry().newCounter(getClass().getName(), String.format("%s-%s-model-message-epochime", sourceType,entityType));
	}
	
	@SuppressWarnings("unchecked")
	private void createPrevalanceModelStreamingServices(Config config, TaskContext context) throws Exception{
		modelsNamesOrder = getConfigStringList(config, "fortscale.models.names.order");
		// get the store that holds models
		String storeName = getConfigString(config, "fortscale.store.name");
		KeyValueStore<String, PrevalanceModel> store = (KeyValueStore<String, PrevalanceModel>)context.getStore(storeName);
		prevalanceModelStreamingServiceMap = new HashMap<>();
		modelToContextFieldNameMap = new HashMap<>();
		for(String modelName: modelsNamesOrder){
			if (modelName.equals(GLOBAL_MODEL_NAME))
				continue;

			List<String> contextFieldList = new ArrayList<>();
			String contextField = getConfigString(config, String.format("fortscale.model.%s.context.fieldname", modelName));
			contextFieldList.add(contextField);
			String optionalContextFieldReplacement = config.get(String.format("fortscale.model.%s.context.fieldname.optional.replacement", modelName));
			if(optionalContextFieldReplacement != null && StringUtils.isNotBlank(optionalContextFieldReplacement)){
				contextFieldList.add(optionalContextFieldReplacement);
			}
			modelToContextFieldNameMap.put(modelName, contextFieldList);
			// get the task windows from config and use it as the time gap to update models
			long timeGapForModelUpdates = config.getLong("task.window.ms");
			// create a model builder based on fields configuration
			PrevalanceModelBuilderImpl modelBuilder = createModelBuilder(modelName, config);
			// create model service based on the store and model builder
			PrevalanceModelStreamingService prevalanceModelStreamingService = new PrevalanceModelStreamingService(store,modelBuilder,timeGapForModelUpdates);
			prevalanceModelStreamingServiceMap.put(modelName, prevalanceModelStreamingService);
		}

		// Create streaming service for global model
		globalModelStreamTaskService = modelsNamesOrder.contains(GLOBAL_MODEL_NAME) ?
			new GlobalModelStreamTaskService(config, GLOBAL_MODEL_NAME, store) : null;
	}
	
	private PrevalanceModelBuilderImpl createModelBuilder(String modelName, Config config) throws Exception {
		// get the fields to include from configuration
		String configPrefix = String.format("fortscale.model.%s.fields", modelName);
		PrevalanceModelBuilderImpl modelBuilder = PrevalanceModelBuilderImpl.createModel(modelName, config, configPrefix);
		
		String fieldsPrefix = String.format("fortscale.model.%s.fields.", modelName);
		Config fieldsSubset = config.subset(fieldsPrefix);
		for (String fieldConfigKey : Iterables.filter(fieldsSubset.keySet(), StringPredicates.endsWith(".model"))) {
			String fieldName = fieldConfigKey.substring(0, fieldConfigKey.indexOf(".model"));
			String fieldModel = getConfigString(config, String.format("%s%s.model", fieldsPrefix,fieldName));
			
			modelBuilder.withField(fieldName, fieldModel);
		}
		return modelBuilder;
	}
	
	public ModelService getModelStreamingService(){
		return new PrevalenceModelServiceMap(prevalanceModelStreamingServiceMap);
	}
	
	public FeatureExtractionService getFeatureExtractionService() {
		return featureExtractionService;
	}

	/** Process incoming events and update the user models stats */
	public void process(IncomingMessageEnvelope envelope, MessageCollector collector, TaskCoordinator coordinator) throws Exception {
		// parse the message into json 
		String messageText = (String)envelope.getMessage();
		JSONObject message = (JSONObject) JSONValue.parseWithException(messageText);
		
		// get the timestamp from the message
		Long timestamp = convertToLong(message.get(timestampField));
		if (timestamp==null) {
			logger.error("message {} does not contains timestamp in field {}", messageText, timestampField);
			throw new StreamMessageNotContainFieldException(messageText, timestampField);
		}
		
		String discriminator = UserTimeBarrier.calculateDisriminator(message, discriminatorsFields);
		boolean afterTimeMark = false;
		for(int i = 0; i < modelsNamesOrder.size(); i++){
			String modelName = modelsNamesOrder.get(i);
			if (modelName.equals(GLOBAL_MODEL_NAME))
				continue;

			// get the context, so that we can get the model from store
			String context = getModelContext(modelName, message);
			if (StringUtils.isBlank(context)) {
				logger.warn("message {} does not contains context in one of the fields {}", messageText, StringUtils.join(modelToContextFieldNameMap.get(modelName),','));
				continue;
			}
			
			PrevalanceModelStreamingService prevalanceModelStreamingService = prevalanceModelStreamingServiceMap.get(modelName);
			// go over each field in the event and add it to the model
			PrevalanceModel model = prevalanceModelStreamingService.getModel(context);
			if(i == 0){
				// skip events that occur before the model time mark in case the task is configured
				// to perform both model computation and scoring (the normal case)
				afterTimeMark = model.getBarrier().isEventAfterBarrier(timestamp, discriminator);
				
				// skip events that occur before the model mark in case the task is configured to
				// perform only model computation and not event scoring 
				if (!afterTimeMark) {
					break;
				}
			}	
			
			model.addFieldValues(featureExtractionService, message, timestamp);
			model.getBarrier().updateBarrier(timestamp, discriminator);
			prevalanceModelStreamingService.updateModel(context, model);
		}
		
		if (afterTimeMark) {
			// update timestamp counter
			lastTimestampCount.set(timestamp);
			processedMessageCount.inc();
		} else{
			skippedMessageCount.inc();
		}

		// Update global model
		if (globalModelStreamTaskService != null)
			globalModelStreamTaskService.updateGlobalModel(timestamp);
	}

	private String getModelContext(String modelName, JSONObject message){
		String context = null;
		for(String contextField: modelToContextFieldNameMap.get(modelName)){
			context = convertToString(featureExtractionService.extract(contextField, message));
			if(StringUtils.isNotBlank(context)){
				break;
			}
		}
		
		return context;
	}
	
	
	/** periodically save the state to mongodb as a secondary backing store */
	public void window(MessageCollector collector, TaskCoordinator coordinator) {
		exportModels();
	}
	
	private void exportModels(){
		if(prevalanceModelStreamingServiceMap != null){
			for(PrevalanceModelStreamingService prevalanceModelStreamingService: prevalanceModelStreamingServiceMap.values()){
				prevalanceModelStreamingService.exportModels();
			}
		}
	}

	/** save the state to mongodb when the job shutsdown */
	public void close() throws Exception {
		exportModels();
	}
}
