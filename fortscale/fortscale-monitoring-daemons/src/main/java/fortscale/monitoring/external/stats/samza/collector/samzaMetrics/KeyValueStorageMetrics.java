package fortscale.monitoring.external.stats.samza.collector.samzaMetrics;

import fortscale.utils.monitoring.stats.StatsMetricsGroup;
import fortscale.utils.monitoring.stats.StatsMetricsGroupAttributes;
import fortscale.utils.monitoring.stats.StatsService;
import fortscale.utils.monitoring.stats.annotations.StatsDoubleMetricParams;
import fortscale.utils.monitoring.stats.annotations.StatsLongMetricParams;
import fortscale.utils.monitoring.stats.annotations.StatsMetricsGroupParams;

/**
 * key value store stats metrics
 */
@StatsMetricsGroupParams(name = "samza.keyValueStore.storage")
public class KeyValueStorageMetrics extends StatsMetricsGroup {
    @StatsDoubleMetricParams(rateSeconds = 1)
    long queries;
    @StatsDoubleMetricParams(rateSeconds = 1)
    long rangeQueries;
    @StatsDoubleMetricParams(rateSeconds = 1)
    long writes;
    @StatsDoubleMetricParams(rateSeconds = 1)
    long deletes;
    @StatsDoubleMetricParams(rateSeconds = 1)
    long flushes;
    @StatsLongMetricParams
    long recordsInStore;
    @StatsDoubleMetricParams(rateSeconds = 1)
    long messagesRestored;
    @StatsDoubleMetricParams(rateSeconds = 1)
    long restoredBytes;

    /**
     * The ctor, in addition to initializing the class, registers the metrics group to the stats service.
     *
     * @param statsService - The stats service to register to. Typically it is obtained via @Autowired
     *                     of the specific service configuration class. If stats service is unavailable,
     *                     as in most unit tests, pass a null.
     * @param process          - samza job name
     * @param store        - key value store name
     */
    public KeyValueStorageMetrics(StatsService statsService, String process, String store) {
        super(statsService, KeyValueStorageMetrics.class, new StatsMetricsGroupAttributes() {{
            overrideProcessName(process,"streaming");
            addTag("store", store);
            setManualUpdateMode(true);
        }});
    }

    public void setQueries(long queries) {
        this.queries = queries;
    }

    public void setRangeQueries(long rangeQueries) {
        this.rangeQueries = rangeQueries;
    }

    public void setWrites(long writes) {
        this.writes = writes;
    }

    public void setDeletes(long deletes) {
        this.deletes = deletes;
    }

    public void setFlushes(long flushes) {
        this.flushes = flushes;
    }

    public void setRecordsInStore(long recordsInStore) {
        this.recordsInStore = recordsInStore;
    }

    public void setMessagesRestored(long messagesRestored) {
        this.messagesRestored = messagesRestored;
    }

    public void setRestoredBytes(long restoredBytes) {
        this.restoredBytes = restoredBytes;
    }

    public long getQueries() {
        return queries;
    }

    public long getRangeQueries() {
        return rangeQueries;
    }

    public long getWrites() {
        return writes;
    }

    public long getDeletes() {
        return deletes;
    }

    public long getFlushes() {
        return flushes;
    }

    public long getRecordsInStore() {
        return recordsInStore;
    }

    public long getMessagesRestored() {
        return messagesRestored;
    }

    public long getRestoredBytes() {
        return restoredBytes;
    }
}
