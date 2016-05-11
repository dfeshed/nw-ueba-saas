package fortscale.monitoring.samza.metrics;

import fortscale.utils.monitoring.stats.StatsMetricsGroup;
import fortscale.utils.monitoring.stats.StatsMetricsGroupAttributes;
import fortscale.utils.monitoring.stats.StatsService;
import fortscale.utils.monitoring.stats.annotations.StatsLongMetricParams;
import fortscale.utils.monitoring.stats.annotations.StatsMetricsGroupParams;

/**
 * Created by cloudera on 5/8/16.
 */
@StatsMetricsGroupParams(name = "KeyValueStore.storage")
public class KeyValueStorageMetrics extends StatsMetricsGroup {
    /**
     * The ctor, in addition to initializing the class, registers the metrics group to the stats service.
     *
     * @param statsService                - The stats service to register to. Typically it is obtained via @Autowired
     *                                    of the specific service configuration class. If stats service is unavailable,
     *                                    as in most unit tests, pass a null.
     * @param statsMetricsGroupAttributes - metrics group attributes (e.g. tag list). Might be null.
     */
    public KeyValueStorageMetrics(StatsService statsService, StatsMetricsGroupAttributes statsMetricsGroupAttributes) {
        super(statsService, KeyValueStorageMetrics.class, statsMetricsGroupAttributes);
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

    public void setNumberOfMessagesRestored(long numberOfMessagesRestored) {
        this.numberOfMessagesRestored = numberOfMessagesRestored;
    }

    public void setNumberOfRestoredBytes(long numberOfRestoredBytes) {
        this.numberOfRestoredBytes = numberOfRestoredBytes;
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
    @StatsLongMetricParams
    long numberOfMessagesRestored;
    @StatsLongMetricParams
    long numberOfRestoredBytes;

    public enum StoreOperation {
        GETS("gets"), //numberOfQueries
        RANGES("ranges"),//numberOfRangeQueries
        PUTS("puts"),//numberOfWrites
        DELETES("deletes"),//numberOfDeletes
        FLUSHES("flushes"),//numberOfFlushes
        ALLS("alls"),//numberOfRecordsInStore
        MESSAGES_RESTORED("messages-restored"), //numberOfMessagesRestored
        RESTORED_BYTES("messages-bytes"); //numberOfRestoredBytes

        private final String name;

        private StoreOperation(String s) {
            name = s;
        }

        public String value() {
            return name;
        }
    }

    public static final String METRIC_NAME = "org.apache.samza.storage.kv.KeyValueStorageEngineMetrics";

}
