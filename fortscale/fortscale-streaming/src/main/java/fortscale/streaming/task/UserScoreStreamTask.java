package fortscale.streaming.task;

import static fortscale.streaming.ConfigUtils.getConfigString;
import static fortscale.utils.ConversionUtils.convertToDouble;
import static fortscale.utils.ConversionUtils.convertToLong;
import static fortscale.utils.ConversionUtils.convertToString;
import net.minidev.json.JSONObject;
import net.minidev.json.JSONValue;

import org.apache.commons.lang.StringUtils;
import org.apache.samza.config.Config;
import org.apache.samza.metrics.Counter;
import org.apache.samza.storage.kv.KeyValueStore;
import org.apache.samza.system.IncomingMessageEnvelope;
import org.apache.samza.task.ClosableTask;
import org.apache.samza.task.InitableTask;
import org.apache.samza.task.MessageCollector;
import org.apache.samza.task.TaskContext;
import org.apache.samza.task.TaskCoordinator;

import fortscale.streaming.exceptions.StreamMessageNotContainFieldException;
import fortscale.streaming.model.UserTopEvents;
import fortscale.streaming.service.SpringService;
import fortscale.streaming.service.UserScoreStreamingService;
import fortscale.utils.TimestampUtils;

public class UserScoreStreamTask  extends AbstractStreamTask  implements InitableTask, ClosableTask{
	
	private String usernameField;
	private String timestampField;
	private String eventScoreField;
	private String classifierId;
	private Counter lastTimestampCount;
	
	private UserScoreStreamingService userScoreStreamingService;
	
	
	@SuppressWarnings("unchecked")
	@Override
	protected void wrappedInit(Config config, TaskContext context) throws Exception {
		// get task configuration parameters
		usernameField = getConfigString(config, "fortscale.username.field");
		timestampField = getConfigString(config, "fortscale.timestamp.field");
		eventScoreField = getConfigString(config, "fortscale.event.score.field");
		classifierId = getConfigString(config, "fortscale.classifier.id");
		boolean isUseLatestEventTimeAsCurrentTime = config.getBoolean("fortscale.use.latest.event.time.as.current.time", false);
		
		// get the store that holds user top events
		String storeName = getConfigString(config, "fortscale.store.name");
		KeyValueStore<String, UserTopEvents> store = (KeyValueStore<String, UserTopEvents>)context.getStore(storeName);
		
		//get the user score streaming service and set it with the store and the classifier id.
		userScoreStreamingService = SpringService.getInstance().resolve(UserScoreStreamingService.class);
		userScoreStreamingService.setClassifierId(classifierId);
		userScoreStreamingService.setStore(store);
		userScoreStreamingService.setUseLatestEventTimeAsCurrentTime(isUseLatestEventTimeAsCurrentTime);
		
		// register metrics
		lastTimestampCount = context.getMetricsRegistry().newCounter(getClass().getName(), String.format("%s-user-score-epochime", classifierId));
	}
	
	/** Process incoming events and update the user models stats */
	@Override
	public void wrappedProcess(IncomingMessageEnvelope envelope, MessageCollector collector, TaskCoordinator coordinator) throws Exception {
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
			//logger.error("message {} does not contains timestamp in field {}", messageText, timestampField);
			throw new StreamMessageNotContainFieldException(messageText, timestampField);
		}
		
		// get the event score from the message
		Double eventScore = convertToDouble(message.get(eventScoreField));
		if (eventScore==null) {
			//logger.error("message {} does not contains event score in field {}", messageText, eventScoreField);
			throw new StreamMessageNotContainFieldException(messageText, eventScoreField);
		}
		
		userScoreStreamingService.updateUserWithEventScore(username, eventScore, TimestampUtils.convertToMilliSeconds(timestamp));
		lastTimestampCount.set(timestamp);
	}
	
	/** periodically save the state to mongodb as a secondary backing store and update the user score in mongodb*/
	@Override public void wrappedWindow(MessageCollector collector, TaskCoordinator coordinator) {
		if (userScoreStreamingService!=null){
			userScoreStreamingService.updateDb();
			userScoreStreamingService.exportSnapshot();
		}
	}
	
	/** save the state to mongodb when the job shutsdown */
	@Override 
	protected void wrappedClose() throws Exception {
		if (userScoreStreamingService!=null) {
			userScoreStreamingService.exportSnapshot();
		}
		userScoreStreamingService = null;
	}
}
