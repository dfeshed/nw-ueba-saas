package presidio.monitoring.endPoint;


import com.google.common.collect.ImmutableSet;
import fortscale.utils.logging.Logger;
import presidio.monitoring.DefaultPublicMetricsNames;
import presidio.monitoring.records.Metric;
import presidio.monitoring.sdk.api.services.enums.MetricEnums;

import java.lang.management.*;
import java.util.*;

public class PresidioSystemMetricsFactory {

    private static final Logger logger = Logger.getLogger(PresidioSystemMetricsFactory.class);

    private static final Set<String> youngGenCollectors = ImmutableSet.of("Copy","PS Scavenge","ParNew","G1 Young Generation");
    private static final Set<String> oldGenCollectors = ImmutableSet.of( "MarkSweepCompact ","PS MarkSweep","ConcurrentMarkSweep","G1 Mixed Generation");

    private List<GarbageCollectorMXBean> garbageCollectorMXBeans;
    private ThreadMXBean threadMxBean;
    private MemoryMXBean memoryMXBean;
    private com.sun.management.OperatingSystemMXBean operatingSystemMXBean;

    private String applicationName;

    public PresidioSystemMetricsFactory(String applicationName) {
        this.applicationName = applicationName;
        garbageCollectorMXBeans = ManagementFactory.getGarbageCollectorMXBeans();
        threadMxBean = ManagementFactory.getThreadMXBean();
        memoryMXBean = ManagementFactory.getMemoryMXBean();
        operatingSystemMXBean = (com.sun.management.OperatingSystemMXBean)ManagementFactory.getOperatingSystemMXBean();
    }

    public List<Metric> metrics() {
        List<Metric> metricsForExport = new LinkedList<>();
        metricsForExport.add(createMemoryMetric(DefaultPublicMetricsNames.SYSTEM_MEMORY));
        metricsForExport.add(createThreadsMetric(DefaultPublicMetricsNames.SYSTEM_THREADS));
        metricsForExport.add(createPerformanceMetric(DefaultPublicMetricsNames.SYSTEM_PERFORMANCE));
        metricsForExport.add(createGarbageCollectionMetric(DefaultPublicMetricsNames.SYSTEM_GC));
        metricsForExport.add(createCPUMetric(DefaultPublicMetricsNames.SYSTEM_CPU));
        return metricsForExport;
    }

    /**
     * Memory metrics
     */
    private Metric createMemoryMetric(String name) {
        Map<MetricEnums.MetricTagKeysEnum, String> tags = createTagsForMetric(MetricEnums.MetricUnitType.B);

        MemoryUsage heapMemoryUsage = ManagementFactory.getMemoryMXBean().getHeapMemoryUsage();
        MemoryUsage nonHeapMemoryUsage = ManagementFactory.getMemoryMXBean().getNonHeapMemoryUsage();

        Map<MetricEnums.MetricValues, Number> map = new HashMap<>();
        map.put(MetricEnums.MetricValues.SYSTEM_HEAP_COMMITTED, heapMemoryUsage.getCommitted());
        map.put(MetricEnums.MetricValues.SYSTEM_HEAP_INIT, heapMemoryUsage.getInit());
        map.put(MetricEnums.MetricValues.SYSTEM_HEAP_USED, heapMemoryUsage.getUsed());
        map.put(MetricEnums.MetricValues.SYSTEM_HEAP, heapMemoryUsage.getMax());
        map.put(MetricEnums.MetricValues.SYSTEM_NONHEAP_COMMITTED, nonHeapMemoryUsage.getCommitted());
        map.put(MetricEnums.MetricValues.SYSTEM_NONHEAP_INIT, nonHeapMemoryUsage.getInit());
        map.put(MetricEnums.MetricValues.SYSTEM_NONHEAP_USED, nonHeapMemoryUsage.getUsed());
        map.put(MetricEnums.MetricValues.SYSTEM_NONHEAP, nonHeapMemoryUsage.getMax());

        return new Metric.MetricBuilder().setMetricName(name).
                setMetricMultipleValues(map).
                setMetricTags(tags).
                build();
    }

