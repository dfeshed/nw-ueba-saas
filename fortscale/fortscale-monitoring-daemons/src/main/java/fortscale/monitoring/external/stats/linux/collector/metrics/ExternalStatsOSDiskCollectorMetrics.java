package fortscale.monitoring.external.stats.linux.collector.metrics;

import fortscale.utils.monitoring.stats.StatsMetricsGroupAttributes;
import fortscale.utils.monitoring.stats.annotations.StatsLongMetricParams;
import fortscale.utils.monitoring.stats.annotations.StatsMetricsGroupParams;

/**
 * Created by galiar on 25/04/2016.
 */
@StatsMetricsGroupParams(name = "LINUX-SYSTEM-DISK-USAGE-COLLECTOR")
public class ExternalStatsOSDiskCollectorMetrics /*extends StatsMetricsGroup*/ { //TODO return the inheritance when the superclass is ready


    public ExternalStatsOSDiskCollectorMetrics(StatsMetricsGroupAttributes attributes) {
        //super(ExternalStatsOSMemoryCollector.class, attributes); //TODO when the inheritance returns, uncomment
    }


    @StatsLongMetricParams(rateSeconds = 1)
    Long readBytes;

    @StatsLongMetricParams(rateSeconds = 1)
    Long writeBytes;

    //regarding the factor param: utilization field in proc file is in millisec. the rate is in seconds. the output is percentage.
    //utilization = (sample1_millisec - sample0_millisec)/time_interval_sec.
    //therefore, we should divide the sample by 1000 (to convert to sec), and then multiply by 100 (to convert to percentage)
    @StatsLongMetricParams(rateSeconds = 1, factor = 100.0/1000)
    Long utilization;


    public Long getReadBytes() {
        return readBytes;
    }

    public void setReadBytes(Long readBytes) {
        this.readBytes = readBytes;
    }

    public Long getUtilization() {
        return utilization;
    }

    public void setUtilization(Long utilization) {
        this.utilization = utilization;
    }

    public Long getWriteBytes() {
        return writeBytes;
    }

    public void setWriteBytes(Long writeBytes) {
        this.writeBytes = writeBytes;
    }


}
