package fortscale.monitoring.external.stats.samza.collector.samzaMetrics;

import fortscale.utils.monitoring.stats.StatsMetricsGroup;
import fortscale.utils.monitoring.stats.StatsMetricsGroupAttributes;
import fortscale.utils.monitoring.stats.StatsService;
import fortscale.utils.monitoring.stats.annotations.StatsLongMetricParams;
import fortscale.utils.monitoring.stats.annotations.StatsMetricsGroupParams;


@StatsMetricsGroupParams(name = "samza.keyvaluestore.store")
public class KeyValueStoreMetrics  extends StatsMetricsGroup {
    @StatsLongMetricParams (rateSeconds = 1)
    long queries;
    @StatsLongMetricParams (rateSeconds = 1)
    long fullTableScans;
    @StatsLongMetricParams (rateSeconds = 1)
    long rangeQueries;
    @StatsLongMetricParams (rateSeconds = 1)
    long writes;
    @StatsLongMetricParams (rateSeconds = 1)
    long deletes;
    @StatsLongMetricParams (rateSeconds = 1)
    long deleteAlls;
    @StatsLongMetricParams (rateSeconds = 1)
    long flushes;
    @StatsLongMetricParams (rateSeconds = 1)
    long bytesWritten;
    @StatsLongMetricParams (rateSeconds = 1)
    long bytesRead;
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
    public KeyValueStoreMetrics(StatsService statsService, StatsMetricsGroupAttributes statsMetricsGroupAttributes) {
        super(statsService, KeyValueStoreMetrics.class, statsMetricsGroupAttributes);
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
        public String value(){return name;}

        public boolean equalsName(String otherName) {
            return otherName != null && name.equals(otherName);
        }
    }
    public static final String METRIC_NAME = "org.apache.samza.storage.kv.KeyValueStoreMetrics";

}
