package fortscale.streaming.service;

import static com.google.common.base.Preconditions.checkNotNull;
import static fortscale.streaming.ConfigUtils.getConfigString;
import static fortscale.streaming.ConfigUtils.getConfigStringList;
import static fortscale.utils.ConversionUtils.convertToLong;

import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

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

import com.google.common.collect.Iterables;

import fortscale.ml.service.ModelService;
import fortscale.streaming.exceptions.KafkaPublisherException;
import fortscale.streaming.exceptions.StreamMessageNotContainFieldException;
import fortscale.streaming.scorer.EventMessage;
import fortscale.streaming.scorer.Scorer;
import fortscale.streaming.scorer.ScorerFactoryService;
import fortscale.utils.StringPredicates;
import fortscale.utils.logging.Logger;

@Configurable(preConstruction=true)
public class EventsScoreStreamTaskService {

	private static final Logger logger = Logger.getLogger(EventsScoreStreamTaskService.class);
	
	@Autowired
	private ScorerFactoryService scorerFactoryService;

	private ModelService modelService;
	private Map<String, Scorer> scorerMap;

	private String outputTopic;
	private String sourceType;
	private String entityType;
	private String timestampField;
	private List<Scorer> scorersToRun;
	
	private Counter processedMessageCount;
	private Counter lastTimestampCount;
	
	
	
	public EventsScoreStreamTaskService(Config config, TaskContext context, ModelService modelService) throws Exception{
		this.modelService = modelService;
		// get task configuration parameters
		sourceType = getConfigString(config, "fortscale.source.type");
		entityType = getConfigString(config, "fortscale.entity.type");
		timestampField = getConfigString(config, "fortscale.timestamp.field");
		outputTopic = config.get("fortscale.output.topic", "");
		
		fillScoreConfig(config);
		
		// create counter metric for processed messages
		processedMessageCount = context.getMetricsRegistry().newCounter(getClass().getName(), String.format("%s-%s-event-score-message-count", sourceType, entityType));
		lastTimestampCount = context.getMetricsRegistry().newCounter(getClass().getName(), String.format("%s-%s-event-score-message-epochime", sourceType, entityType));
	}
	
	private void fillScoreConfig(Config config) throws Exception {
		scorerMap = new HashMap<>();
		Config fieldsSubset = config.subset("fortscale.score.");		
		for (String fieldConfigKey : Iterables.filter(fieldsSubset.keySet(), StringPredicates.endsWith(".scorer"))) {
			String scoreName = fieldConfigKey.substring(0, fieldConfigKey.indexOf(".scorer"));
			String scorerName = getConfigString(config, String.format("fortscale.score.%s.scorer", scoreName));
			Scorer scorer = scorerFactoryService.getScorer(scorerName, scoreName, config, modelService);
			checkNotNull(scorer);
			scorerMap.put(scoreName, scorer);			
		}
		
		for(Scorer scorer: scorerMap.values()){
			scorer.afterPropertiesSet(scorerMap);
		}
		
		List<String> scorers = getConfigStringList(config, "fortscale.scorers");
		for(String ScorerStr: scorers){
			Scorer scorer = scorerMap.get(ScorerStr);
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
			scorer.calculateScore(eventMessage);
		}
		
		Iterator<Entry<String, Double>> iter = eventMessage.getScoreIterator();
		while (iter.hasNext()) {
			Entry<String, Double> entry = iter.next();
			message.put(entry.getKey(), entry.getValue());
		}
	
		
		// publish the event with score to the subsequent topic in the topology
		if (StringUtils.isNotEmpty(outputTopic)){
			try{
				collector.send(new OutgoingMessageEnvelope(new SystemStream("kafka", outputTopic), message.toJSONString()));
			} catch(Exception exception){
				throw new KafkaPublisherException(String.format("failed to send scoring message after processing the message %s.", messageText), exception);
			}
		}
		
		processedMessageCount.inc();
		lastTimestampCount.set(timestamp);
	}
}
