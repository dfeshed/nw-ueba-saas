package fortscale.streaming.task;

import fortscale.common.event.Event;
import fortscale.common.event.service.EventService;
import fortscale.streaming.exceptions.FilteredEventException;
import fortscale.streaming.exceptions.KafkaPublisherException;
import fortscale.streaming.service.SpringService;
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

    private ScoringTaskService scoringTaskService;
    private EventService eventService;
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
        eventService = SpringService.getInstance().resolve(EventService.class);
    }

    @Override
    protected void wrappedProcess(IncomingMessageEnvelope envelope, MessageCollector collector, TaskCoordinator coordinator) throws Exception {
        String messageText = (String)envelope.getMessage();
        JSONObject message = (JSONObject)JSONValue.parseWithException(messageText);
        Long timestamp = extractTimeStamp(message, messageText);
        Event event = eventService.createEvent(message);
        StreamingTaskDataSourceConfigKey configKey = extractDataSourceConfigKey(message);

        try {
            message = scoringTaskService.calculateScoresAndUpdateMessage(event, timestamp);
            handleUnfilteredEvent(message, configKey);
        } catch (FilteredEventException | KafkaPublisherException e) {
            taskMonitoringHelper.countNewFilteredEvents(configKey, e.getMessage());
            throw e;
        }

        scoringTaskService.sendEventToOutputTopic(collector, message);
        processedMessageCount.inc();
        lastTimestampCount.set(timestamp);
    }

    @Override
    protected StreamingTaskDataSourceConfigKey extractDataSourceConfigKey(JSONObject message) {
        Event event = eventService.createEvent(message);
        String dataSource = event.getDataSource();
        String lastState = (String) message.get(LAST_STATE_FIELD_NAME);

        if (dataSource == null) {
            throw new IllegalStateException("Message does not contain data source" + message.toJSONString());
        }

        return new StreamingTaskDataSourceConfigKey(dataSource, lastState);
    }

    private Long extractTimeStamp(JSONObject message, String messageText) throws FilteredEventException {
        Long timestamp = convertToLong(message.get(timestampField));
        if (timestamp==null) {
            logger.error("message {} does not contains timestamp in field {}", messageText, timestampField);
            throw new FilteredEventException(MonitorMessaages.MESSAGE_DOES_NOT_CONTAINS_TIMESTAMP_IN_FIELD);
        }
        return timestamp;
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
