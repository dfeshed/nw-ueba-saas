package fortscale.monitoring.samza.metrics;

import fortscale.utils.monitoring.stats.StatsMetricsGroup;
import fortscale.utils.monitoring.stats.StatsMetricsGroupAttributes;
import fortscale.utils.monitoring.stats.StatsService;
import fortscale.utils.monitoring.stats.annotations.StatsLongMetricParams;

/**
 * Created by cloudera on 5/8/16.
 */
public class KeyValueStoreMetrics  extends StatsMetricsGroup {
    /**
     * The ctor, in addition to initializing the class, registers the metrics group to the stats service.
     *
     * @param statsService                - The stats service to register to. Typically it is obtained via @Autowired
     *                                    of the specific service configuration class. If stats service is unavailable,
     *                                    as in most unit tests, pass a null.

     * @param statsMetricsGroupAttributes - metrics group attributes (e.g. tag list). Might be null.
     */
    public KeyValueStoreMetrics(StatsService statsService, StatsMetricsGroupAttributes statsMetricsGroupAttributes) {
        super(statsService, KeyValueStoreMetrics.class, statsMetricsGroupAttributes);
    }

    public void setNumberOfQueries(long numberOfQueries) {
        this.numberOfQueries = numberOfQueries;
    }

    public void setNumberOfFullTableScans(long numberOfFullTableScans) {
        this.numberOfFullTableScans = numberOfFullTableScans;
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

    public void setNumberOfDeleteAlls(long deleteAlls) {
        this.numberOfDeleteAlls = deleteAlls;
    }

    public void setNumberOfFlushes(long numberOfFlushes) {
        this.numberOfFlushes = numberOfFlushes;
    }

    public void setNumberOfBytesWritten(long numberOfBytesWritten) {
        this.numberOfBytesWritten = numberOfBytesWritten;
    }

    public void setNumberOfBytesRead(long numberOfBytesRead) {
        this.numberOfBytesRead = numberOfBytesRead;
    }

    @StatsLongMetricParams
    long numberOfQueries;
    @StatsLongMetricParams
    long numberOfFullTableScans;
    @StatsLongMetricParams
    long numberOfRangeQueries;
    @StatsLongMetricParams
    long numberOfWrites;
    @StatsLongMetricParams
    long numberOfDeletes;
    @StatsLongMetricParams
    long numberOfDeleteAlls;
    @StatsLongMetricParams
    long numberOfFlushes;
    @StatsLongMetricParams
    long numberOfBytesWritten;
    @StatsLongMetricParams
    long numberOfBytesRead;

    public enum StoreOperation {
        GETS("gets"),
        GET_ALLS("getAlls"),
        RANGES("ranges"),
        PUTS("puts"),
        DELETES("deletes"),
        DELETE_ALLS("deleteAlls"),
        FLUSHES("flushes"),
        BYTES_WRITTEN("bytes-written"),
        BYTES_READ("bytes-read"),
        ALLS("alls");

        private final String name;
        private StoreOperation(String s)
        {
            name=s;
        }
        public boolean equalsName(String otherName) {
            return otherName != null && name.equals(otherName);
        }
    }

}
