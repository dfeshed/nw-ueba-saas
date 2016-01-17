package fortscale.streaming.task;

import fortscale.ml.scorer.FeatureScore;
import fortscale.ml.scorer.FeatureScoreJsonEventHandler;
import fortscale.ml.model.cache.ModelsCacheService;
import fortscale.ml.scorer.factory.ScorersFactoryService;
import fortscale.streaming.ExtendedSamzaTaskContext;
import fortscale.streaming.exceptions.FilteredEventException;
import fortscale.streaming.exceptions.KafkaPublisherException;
import fortscale.streaming.service.config.StreamingTaskDataSourceConfigKey;
import fortscale.streaming.service.event.EventPersistencyHandler;
import fortscale.streaming.service.event.EventPersistencyHandlerFactory;
import fortscale.streaming.service.model.ModelsCacheServiceSamza;
import fortscale.ml.scorer.ScorersService;
import fortscale.streaming.task.monitor.MonitorMessaages;
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
import java.util.List;

import static fortscale.streaming.ConfigUtils.getConfigString;
import static fortscale.utils.ConversionUtils.convertToLong;

@Configurable(preConstruction = true)
public class ScorerTask extends AbstractStreamTask {
    private static final Logger logger = Logger.getLogger(ScorerTask.class);
    private ModelsCacheService modelsCacheService;
    private ScorersService scorersService;
    private String outputTopic;
    private String bdpOutputTopic;
    private boolean forwardEvent;
    private String timestampField;
    private Counter processedMessageCount;
    private Counter lastTimestampCount;

    @Autowired
    private FeatureScoreJsonEventHandler featureScoreJsonEventHandler;

    @Autowired
    private EventPersistencyHandlerFactory eventPersistencyHandlerFactory;

    @Value("${fortscale.bdp.run}")
    private boolean isBDPRunning;

    @Value("${streaming.event.field.type}")
    private String eventTypeFieldName;

    @Autowired
    private ScorersFactoryService scorersFactoryService;


    @Override
    protected void wrappedInit(Config config, TaskContext context) throws Exception {
        modelsCacheService = new ModelsCacheServiceSamza(new ExtendedSamzaTaskContext(context, config));
        scorersFactoryService.setModelCacheService(modelsCacheService);
        scorersService = new ScorersService(modelsCacheService);

        // get task configuration parameters
        String sourceType = getConfigString(config, "fortscale.source.type");
        String entityType = getConfigString(config, "fortscale.entity.type");
        timestampField = getConfigString(config, "fortscale.timestamp.field");
        outputTopic = config.get("fortscale.output.topic", "");
        forwardEvent = true;
        if (isBDPRunning && config.containsKey("fortscale.bdp.output.topic")) {
            bdpOutputTopic = config.get("fortscale.bdp.output.topic", "");
            if (StringUtils.isEmpty(bdpOutputTopic)) {
                forwardEvent = false;
            }
        }

        // create counter metric for processed messages
        processedMessageCount = context.getMetricsRegistry().newCounter(getClass().getName(), String.format("%s-%s-event-score-message-count", sourceType, entityType));
        lastTimestampCount = context.getMetricsRegistry().newCounter(getClass().getName(), String.format("%s-%s-event-score-message-epochime", sourceType, entityType));

    }

    @Override
    protected void wrappedProcess(IncomingMessageEnvelope envelope, MessageCollector collector, TaskCoordinator coordinator) throws Exception {
        String messageText = (String)envelope.getMessage();
        JSONObject message = (JSONObject) JSONValue.parseWithException(messageText);

        Long timestamp = convertToLong(message.get(timestampField));
        if (timestamp==null) {
            logger.error("message {} does not contains timestamp in field {}", messageText, timestampField);
            throw new FilteredEventException(MonitorMessaages.MESSAGE_DOES_NOT_CONTAINS_TIMESTAMP_IN_FIELD);
        }

        StreamingTaskDataSourceConfigKey configKey = extractDataSourceConfigKeySafe(message);
        if (configKey == null){
            taskMonitoringHelper.countNewFilteredEvents(super.UNKNOW_CONFIG_KEY, MonitorMessaages.CANNOT_EXTRACT_STATE_MESSAGE);
            throw new IllegalStateException("No configuration found for config key " + configKey + ". Message received: " + message.toJSONString());
        }


        try {
            List<FeatureScore> featureScores = scorersService.calculateScores(message, timestamp, configKey.getDataSource());
            if(featureScores!=null) {
                message = featureScoreJsonEventHandler.updateEventWithScoreInfo(message, featureScores);
            }
            handleUnfilteredEvent(message, configKey);
        } catch (FilteredEventException  | KafkaPublisherException e){
            taskMonitoringHelper.countNewFilteredEvents(configKey,e.getMessage());
            throw e;
        }

        if (StringUtils.isNotEmpty(outputTopic) || StringUtils.isNotEmpty(bdpOutputTopic)){
            saveEvent(message);
            // publish the event with score to the subsequent topic in the topology
            if (forwardEvent){
                try {
                    if (StringUtils.isNotEmpty(bdpOutputTopic)) {
                        collector.send(new OutgoingMessageEnvelope(new SystemStream("kafka", bdpOutputTopic), message.toJSONString()));
                    } else {
                        collector.send(new OutgoingMessageEnvelope(new SystemStream("kafka", outputTopic), message.toJSONString()));
                    }

                } catch (Exception exception) {
                    throw new KafkaPublisherException(String.format("failed to send scoring message after processing the message %s.", messageText), exception);
                }
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

        EventPersistencyHandler eventPersistencyHandler = eventPersistencyHandlerFactory.getEventPersitencyHandler(event);
        if (eventPersistencyHandler != null) {
            eventPersistencyHandler.saveEvent(event);
        }
    }

    @Override
    protected void wrappedWindow(MessageCollector collector, TaskCoordinator coordinator) throws Exception {
        modelsCacheService.window();
    }


    @Override
    protected void wrappedClose() throws Exception {
        modelsCacheService.close();
    }
}
