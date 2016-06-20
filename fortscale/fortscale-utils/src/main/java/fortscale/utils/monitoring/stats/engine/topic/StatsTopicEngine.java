package fortscale.utils.monitoring.stats.engine.topic;

import fortscale.utils.kafka.KafkaEventsWriter;

import fortscale.utils.logging.Logger;
import fortscale.utils.monitoring.stats.engine.StatsEngineBase;
import fortscale.utils.monitoring.stats.engine.StatsEngineMetricsGroupData;
import fortscale.utils.monitoring.stats.models.engine.EngineData;

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

    final static String NEW_LINE = System.getProperty("line.separator");

    protected String topicName;

    protected long firstEpochTime = 0;

    protected KafkaEventsWriter kafkaEventsWriter;

    // Max number of metric groups to be written at one the Kafka message
    protected int metricGroupBatchWriteSize;

    // Writing a message longer than this value will generate a warning
    protected long messageSizeWarningThreshold;

    /**
     * The regular ctor
     *
     * @param kafkaEventsWriter           - Kafka message writer
     * @param metricGroupBatchWriteSize   - Max number of metric groups to be written at one the Kafka message
     * @param messageSizeWarningThreshold - Writing a message longer than this value will generate a warning
     */
    public StatsTopicEngine(KafkaEventsWriter kafkaEventsWriter, long metricGroupBatchWriteSize,
                            long messageSizeWarningThreshold) {

        super();

        this.kafkaEventsWriter           = kafkaEventsWriter;
        this.metricGroupBatchWriteSize   = (int)metricGroupBatchWriteSize;
        this.messageSizeWarningThreshold = messageSizeWarningThreshold;

        logger.info("Created StatsTopicEngine. kafkaEventsWriter={}  metricGroupBatchWriteSize={} messageSizeWarningThreshold={}",
                    kafkaEventsWriter, metricGroupBatchWriteSize, messageSizeWarningThreshold);
    }

    /**
     *
     * A simplified ctor - USE IT ONLY FOR TESTING
     *
     *
     * @param kafkaEventsWriter
     */
    public StatsTopicEngine(KafkaEventsWriter kafkaEventsWriter) {
        // Call the real ctor with some default values
        this(kafkaEventsWriter, 20, 100 * 1024);
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

        List<StatsEngineMetricsGroupData> metricsGroupDataListToWrite = null;

        // Grub the accumulated metrics group data list and empty it
        synchronized (accumulatedMetricsGroupDataListLock) {
            metricsGroupDataListToWrite = accumulatedMetricsGroupDataList;
            accumulatedMetricsGroupDataList = null;
        }


        // Check nothing was accumulated
        if (metricsGroupDataListToWrite == null || metricsGroupDataListToWrite.size() == 0) {
            logger.debug("Flush accumulated metrics groups data called but nothing to flush");
            return;
        }


        // Write the list in batches
        int start = 0 ;  // inclusive index
        int last;        // exclusive index
        int listSize  = metricsGroupDataListToWrite.size();

        do {

            // Calc the last index (exclusive) to write
            last = Math.min(start + metricGroupBatchWriteSize, listSize);

            // Calc the sub list to write at this iteration
            List<StatsEngineMetricsGroupData> subListToWrite = metricsGroupDataListToWrite.subList(start, last);

            // Log the results
            if (logger.isDebugEnabled()) {
                // Convert the metrics data list to a string
                String subListAsString = StatsEngineMetricsGroupData.listToString(subListToWrite);

                // Log it
                logger.debug("Stats topic engine, writing items [{},{}) of {} items to the topic{}",
                             start, last, listSize, subListAsString.toString());
            }

            // We have a metrics group data to write, convert the metrics group data to engine data model object
            EngineData engineData = statsEngineDataToModelData(subListToWrite);

            // Get the first and list entries time
            long firstEpochTime = subListToWrite.get(0).getMeasurementEpoch();
            long lastEpochTime  = subListToWrite.get(subListToWrite.size() - 1).getMeasurementEpoch();

            // Convert the engine data into a JSON string to be written to a topic
            String topicMessage = modelMetricGroupToJsonInString(engineData);

            // Log it
            logger.debug("Writing message to topic with {} metric groups entries. bytes={} firstEpochTime={} timeSpan={}",
                    subListToWrite.size(), topicMessage.length(), firstEpochTime, lastEpochTime - firstEpochTime);

            // Warn if message too long
            if (topicMessage.length() > messageSizeWarningThreshold) {
                logger.warn("Too long message to write topic with {} metric group entries. bytes={} firstEpochTime={} timeSpan={} warningSize={}",
                        subListToWrite.size(), topicMessage.length(), firstEpochTime, lastEpochTime - firstEpochTime,
                        messageSizeWarningThreshold);

            }

            // Write the data to the topic :-)
            writeMessageStringToTopic(topicMessage);

            // Next
            start = last;

        } while ( last < listSize);

    }


    /**
     *
     * Writes a message to the kafka topic
     *
     * @param message - to write
     */
    public void writeMessageStringToTopic(String message) {

        logger.trace("Writing message to topic. content is {}", message);

        kafkaEventsWriter.send("", message);
    }


}