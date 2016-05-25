package fortscale.utils.process.metrics.jvm.stats;

import fortscale.utils.monitoring.stats.StatsMetricsGroup;
import fortscale.utils.monitoring.stats.StatsMetricsGroupAttributes;
import fortscale.utils.monitoring.stats.StatsService;
import fortscale.utils.monitoring.stats.annotations.StatsLongMetricParams;
import fortscale.utils.monitoring.stats.annotations.StatsMetricsGroupParams;

@StatsMetricsGroupParams(name = "process.jvm")
public class JVMMetrics extends StatsMetricsGroup {

    // memory
    @StatsLongMetricParams
    public long nonHeapUsedMemory;
    @StatsLongMetricParams
    public long nonHeapCommittedMemory;
    @StatsLongMetricParams
    public long heapUsedMemory;
    @StatsLongMetricParams
    public long heapCommittedMemory;
    @StatsLongMetricParams
    public long heapInitMemory;
    @StatsLongMetricParams
    public long heapMaxMemory;
    @StatsLongMetricParams
    public long nonHeapInitMemory;
    @StatsLongMetricParams
    public long nonHeapMaxMemory;

    //GC
    @StatsLongMetricParams(rateSeconds = 1)
    public long garageCollectorsTimeUtilization;

    /**
     * The ctor, in addition to initializing the class, registers the metrics group to the stats service.
     *
     * @param statsService - The stats service to register to. Typically it is obtained via @Autowired
     *                     of the specific service configuration class. If stats service is unavailable,
     *                     as in most unit tests, pass a null.
     * @param pid          - The process id
     */
    public JVMMetrics(StatsService statsService, String pid) {
        super(statsService, JVMMetrics.class, new StatsMetricsGroupAttributes() {{
            addTag("pid", pid);
            setManualUpdateMode(true);
        }});
    }
}
