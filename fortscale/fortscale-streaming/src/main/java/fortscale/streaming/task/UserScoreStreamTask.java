package fortscale.streaming.task;

import static fortscale.streaming.ConfigUtils.getConfigString;
import static fortscale.utils.ConversionUtils.convertToDouble;
import static fortscale.utils.ConversionUtils.convertToLong;
import static fortscale.utils.ConversionUtils.convertToString;
import net.minidev.json.JSONObject;
import net.minidev.json.JSONValue;

import org.apache.commons.lang.StringUtils;
import org.apache.samza.config.Config;
import org.apache.samza.storage.kv.KeyValueStore;
import org.apache.samza.system.IncomingMessageEnvelope;
import org.apache.samza.task.ClosableTask;
import org.apache.samza.task.InitableTask;
import org.apache.samza.task.MessageCollector;
import org.apache.samza.task.StreamTask;
import org.apache.samza.task.TaskContext;
import org.apache.samza.task.TaskCoordinator;
import org.apache.samza.task.WindowableTask;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import fortscale.streaming.service.SpringService;
import fortscale.streaming.service.UserScoreStreamingService;
import fortscale.streaming.model.UserTopEvents;
import fortscale.utils.TimestampUtils;

public class UserScoreStreamTask implements StreamTask, InitableTask, WindowableTask, ClosableTask{
	private static final Logger logger = LoggerFactory.getLogger(UserScoreStreamTask.class);
	
	
	private String usernameField;
	private String timestampField;
	private String eventScoreField;
	private String classifierId;
	
	private UserScoreStreamingService userScoreStreamingService;
	
	
	@SuppressWarnings("unchecked")
	@Override
	public void init(Config config, TaskContext context) throws Exception {
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
		userScoreStreamingService = SpringService.getInstance("classpath*:META-INF/spring/streaming-user-score-context.xml").resolve(UserScoreStreamingService.class);
		userScoreStreamingService.setClassifierId(classifierId);
		userScoreStreamingService.setStore(store);
		userScoreStreamingService.setUseLatestEventTimeAsCurrentTime(isUseLatestEventTimeAsCurrentTime);
	}
	
	/** Process incoming events and update the user models stats */
	@Override
	public void process(IncomingMessageEnvelope envelope, MessageCollector collector, TaskCoordinator coordinator) throws Exception {
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
		
		// get the event score from the message
		Double eventScore = convertToDouble(message.get(eventScoreField));
		if (eventScore==null) {
			logger.error("message {} does not contains event score in field {}", messageText, eventScoreField);
			return;
		}
		
		userScoreStreamingService.updateUserWithEventScore(username, eventScore, TimestampUtils.convertToMilliSeconds(timestamp));
	}
	
	/** periodically save the state to mongodb as a secondary backing store and update the user score in mongodb*/
	@Override public void window(MessageCollector collector, TaskCoordinator coordinator) {
		if (userScoreStreamingService!=null){
			userScoreStreamingService.updateDb();
			userScoreStreamingService.exportSnapshot();
		}
	}
	
	/** save the state to mongodb when the job shutsdown */
	@Override 
	public void close() throws Exception {
		if (userScoreStreamingService!=null) {
			userScoreStreamingService.exportSnapshot();
			SpringService.getInstance().shutdown();
		}
		userScoreStreamingService = null;
	}
}
