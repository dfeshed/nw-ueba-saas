package presidio.monitoring.endPoint;


import fortscale.utils.logging.Logger;
import org.springframework.util.StringUtils;
import presidio.monitoring.records.Metric;

import java.lang.management.GarbageCollectorMXBean;
import java.lang.management.ManagementFactory;
import java.lang.management.MemoryUsage;
import java.lang.management.ThreadMXBean;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Set;

import static presidio.monitoring.DefaultPublicMetricsNames.*;

public class PresidioSystemMetrics {

    private static final Logger logger = Logger.getLogger(PresidioSystemMetrics.class);

    private MemoryUsage heapMemoryUsage;
    private MemoryUsage nonHeapMemoryUsage;
    private Runtime runtime;
    private List<GarbageCollectorMXBean> garbageCollectorMxBeans;
    private ThreadMXBean threadMxBean;
    private Set<String> tags;

    public PresidioSystemMetrics(String applicationName) {
        tags = new HashSet<>();
        tags.add(applicationName);
        runtime = Runtime.getRuntime();
        garbageCollectorMxBeans = ManagementFactory.getGarbageCollectorMXBeans();
        threadMxBean = ManagementFactory.getThreadMXBean();
    }

    private Metric createMemoryMetric(String name, long value) {
        return new Metric(name, value, tags, "memory", false);
    }

    private Metric createSystemMetric(String name, long value) {
        return new Metric(name, value, tags, "system", false);
    }

    private Metric createThreadsMetric(String name, long value) {
        return new Metric(name, value, tags, "threads", false);
    }

    public List<Metric> metrics() {
        heapMemoryUsage = ManagementFactory.getMemoryMXBean().getHeapMemoryUsage();
        nonHeapMemoryUsage = ManagementFactory.getMemoryMXBean().getNonHeapMemoryUsage();
        List<Metric> result2 = new LinkedList<>();
        addMemoryMetrics(result2);
        addManagementMetrics(result2);
        addThreadMetrics(result2);
        return result2;
    }


    private void addMemoryMetrics(List<Metric> result2) {
        result2.add(createMemoryMetric(MEM, runtime.totalMemory() + getTotalNonHeapMemoryIfPossible()));
        result2.add(createMemoryMetric(MEM_FREE, runtime.freeMemory()));
        result2.add(createMemoryMetric(HEAP_COMMITTED, heapMemoryUsage.getCommitted()));
        result2.add(createMemoryMetric(HEAP_INIT, heapMemoryUsage.getInit()));
        result2.add(createMemoryMetric(HEAP_USED, heapMemoryUsage.getUsed()));
        result2.add(createMemoryMetric(HEAP, heapMemoryUsage.getMax()));
        result2.add(createMemoryMetric(NONHEAP_COMMITTED, nonHeapMemoryUsage.getCommitted()));
        result2.add(createMemoryMetric(NONHEAP_INIT, nonHeapMemoryUsage.getInit()));
        result2.add(createMemoryMetric(NONHEAP_USED, nonHeapMemoryUsage.getUsed()));
        result2.add(createMemoryMetric(NONHEAP, nonHeapMemoryUsage.getMax()));
    }

    private void addManagementMetrics(List<Metric> result2) {
        try {
            result2.add(createSystemMetric(UPTIME, ManagementFactory.getRuntimeMXBean().getUptime()));
            result2.add(createSystemMetric(SYSTEMLOAD_AVERAGE, ((Double) ManagementFactory.getOperatingSystemMXBean().getSystemLoadAverage()).longValue()));
            for (GarbageCollectorMXBean garbageCollectorMXBean : garbageCollectorMxBeans) {
                String name = beautifyGcName(garbageCollectorMXBean.getName());
                result2.add(createSystemMetric("gc." + name + ".count", garbageCollectorMXBean.getCollectionCount()));
                result2.add(createSystemMetric("gc." + name + ".time", garbageCollectorMXBean.getCollectionTime()));
            }
            result2.add(createSystemMetric(PROCESSORS, runtime.availableProcessors()));
        } catch (Exception ex) {
            logger.info("Error when trying to collect defoult metrics.", ex);
        }
    }


    private void addThreadMetrics(List<Metric> result2) {
        result2.add(createThreadsMetric(THREADS_PEAK, (long) threadMxBean.getPeakThreadCount()));
        result2.add(createThreadsMetric(THREADS_DAEMON, (long) threadMxBean.getDaemonThreadCount()));
        result2.add(createThreadsMetric(THREADS_TOTAL_STARTED, threadMxBean.getTotalStartedThreadCount()));
        result2.add(createThreadsMetric(THREADS, (long) threadMxBean.getThreadCount()));
    }


    /**
     * Turn GC names like 'PS Scavenge' or 'PS MarkSweep' into something that is more
     * metrics friendly.
     *
     * @param name the source name
     * @return a metric friendly name
     */
    private String beautifyGcName(String name) {
        return StringUtils.replace(name, " ", ".").toLowerCase();
    }

    private long getTotalNonHeapMemoryIfPossible() {
        try {
            return ManagementFactory.getMemoryMXBean().getNonHeapMemoryUsage().getUsed();
        } catch (Throwable ex) {
            logger.info("Error when trying to collect defoult metrics.", ex);
            return 0;
        }
    }

    public void addTag(String tag) {
        this.tags.add(tag);
    }
}
