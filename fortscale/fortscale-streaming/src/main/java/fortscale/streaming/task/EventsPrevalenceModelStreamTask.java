package fortscale.streaming.task;

import static com.google.common.base.Preconditions.*;
import static fortscale.utils.ConversionUtils.*;
import net.minidev.json.JSONObject;
import net.minidev.json.JSONValue;

import org.apache.commons.lang.StringUtils;
import org.apache.samza.config.Config;
import org.apache.samza.storage.kv.KeyValueStore;
import org.apache.samza.system.IncomingMessageEnvelope;
import org.apache.samza.task.ClosableTask;
import org.apache.samza.task.MessageCollector;
import org.apache.samza.task.StreamTask;
import org.apache.samza.task.TaskContext;
import org.apache.samza.task.TaskCoordinator;
import org.apache.samza.task.InitableTask;
import org.apache.samza.task.WindowableTask;
import org.apache.samza.metrics.Counter;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.google.common.collect.Iterables;

import fortscale.streaming.model.PrevalanceModel;
import fortscale.streaming.model.PrevalanceModelBuilder;
import fortscale.streaming.service.PrevalanceModelService;
import fortscale.utils.StringPredicates;


/**
 * Streaming task that receive events and build a model that aggregated prevalence of fields 
 * extracted from the events. 
 */
public class EventsPrevalenceModelStreamTask implements StreamTask, InitableTask, WindowableTask, ClosableTask {

	private static final Logger logger = LoggerFactory.getLogger(EventsPrevalenceModelStreamTask.class);
	
	private String usernameField;
	private String timestampField;
	private String modelName;
	private PrevalanceModelService modelService;
	private Counter processedMessageCount;
	private Counter skippedMessageCount;
	
	
	@SuppressWarnings("unchecked")
	@Override
	public void init(Config config, TaskContext context) throws Exception {
		// get task configuration parameters
		usernameField = config.get("fortscale.username.field");
		checkNotNull(usernameField, "fortscale.username.field is missing");
		
		timestampField = config.get("fortscale.timestamp.field");
		checkNotNull(timestampField, "fortscale.timestamp.field is missing");
		
		// get the store that holds models
		String storeName = config.get("fortscale.store.name");
		checkNotNull(storeName, "fortscale.store.name is missing");
		KeyValueStore<String, PrevalanceModel> store = (KeyValueStore<String, PrevalanceModel>)context.getStore(storeName);
		
		// create a model builder based on fields configuration
		PrevalanceModelBuilder builder = createModelBuilder(config);
		
		// create model service based on the store and model builder
		modelService = new PrevalanceModelService(store, builder);
		
		// create counter metric for processed messages
		processedMessageCount = context.getMetricsRegistry().newCounter(getClass().getName(), String.format("%s-message-count", modelName));
		skippedMessageCount = context.getMetricsRegistry().newCounter(getClass().getName(), String.format("%s-skip-count", modelName));
	}
	
	private PrevalanceModelBuilder createModelBuilder(Config config) throws Exception {
		// get the model name and fields to include from configuration
		modelName = config.get("fortscale.model.name");
		checkNotNull(modelName, "fortscale.model.name is missing");
		PrevalanceModelBuilder modelBuilder = PrevalanceModelBuilder.createModel(modelName);
		
		Config fieldsSubset = config.subset("fortscale.fields.");		
		for (String fieldConfigKey : Iterables.filter(fieldsSubset.keySet(), StringPredicates.endsWith(".model"))) {
			String fieldName = fieldConfigKey.substring(0, fieldConfigKey.indexOf(".model"));
			String fieldModel = config.get(String.format("fortscale.fields.%s.model", fieldName));
			checkNotNull(fieldModel, String.format("fortscale.fields.%s.model", fieldName) + " is missing");
			
			modelBuilder.withField(fieldName, fieldModel);
		}
		return modelBuilder;
	}
	
	/** Process incoming events and update the user models stats */
	@Override public void process(IncomingMessageEnvelope envelope, MessageCollector collector, TaskCoordinator coordinator) throws Exception {
		try {
			// parse the message into json 
			String messageText = (String)envelope.getMessage();
			JSONObject message = (JSONObject) JSONValue.parse(messageText);
			if (message==null) {
				logger.error("message in envelope cannot be parsed - {}", messageText);
				return;
			}
			
			// get the username, so that we can get the model from store
			String username = convertToString(message.get(usernameField));
			if (StringUtils.isEmpty(username)) {
				logger.error("message {} does not contains username in field {}", messageText, usernameField);
				return;
			}
			
			// get the timestamp from the message
			Long timestamp = convertToLong(message.get(timestampField));
			if (timestamp==null) {
				logger.error("message {} does not contains timestamp in field {}", messageText, timestampField);
				return;
			}
			
			if (!acceptMessage(message)) {
				skippedMessageCount.inc();
				return;
			}
			
			// go over each field in the event and add it to the model
			PrevalanceModel model = modelService.getModelForUser(username);
				
			// skip events that occur before the model mark
			if (!model.isTimeMarkAfter(timestamp)) {
				for (String fieldName : model.getFieldNames()) {
					Object value = message.get(fieldName);
					model.addFieldValue(fieldName, value, timestamp);
					double score = model.calculateScore(fieldName, value);
					message.put(String.format("%sscore", fieldName), score);
				}
				modelService.updateUserModelInStore(username, model);
				processedMessageCount.inc();
			} else {
				skippedMessageCount.inc();
			}
		} catch (Exception e) {
			logger.error("error while computing model for " + modelName + " with mesage " + envelope.getMessage(), e);
		}
	}

	
	
	/** periodically save the state to mongodb as a secondary backing store */
	@Override public void window(MessageCollector collector, TaskCoordinator coordinator) {
		if (modelService!=null)
			modelService.exportModels();
	}

	/** save the state to mongodb when the job shutsdown */
	@Override public void close() throws Exception {
		if (modelService!=null)
			modelService.exportModels();
		modelService = null;
	}
	
	/** Auxiliary method to enable filtering messages on specific events types */
	protected boolean acceptMessage(JSONObject message) {
		return true;
	}
}
