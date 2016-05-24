package fortscale.utils.jvm.metrics;

import fortscale.utils.monitoring.stats.StatsMetricsGroup;
import fortscale.utils.monitoring.stats.StatsMetricsGroupAttributes;
import fortscale.utils.monitoring.stats.StatsService;

import java.lang.management.ManagementFactory;
import java.lang.management.OperatingSystemMXBean;
import java.lang.management.ThreadMXBean;

/**
 * Created by cloudera on 5/23/16.
 */
public class JVMMetrics extends StatsMetricsGroup {
    /**
     * The ctor, in addition to initializing the class, registers the metrics group to the stats service.
     *
     * @param statsService                - The stats service to register to. Typically it is obtained via @Autowired
     *                                    of the specific service configuration class. If stats service is unavailable,
     *                                    as in most unit tests, pass a null.
     * @param statsMetricsGroupAttributes - metrics group attributes (e.g. tag list). Might be null.
     */
    public JVMMetrics(StatsService statsService, StatsMetricsGroupAttributes statsMetricsGroupAttributes) {
        super(statsService, JVMMetrics.class, statsMetricsGroupAttributes);
        Runtime r = Runtime.getRuntime();
        r.freeMemory();
//        ThreadMXBean operatingSystemMXBean =ManagementFactory.g.cp.cpu.getCurrentThreadCpuTime().g.getGarbageCollectorMXBeans().get(0).getCollectionCount()getHeapMemoryUsage().getCommitted().getThreadInfo(0)..getGarbageCollectorMXBeans().get(0)..getOperatingSystemMXBean();
//        operatingSystemMXBean.ge
    }

    //threads
//    long newThreads;
//    long runnableThreads;
//    long blockedThreads;
//    long waitingThreads;
//    long terminatedThreads;
//    long timedWaitingThreads;

    //memory
    long nonHeapUsedMemory;
    long nonHeapCommittedMemory;
    long heapUsedMemory;
    long heapCommittedMemory;


    //GC
    long grabageCollectorsTimeUtilization; //rate
    //long garbageCollectors;

    //cputime
//    long cpuTime;
//cpuUsage?

    //process
//    String processArgs;
//    String username;
    long pid;
//    long uptime;
//    String osName;

}
