package fortscale.monitoring.external.stats.samza.collector.service.impl.converter;

import com.kenai.jaffl.struct.Struct;
import fortscale.utils.monitoring.stats.StatsService;
import fortscale.utils.samza.metricMessageModels.MetricMessage;

import java.util.*;

/**
 * Basic class for samza metrics conversion to stats metrics.
 */
public abstract class BaseSamzaMetricsToStatsConverter {
    protected List<String> topicOperations = new ArrayList<>();
    protected List<String> storeOperations = new ArrayList<>();
    protected StatsService statsService;

    public BaseSamzaMetricsToStatsConverter(StatsService statsService)
    {
        this.statsService=statsService;
    }

    /**
     * converts Integer/Double/Object to long
     *
     * @param entry
     * @return long value of entry
     */
    protected long entryValueToLong(Object entry) {
        long result;
        if (entry.getClass().equals(Integer.class)) {
            result = ((Integer) entry).longValue();
        } else if (entry.getClass().equals(Double.class)) {
            result = ((Double) entry).longValue();
        } else {
            result = (Long) entry;
        }
        return result;
    }

    /**
     * gets topic name - cleans unnecessary strings
     *
     * @param rawTopicName
     * @return clean topic name
     */
    protected String getTopicName(String rawTopicName) {
        String topicName = rawTopicName;

        if (topicName.startsWith("kafka-")) {
            topicName = topicName.substring("kafka-".length());
        }
        if (topicName.contains("-offset")) {
            topicName = topicName.replaceAll("-offset", "");
        }
        if (topicName.contains("-0"))
            topicName = topicName.replaceAll("-0", "");
        if (topicName.contains("-SystemStreamPartition")) {
            topicName = topicName.replaceAll("-SystemStreamPartition", "").split(",")[1];
        }
        for (String topicOperation : topicOperations) {
            String updatedTopicOperation = String.format("-%s", topicOperation);
            if (topicName.contains(updatedTopicOperation)) {
                topicName = topicName.replaceAll(String.format("-%s", updatedTopicOperation), "");
            }
        }

        topicName = topicName.trim();
        return topicName;
    }

    /**
     * gets store name - cleans unnecessery strings
     *
     * @param rawStoreName
     * @return clean store name
     */
    protected String getStoreName(String rawStoreName) {
        String storeName = rawStoreName;

        for (String operation : storeOperations) {
            String updatedStoreOperation = String.format("-%s", operation);
            if (storeName.contains(updatedStoreOperation)) {
                storeName = storeName.replaceAll(updatedStoreOperation, "");
            }
        }

        storeName = storeName.trim();
        return storeName;
    }

    /**
     * converts Samza metric message time from milliseconds to seconds
     *
     * @param metricMessage with time in milliseconds
     * @return time in seconds
     */
    protected long getMetricMessageTime(MetricMessage metricMessage) {
        long result = metricMessage.getHeader().getTime() / 1000;
        return result;
    }

    protected abstract void convert(Map<String, Object> metricEntries, String JobName, long time, String hostname);

    /**
     * @return full samza metric group name
     */
    protected abstract String getMetricName();

    /**
     * concatenate list of strings to single string representing a key
     * @param keys
     * @return
     */
    protected String getMetricsKey(List<String> keys)
    {
        StringBuilder sb = new StringBuilder();
        String result = "";
        sb.append(keys.get(0));
        for (String key:keys.subList(1,keys.size())) {
            sb.append("__");
            sb.append(key);
        }
        return sb.toString();
    }

    protected abstract void manualUpdateMetricsByKeys(HashSet<String> keys,long time);


}
