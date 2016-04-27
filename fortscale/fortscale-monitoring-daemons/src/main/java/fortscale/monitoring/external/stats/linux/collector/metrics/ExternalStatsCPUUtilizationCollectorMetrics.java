package fortscale.monitoring.external.stats.linux.collector.metrics;

import fortscale.utils.monitoring.stats.StatsMetricsGroupAttributes;
import fortscale.utils.monitoring.stats.annotations.StatsLongMetricParams;
import fortscale.utils.monitoring.stats.annotations.StatsMetricsGroupParams;

/**
 * metrics of external stats collector - cpu utilization.
 * the metrics units is USER_HZ (also known as 'jiffies'). assumed 10 millis (the common one)
 * Created by galiar on 26/04/2016.
 */

@StatsMetricsGroupParams(name = "LINUX-SYSTEM-CPU-UTILIZATION-COLLECTOR")
public class ExternalStatsCPUUtilizationCollectorMetrics /*extends StatsMetricsGroup*/ { //TODO return the inheritance when the superclass is ready

    public ExternalStatsCPUUtilizationCollectorMetrics(StatsMetricsGroupAttributes attributes) {
        //super(ExternalStatsOSMemoryCollector.class, attributes); //TODO when the inheritance returns, uncomment
    }


    //regarding the factor param: cpu statistics fields in proc file is in 10 millisec. the rate is in seconds. the output is percentage.
    //rate = (sample1_millisec - sample0_millisec)/time_interval_sec.
    //therefore, we should divide the sample by 1000 (to convert to sec), and then multiply by 10 (to convert to percentage)
    @StatsLongMetricParams(rateSeconds = 1,factor = 10.0/1000)
    Long user;

    @StatsLongMetricParams(rateSeconds = 1,factor = 10.0/1000)
    Long system;

    @StatsLongMetricParams(rateSeconds = 1,factor = 10.0/1000)
    Long nice;

    @StatsLongMetricParams(rateSeconds = 1,factor = 10.0/1000)
    Long idle;

    @StatsLongMetricParams(rateSeconds = 1,factor = 10.0/1000)
    Long wait;

    @StatsLongMetricParams(rateSeconds = 1,factor = 10.0/1000)
    Long hardwareInterrupts;

    @StatsLongMetricParams(rateSeconds = 1,factor = 10.0/1000)
    Long softwareInterrupts;

    @StatsLongMetricParams(rateSeconds = 1,factor = 10.0/1000)
    Long steal;

    public Long getUser() {
        return user;
    }

    public void setUser(Long user) {
        this.user = user;
    }

    public Long getSystem() {
        return system;
    }

    public void setSystem(Long system) {
        this.system = system;
    }

    public Long getNice() {
        return nice;
    }

    public void setNice(Long nice) {
        this.nice = nice;
    }

    public Long getIdle() {
        return idle;
    }

    public void setIdle(Long idle) {
        this.idle = idle;
    }

    public Long getWait() {
        return wait;
    }

    public void setWait(Long wait) {
        this.wait = wait;
    }

    public Long getHardwareInterrupts() {
        return hardwareInterrupts;
    }

    public void setHardwareInterrupts(Long hardwareInterrupts) {
        this.hardwareInterrupts = hardwareInterrupts;
    }

    public Long getSoftwareInterrupts() {
        return softwareInterrupts;
    }

    public void setSoftwareInterrupts(Long softwareInterrupts) {
        this.softwareInterrupts = softwareInterrupts;
    }

    public Long getSteal() {
        return steal;
    }

    public void setSteal(Long steal) {
        this.steal = steal;
    }

}
