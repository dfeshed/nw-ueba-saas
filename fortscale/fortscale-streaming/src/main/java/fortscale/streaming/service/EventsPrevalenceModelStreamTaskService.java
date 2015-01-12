package fortscale.streaming.service;

import static fortscale.streaming.ConfigUtils.getConfigString;
import static fortscale.streaming.ConfigUtils.getConfigStringList;
import static fortscale.utils.ConversionUtils.convertToLong;
import static fortscale.utils.ConversionUtils.convertToString;

import java.util.List;

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
import fortscale.streaming.exceptions.StreamMessageNotContainFieldException;
import fortscale.utils.StringPredicates;


public class EventsPrevalenceModelStreamTaskService {

	private static final Logger logger = LoggerFactory.getLogger(EventsPrevalenceModelStreamTaskService.class);
	
	private PrevalanceModelStreamingService prevalanceModelStreamingService;

	private String usernameField;
	private String timestampField;
	private String modelName;
	
	private Counter processedMessageCount;
	private Counter skippedMessageCount;
	private Counter lastTimestampCount;
	private List<String> discriminatorsFields;
	
	@SuppressWarnings("unchecked")
	public EventsPrevalenceModelStreamTaskService(Config config, TaskContext context) throws Exception {
		// get task configuration parameters
		usernameField = getConfigString(config, "fortscale.username.field");
		timestampField = getConfigString(config, "fortscale.timestamp.field");
		discriminatorsFields = getConfigStringList(config, "fortscale.discriminator.fields");
		
		// get the store that holds models
		String storeName = getConfigString(config, "fortscale.store.name");
		KeyValueStore<String, PrevalanceModel> store = (KeyValueStore<String, PrevalanceModel>)context.getStore(storeName);
		
		// create a model builder based on fields configuration
		PrevalanceModelBuilderImpl modelBuilder = createModelBuilder(config);
		
		// create model service based on the store and model builder
		prevalanceModelStreamingService = new PrevalanceModelStreamingService(store,modelBuilder);
		
		// create counter metric for processed messages
		processedMessageCount = context.getMetricsRegistry().newCounter(getClass().getName(), String.format("%s-model-message-count", modelName));
		skippedMessageCount = context.getMetricsRegistry().newCounter(getClass().getName(), String.format("%s-model-skip-count", modelName));
		lastTimestampCount = context.getMetricsRegistry().newCounter(getClass().getName(), String.format("%s-model-message-epochime", modelName));
	}
	
	public PrevalanceModelStreamingService getPrevalanceModelStreamingService(){
		return prevalanceModelStreamingService;
	}
	
	private PrevalanceModelBuilderImpl createModelBuilder(Config config) throws Exception {
		// get the model name and fields to include from configuration
		modelName = getConfigString(config, "fortscale.model.name");
		PrevalanceModelBuilderImpl modelBuilder = PrevalanceModelBuilderImpl.createModel(modelName, config);
		
		Config fieldsSubset = config.subset("fortscale.fields.");		
		for (String fieldConfigKey : Iterables.filter(fieldsSubset.keySet(), StringPredicates.endsWith(".model"))) {
			String fieldName = fieldConfigKey.substring(0, fieldConfigKey.indexOf(".model"));
			String fieldModel = getConfigString(config, String.format("fortscale.fields.%s.model", fieldName));
			String fieldBooster = config.get(String.format("fortscale.fields.%s.booster", fieldName), "");
			
			modelBuilder.withField(fieldName, fieldModel, fieldBooster);
		}
		return modelBuilder;
	}
	
	/** Process incoming events and update the user models stats */
	public void process(IncomingMessageEnvelope envelope, MessageCollector collector, TaskCoordinator coordinator) throws Exception {
		// parse the message into json 
		String messageText = (String)envelope.getMessage();
		JSONObject message = (JSONObject) JSONValue.parseWithException(messageText);
		
		
		// get the username, so that we can get the model from store
		String username = convertToString(message.get(usernameField));
		if (StringUtils.isEmpty(username)) {
			//logger.error("message {} does not contains username in field {}", messageText, usernameField);
			throw new StreamMessageNotContainFieldException(messageText, usernameField);
		}
		
		// get the timestamp from the message
		Long timestamp = convertToLong(message.get(timestampField));
		if (timestamp==null) {
			logger.error("message {} does not contains timestamp in field {}", messageText, timestampField);
			throw new StreamMessageNotContainFieldException(messageText, timestampField);
		}
		
		// go over each field in the event and add it to the model
		PrevalanceModel model = prevalanceModelStreamingService.getModelForUser(username,modelName);
		
		// skip events that occur before the model time mark in case the task is configured
		// to perform both model computation and scoring (the normal case)
		String discriminator = UserTimeBarrier.calculateDisriminator(message, discriminatorsFields);
		boolean afterTimeMark = model.getBarrier().isEventAfterBarrier(timestamp, discriminator);
		
		// skip events that occur before the model mark in case the task is configured to
		// perform only model computation and not event scoring 
		if (afterTimeMark) {
			model.addFieldValues(message, timestamp);
			model.getBarrier().updateBarrier(timestamp, discriminator);
			prevalanceModelStreamingService.updateUserModel(username, model);
			// update timestamp counter
			lastTimestampCount.set(timestamp);
			
			processedMessageCount.inc();
		} else{
			skippedMessageCount.inc();
		}
	}

	
	
	/** periodically save the state to mongodb as a secondary backing store */
	public void window(MessageCollector collector, TaskCoordinator coordinator) {
		if (prevalanceModelStreamingService!=null)
			prevalanceModelStreamingService.exportModels();
	}

	/** save the state to mongodb when the job shutsdown */
	public void close() throws Exception {
		if (prevalanceModelStreamingService!=null) {
			prevalanceModelStreamingService.exportModels();
		}
		prevalanceModelStreamingService = null;
	}
}
