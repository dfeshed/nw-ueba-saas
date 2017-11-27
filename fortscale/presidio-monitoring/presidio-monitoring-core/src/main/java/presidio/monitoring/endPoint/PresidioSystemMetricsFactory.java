package presidio.monitoring.endPoint;


import fortscale.utils.logging.Logger;
import org.springframework.util.StringUtils;
import presidio.monitoring.records.Metric;
import presidio.monitoring.sdk.api.services.enums.MetricEnums;

import java.lang.management.GarbageCollectorMXBean;
import java.lang.management.ManagementFactory;
import java.lang.management.MemoryUsage;
import java.lang.management.ThreadMXBean;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

import static presidio.monitoring.DefaultPublicMetricsNames.*;

public class PresidioSystemMetricsFactory {

    private static final Logger logger = Logger.getLogger(PresidioSystemMetricsFactory.class);

    private MemoryUsage heapMemoryUsage;
    private MemoryUsage nonHeapMemoryUsage;
    private Runtime runtime;
    private List<GarbageCollectorMXBean> garbageCollectorMxBeans;
    private ThreadMXBean threadMxBean;
    private String applicationName;

    public PresidioSystemMetricsFactory(String applicationName) {
        this.applicationName = applicationName;
        runtime = Runtime.getRuntime();
        garbageCollectorMxBeans = ManagementFactory.getGarbageCollectorMXBeans();
        threadMxBean = ManagementFactory.getThreadMXBean();
    }

    private Metric createMemoryMetric(String name, long value, MetricEnums.MetricUnitType metricUnitType) {
        Map<MetricEnums.MetricTagKeysEnum, String> tags = createTagsForMetric(metricUnitType);
        Map<MetricEnums.MetricValues, Number> map = new HashMap<>();
        map.put(MetricEnums.MetricValues.DEFAULT_METRIC_VALUE, value);
        return new Metric.MetricBuilder().setMetricName(name).
                setMetricMultipleValues(map).
                setMetricTags(tags).
                build();
    }

    private Metric createSystemMetric(String name, long value, MetricEnums.MetricUnitType metricUnitType) {
        Map<MetricEnums.MetricTagKeysEnum, String> tags = createTagsForMetric(metricUnitType);
        Map<MetricEnums.MetricValues, Number> map = new HashMap<>();
        map.put(MetricEnums.MetricValues.DEFAULT_METRIC_VALUE, value);
        return new Metric.MetricBuilder().setMetricName(name).
                setMetricMultipleValues(map).
                setMetricTags(tags).
                build();
    }

    private Metric createThreadsMetric(String name, long value, MetricEnums.MetricUnitType metricUnitType) {
        Map<MetricEnums.MetricTagKeysEnum, String> tags = createTagsForMetric(metricUnitType);
        Map<MetricEnums.MetricValues, Number> map = new HashMap<>();
        map.put(MetricEnums.MetricValues.DEFAULT_METRIC_VALUE, value);
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
        metricsForExport.add(createMemoryMetric(MEM, runtime.totalMemory() + getTotalNonHeapMemoryIfPossible(), MetricEnums.MetricUnitType.B));
        metricsForExport.add(createMemoryMetric(MEM_FREE, runtime.freeMemory(), MetricEnums.MetricUnitType.B));
        metricsForExport.add(createMemoryMetric(HEAP_COMMITTED, heapMemoryUsage.getCommitted(), MetricEnums.MetricUnitType.B));
        metricsForExport.add(createMemoryMetric(HEAP_INIT, heapMemoryUsage.getInit(), MetricEnums.MetricUnitType.B));
        metricsForExport.add(createMemoryMetric(HEAP_USED, heapMemoryUsage.getUsed(), MetricEnums.MetricUnitType.B));
        metricsForExport.add(createMemoryMetric(HEAP, heapMemoryUsage.getMax(), MetricEnums.MetricUnitType.B));
        metricsForExport.add(createMemoryMetric(NONHEAP_COMMITTED, nonHeapMemoryUsage.getCommitted(), MetricEnums.MetricUnitType.B));
        metricsForExport.add(createMemoryMetric(NONHEAP_INIT, nonHeapMemoryUsage.getInit(), MetricEnums.MetricUnitType.B));
        metricsForExport.add(createMemoryMetric(NONHEAP_USED, nonHeapMemoryUsage.getUsed(), MetricEnums.MetricUnitType.B));
        metricsForExport.add(createMemoryMetric(NONHEAP, nonHeapMemoryUsage.getMax(), MetricEnums.MetricUnitType.B));
    }

    private void addManagementMetrics(List<Metric> metricsForExport) {
        try {
            metricsForExport.add(createSystemMetric(UPTIME, ManagementFactory.getRuntimeMXBean().getUptime(), MetricEnums.MetricUnitType.MILLI_SECOND));
            metricsForExport.add(createSystemMetric(SYSTEMLOAD_AVERAGE, ((Double) ManagementFactory.getOperatingSystemMXBean().getSystemLoadAverage()).longValue(), MetricEnums.MetricUnitType.NUMBER));
            for (GarbageCollectorMXBean garbageCollectorMXBean : garbageCollectorMxBeans) {
                String name = beautifyGcName(garbageCollectorMXBean.getName());
                metricsForExport.add(createSystemMetric("gc." + name + ".count", garbageCollectorMXBean.getCollectionCount(), MetricEnums.MetricUnitType.NUMBER));
                metricsForExport.add(createSystemMetric("gc." + name + ".time", garbageCollectorMXBean.getCollectionTime(), MetricEnums.MetricUnitType.NUMBER));
            }
            metricsForExport.add(createSystemMetric(PROCESSORS, runtime.availableProcessors(), MetricEnums.MetricUnitType.MILLI_SECOND));
        } catch (Exception ex) {
            logger.info("Error when trying to collect metric.", ex);
        }
    }


    private void addThreadMetrics(List<Metric> metricsForExport) {
        metricsForExport.add(createThreadsMetric(THREADS_PEAK, (long) threadMxBean.getPeakThreadCount(), MetricEnums.MetricUnitType.NUMBER));
        metricsForExport.add(createThreadsMetric(THREADS_DAEMON, (long) threadMxBean.getDaemonThreadCount(), MetricEnums.MetricUnitType.NUMBER));
        metricsForExport.add(createThreadsMetric(THREADS_TOTAL_STARTED, threadMxBean.getTotalStartedThreadCount(), MetricEnums.MetricUnitType.NUMBER));
        metricsForExport.add(createThreadsMetric(THREADS, (long) threadMxBean.getThreadCount(), MetricEnums.MetricUnitType.NUMBER));
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
            logger.info("Error when trying to collect default metrics.", ex);
            return 0;
        }
    }

    public void setApplicationName(String applicationName) {
        this.applicationName = applicationName;
    }
}
