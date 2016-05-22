package fortscale.monitoring.external.stats.samza.collector.samzaMetrics;

import fortscale.utils.monitoring.stats.StatsMetricsGroup;
import fortscale.utils.monitoring.stats.StatsMetricsGroupAttributes;
import fortscale.utils.monitoring.stats.StatsService;
import fortscale.utils.monitoring.stats.annotations.StatsLongMetricParams;
import fortscale.utils.monitoring.stats.annotations.StatsMetricsGroupParams;

/**
 * Created by cloudera on 5/8/16.
 */
@StatsMetricsGroupParams(name = "samza.keyvaluestore.storage")
public class KeyValueStorageMetrics extends StatsMetricsGroup {
    @StatsLongMetricParams(rateSeconds = 1)
    long queries;
    @StatsLongMetricParams(rateSeconds = 1)
    long rangeQueries;
    @StatsLongMetricParams(rateSeconds = 1)
    long writes;
    @StatsLongMetricParams(rateSeconds = 1)
    long deletes;
    @StatsLongMetricParams(rateSeconds = 1)
    long flushes;
    @StatsLongMetricParams
    long recordsInStore;
    @StatsLongMetricParams(rateSeconds = 1)
    long messagesRestored;
    @StatsLongMetricParams(rateSeconds = 1)
    long restoredBytes;

    /**
     * The ctor, in addition to initializing the class, registers the metrics group to the stats service.
     *
     * @param statsService - The stats service to register to. Typically it is obtained via @Autowired
     *                     of the specific service configuration class. If stats service is unavailable,
     *                     as in most unit tests, pass a null.
     */
    public KeyValueStorageMetrics(StatsService statsService, String jobName, String storeName) {
        super(statsService, KeyValueStorageMetrics.class, new StatsMetricsGroupAttributes() {{
            addTag("job", jobName);
            addTag("store", storeName);
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

}
