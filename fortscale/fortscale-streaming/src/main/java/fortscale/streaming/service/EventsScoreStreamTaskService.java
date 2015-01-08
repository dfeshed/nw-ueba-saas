package fortscale.streaming.service;

import static fortscale.streaming.ConfigUtils.getConfigString;
import static fortscale.utils.ConversionUtils.convertToString;

import java.util.HashMap;
import java.util.Map;

import net.minidev.json.JSONObject;
import net.minidev.json.JSONValue;

import org.apache.commons.lang.StringUtils;
import org.apache.samza.config.Config;
import org.apache.samza.metrics.Counter;
import org.apache.samza.system.IncomingMessageEnvelope;
import org.apache.samza.system.OutgoingMessageEnvelope;
import org.apache.samza.system.SystemStream;
import org.apache.samza.task.MessageCollector;
import org.apache.samza.task.TaskContext;
import org.apache.samza.task.TaskCoordinator;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.google.common.collect.Iterables;

import fortscale.ml.model.prevalance.PrevalanceModel;
import fortscale.ml.service.ModelService;
import fortscale.streaming.exceptions.KafkaPublisherException;
import fortscale.streaming.exceptions.StreamMessageNotContainFieldException;
import fortscale.utils.StringPredicates;

@Service
public class EventsScoreStreamTaskService {

	private static final Logger logger = LoggerFactory.getLogger(EventsScoreStreamTaskService.class);
	

	@Autowired
	private ModelService modelService;

	private String outputTopic;
	private String usernameField;
	private String modelName;
	
	private Counter processedMessageCount;
	private Map<String, String> outputFields = new HashMap<String, String>();
	private String eventScoreField;
	
	
	public void init(Config config, TaskContext context) throws Exception {
		// get task configuration parameters
		usernameField = getConfigString(config, "fortscale.username.field");
		outputTopic = config.get("fortscale.output.topic", "");
		fillModelConfig(config);
		eventScoreField = getConfigString(config, "fortscale.event.score.field");
		
		// create counter metric for processed messages
		processedMessageCount = context.getMetricsRegistry().newCounter(getClass().getName(), String.format("%s-event-score-message-count", modelName));
	}
	
	private void fillModelConfig(Config config) throws Exception {
		// get the model name and fields to include from configuration
		modelName = getConfigString(config, "fortscale.model.name");
		
		Config fieldsSubset = config.subset("fortscale.fields.");		
		for (String fieldConfigKey : Iterables.filter(fieldsSubset.keySet(), StringPredicates.endsWith(".model"))) {
			String fieldName = fieldConfigKey.substring(0, fieldConfigKey.indexOf(".model"));
			String outputField = getConfigString(config, String.format("fortscale.fields.%s.output", fieldName));
			
			if (outputField==null)
				logger.error("output field is null for field {}", fieldName);
			else
				outputFields.put(fieldName, outputField);
		}
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
				
		// go over each field in the event and add it to the model
		PrevalanceModel model = modelService.getModelForUser(username, modelName);
		
		double eventScore = 0;
		for (String fieldName : model.getFieldNames()) {
			double score = 0;
			if(model != null){
				score = model.calculateScore(message, fieldName);
		
				// set the max field score as the event score
				if (model.shouldAffectEventScore(fieldName))
					eventScore = Math.max(eventScore, score);
			}
			
			// store the field score in the message
			String outputFieldname = outputFields.get(fieldName); 
			if (outputFieldname!=null)
				message.put(outputFieldname, score);
		}
	
		// put the event score in the message
		message.put(eventScoreField, eventScore);
		
		// publish the event with score to the subsequent topic in the topology
		if (StringUtils.isNotEmpty(outputTopic)){
			try{
				collector.send(new OutgoingMessageEnvelope(new SystemStream("kafka", outputTopic), message.toJSONString()));
			} catch(Exception exception){
				throw new KafkaPublisherException(String.format("failed to send scoring message after processing the message %s.", messageText), exception);
			}
		}
		
		processedMessageCount.inc();
	}
}
