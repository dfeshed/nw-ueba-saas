package fortscale.monitoring.samza.metrics;

import fortscale.utils.monitoring.stats.StatsMetricsGroup;
import fortscale.utils.monitoring.stats.StatsMetricsGroupAttributes;
import fortscale.utils.monitoring.stats.StatsService;
import fortscale.utils.monitoring.stats.annotations.StatsLongMetricParams;

/**
 * Created by cloudera on 5/8/16.
 */
public class KeyValueChangeLogTopicMetrics extends StatsMetricsGroup {
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

    public void setNumberOfQueries(long numberOfQueries) {
        this.numberOfQueries = numberOfQueries;
    }

    public void setNumberOfRangeQueries(long numberOfRangeQueries) {
        this.numberOfRangeQueries = numberOfRangeQueries;
    }

    public void setNumberOfWrites(long numberOfWrites) {
        this.numberOfWrites = numberOfWrites;
    }

    public void setNumberOfDeletes(long numberOfDeletes) {
        this.numberOfDeletes = numberOfDeletes;
    }

    public void setNumberOfFlushes(long numberOfFlushes) {
        this.numberOfFlushes = numberOfFlushes;
    }

    public void setNumberOfRecordsInStore(long numberOfRecordsInStore) {
        this.numberOfRecordsInStore = numberOfRecordsInStore;
    }
    @StatsLongMetricParams
    long numberOfQueries;
    @StatsLongMetricParams
    long numberOfRangeQueries;
    @StatsLongMetricParams
    long numberOfWrites;
    @StatsLongMetricParams
    long numberOfDeletes;
    @StatsLongMetricParams
    long numberOfFlushes;
    @StatsLongMetricParams
    long numberOfRecordsInStore;


    public enum StoreOperation {
        GETS("gets"), //numberOfQueries
        RANGES("ranges"),//numberOfRangeQueries
        PUTS("puts"),//numberOfWrites
        DELETES("deletes"),//numberOfDeletes
        FLUSHES("flushes"),//numberOfFlushes
        ALLS("alls");//numberOfRecordsInStore

        private final String name;
        private StoreOperation(String s)
        {
            name=s;
        }
        public String value(){return name;}
    }

    public static final String METRIC_NAME="org.apache.samza.storage.kv.LoggedStoreMetrics";

}
