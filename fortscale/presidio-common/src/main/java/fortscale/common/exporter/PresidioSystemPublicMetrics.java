package fortscale.common.exporter;

import org.springframework.boot.actuate.endpoint.PublicMetrics;
import org.springframework.boot.actuate.metrics.Metric;
import org.springframework.core.Ordered;
import org.springframework.util.StringUtils;

import java.lang.management.*;
import java.util.Collection;
import java.util.LinkedHashSet;
import java.util.List;


public class PresidioSystemPublicMetrics implements PublicMetrics, Ordered {


    public PresidioSystemPublicMetrics() {
    }

    private Metric<Long> newMemoryMetric(String name, long bytes) {
        return new Metric<>(name, bytes / 1024);
    }


    @Override
    public int getOrder() {
        return Ordered.HIGHEST_PRECEDENCE + 10;
    }

    @Override
    public Collection<Metric<?>> metrics() {
        Collection<Metric<?>> result = new LinkedHashSet<>();
        addBasicMetrics(result);
        addManagementMetrics(result);
        return result;
    }


    private void addBasicMetrics(Collection<Metric<?>> result) {
        // NOTE: ManagementFactory must not be used here since it fails on GAE
        Runtime runtime = Runtime.getRuntime();
        result.add(newMemoryMetric("mem",
                runtime.totalMemory() + getTotalNonHeapMemoryIfPossible()));
        result.add(newMemoryMetric("mem.free", runtime.freeMemory()));
        result.add(new Metric<>("processors", runtime.availableProcessors()));
    }

    private long getTotalNonHeapMemoryIfPossible() {
        try {
            return ManagementFactory.getMemoryMXBean().getNonHeapMemoryUsage().getUsed();
        }
        catch (Throwable ex) {
            return 0;
        }
    }

    /**
     * Add metrics from ManagementFactory if possible. Note that ManagementFactory is not
     * available on Google App Engine.
     * @param result the result
     */
    private void addManagementMetrics(Collection<Metric<?>> result) {
        try {
            // Add JVM up time in ms
            result.add(new Metric<>("uptime",
                    ManagementFactory.getRuntimeMXBean().getUptime()));
            result.add(new Metric<>("systemload.average",
                    ManagementFactory.getOperatingSystemMXBean().getSystemLoadAverage()));
            addHeapMetrics(result);
            addNonHeapMetrics(result);
            addThreadMetrics(result);
            addGarbageCollectionMetrics(result);
        }
        catch (NoClassDefFoundError ex) {
            // Expected on Google App Engine
        }
    }

    /**
     * Add JVM heap metrics.
     * @param result the result
     */
    private void addHeapMetrics(Collection<Metric<?>> result) {
        MemoryUsage memoryUsage = ManagementFactory.getMemoryMXBean()
                .getHeapMemoryUsage();
        result.add(newMemoryMetric("heap.committed", memoryUsage.getCommitted()));
        result.add(newMemoryMetric("heap.init", memoryUsage.getInit()));
        result.add(newMemoryMetric("heap.used", memoryUsage.getUsed()));
        result.add(newMemoryMetric("heap", memoryUsage.getMax()));
    }

    /**
     * Add JVM non-heap metrics.
     * @param result the result
     */
    private void addNonHeapMetrics(Collection<Metric<?>> result) {
        MemoryUsage memoryUsage = ManagementFactory.getMemoryMXBean()
                .getNonHeapMemoryUsage();
        result.add(newMemoryMetric("nonheap.committed", memoryUsage.getCommitted()));
        result.add(newMemoryMetric("nonheap.init", memoryUsage.getInit()));
        result.add(newMemoryMetric("nonheap.used", memoryUsage.getUsed()));
        result.add(newMemoryMetric("nonheap", memoryUsage.getMax()));
    }


    /**
     * Add thread metrics.
     * @param result the result
     */
    private void addThreadMetrics(Collection<Metric<?>> result) {
        ThreadMXBean threadMxBean = ManagementFactory.getThreadMXBean();
        result.add(new Metric<>("threads.peak",
                (long) threadMxBean.getPeakThreadCount()));
        result.add(new Metric<>("threads.daemon",
                (long) threadMxBean.getDaemonThreadCount()));
        result.add(new Metric<>("threads.totalStarted",
                threadMxBean.getTotalStartedThreadCount()));
        result.add(new Metric<>("threads", (long) threadMxBean.getThreadCount()));
    }


    /**
     * Add garbage collection metrics.
     * @param result the result
     */
    private void addGarbageCollectionMetrics(Collection<Metric<?>> result) {
        List<GarbageCollectorMXBean> garbageCollectorMxBeans = ManagementFactory
                .getGarbageCollectorMXBeans();
        for (GarbageCollectorMXBean garbageCollectorMXBean : garbageCollectorMxBeans) {
            String name = beautifyGcName(garbageCollectorMXBean.getName());
            result.add(new Metric<>("gc." + name + ".count",
                    garbageCollectorMXBean.getCollectionCount()));
            result.add(new Metric<>("gc." + name + ".time",
                    garbageCollectorMXBean.getCollectionTime()));
        }
    }

    /**
     * Turn GC names like 'PS Scavenge' or 'PS MarkSweep' into something that is more
     * metrics friendly.
     * @param name the source name
     * @return a metric friendly name
     */
    private String beautifyGcName(String name) {
        return StringUtils.replace(name, " ", "_").toLowerCase();
    }



}
