package presidio.monitoring.aspect.metrics;

import fortscale.utils.logging.Logger;
import org.springframework.boot.actuate.endpoint.PublicMetrics;
import org.springframework.boot.actuate.metrics.Metric;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;
import presidio.monitoring.aspect.MonitoringAspects;

import java.lang.management.GarbageCollectorMXBean;
import java.lang.management.ManagementFactory;
import java.lang.management.MemoryUsage;
import java.lang.management.ThreadMXBean;
import java.util.Collection;
import java.util.LinkedHashSet;
import java.util.List;

import static presidio.monitoring.DefaultPublicMetricsNames.*;

@Component
public class PresidioDefaultMetrics implements PublicMetrics {

    private static final Logger logger = Logger.getLogger(PresidioDefaultMetrics.class);

    private Collection<Metric<?>> result;
    private MemoryUsage heapMemoryUsage;
    private MemoryUsage nonHeapMemoryUsage;
    private Runtime runtime;
    private List<GarbageCollectorMXBean> garbageCollectorMxBeans;
    private ThreadMXBean threadMxBean;

    public PresidioDefaultMetrics() {
        result = new LinkedHashSet<>();
        heapMemoryUsage = ManagementFactory.getMemoryMXBean().getHeapMemoryUsage();
        nonHeapMemoryUsage = ManagementFactory.getMemoryMXBean().getNonHeapMemoryUsage();
        runtime = Runtime.getRuntime();
        garbageCollectorMxBeans = ManagementFactory.getGarbageCollectorMXBeans();
        threadMxBean = ManagementFactory.getThreadMXBean();
    }

    private Metric<Long> newMemoryMetric(String name, long bytes) {
        return new Metric<>(name, bytes / 1024);
    }


    @Override
    public Collection<Metric<?>> metrics() {
        result.clear();
        addMemoryMetrics(result);
        addManagementMetrics(result);
        addThreadMetrics(result);
        return result;
    }


    private void addMemoryMetrics(Collection<Metric<?>> result) {
        result.add(newMemoryMetric(MEM, runtime.totalMemory() + getTotalNonHeapMemoryIfPossible()));
        result.add(newMemoryMetric(MEM_FREE, runtime.freeMemory()));
        result.add(newMemoryMetric(HEAP_COMMITTED, heapMemoryUsage.getCommitted()));
        result.add(newMemoryMetric(HEAP_INIT, heapMemoryUsage.getInit()));
        result.add(newMemoryMetric(HEAP_USED, heapMemoryUsage.getUsed()));
        result.add(newMemoryMetric(HEAP, heapMemoryUsage.getMax()));
        result.add(newMemoryMetric(NONHEAP_COMMITTED, nonHeapMemoryUsage.getCommitted()));
        result.add(newMemoryMetric(NONHEAP_INIT, nonHeapMemoryUsage.getInit()));
        result.add(newMemoryMetric(NONHEAP_USED, nonHeapMemoryUsage.getUsed()));
        result.add(newMemoryMetric(NONHEAP, nonHeapMemoryUsage.getMax()));
    }

    private void addManagementMetrics(Collection<Metric<?>> result) {
        try {
            result.add(new Metric<>(UPTIME, ManagementFactory.getRuntimeMXBean().getUptime()));
            result.add(new Metric<>(SYSTEMLOAD_AVERAGE, ManagementFactory.getOperatingSystemMXBean().getSystemLoadAverage()));
            for (GarbageCollectorMXBean garbageCollectorMXBean : garbageCollectorMxBeans) {
                String name = beautifyGcName(garbageCollectorMXBean.getName());
                result.add(new Metric<>("gc." + name + ".count", garbageCollectorMXBean.getCollectionCount()));
                result.add(new Metric<>("gc." + name + ".time", garbageCollectorMXBean.getCollectionTime()));
            }
            result.add(new Metric<>(PROCESSORS, runtime.availableProcessors()));
        } catch (Exception ex) {
            logger.info("Error when trying to collect defoult metrics.",ex);
        }
    }


    private void addThreadMetrics(Collection<Metric<?>> result) {
        result.add(new Metric<>(THREADS_PEAK, (long) threadMxBean.getPeakThreadCount()));
        result.add(new Metric<>(THREADS_DAEMON, (long) threadMxBean.getDaemonThreadCount()));
        result.add(new Metric<>(THREADS_TOTAL_STARTED, threadMxBean.getTotalStartedThreadCount()));
        result.add(new Metric<>(THREADS, (long) threadMxBean.getThreadCount()));
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
            logger.info("Error when trying to collect defoult metrics.",ex);
            return 0;
        }
    }

}