    /**
     * Threads metrics
     */
    private Metric createThreadsMetric(String name) {
        Map<MetricEnums.MetricTagKeysEnum, String> tags = createTagsForMetric(MetricEnums.MetricUnitType.NUMBER);

        Map<MetricEnums.MetricValues, Number> map = new HashMap<>();
        map.put(MetricEnums.MetricValues.SYSTEM_THREADS_PEAK, (long) threadMxBean.getPeakThreadCount());
        map.put(MetricEnums.MetricValues.SYSTEM_THREADS_DAEMON, (long) threadMxBean.getDaemonThreadCount());
        map.put(MetricEnums.MetricValues.SYSTEM_THREADS_TOTAL_STARTED, threadMxBean.getTotalStartedThreadCount());
        map.put(MetricEnums.MetricValues.SYSTEM_THREADS, (long) threadMxBean.getThreadCount());

        return new Metric.MetricBuilder().setMetricName(name).
                setMetricMultipleValues(map).
                setMetricTags(tags).
                build();
    }

    /**
     * Garbage collection metrics
     */
    private Metric createGarbageCollectionMetric(String name) {
        Map<MetricEnums.MetricTagKeysEnum, String> tags = createTagsForMetric(MetricEnums.MetricUnitType.NUMBER);

        Map<MetricEnums.MetricValues, Number> map = new HashMap<>();

        for (GarbageCollectorMXBean garbageCollectorMXBean : garbageCollectorMXBeans) {

            if (youngGenCollectors.contains(garbageCollectorMXBean.getName())) {
                tags.put(MetricEnums.MetricTagKeysEnum.GC_YOUNG_COLLECTOR, garbageCollectorMXBean.getName());
                map.put(MetricEnums.MetricValues.SYSTEM_GC_YOUNG_COUNT, (long) garbageCollectorMXBean.getCollectionCount());
                map.put(MetricEnums.MetricValues.SYSTEM_GC_YOUNG_TIME, (long) garbageCollectorMXBean.getCollectionTime());
            }
            else if (oldGenCollectors.contains(garbageCollectorMXBean.getName())) {
                tags.put(MetricEnums.MetricTagKeysEnum.GC_OLD_COLLECTOR, garbageCollectorMXBean.getName());
                map.put(MetricEnums.MetricValues.SYSTEM_GC_OLD_COUNT, (long) garbageCollectorMXBean.getCollectionCount());
                map.put(MetricEnums.MetricValues.SYSTEM_GC_OLD_TIME, (long) garbageCollectorMXBean.getCollectionTime());
            }
        }

        return new Metric.MetricBuilder().setMetricName(name).
                setMetricMultipleValues(map).
                setMetricTags(tags).
                build();
    }

    /**
     * Performance metrics
     */
    private Metric createPerformanceMetric(String name) {
        Map<MetricEnums.MetricTagKeysEnum, String> tags = createTagsForMetric(MetricEnums.MetricUnitType.MILLI_SECOND);
        return new Metric.MetricBuilder().setMetricName(name).
                setMetricValue(ManagementFactory.getRuntimeMXBean().getUptime()).
                setMetricTags(tags).
                setMetricReportOnce(true).
                build();

    }

    /**
     * CPU metrics
     */
    private Metric createCPUMetric(String name) {
        Map<MetricEnums.MetricTagKeysEnum, String> tags = createTagsForMetric(MetricEnums.MetricUnitType.NUMBER);

        Map<MetricEnums.MetricValues, Number> map = new HashMap<>();
        map.put(MetricEnums.MetricValues.SYSTEM_CPU_LOAD, (long) operatingSystemMXBean.getProcessCpuLoad());
        map.put(MetricEnums.MetricValues.SYSTEM_CPU_TIME, (long) operatingSystemMXBean.getProcessCpuTime());

        return new Metric.MetricBuilder().setMetricName(name).
                setMetricMultipleValues(map).
                setMetricTags(tags).
                build();

    }


    private Map<MetricEnums.MetricTagKeysEnum, String> createTagsForMetric(MetricEnums.MetricUnitType metricUnitType) {
        Map<MetricEnums.MetricTagKeysEnum, String> tags = new HashMap<>();
        tags.put(MetricEnums.MetricTagKeysEnum.APPLICATION_NAME, applicationName);
        tags.put(MetricEnums.MetricTagKeysEnum.UNIT, metricUnitType.toString());
        tags.put(MetricEnums.MetricTagKeysEnum.IS_SYSTEM_METRIC, Boolean.TRUE.toString());
        return tags;
    }


    public void setApplicationName(String applicationName) {
        this.applicationName = applicationName;
    }
}
