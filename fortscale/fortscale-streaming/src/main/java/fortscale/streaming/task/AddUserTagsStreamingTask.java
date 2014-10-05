package fortscale.streaming.task;

import static fortscale.streaming.ConfigUtils.getConfigString;
import static fortscale.utils.ConversionUtils.convertToString;

import java.util.HashMap;
import java.util.Map;
import java.util.Map.Entry;

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

import fortscale.streaming.exceptions.KafkaPublisherException;
import fortscale.streaming.exceptions.StreamMessageNotContainFieldException;
import fortscale.streaming.service.UserTagsService;

/**
 * Streaming task that adds user tags to events that pass through 
 */
public class AddUserTagsStreamingTask extends AbstractStreamTask implements InitableTask {

	private String outputTopic;
	private String usernameField;
	private UserTagsService tagService;
	
	@Override
	protected void wrappedInit(Config config, TaskContext context) throws Exception {
		// get task configuration 
		outputTopic = config.get("fortscale.output.topic");
		usernameField = getConfigString(config, "fortscale.username.field");
		
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
		
		// get the username, so that we can get the model from store
		String username = convertToString(message.get(usernameField));
		if (StringUtils.isEmpty(username)) {
			//logger.error("message {} does not contains username in field {}", messageText, usernameField);
			throw new StreamMessageNotContainFieldException(messageText, usernameField);
		}
		
		// add the tags to the topic message
		tagService.addTagsToEvent(username, message);

		// send the event to the output topic
		try{
			collector.send(new OutgoingMessageEnvelope(new SystemStream("kafka", outputTopic), message.toJSONString()));
		} catch(Exception exception){
			throw new KafkaPublisherException(String.format("failed to send scoring message after processing the message %s.", messageText), exception);
		}
	}


	@Override
	protected void wrappedClose() throws Exception {
		tagService = null;
	}

	@Override
	protected void wrappedWindow(MessageCollector collector, TaskCoordinator coordinator) throws Exception {}
}
