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
        if (entry.getClass().isAssignableFrom(Integer.class)) {
            result = ((Integer) entry).longValue();
        } else if (entry.getClass().isAssignableFrom(Double.class)) {
            result = ((Double) entry).longValue();
        } else if (entry.getClass().isAssignableFrom(Boolean.class)) {
            if ((boolean) entry) {
                result = 1;
            } else {
                result = 0;
            }
        } else {
            result = (Long) entry;
        }
        return result;
    }

    /**
     * gets store name - drops the operation name
     *
     * @param text store name before conversion
     * @return clean store name
     */
    public static String getStoreName(String text, List<String> storeOperations) {
        String storeName = text;

        for (String operation : storeOperations) {
            String updatedStoreOperation = String.format("-%s", operation);
            if (storeName.contains(updatedStoreOperation)) {
                storeName = storeName.replaceAll(updatedStoreOperation, "");
                break;
            }
        }
        // store name may contain space as a prefix
        storeName = storeName.trim();
        return storeName;
    }

    /**
     * gets topic name - cleans unnecessary strings
     *
     * @param text raw topic name before conversion
     * @return clean topic name
     */
    public static String getTopicName(String text, List<String> topicOperations) {
        String topicName = text;

        if (topicOperations != null) {
            for (String topicOperation : topicOperations) {
                String afterHyphenTopicOperation = String.format("-%s", topicOperation);
                String beforeHyphenTopicOperation = String.format("%s-", topicOperation);
                if (topicName.contains(afterHyphenTopicOperation)) {
                    topicName = topicName.replaceAll(afterHyphenTopicOperation, "");
                } else if (topicName.contains(beforeHyphenTopicOperation)) {
                    topicName = topicName.replaceAll(beforeHyphenTopicOperation, "");
                }
            }
        }

        if (topicName.startsWith("kafka-")) {
            topicName = topicName.substring("kafka-".length());
        }
        if (topicName.contains("-offset")) {
            topicName = topicName.replaceAll("-offset", "");
        }
        if (topicName.contains("-0"))
            topicName = topicName.replaceAll("-0", "");
        if (topicName.contains("SystemStreamPartition")) {
            topicName = topicName.replaceAll("SystemStreamPartition", "").split(",")[1];
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

    /**
     * gets operation name from text
     * @param text operation name before conversion (concatenated with store name)
     * @param storeName the store name
     * @return operation name
     */
    public static String getOperationName(String text, String storeName) {
        return text.replaceFirst(String.format("%s-", storeName), "").trim();
    }
}
