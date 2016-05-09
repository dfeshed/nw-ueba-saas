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
     * @param instrumentedClass           - The class being instrumented. This is typically the "service" class. It is
     *                                    used for logging and debugging
     * @param statsMetricsGroupAttributes - metrics group attributes (e.g. tag list). Might be null.
     */
    public KeyValueStoreMetrics(StatsService statsService, Class instrumentedClass, StatsMetricsGroupAttributes statsMetricsGroupAttributes) {
        super(statsService, KeyValueStoreMetrics.class, statsMetricsGroupAttributes);
    }

    public void setGets(long gets) {
        this.gets = gets;
    }

    public void setGetAlls(long getAlls) {
        this.getAlls = getAlls;
    }

    public void setRanges(long ranges) {
        this.ranges = ranges;
    }

    public void setAlls(long alls) {
        this.alls = alls;
    }

    public void setPuts(long puts) {
        this.puts = puts;
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

    @StatsLongMetricParams
    long gets;
    @StatsLongMetricParams
    long getAlls;
    @StatsLongMetricParams
    long ranges;
    @StatsLongMetricParams
    long alls;
    @StatsLongMetricParams
    long puts;
    @StatsLongMetricParams
    long deletes;
    @StatsLongMetricParams
    long deleteAlls;
    @StatsLongMetricParams
    long flushes;
    @StatsLongMetricParams
    long bytesWritten;
    @StatsLongMetricParams
    long bytesRead;
}
