package fortscale.monitoring.external.stats.samza.collector.samzaMetrics;

import fortscale.utils.monitoring.stats.StatsMetricsGroup;
import fortscale.utils.monitoring.stats.StatsMetricsGroupAttributes;
import fortscale.utils.monitoring.stats.StatsService;
import fortscale.utils.monitoring.stats.annotations.StatsDoubleMetricParams;
import fortscale.utils.monitoring.stats.annotations.StatsMetricsGroupParams;


@StatsMetricsGroupParams(name = "samza.keyValueStore.store")
public class KeyValueStoreMetrics extends StatsMetricsGroup {
    @StatsDoubleMetricParams(rateSeconds = 1)
    long queries;
    @StatsDoubleMetricParams(rateSeconds = 1)
    long fullTableScans;
    @StatsDoubleMetricParams(rateSeconds = 1)
    long rangeQueries;
    @StatsDoubleMetricParams(rateSeconds = 1)
    long writes;
    @StatsDoubleMetricParams(rateSeconds = 1)
    long deletes;
    @StatsDoubleMetricParams(rateSeconds = 1)
    long deleteAlls;
    @StatsDoubleMetricParams(rateSeconds = 1)
    long flushes;
    @StatsDoubleMetricParams(rateSeconds = 1)
    long bytesWritten;
    @StatsDoubleMetricParams(rateSeconds = 1)
    long bytesRead;
    @StatsDoubleMetricParams
    long recordsInStore;

    /**
     * The ctor, in addition to initializing the class, registers the metrics group to the stats service.
     *
     * @param statsService - The stats service to register to. Typically it is obtained via @Autowired
     *                     of the specific service configuration class. If stats service is unavailable,
     *                     as in most unit tests, pass a null.
     * @param process      - samza job name
     * @param store        - key value store name
     */
    public KeyValueStoreMetrics(StatsService statsService, String process, String store) {
        super(statsService, KeyValueStoreMetrics.class, new StatsMetricsGroupAttributes() {{
            overrideProcessName(process, "streaming");
            addTag("store", store);
            setManualUpdateMode(true);
        }});
    }

    public void setQueries(long queries) {
        this.queries = queries;
    }

    public void setFullTableScans(long fullTableScans) {
        this.fullTableScans = fullTableScans;
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

    public void setDeleteAlls(long deleteAlls) {
        this.deleteAlls = deleteAlls;
    }

    public void setFlushes(long flushes) {
        this.flushes = flushes;
    }

    public void setBytesWritten(long bytesWritten) {
        this.bytesWritten = bytesWritten;
    }

    public void setBytesRead(long bytesRead) {
        this.bytesRead = bytesRead;
    }

    public void setRecordsInStore(long recordsInStore) {
        this.recordsInStore = recordsInStore;
    }

    public long getQueries() {
        return queries;
    }

    public long getFullTableScans() {
        return fullTableScans;
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

    public long getDeleteAlls() {
        return deleteAlls;
    }

    public long getFlushes() {
        return flushes;
    }

    public long getBytesWritten() {
        return bytesWritten;
    }

    public long getBytesRead() {
        return bytesRead;
    }

    public long getRecordsInStore() {
        return recordsInStore;
    }
}
