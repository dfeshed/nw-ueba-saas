package fortscale.monitoring.external.stats.samza.collector.service.impl.converter;

import fortscale.monitoring.external.stats.samza.collector.samzaMetrics.SamzaContainerMetrics;
import fortscale.monitoring.external.stats.samza.collector.service.stats.SamzaMetricCollectorMetrics;
import fortscale.utils.logging.Logger;
import fortscale.utils.monitoring.stats.StatsService;

import java.util.HashMap;
import java.util.Map;

import static fortscale.monitoring.external.stats.Util.CollectorsUtil.entryValueToLong;

/**
 * converts samza standard metric to stats.
 * key value changelog metric entries example:
 */
public class SamzaContainerToStatsConverter extends BaseSamzaMetricsToStatsConverter {
    private static final Logger logger = Logger.getLogger(SamzaContainerToStatsConverter.class);

    public static final String METRIC_NAME = "org.apache.samza.container.SamzaContainerMetrics";

    /**
     * ctor
     */
    public SamzaContainerToStatsConverter(StatsService statsService, SamzaMetricCollectorMetrics samzaMetricCollectorMetrics) {
        super(statsService, samzaMetricCollectorMetrics);
    }

    /**
     * converts metricmessage entries to stats metrics
     *
     * @param metricEntries metric message stats entries
     * @param jobName       metric message samza task
     * @param time          metric message update time
     * @param hostname      job hostname
     */
    @Override
    public void convert(Map<String, Object> metricEntries, String jobName, long time, String hostname) {
        super.convert(metricEntries, jobName, time, hostname);
        Map<String, SamzaContainerMetrics> updatedMetrics = new HashMap<>();

        for (Map.Entry<String, Object> entry : metricEntries.entrySet()) {
            try {
                String entryKey = entry.getKey();

                double doubleEntryValue = 0;
                long entryValue = 0;
                if (entry.getValue().getClass().isAssignableFrom(Double.class)) {
                    doubleEntryValue = (double) entry.getValue();
                } else {
                    entryValue = entryValueToLong(entry.getValue());
                }
                SamzaContainerMetrics metrics;
                // if there is no metric for this store, create one
                if (!metricsMap.containsKey(jobName)) {
                    metrics = new SamzaContainerMetrics(statsService, jobName);
                    metricsMap.put(jobName, metrics);
                }
                metrics = (SamzaContainerMetrics) metricsMap.get(jobName);

                if (entryKey.equals(operations.CHOOSE_MS.value())) {
                    metrics.setChooseSeconds(doubleEntryValue / 1000);
                } else if (entryKey.equals(operations.COMMIT_MS.value())) {
                    metrics.setCommitSeconds(doubleEntryValue / 1000);
                } else if (entryKey.equals(operations.PROCESS_MS.value())) {
                    metrics.setProcessSeconds(doubleEntryValue / 1000);
                } else if (entryKey.equals(operations.WINDOW_MS.value())) {
                    metrics.setWindowSeconds(doubleEntryValue / 1000);
                } else if (entryKey.equals(operations.COMMITS.value())) {
                    metrics.setCommit(entryValue);
                } else if (entryKey.equals(operations.SENDS.value())) {
                    metrics.setSend(entryValue);
                } else if (entryKey.equals(operations.PROCESSES.value())) {
                    metrics.setProcesses(entryValue);
                } else if (entryKey.equals(operations.WINDOWS.value())) {
                    metrics.setWindow(entryValue);
                } else if (entryKey.equals(operations.ENVELOPES.value())) {
                    metrics.setProcessEnvelopes(entryValue);
                } else if (entryKey.equals(operations.NULL_ENVELOPES.value())) {
                    metrics.setProcessNullEnvelopes(entryValue);
                } else {
                    logger.warn("{} is an unknown operation name", entryKey);
                    samzaMetricCollectorMetrics.entriesConversionFailures++;
                }
                updatedMetrics.put(jobName, metrics);
                samzaMetricCollectorMetrics.convertedEntries++;
            } catch (Exception e) {
                String errMessage = String.format("failed to convert entry %s: %s", entry.getKey(), entry.getValue());
                logger.error(errMessage, e);
            }
        }
        manualUpdateMetricsMap(updatedMetrics, time);
    }


    public enum operations {
        COMMITS("commit-calls"), //commit
        WINDOWS("window-calls"),//window
        PROCESSES("process-calls"),//process
        SENDS("send-calls"),//send
        ENVELOPES("process-envelopes"),//processEnvelopes
        NULL_ENVELOPES("process-null-envelopes"),//processNullEnvelopes
        CHOOSE_MS("choose-ms"), //chooseSeconds
        WINDOW_MS("window-ms"),//windowSeconds
        PROCESS_MS("process-ms"),//processSeconds
        COMMIT_MS("commit-ms"); //commitSeconds
        private final String name;

        operations(String s) {
            name = s;
        }

        public String value() {
            return name;
        }

    }


}
