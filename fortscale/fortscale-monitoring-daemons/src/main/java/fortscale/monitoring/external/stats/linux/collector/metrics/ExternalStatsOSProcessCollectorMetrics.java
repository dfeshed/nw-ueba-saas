package fortscale.monitoring.external.stats.linux.collector.metrics;

import fortscale.utils.monitoring.stats.StatsMetricsGroupAttributes;
import fortscale.utils.monitoring.stats.annotations.StatsLongMetricParams;
import fortscale.utils.monitoring.stats.annotations.StatsMetricsGroupParams;

/**
 * holds the metircs of external stats os process collector.
 * Created by galiar on 27/04/2016.
 */
@StatsMetricsGroupParams(name = "LINUX-SYSTEM-OS-PROCESS-COLLECTOR")
public class ExternalStatsOSProcessCollectorMetrics /*extends StatsMetricsGroup*/ { //TODO return the inheritance when the superclass is ready

    public ExternalStatsOSProcessCollectorMetrics(StatsMetricsGroupAttributes attributes) {
        //super(ExternalStatsOSMemoryCollector.class, attributes); //TODO when the inheritance returns, uncomment
    }
    @StatsLongMetricParams
    Long pid;

    @StatsLongMetricParams
    Long memoryRSS;

    @StatsLongMetricParams
    Long memoryVSize;

    //regarding the factor param: cpu statistics fields in proc file is in 10 millisec. the rate is in seconds. the output is percentage.
    //rate = (sample1_millisec - sample0_millisec)/time_interval_sec.
    //therefore, we should divide the sample by 1000 (to convert to sec), and then multiply by 10 (to convert to percentage)
    @StatsLongMetricParams(rateSeconds = 1,factor = 10.0/1000)
    Long kernelTime;

    @StatsLongMetricParams(rateSeconds = 1,factor = 10.0/1000)
    Long userTime;

    @StatsLongMetricParams(rateSeconds = 1,factor = 10.0/1000)
    Long childrenWaitTime;

    @StatsLongMetricParams
    Long numThreads;

    String processCommandLine;;

    public Long getPid() {
        return pid;
    }

    public void setPid(Long pid) {
        this.pid = pid;
    }

    public Long getMemoryRSS() {
        return memoryRSS;
    }

    public void setMemoryRSS(Long memoryRSS) {
        this.memoryRSS = memoryRSS;
    }

    public Long getMemoryVSize() {
        return memoryVSize;
    }

    public void setMemoryVSize(Long memoryVSize) {
        this.memoryVSize = memoryVSize;
    }

    public Long getKernelTime() {
        return kernelTime;
    }

    public void setKernelTime(Long kernelTime) {
        this.kernelTime = kernelTime;
    }

    public Long getUserTime() {
        return userTime;
    }

    public void setUserTime(Long userTime) {
        this.userTime = userTime;
    }

    public Long getNumThreads() {
        return numThreads;
    }

    public void setNumThreads(Long numThreads) {
        this.numThreads = numThreads;
    }

    public Long getChildrenWaitTime() {
        return childrenWaitTime;
    }

    public void setChildrenWaitTime(Long childrenWaitTime) {
        this.childrenWaitTime = childrenWaitTime;
    }

    public String getProcessCommandLine() {
        return processCommandLine;
    }

    public void setProcessCommandLine(String processCommandLine) {
        this.processCommandLine = processCommandLine;
    }
}
