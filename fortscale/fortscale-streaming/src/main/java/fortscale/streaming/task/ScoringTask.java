package fortscale.streaming.task;

import fortscale.common.event.Event;
import fortscale.common.event.service.EventService;
import fortscale.services.impl.SpringService;
import fortscale.streaming.exceptions.FilteredEventException;
import fortscale.streaming.exceptions.KafkaPublisherException;
import fortscale.streaming.service.FortscaleValueResolver;
import fortscale.streaming.service.config.StreamingTaskDataSourceConfigKey;
import fortscale.streaming.service.scorer.ScoringTaskService;
import fortscale.streaming.task.metrics.ScoringStreamingTaskMetrics;
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
import org.springframework.util.Assert;

import static fortscale.streaming.ConfigUtils.getConfigString;
import static fortscale.streaming.task.ModelBuildingStreamTask.CONTROL_OUTPUT_TOPIC_KEY;
import static fortscale.utils.ConversionUtils.convertToLong;


public class ScoringTask extends AbstractStreamTask {
    private static final Logger logger = Logger.getLogger(ScoringTask.class);
    private ScoringStreamingTaskMetrics taskMetrics;
    private ScoringTaskService scoringTaskService;
    private EventService eventService;
    private String timestampField;
    private Counter processedMessageCount;
    private Counter lastTimestampCount;
    private String modelBuildingControlOutputTopic;


    @Override
    protected void wrappedInit(Config config, TaskContext context) throws Exception {
        // get task configuration parameters
        timestampField = getConfigString(config, "fortscale.timestamp.field");
        // create counter metric for processed messages
        processedMessageCount = context.getMetricsRegistry().newCounter(getClass().getName(), "event-score-message-count");
        lastTimestampCount = context.getMetricsRegistry().newCounter(getClass().getName(), "event-score-message-epochime");

        // create task metrics
        wrappedCreateTaskMetrics();

        scoringTaskService = new ScoringTaskService(config, context);
        SpringService springService = SpringService.getInstance();
        eventService = springService.resolve(EventService.class);
        FortscaleValueResolver resolver = springService.resolve(FortscaleValueResolver.class);

        modelBuildingControlOutputTopic = resolver.resolveStringValue(config, CONTROL_OUTPUT_TOPIC_KEY);
        Assert.hasText(modelBuildingControlOutputTopic);
    }

    @Override
    protected void wrappedProcess(IncomingMessageEnvelope envelope, MessageCollector collector, TaskCoordinator coordinator) throws Exception {
        String topicName = getIncomingMessageTopicName(envelope);


        if(topicName.equals(modelBuildingControlOutputTopic))
        {
            // TODO: 10/19/16 update model cache
        }
        String messageText = (String)envelope.getMessage();
        JSONObject message = (JSONObject)JSONValue.parseWithException(messageText);
        Long timestamp = extractTimeStamp(message, messageText);
        taskMetrics.eventsTime = timestamp;
        Event event = eventService.createEvent(message);
        StreamingTaskDataSourceConfigKey configKey = extractDataSourceConfigKey(message);

        try {
            taskMetrics.calculateScores++;
            message = scoringTaskService.calculateScoresAndUpdateMessage(event, timestamp);
            handleUnfilteredEvent(message, configKey);
        } catch (FilteredEventException | KafkaPublisherException e) {
            taskMonitoringHelper.countNewFilteredEvents(configKey, e.getMessage());
            taskMetrics.filteredEvents++;
            throw e;
        }

        scoringTaskService.sendEventToOutputTopic(collector, message);
        taskMetrics.sentEvents++;

        // todo: this metric should we removed after DPM is part of ther project
        processedMessageCount.inc();

        lastTimestampCount.set(timestamp);
    }

    @Override
    protected StreamingTaskDataSourceConfigKey extractDataSourceConfigKey(JSONObject message) {
        Event event = eventService.createEvent(message);
        String dataSource = event.getDataSource();
        String lastState = (String) message.get(LAST_STATE_FIELD_NAME);

        if (dataSource == null) {
            streamingTaskCommonMetrics.messagesWithoutDataSourceName++;
            throw new IllegalStateException("Message does not contain data source" + message.toJSONString());
        }

        return new StreamingTaskDataSourceConfigKey(dataSource, lastState);
    }

    private Long extractTimeStamp(JSONObject message, String messageText) throws FilteredEventException {
        Long timestamp = convertToLong(message.get(timestampField));
        if (timestamp==null) {
            taskMetrics.eventsWithoutTimestamp++;
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

    /**
     * Create the task's specific metrics.
     *
     * Typically, the function is called from AbstractStreamTask.createTaskMetrics() at init()
     */
    @Override
    protected void wrappedCreateTaskMetrics() {

        // Create the task's specific metrics
        taskMetrics = new ScoringStreamingTaskMetrics(statsService);
    }
}
