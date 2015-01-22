package fortscale.streaming.task.enrichment;

import fortscale.streaming.exceptions.KafkaPublisherException;
import fortscale.streaming.exceptions.StreamMessageNotContainFieldException;
import fortscale.streaming.service.UserTagsService;
import fortscale.streaming.task.AbstractStreamTask;
import net.minidev.json.JSONObject;
import net.minidev.json.JSONValue;
import org.apache.commons.lang.StringUtils;
import org.apache.samza.config.Config;
import org.apache.samza.system.IncomingMessageEnvelope;
import org.apache.samza.system.OutgoingMessageEnvelope;
import org.apache.samza.system.SystemStream;
import org.apache.samza.task.InitableTask;
import org.apache.samza.task.MessageCollector;
import org.apache.samza.task.TaskContext;
import org.apache.samza.task.TaskCoordinator;
import parquet.org.slf4j.Logger;
import parquet.org.slf4j.LoggerFactory;

import java.util.HashMap;
import java.util.Map;
import java.util.Map.Entry;

import static fortscale.streaming.ConfigUtils.getConfigString;
import static fortscale.utils.ConversionUtils.convertToString;

/**
 * Several enrichment regarding the user:
 * 1. Username normalization
 * 2. User tagging
 */
public class UsernameNormalizationAndTaggingTask extends AbstractStreamTask implements InitableTask {


	/**
	 * Logger
	 */
	private static Logger logger = LoggerFactory.getLogger(UserLastActivityTask.class);


	// TODO replace with map from input to output topic
	private String outputTopic;


	/**
	 * The username field in the input event
	 */
	private String normalizedUsernameField;


	private UserTagsService tagService;

	/**
	 * Init task after spring context is up
	 * @param config	Samza task configuration
	 * @param context	Samza task context
	 * @throws Exception
	 */
	@Override
	protected void wrappedInit(Config config, TaskContext context) throws Exception {

		// get task configuration
		outputTopic = config.get("fortscale.output.topic");

		// Get field names
		normalizedUsernameField = getConfigString(config, "fortscale.normalizedusername.field");
		
		// construct tagging service with the tags that are required from configuration
		Map<String, String> tags = new HashMap<String, String>();
		for (Entry<String,String> tagConfigField : config.subset("fortscale.tags.").entrySet()) {
			String tagName = tagConfigField.getKey();
			String tagField = tagConfigField.getValue();
			tags.put(tagName, tagField);
		}
		tagService = new UserTagsService(tags);
	}

	
	@Override
	protected void wrappedProcess(IncomingMessageEnvelope envelope, MessageCollector collector, TaskCoordinator coordinator) throws Exception {
		// parse the message into json 
		String messageText = (String)envelope.getMessage();
		JSONObject message = (JSONObject) JSONValue.parseWithException(messageText);
		
		// get the username from input record
		String username = convertToString(message.get(normalizedUsernameField));
		if (StringUtils.isEmpty(username)) {
			logger.error("message {} does not contains username in field {}", messageText, normalizedUsernameField);
			throw new StreamMessageNotContainFieldException(messageText, normalizedUsernameField);
		}
		
		// add the tags to the event
		tagService.addTagsToEvent(username, message);

		// send the event to the output topic
		try{
			collector.send(new OutgoingMessageEnvelope(new SystemStream("kafka", outputTopic), message.toJSONString()));
		} catch(Exception exception){
			throw new KafkaPublisherException(String.format("failed to send message to topic %s after processing. Message: %s.", outputTopic, messageText), exception);
		}
	}


	@Override
	protected void wrappedClose() throws Exception {
		tagService = null;
	}

	@Override
	protected void wrappedWindow(MessageCollector collector, TaskCoordinator coordinator) throws Exception {
		// Do nothing
	}
}
