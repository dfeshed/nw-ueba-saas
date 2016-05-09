package fortscale.utils.monitoring.stats.engine.topic;

import com.fasterxml.jackson.databind.ObjectMapper;
import fortscale.utils.kafka.KafkaEventsWriter;
import fortscale.utils.samza.metricMessageModels.Header;
import fortscale.utils.samza.metricMessageModels.MetricMessage;
import fortscale.utils.samza.metricMessageModels.Metrics;
import fortscale.utils.logging.Logger;
import fortscale.utils.monitoring.stats.engine.StatsEngineExceptions;
import fortscale.utils.monitoring.stats.engine.StatsEngineBase;
import fortscale.utils.monitoring.stats.engine.StatsEngineMetricsGroupData;
import fortscale.utils.monitoring.stats.models.engine.EngineData;

import java.util.HashMap;
import java.util.List;

/**
 *
 * The stats topic engine writes metrics group data directly to a Kafka ("metrics") topic.
 *
 * The engine accumulates the metrics group data. When the engine is flushed, the accumulated data is converted and
 * written to the topic.
 *
 * It should not be used when Samaza is available.
 *
 * Created by gaashh on 4/26/16.
 */
public class StatsTopicEngine extends StatsEngineBase {

    private static final Logger logger = Logger.getLogger(StatsTopicEngine.class);

    final protected String ENGINE_DATA_METRICS_HEADER_VERSION = "0.0.1"; // Same as Samza version

    final protected long TOPIC_MESSAGE_WARNING_SIZE = 100 * 1024;

    // Must match metric adapter
    // DO not change even is class is relocated
    final protected String ENGINE_DATA_METRIC_FIELD_NAME = "fortscale.utils.monitoring.stats.models.engine";
    final protected String ENGINE_DATA_METRIC_METRIC_NAME = "EngineData";

    protected String topicName;

    protected long firstEpochTime = 0;

    protected KafkaEventsWriter kafkaEventsWriter;

    public StatsTopicEngine(KafkaEventsWriter kafkaEventsWriter) {

        super();

        this.kafkaEventsWriter = kafkaEventsWriter;

    }


    /**
     *
     * Grubs the accumulated metrics data list (if any), converts it to Engine data model and then to JSON string.
     *
     * The last step is to write the JSON string to a Kafka topic
     *
     * This function is called once a collection cycle was completed.
     *
     */
    @Override
    public void flushMetricsGroupData() {

        List<StatsEngineMetricsGroupData> metricsGroupDataToWrite = null;

        // Grub the accumulated metrics group data list and empty it
        synchronized (accumulatedMetricsGroupDataListLock) {
            metricsGroupDataToWrite = accumulatedMetricsGroupDataList;
            accumulatedMetricsGroupDataList = null;
        }

        // Check nothing was accumulated
        if (metricsGroupDataToWrite == null || metricsGroupDataToWrite.size() == 0) {
            logger.debug("Flush accumulated metrics group data - nothing to flush");
            return;
        }

        // We have a metrics group data to write, convert the metrics group data to engine data model object
        EngineData engineData = statsEngineDataToModelData(metricsGroupDataToWrite);

        // Get the first and list entries time
        long firstEpochTime = metricsGroupDataToWrite.get(0).getMeasurementEpoch();
        long lastEpochTime  = metricsGroupDataToWrite.get(metricsGroupDataToWrite.size() - 1 ). getMeasurementEpoch();

        // Convert the engine data model object into a JSON string we can write to the topic
        String topicMessage = engineDataToMetricsTopicMessageString(engineData, firstEpochTime);

        // Log it
        logger.debug("Writing message to topic with {} metric group entries. bytes={} firstEpochTime={} timeSpan={}",
                metricsGroupDataToWrite.size(), topicMessage.length(), firstEpochTime, lastEpochTime - firstEpochTime);

        // Warn if message too long
        if (topicMessage.length() > TOPIC_MESSAGE_WARNING_SIZE) {
            logger.warn("Too long message to write topic with {} metric group entries. bytes={} firstEpochTime={} timeSpan={} warningSize={}",
                    metricsGroupDataToWrite.size(), topicMessage.length(), firstEpochTime, lastEpochTime - firstEpochTime,
                    TOPIC_MESSAGE_WARNING_SIZE);

        }

        // Write the data to the topic :-)
        writeMessageStringToMetricsTopic(topicMessage);

    }

    /**
     *
     * Encode engine data model object as a JSON string to be sent in Samza metrics topic message format
     *
     * @param engineData - data to convert
     * @param epochTime  - epoch time (in seconds) to embedded in the header. Note the actual measurement time is at
     *                   - the engine data.
     * @return Message JSON string
     */
    protected String engineDataToMetricsTopicMessageString(EngineData engineData, long epochTime) {

        // On the first time, update first epoch time
        if (firstEpochTime == 0) {
            firstEpochTime = epochTime;
        }

        // Build the metrics topic header
        Header header = new Header();
        header.setHost("TODO-hostname"); // TODO
        header.setJobName("TODO-jobname"); // TODO
        header.setVersion(ENGINE_DATA_METRICS_HEADER_VERSION);
        header.setTime(epochTime * 1000);  // in mSec
        header.setResetTime(firstEpochTime * 1000); // in mSec

        // Convert the engine data into a JSON string
        String engineDataInJsonString = modelMetricGroupToJsonInString(engineData);

        // Creates a metrics entry
        HashMap<String, Object> engineDataMetricsEntry = new HashMap<>();
        engineDataMetricsEntry.put(ENGINE_DATA_METRIC_METRIC_NAME, engineDataInJsonString);

        // Create the metrics object and populate it with the entry
        Metrics metrics = new Metrics();
        metrics.setAdditionalProperty(ENGINE_DATA_METRIC_FIELD_NAME, engineDataMetricsEntry);

        // Build the metric message
        MetricMessage metricMessage = new MetricMessage();
        metricMessage.setHeader(header);
        metricMessage.setMetrics(metrics);

        // Encode the metrics message as a JSON string
        // In case of error, just log an error
        ObjectMapper mapper = new ObjectMapper();

        String jsonInString = null;

        try {
            jsonInString = mapper.writeValueAsString(metricMessage);
        }
        catch (Exception ex) {
            String msg = "Failed to encode metrics message as a JSON string";
            logger.error(msg, ex);
            throw ( new StatsEngineExceptions.ModelEngineDataToMetricsMessageJsonFailureException(msg, ex) );
        }

        return jsonInString;

    }

    /**
     *
     * Writes a message to the kafka topic
     *
     * @param message - to write
     */
    public void writeMessageStringToMetricsTopic(String message) {

        logger.trace("Writing message to topic. content is {}", message);

        kafkaEventsWriter.send("", message);
    }


}