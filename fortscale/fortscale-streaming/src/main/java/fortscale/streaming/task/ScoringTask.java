package fortscale.streaming.task;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jsonorg.JsonOrgModule;
import fortscale.common.event.Event;
import fortscale.common.event.service.EventService;
import fortscale.ml.model.message.ModelBuildingStatusMessage;
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
    private ObjectMapper objectMapper;


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

        objectMapper = new ObjectMapper().registerModule(new JsonOrgModule());
    }

    @Override
    protected void wrappedProcess(IncomingMessageEnvelope envelope, MessageCollector collector, TaskCoordinator coordinator) throws Exception {
        String topicName = getIncomingMessageTopicName(envelope);

        String messageText = (String)envelope.getMessage();
        JSONObject message = (JSONObject)JSONValue.parseWithException(messageText);
        Long timestamp = extractTimeStamp(message, messageText);
        taskMetrics.eventsTime = timestamp;

        if(topicName.equals(modelBuildingControlOutputTopic))
        {
            handleModelBuildingEvent(messageText, message);
        }
        else {
            handleEventToScore(collector, message, timestamp);
        }

        // todo: this metric should we removed after DPM is part of ther project
        processedMessageCount.inc();

        lastTimestampCount.set(timestamp);
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
