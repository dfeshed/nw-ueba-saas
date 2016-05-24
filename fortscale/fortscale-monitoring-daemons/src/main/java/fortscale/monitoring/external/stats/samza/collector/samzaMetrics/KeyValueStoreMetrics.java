package fortscale.monitoring.external.stats.samza.collector.samzaMetrics;

import fortscale.utils.monitoring.stats.StatsMetricsGroup;
import fortscale.utils.monitoring.stats.StatsMetricsGroupAttributes;
import fortscale.utils.monitoring.stats.StatsService;
import fortscale.utils.monitoring.stats.annotations.StatsLongMetricParams;
import fortscale.utils.monitoring.stats.annotations.StatsMetricsGroupParams;


@StatsMetricsGroupParams(name = "samza.keyValueStore.store")
public class KeyValueStoreMetrics extends StatsMetricsGroup {
    @StatsLongMetricParams(rateSeconds = 1)
    long queries;
    @StatsLongMetricParams(rateSeconds = 1)
    long fullTableScans;
    @StatsLongMetricParams(rateSeconds = 1)
    long rangeQueries;
    @StatsLongMetricParams(rateSeconds = 1)
    long writes;
    @StatsLongMetricParams(rateSeconds = 1)
    long deletes;
    @StatsLongMetricParams(rateSeconds = 1)
    long deleteAlls;
    @StatsLongMetricParams(rateSeconds = 1)
    long flushes;
    @StatsLongMetricParams(rateSeconds = 1)
    long bytesWritten;
    @StatsLongMetricParams(rateSeconds = 1)
    long bytesRead;
    @StatsLongMetricParams
    long recordsInStore;

    /**
     * The ctor, in addition to initializing the class, registers the metrics group to the stats service.
     *
     * @param statsService - The stats service to register to. Typically it is obtained via @Autowired
     *                     of the specific service configuration class. If stats service is unavailable,
     *                     as in most unit tests, pass a null.
     * @param job          - samza job name
     * @param store        - key value store name
     */
    public KeyValueStoreMetrics(StatsService statsService, String job, String store) {
        super(statsService, KeyValueStoreMetrics.class, new StatsMetricsGroupAttributes() {{
            addTag("job", job);
            addTag("store", store);
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
