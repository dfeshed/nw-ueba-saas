package presidio.monitoring.endPoint;


import fortscale.utils.logging.Logger;
import org.springframework.util.StringUtils;
import presidio.monitoring.enums.MetricEnums;
import presidio.monitoring.records.Metric;

import java.lang.management.GarbageCollectorMXBean;
import java.lang.management.ManagementFactory;
import java.lang.management.MemoryUsage;
import java.lang.management.ThreadMXBean;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Set;

import static presidio.monitoring.DefaultPublicMetricsNames.*;

public class PresidioSystemMetricsFactory {

    private static final Logger logger = Logger.getLogger(PresidioSystemMetricsFactory.class);

    private final String MEMORY = "memory";
    private final String SYSTEM = "system";
    private final String THREADS = "threads";

    private MemoryUsage heapMemoryUsage;
    private MemoryUsage nonHeapMemoryUsage;
    private Runtime runtime;
    private List<GarbageCollectorMXBean> garbageCollectorMxBeans;
    private ThreadMXBean threadMxBean;
    private Map<MetricEnums.MetricTagKeysEnum, String> tags;

    public PresidioSystemMetricsFactory(String applicationName) {
        tags = new HashMap<>();
        tags.put(MetricEnums.MetricTagKeysEnum.APPLICATION_NAME, applicationName);
        runtime = Runtime.getRuntime();
        garbageCollectorMxBeans = ManagementFactory.getGarbageCollectorMXBeans();
        threadMxBean = ManagementFactory.getThreadMXBean();
    }

    private Metric createMemoryMetric(String name, long value) {
        Map<MetricEnums.MetricValues, Number> map = new HashMap<>();
        map.put(MetricEnums.MetricValues.COUNT, value);
        tags.put(MetricEnums.MetricTagKeysEnum.UNIT, MEMORY);
        return new Metric(name, map, tags, false);
    }

    private Metric createSystemMetric(String name, long value) {
        Map<MetricEnums.MetricValues, Number> map = new HashMap<>();
        map.put(MetricEnums.MetricValues.COUNT, value);
        tags.put(MetricEnums.MetricTagKeysEnum.UNIT, SYSTEM);
        return new Metric(name, map, tags, false);
    }

    private Metric createThreadsMetric(String name, long value) {
        Map<MetricEnums.MetricValues, Number> map = new HashMap<>();
        map.put(MetricEnums.MetricValues.COUNT, value);
        tags.put(MetricEnums.MetricTagKeysEnum.UNIT, THREADS);
        return new Metric(name, map, tags, false);
    }

    public List<Metric> metrics() {
        heapMemoryUsage = ManagementFactory.getMemoryMXBean().getHeapMemoryUsage();
        nonHeapMemoryUsage = ManagementFactory.getMemoryMXBean().getNonHeapMemoryUsage();
        List<Metric> metricsForExport = new LinkedList<>();
        addMemoryMetrics(metricsForExport);
        addManagementMetrics(metricsForExport);
        addThreadMetrics(metricsForExport);
        return metricsForExport;
    }


    private void addMemoryMetrics(List<Metric> metricsForExport) {
        metricsForExport.add(createMemoryMetric(MEM, runtime.totalMemory() + getTotalNonHeapMemoryIfPossible()));
        metricsForExport.add(createMemoryMetric(MEM_FREE, runtime.freeMemory()));
        metricsForExport.add(createMemoryMetric(HEAP_COMMITTED, heapMemoryUsage.getCommitted()));
        metricsForExport.add(createMemoryMetric(HEAP_INIT, heapMemoryUsage.getInit()));
        metricsForExport.add(createMemoryMetric(HEAP_USED, heapMemoryUsage.getUsed()));
        metricsForExport.add(createMemoryMetric(HEAP, heapMemoryUsage.getMax()));
        metricsForExport.add(createMemoryMetric(NONHEAP_COMMITTED, nonHeapMemoryUsage.getCommitted()));
        metricsForExport.add(createMemoryMetric(NONHEAP_INIT, nonHeapMemoryUsage.getInit()));
        metricsForExport.add(createMemoryMetric(NONHEAP_USED, nonHeapMemoryUsage.getUsed()));
        metricsForExport.add(createMemoryMetric(NONHEAP, nonHeapMemoryUsage.getMax()));
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


    private void addThreadMetrics(List<Metric> metricsForExport) {
        metricsForExport.add(createThreadsMetric(THREADS_PEAK, (long) threadMxBean.getPeakThreadCount()));
        metricsForExport.add(createThreadsMetric(THREADS_DAEMON, (long) threadMxBean.getDaemonThreadCount()));
        metricsForExport.add(createThreadsMetric(THREADS_TOTAL_STARTED, threadMxBean.getTotalStartedThreadCount()));
        metricsForExport.add(createThreadsMetric(THREADS, (long) threadMxBean.getThreadCount()));
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

    public void addTag(MetricEnums.MetricTagKeysEnum tagKey, String tagValue) {
        this.tags.put(tagKey, tagValue);
    }
}
