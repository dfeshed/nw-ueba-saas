package fortscale.monitoring.external.stats.samza.collector.service.impl.converter;

import fortscale.monitoring.external.stats.samza.collector.samzaMetrics.KeyValueChangelogTopicMetrics;
import fortscale.utils.logging.Logger;
import fortscale.utils.monitoring.stats.StatsService;
import org.apache.commons.collections.keyvalue.MultiKey;

import java.util.*;

import static fortscale.monitoring.external.stats.samza.collector.service.impl.converter.SamzaMetricsConversionUtil.*;

/**
 * converts samza standard metric to stats.
 * key value changelog metric entries example:
 *
 */
public class KeyValueChanglogTopicToStatsConverter extends BaseSamzaMetricsToStatsConverter {
    private static final Logger logger = Logger.getLogger(KeyValueChanglogTopicToStatsConverter.class);

    public static final String METRIC_NAME="org.apache.samza.storage.kv.LoggedStoreMetrics";
    protected List<String> storeOperations;

    /**
     * ctor
     */
    public KeyValueChanglogTopicToStatsConverter(StatsService statsService) {
        super(statsService);
        storeOperations = new LinkedList<>();
        Arrays.asList(operations.values()).stream().forEach(operation -> storeOperations.add(operation.value()));
    }

    /**
     * converts metricmessage entries to stats metrics
     * @param metricEntries
     * @param jobName metric message samza task
     * @param time metric message update time
     * @param hostname job hostname
     */
    @Override
    public void convert(Map<String, Object> metricEntries, String jobName, long time, String hostname) {
        Map updatedMetrics = new HashMap<>();

        for (Map.Entry<String, Object> entry : metricEntries.entrySet()) {
            try {
                String entryKey = entry.getKey();
                String storeName = getStoreName(entryKey, storeOperations);
                String operation = getOperationName(entryKey,storeName);
                MultiKey multiKey = new MultiKey(jobName, storeName);

                long entryValue = entryValueToLong(entry.getValue());

                KeyValueChangelogTopicMetrics metrics;
                // if there is no metric for this store, create one
                if (!metricsMap.containsKey(multiKey)) {
                    metrics = new KeyValueChangelogTopicMetrics(statsService, jobName, storeName);
                    metricsMap.put(multiKey, metrics);
                }
                metrics = (KeyValueChangelogTopicMetrics) metricsMap.get(multiKey);

                if (operation.equals(operations.GETS.value())) {
                    metrics.setQueries(entryValue);
                } else if (operation.equals(operations.RANGES.value())) {
                    metrics.setRangeQueries(entryValue);
                } else if (operation.equals(operations.PUTS.value())) {
                    metrics.setWrites(entryValue);
                } else if (operation.equals(operations.DELETES.value())) {
                    metrics.setWrites(entryValue);
                }  else if (operation.equals(operations.FLUSHES.value())) {
                    metrics.setFlushes(entryValue);
                }  else if (operation.equals(operations.ALLS.value())) {
                    metrics.setRecordsInStore(entryValue);
                } else {
                    String errorMsg = String.format("store %s has an unknown operation name", entry.getKey());
                    logger.error(errorMsg);
                    throw new RuntimeException(errorMsg);
                }
                updatedMetrics.put(multiKey,metrics);
            } catch (Exception e) {
                String errMessage = String.format("failed to convert entry %s: %s", entry.getKey(), entry.getValue());
                logger.error(errMessage, e);
            }
        }
        manualUpdateMetricsMap(updatedMetrics,time);
    }



    /**
     * all store operations
     */
    public enum operations {
        GETS("gets"), //queries
        RANGES("ranges"),//rangeQueries
        PUTS("puts"),//writes
        DELETES("deletes"),//deletes
        FLUSHES("flushes"),//flushes
        ALLS("alls");//recordsInStore

        private final String name;

        operations(String s) {
            name = s;
        }

        public String value() {
            return name;
        }
    }

}
