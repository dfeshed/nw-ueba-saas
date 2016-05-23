package fortscale.monitoring.external.stats.samza.collector.service.impl.converter;

import fortscale.utils.samza.metricMessageModels.MetricMessage;

import java.util.List;

/**
 * samza standard metrics conversion utility
 */
public class SamzaMetricsConversionUtil {
    /**
     * converts Integer/Double/Object to long
     *
     * @param entry entry to convert
     * @return long value of entry
     */
    public static long entryValueToLong(Object entry) {
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
     * gets store name - cleans unnecessary strings
     *
     * @param rawStoreName store name before conversion
     * @return clean store name
     */
    public static String getStoreName(String rawStoreName, List<String> storeOperations) {
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
     * gets topic name - cleans unnecessary strings
     *
     * @param rawTopicName raw topic name before conversion
     * @return clean topic name
     */
    public static String getTopicName(String rawTopicName, List<String> topicOperations) {
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
     * converts Samza metric message time from milliseconds to seconds
     *
     * @param metricMessage with time in milliseconds
     * @return time in seconds
     */
    public static long getMetricMessageTime(MetricMessage metricMessage) {
        return metricMessage.getHeader().getTime() / 1000;
    }

    public static String getOperationName(String rawEntryKey, String storeName)
    {
        return rawEntryKey.replaceFirst(String.format("%s-", storeName), "");
    }
}
