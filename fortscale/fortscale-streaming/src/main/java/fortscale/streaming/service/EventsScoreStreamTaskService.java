package fortscale.streaming.service;

import fortscale.ml.service.ModelService;
import fortscale.streaming.exceptions.KafkaPublisherException;
import fortscale.streaming.exceptions.StreamMessageNotContainFieldException;
import fortscale.streaming.feature.extractor.FeatureExtractionService;
import fortscale.streaming.scorer.EventMessage;
import fortscale.streaming.scorer.FeatureScore;
import fortscale.streaming.scorer.Scorer;
import fortscale.streaming.scorer.ScorerContext;
import fortscale.streaming.service.event.EventPersistencyHandler;
import fortscale.streaming.service.event.EventPersistencyHandlerFactory;
import fortscale.utils.logging.Logger;
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
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Configurable;
import org.springframework.beans.factory.annotation.Value;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import static com.google.common.base.Preconditions.checkNotNull;
import static fortscale.streaming.ConfigUtils.getConfigString;
import static fortscale.streaming.ConfigUtils.getConfigStringList;
import static fortscale.utils.ConversionUtils.convertToLong;

@Configurable(preConstruction=true)
public class EventsScoreStreamTaskService {

	private static final Logger logger = Logger.getLogger(EventsScoreStreamTaskService.class);
	private static final String SCORED_EVENTS_COLLECTION_NAME_PREFIX = "scored_";

	private ModelService modelService;

	private String outputTopic;
	private String sourceType;
	private String entityType;
	private String timestampField;
	private List<Scorer> scorersToRun;
	
	private Counter processedMessageCount;
	private Counter lastTimestampCount;

	@Value("${streaming.event.field.type}")
	private String eventTypeFieldName;

	@Autowired
	private EventPersistencyHandlerFactory eventPersitencyHandlerFactory;

	public EventsScoreStreamTaskService(Config config, TaskContext context, ModelService modelService, FeatureExtractionService featureExtractionService) throws Exception{
		this.modelService = modelService;
		// get task configuration parameters
		sourceType = getConfigString(config, "fortscale.source.type");
		entityType = getConfigString(config, "fortscale.entity.type");
		timestampField = getConfigString(config, "fortscale.timestamp.field");
		outputTopic = config.get("fortscale.output.topic", "");
		
		fillScoreConfig(config, featureExtractionService);
		
		// create counter metric for processed messages
		processedMessageCount = context.getMetricsRegistry().newCounter(getClass().getName(), String.format("%s-%s-event-score-message-count", sourceType, entityType));
		lastTimestampCount = context.getMetricsRegistry().newCounter(getClass().getName(), String.format("%s-%s-event-score-message-epochime", sourceType, entityType));
	}
	
	private void fillScoreConfig(Config config, FeatureExtractionService featureExtractionService) throws Exception {
		List<String> scorers = getConfigStringList(config, "fortscale.scorers");
		ScorerContext context = new ScorerContext(config);
		context.setBean("modelService", modelService);
		context.setBean("featureExtractionService", featureExtractionService);
		scorersToRun = new ArrayList<>();
		for(String ScorerStr: scorers){
			Scorer scorer = (Scorer) context.resolve(Scorer.class, ScorerStr);
			checkNotNull(scorer);
			scorersToRun.add(scorer);
		}
	}
	
	/** Process incoming events and update the user models stats */
	public void process(IncomingMessageEnvelope envelope, MessageCollector collector, TaskCoordinator coordinator) throws Exception {
		// parse the message into json 
		String messageText = (String)envelope.getMessage();
		JSONObject message = (JSONObject) JSONValue.parseWithException(messageText);
		
		// get the timestamp from the message
		// the timestamp for now is only used for monitoring but later it will be used in order to recieve the right model.
		Long timestamp = convertToLong(message.get(timestampField));
		if (timestamp==null) {
			logger.error("message {} does not contains timestamp in field {}", messageText, timestampField);
			throw new StreamMessageNotContainFieldException(messageText, timestampField);
		}

		
		EventMessage eventMessage = new EventMessage(message);
		for (Scorer scorer: scorersToRun) {
			FeatureScore eventFeatureScore = scorer.calculateScore(eventMessage);
			message.put(eventFeatureScore.getName(), (double)Math.round(eventFeatureScore.getScore()));
			if(eventFeatureScore.getFeatureScores() != null){
				for(FeatureScore featureScore: eventFeatureScore.getFeatureScores()){
					message.put(featureScore.getName(), (double)Math.round(featureScore.getScore()));
				}
			}
		}
	
		
		// publish the event with score to the subsequent topic in the topology
		if (StringUtils.isNotEmpty(outputTopic)){
			try{
				collector.send(new OutgoingMessageEnvelope(new SystemStream("kafka", outputTopic), message.toJSONString()));
				saveEvent(message);
			} catch(Exception exception){
				throw new KafkaPublisherException(String.format("failed to send scoring message after processing the message %s.", messageText), exception);
			}
		}
		
		processedMessageCount.inc();
		lastTimestampCount.set(timestamp);
	}
	
	private void saveEvent(JSONObject event) throws IOException {
		String eventTypeValue = (String) event.get(eventTypeFieldName);
		if(StringUtils.isBlank(eventTypeValue)){
			return; //raw events are saved in hdfs. currently raw events don't have event type value in the message, so isBlank is the condition.
		}

		EventPersistencyHandler eventPersitencyHandler = eventPersitencyHandlerFactory.getEventPersitencyHandler(event);
		if(eventPersitencyHandler != null){
			eventPersitencyHandler.saveEvent(event, SCORED_EVENTS_COLLECTION_NAME_PREFIX);
		}

	}
}
