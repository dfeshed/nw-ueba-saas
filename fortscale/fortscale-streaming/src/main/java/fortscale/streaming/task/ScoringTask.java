package fortscale.streaming.task;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jsonorg.JsonOrgModule;
import fortscale.common.event.Event;
import fortscale.common.event.service.EventService;
import fortscale.ml.model.message.ModelBuildingStatusMessage;
import fortscale.streaming.exceptions.FilteredEventException;
import fortscale.streaming.exceptions.KafkaPublisherException;
import fortscale.streaming.service.config.StreamingTaskDataSourceConfigKey;
import fortscale.streaming.service.scorer.ScoringTaskService;
import fortscale.streaming.task.message.ProcessMessageContext;
import fortscale.streaming.task.message.StreamingProcessMessageContext;
import fortscale.streaming.task.metrics.ScoringStreamingTaskMetrics;
import fortscale.streaming.task.monitor.MonitorMessaages;
import fortscale.utils.logging.Logger;
import net.minidev.json.JSONObject;
import org.apache.samza.config.Config;
import org.apache.samza.metrics.Counter;
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
    private ObjectMapper objectMapper;


    @Override
    protected void processInit(Config config, TaskContext context) throws Exception {
        // get task configuration parameters
        timestampField = getConfigString(config, "fortscale.timestamp.field");
        // create counter metric for processed messages
        processedMessageCount = context.getMetricsRegistry().newCounter(getClass().getName(), "event-score-message-count");
        lastTimestampCount = context.getMetricsRegistry().newCounter(getClass().getName(), "event-score-message-epochime");

        // create task metrics
        wrappedCreateTaskMetrics();

        scoringTaskService = new ScoringTaskService(config, context);

        eventService = springService.resolve(EventService.class);

        modelBuildingControlOutputTopic = resolveStringValue(config, CONTROL_OUTPUT_TOPIC_KEY,res);
        Assert.hasText(modelBuildingControlOutputTopic);

        objectMapper = new ObjectMapper().registerModule(new JsonOrgModule());
    }

    @Override
    protected void processMessage(ProcessMessageContext messageContext) throws Exception {
        String topicName = messageContext.getTopicName();

        JSONObject message = messageContext.getMessageAsJson();
        String messageText = messageContext.getMessageAsString();
        if(topicName.equals(modelBuildingControlOutputTopic))
        {
            taskMetrics.modelBuildingEvents++;

            handleModelBuildingEvent(messageText, message);
        }
        else {
            Long timestamp = extractTimeStamp(message, messageText);
            taskMetrics.eventsTime = timestamp;
            MessageCollector collector = ((StreamingProcessMessageContext) messageContext).getCollector();
            handleEventToScore(collector, message, timestamp);
            // todo: this metric should we removed after DPM is part of ther project
            processedMessageCount.inc();

            lastTimestampCount.set(timestamp);
        }

    }

    private void handleEventToScore(MessageCollector collector, JSONObject message, Long timestamp) throws Exception {
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
    }

    /**
     * refreshes model cache if relevant
     *
     * @param messageText
     * @param message
     * @throws java.io.IOException
     */
    private void handleModelBuildingEvent(String messageText, JSONObject message) throws java.io.IOException {
        // Check that input message is a status message
        if (message.containsKey(ModelBuildingStatusMessage.CONTEXT_ID_FIELD_NAME)) {
            ModelBuildingStatusMessage modelBuildingStatusMessage =
                    objectMapper.readValue(messageText, ModelBuildingStatusMessage.class);
            if (modelBuildingStatusMessage.isSuccessful()) {
                taskMetrics.refreshModelCache++;
                scoringTaskService.refreshModelCache(modelBuildingStatusMessage);
            } else {
                logger.warn("received modelBuildingStatusMessage with failure status: {} , not updating model cache",
                        modelBuildingStatusMessage);
            }
        } else {
            logger.debug("received model building summary message={}", message);
        }
    }

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
    protected void processWindow(MessageCollector collector, TaskCoordinator coordinator) throws Exception {
        scoringTaskService.window(collector, coordinator);
    }


    @Override
    protected void processClose() throws Exception {
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
