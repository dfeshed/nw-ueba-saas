package fortscale.monitoring.external.stats.samza.collector.service.impl.converter;

import fortscale.monitoring.external.stats.samza.collector.samzaMetrics.KeyValueStorageMetrics;
import fortscale.monitoring.external.stats.samza.collector.service.stats.SamzaMetricCollectorMetrics;
import fortscale.utils.logging.Logger;
import fortscale.utils.monitoring.stats.StatsService;
import org.apache.commons.collections.keyvalue.MultiKey;

import java.util.*;

import static fortscale.monitoring.external.stats.samza.collector.service.impl.converter.SamzaMetricsConversionUtil.*;

public class KeyValueStorageMetricsToStatsConverter extends BaseSamzaMetricsToStatsConverter {
    private static final Logger logger = Logger.getLogger(KeyValueStorageMetricsToStatsConverter.class);

    public static final String METRIC_NAME = "org.apache.samza.storage.kv.KeyValueStorageEngineMetrics";
    protected List<String> storeOperations;

    /**
     * ctor
     */
    public KeyValueStorageMetricsToStatsConverter(StatsService statsService, SamzaMetricCollectorMetrics samzaMetricCollectorMetrics) {
        super(statsService ,samzaMetricCollectorMetrics);
        storeOperations = new LinkedList<>();
        Arrays.asList(operations.values()).stream().forEach(operation -> storeOperations.add(operation.value()));
    }

    /**
     * converts metricmessage entries to stats metrics
     *
     * @param metricEntries
     * @param jobName       metric message samza task
     * @param time          metric message update time
     * @param hostname      job hostname
     */
    @Override
    public void convert(Map<String, Object> metricEntries, String jobName, long time, String hostname) {
        super.convert(metricEntries,jobName,time,hostname);
        Map<MultiKey,KeyValueStorageMetrics> updatedMetrics = new HashMap<>();

        for (Map.Entry<String, Object> entry : metricEntries.entrySet()) {
            try {
                String entryKey = entry.getKey();
                String storeName = getStoreName(entryKey, storeOperations);
                String operation = getOperationName(entryKey,storeName);

                MultiKey multiKey = new MultiKey(jobName, storeName);

                long entryValue = entryValueToLong(entry.getValue());

                KeyValueStorageMetrics metrics;

                // if there is no metric for this store, create one
                if (!metricsMap.containsKey(multiKey)) {
                    metrics = new KeyValueStorageMetrics(statsService, jobName, storeName);
                    metricsMap.put(multiKey, metrics);
                }
                metrics = (KeyValueStorageMetrics) metricsMap.get(multiKey);

                if (operation.equals(operations.GETS.value())) {
                    metrics.setQueries(entryValue);
                } else if (operation.equals(operations.RANGES.value())) {
                    metrics.setRangeQueries(entryValue);
                } else if (operation.equals(operations.PUTS.value())) {
                    metrics.setWrites(entryValue);
                } else if (operation.equals(operations.DELETES.value())) {
                    metrics.setDeletes(entryValue);
                } else if (operation.equals(operations.FLUSHES.value())) {
                    metrics.setFlushes(entryValue);
                } else if (operation.equals(operations.MESSAGES_RESTORED.value())) {
                    metrics.setMessagesRestored(entryValue);
                } else if (operation.equals(operations.RESTORED_BYTES.value())) {
                    metrics.setRestoredBytes(entryValue);
                } else if (operation.equals(operations.ALLS.value())) {
                    metrics.setRecordsInStore(entryValue);
                }  else {
                    logger.warn("{} is an unknown operation name",entryKey);
                    samzaMetricCollectorMetrics.entriesConversionFailures++;
                }
                updatedMetrics.put(multiKey, metrics);
                samzaMetricCollectorMetrics.convertedEntries++;

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
        ALLS("alls"),//recordsInStore
        MESSAGES_RESTORED("messages-restored"), //messagesRestored
        RESTORED_BYTES("messages-bytes"); //restoredBytes

        private final String name;

        operations(String s) {
            name = s;
        }

        public String value() {
            return name;
        }
    }

}
