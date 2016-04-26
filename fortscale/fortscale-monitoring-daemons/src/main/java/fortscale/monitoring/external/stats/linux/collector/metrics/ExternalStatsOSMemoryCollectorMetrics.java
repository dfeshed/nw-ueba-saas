package fortscale.monitoring.external.stats.linux.collector.metrics;



import fortscale.utils.monitoring.stats.StatsMetricsGroupAttributes;
import fortscale.utils.monitoring.stats.annotations.StatsLongMetricParams;
import fortscale.utils.monitoring.stats.annotations.StatsMetricsGroupParams;

/**
 * a container class for memory collector metrics
 * inherits from statsMetricsGroup, therefore the class fields that marked with @Stats<Type>MetricParams
 * are being written to fluxDB by Stats service mechanism
 *
 * Created by galiar on 18/04/2016.
 */
@StatsMetricsGroupParams(name = "EXTERNAL-STATS-MEMORY-COLLECTOR")
public class ExternalStatsOSMemoryCollectorMetrics /*extends StatsMetricsGroup*/ { //TODO return the inheritance when the superclass is ready

    public ExternalStatsOSMemoryCollectorMetrics(StatsMetricsGroupAttributes attributes) {
        //super(ExternalStatsOSMemoryCollector.class, attributes); //TODO when the inheritance returns, uncomment
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

    @StatsLongMetricParams(rateSeconds = 1)
    Long swapInMemoryMB;

    @StatsLongMetricParams(rateSeconds = 1)
    Long swapOutMemoryMB;

    @StatsLongMetricParams(rateSeconds = 1)
    Long bufferInMemoryMB;

    @StatsLongMetricParams(rateSeconds = 1)
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
    public Long getTotalMemoryMB() {
        return totalMemoryMB;
    }

    public Long getUsedMemoryMB() {
        return usedMemoryMB;
    }

    public Long getFreeMemoryMB() {
        return freeMemoryMB;
    }

    public Long getRealFreeMemoryMB() {
        return realFreeMemoryMB;
    }

    public Long getBuffersMemoryMB() {
        return buffersMemoryMB;
    }

    public Long getCacheMemoryMB() {
        return cacheMemoryMB;
    }

    public Long getSharedMemoryMB() {
        return sharedMemoryMB;
    }

    public Long getDirtyMemoryMB() {
        return dirtyMemoryMB;
    }

    public Long getSwapInMemoryMB() {
        return swapInMemoryMB;
    }

    public Long getSwapOutMemoryMB() {
        return swapOutMemoryMB;
    }

    public Long getBufferInMemoryMB() {
        return bufferInMemoryMB;
    }

    public Long getBufferOutMemoryMB() {
        return bufferOutMemoryMB;
    }


}
