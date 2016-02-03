package fortscale.streaming.task;

import fortscale.streaming.exceptions.FilteredEventException;
import fortscale.streaming.exceptions.KafkaPublisherException;
import fortscale.streaming.service.config.StreamingTaskDataSourceConfigKey;
import fortscale.streaming.service.scorer.ScoringTaskService;
import fortscale.streaming.task.monitor.MonitorMessaages;
import fortscale.utils.logging.Logger;
import net.minidev.json.JSONObject;
import net.minidev.json.JSONValue;
import org.apache.samza.config.Config;
import org.apache.samza.metrics.Counter;
import org.apache.samza.system.IncomingMessageEnvelope;
import org.apache.samza.task.MessageCollector;
import org.apache.samza.task.TaskContext;
import org.apache.samza.task.TaskCoordinator;

import static fortscale.streaming.ConfigUtils.getConfigString;
import static fortscale.utils.ConversionUtils.convertToLong;


public class ScoringTask extends AbstractStreamTask {
    private static final Logger logger = Logger.getLogger(ScoringTask.class);

    ScoringTaskService scoringTaskService;
    private String timestampField;
    private Counter processedMessageCount;
    private Counter lastTimestampCount;



    @Override
    protected void wrappedInit(Config config, TaskContext context) throws Exception {
        // get task configuration parameters
        timestampField = getConfigString(config, "fortscale.timestamp.field");
        // create counter metric for processed messages
        processedMessageCount = context.getMetricsRegistry().newCounter(getClass().getName(), "event-score-message-count");
        lastTimestampCount = context.getMetricsRegistry().newCounter(getClass().getName(), "event-score-message-epochime");

        scoringTaskService = new ScoringTaskService(config, context);
    }

    @Override
    protected void wrappedProcess(IncomingMessageEnvelope envelope, MessageCollector collector, TaskCoordinator coordinator) throws Exception {
        String messageText = (String)envelope.getMessage();
        JSONObject message = (JSONObject) JSONValue.parseWithException(messageText);
        Long timestamp = extractTimeStamp(message, messageText);
        StreamingTaskDataSourceConfigKey configKey = extractConfigKey(message);

        try {
            message = scoringTaskService.calculateScoresAndUpdateMessage(message, timestamp, configKey.getDataSource());
            handleUnfilteredEvent(message, configKey);
        } catch (FilteredEventException  | KafkaPublisherException e){
            taskMonitoringHelper.countNewFilteredEvents(configKey,e.getMessage());
            throw e;
        }

        scoringTaskService.saveAndSendEventToOutputTopic(envelope, collector, coordinator, message);
        processedMessageCount.inc();
        lastTimestampCount.set(timestamp);
    }

    private Long extractTimeStamp(JSONObject message, String messageText) throws FilteredEventException {
        Long timestamp = convertToLong(message.get(timestampField));
        if (timestamp==null) {
            logger.error("message {} does not contains timestamp in field {}", messageText, timestampField);
            throw new FilteredEventException(MonitorMessaages.MESSAGE_DOES_NOT_CONTAINS_TIMESTAMP_IN_FIELD);
        }
        return timestamp;
    }

    private StreamingTaskDataSourceConfigKey extractConfigKey(JSONObject message) throws IllegalStateException {
        StreamingTaskDataSourceConfigKey configKey = extractDataSourceConfigKeySafe(message);
        if (configKey == null){
            taskMonitoringHelper.countNewFilteredEvents(super.UNKNOW_CONFIG_KEY, MonitorMessaages.CANNOT_EXTRACT_STATE_MESSAGE);
            throw new IllegalStateException("No configuration found for config key " + configKey + ". Message received: " + message.toJSONString());
        }
        return configKey;
    }


    @Override
    protected void wrappedWindow(MessageCollector collector, TaskCoordinator coordinator) throws Exception {
        scoringTaskService.window(collector, coordinator);
    }


    @Override
    protected void wrappedClose() throws Exception {
        scoringTaskService.close();
    }
}
