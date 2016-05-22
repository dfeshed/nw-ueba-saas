package fortscale.monitoring.external.stats.samza.collector.samzaMetrics;

import fortscale.utils.monitoring.stats.StatsMetricsGroup;
import fortscale.utils.monitoring.stats.StatsMetricsGroupAttributes;
import fortscale.utils.monitoring.stats.StatsService;
import fortscale.utils.monitoring.stats.annotations.StatsLongMetricParams;
import fortscale.utils.monitoring.stats.annotations.StatsMetricsGroupParams;

/**
 * Created by cloudera on 5/8/16.
 */
@StatsMetricsGroupParams(name = "samza.keyvaluestore.changelogtopic")
public class KeyValueChangeLogTopicMetrics extends StatsMetricsGroup {
    public static final String METRIC_NAME="org.apache.samza.storage.kv.LoggedStoreMetrics";

    @StatsLongMetricParams (rateSeconds = 1)
    long queries;
    @StatsLongMetricParams (rateSeconds = 1)
    long rangeQueries;
    @StatsLongMetricParams (rateSeconds = 1)
    long writes;
    @StatsLongMetricParams (rateSeconds = 1)
    long deletes;
    @StatsLongMetricParams (rateSeconds = 1)
    long flushes;
    @StatsLongMetricParams
    long recordsInStore;

    /**
     * The ctor, in addition to initializing the class, registers the metrics group to the stats service.
     *
     * @param statsService                - The stats service to register to. Typically it is obtained via @Autowired
     *                                    of the specific service configuration class. If stats service is unavailable,
     *                                    as in most unit tests, pass a null.

     * @param statsMetricsGroupAttributes - metrics group attributes (e.g. tag list). Might be null.
     */
    public KeyValueChangeLogTopicMetrics(StatsService statsService, StatsMetricsGroupAttributes statsMetricsGroupAttributes) {
        super(statsService, KeyValueChangeLogTopicMetrics.class, statsMetricsGroupAttributes);
    }

    public KeyValueChangeLogTopicMetrics(StatsService statsService, String jobName, String storeName) {
        super();
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

    public enum StoreOperation {
        GETS("gets"), //queries
        RANGES("ranges"),//rangeQueries
        PUTS("puts"),//writes
        DELETES("deletes"),//deletes
        FLUSHES("flushes"),//flushes
        ALLS("alls");//recordsInStore

        private final String name;
        private StoreOperation(String s)
        {
            name=s;
        }
        public String value(){return name;}
    }


}
