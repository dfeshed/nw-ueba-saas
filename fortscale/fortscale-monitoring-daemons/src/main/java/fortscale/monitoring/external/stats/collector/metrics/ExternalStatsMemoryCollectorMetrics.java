package fortscale.monitoring.external.stats.collector.metrics;



import fortscale.services.monitoring.stats.StatsMetricsGroup;
import fortscale.services.monitoring.stats.StatsMetricsGroupAttributes;
import fortscale.services.monitoring.stats.annotations.StatsLongMetricParams;

/**
 * a container class for memory collector metrics
 * inherits from statsMetricsGroup, therefore the class fields that marked with @Stats<Type>MetricParams
 * are being written to fluxDB by Stats service mechanism
 *
 * Created by galiar on 18/04/2016.
 */
public class ExternalStatsMemoryCollectorMetrics extends StatsMetricsGroup {

    ExternalStatsMemoryCollectorMetrics(Class cls, StatsMetricsGroupAttributes attributes) {
        super(cls, attributes);
    }


    @StatsLongMetricParams
    Long totalMemoryMB;

    @StatsLongMetricParams
    Long usedMemoryMB;

    @StatsLongMetricParams
    Long freeMemoryMB;

    @StatsLongMetricParams
    Long realFreeMemoryMB;

    @StatsLongMetricParams
    Long buffersMemoryMB;

    @StatsLongMetricParams
    Long cacheMemoryMB;

    @StatsLongMetricParams
    Long sharedMemoryMB;

    @StatsLongMetricParams
    Long dirtyMemoryMB;

    @StatsLongMetricParams
    Long swapInMemoryMB;

    @StatsLongMetricParams
    Long swapOutMemoryMB;

    @StatsLongMetricParams
    Long bufferInMemoryMB;

    @StatsLongMetricParams
    Long bufferOutMemoryMB;

    public void setTotalMemoryMB(Long totalMemoryMB) {
        this.totalMemoryMB = totalMemoryMB;
    }

    public void setUsedMemoryMB(Long usedMemoryMB) {
        this.usedMemoryMB = usedMemoryMB;
    }

    public void setFreeMemoryMB(Long freeMemoryMB) {
        this.freeMemoryMB = freeMemoryMB;
    }

    public void setRealFreeMemoryMB(Long realFreeMemoryMB) {
        this.realFreeMemoryMB = realFreeMemoryMB;
    }

    public void setBuffersMemoryMB(Long buffersMemoryMB) {
        this.buffersMemoryMB = buffersMemoryMB;
    }

    public void setCacheMemoryMB(Long cacheMemoryMB) {
        this.cacheMemoryMB = cacheMemoryMB;
    }

    public void setSharedMemoryMB(Long sharedMemoryMB) {
        this.sharedMemoryMB = sharedMemoryMB;
    }

    public void setDirtyMemoryMB(Long dirtyMemoryMB) {
        this.dirtyMemoryMB = dirtyMemoryMB;
    }

    public void setSwapInMemoryMB(Long swapInMemoryMB) {
        this.swapInMemoryMB = swapInMemoryMB;
    }

    public void setSwapOutMemoryMB(Long swapOutMemoryMB) {
        this.swapOutMemoryMB = swapOutMemoryMB;
    }

    public void setBufferInMemoryMB(Long bufferInMemoryMB) {
        this.bufferInMemoryMB = bufferInMemoryMB;
    }

    public void setBufferOutMemoryMB(Long bufferOutMemoryMB) {
        this.bufferOutMemoryMB = bufferOutMemoryMB;
    }


}
