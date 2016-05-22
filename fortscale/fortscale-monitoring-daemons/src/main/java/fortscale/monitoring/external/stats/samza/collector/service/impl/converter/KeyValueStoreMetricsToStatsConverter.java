package fortscale.monitoring.external.stats.samza.collector.service.impl.converter;

import fortscale.monitoring.external.stats.samza.collector.samzaMetrics.KeyValueStoreMetrics;
import fortscale.utils.logging.Logger;
import fortscale.utils.monitoring.stats.StatsService;

import java.util.Arrays;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;

/**
 * Created by cloudera on 5/22/16.
 */
public class KeyValueStoreMetricsToStatsConverter extends BaseSamzaMetricsToStatsConverter {
    private static final Logger logger = Logger.getLogger(KeyValueStoreMetricsToStatsConverter.class);

    public static final String METRIC_NAME = "org.apache.samza.storage.kv.KeyValueStoreMetrics";
    Map<String, KeyValueStoreMetrics> metricsMap;

    /**
     * ctor
     */
    public KeyValueStoreMetricsToStatsConverter(StatsService statsService) {
        super(statsService);
        metricsMap = new HashMap<>();
        Arrays.asList(operations.values()).stream().forEach(operation -> storeOperations.add(operation.value()));
    }

    /**
     * converts metricmessage entries to keyValueStoreMetrics
     * @param metricEntries
     * @param jobName metric message samza task
     * @param time metric message update time
     * @param hostname job hostname
     */
    @Override
    protected void convert(Map<String, Object> metricEntries, String jobName, long time, String hostname) {
        HashSet<String> updatedMetricsKeys = new HashSet<>();

        for (Map.Entry<String, Object> entry : metricEntries.entrySet()) {
            try {
                String storeName = getStoreName(entry.getKey());
                String operation = entry.getKey().replaceFirst(String.format("%s-", storeName), "");
                String metricsKey = getMetricsKey(Arrays.asList(jobName, storeName));
                updatedMetricsKeys.add(metricsKey);
                long entryValue = entryValueToLong(entry.getValue());

                // if there is no metric for this store, create one
                if (metricsMap.get(metricsKey) == null) {
                    KeyValueStoreMetrics newMetrics = new KeyValueStoreMetrics(statsService, jobName, storeName);
                    metricsMap.put(metricsKey, newMetrics);
                }

                if (operation.equals(operations.GETS.value())) {
                    metricsMap.get(metricsKey).setQueries(entryValue);
                } else if (operation.equals(operations.GET_ALLS.value())) {
                    metricsMap.get(metricsKey).setFullTableScans(entryValue);
                } else if (operation.equals(operations.RANGES.value())) {
                    metricsMap.get(metricsKey).setRangeQueries(entryValue);
                } else if (operation.equals(operations.PUTS.value())) {
                    metricsMap.get(metricsKey).setWrites(entryValue);
                } else if (operation.equals(operations.DELETES.value())) {
                    metricsMap.get(metricsKey).setWrites(entryValue);
                } else if (operation.equals(operations.DELETE_ALLS.value())) {
                    metricsMap.get(metricsKey).setDeleteAlls(entryValue);
                } else if (operation.equals(operations.FLUSHES.value())) {
                    metricsMap.get(metricsKey).setFlushes(entryValue);
                } else if (operation.equals(operations.BYTES_WRITTEN.value())) {
                    metricsMap.get(metricsKey).setBytesWritten(entryValue);
                } else if (operation.equals(operations.BYTES_READ.value())) {
                    metricsMap.get(metricsKey).setBytesRead(entryValue);
                } else if (operation.equals(operations.ALLS.value())) {
                    metricsMap.get(metricsKey).setRecordsInStore(entryValue);
                } else {
                    String errorMsg = String.format("store %s has an unknown operation name", entry.getKey());
                    logger.error(errorMsg);
                    throw new RuntimeException(errorMsg);
                }
            } catch (Exception e) {
                String errMessage = String.format("failed to convert entry %s: %s", entry.getKey(), entry.getValue());
                logger.error(errMessage, e);
            }
        }
        manualUpdateMetricsByKeys(updatedMetricsKeys,time);
    }

    /**
     *
     * @return metrics name
     */
    @Override
    protected String getMetricName() {
        return METRIC_NAME;
    }

    /**
     * manual updates metrics by time
     * @param keys metrics to manual update
     * @param time manual update time
     */
    @Override
    protected void manualUpdateMetricsByKeys(HashSet<String> keys, long time) {

        for (String key: keys) {
            try {
                metricsMap.get(key).manualUpdate(time);
            }
            catch (Exception e)
            {
                String message = String.format("unexpected error happened while manul updating metric with key: %s ",key);
                logger.error(message ,e);
            }
        }
    }

    /**
     * all store operations
     */
    public enum operations {
        GETS("gets"),
        GET_ALLS("getAlls"),
        RANGES("ranges"),
        PUTS("puts"),
        DELETES("deletes"),
        DELETE_ALLS("deleteAlls"),
        FLUSHES("flushes"),
        BYTES_WRITTEN("bytes-written"),
        BYTES_READ("bytes-read"),
        ALLS("alls");

        private final String name;

        private operations(String s) {
            name = s;
        }

        public String value() {
            return name;
        }
    }
}
