package fortscale.common.exporter;

import javafx.application.Application;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.actuate.endpoint.PublicMetrics;
import org.springframework.boot.actuate.metrics.Metric;
import org.springframework.context.ApplicationContext;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;

import java.lang.management.*;
import java.util.*;

@Component
public class PresidioSystemPublicMetrics implements PublicMetrics{


    private Collection<Metric<?>> result;
    private MemoryUsage heapMemoryUsage;
    private MemoryUsage nonHeapMemoryUsage;
    private Runtime runtime;
    private List<GarbageCollectorMXBean> garbageCollectorMxBeans;
    private ThreadMXBean threadMxBean;

    public PresidioSystemPublicMetrics() {
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
        result.add(newMemoryMetric("mem", runtime.totalMemory() + getTotalNonHeapMemoryIfPossible()));
        result.add(newMemoryMetric("mem.free", runtime.freeMemory()));
        result.add(newMemoryMetric("heap.committed", heapMemoryUsage.getCommitted()));
        result.add(newMemoryMetric("heap.init", heapMemoryUsage.getInit()));
        result.add(newMemoryMetric("heap.used", heapMemoryUsage.getUsed()));
        result.add(newMemoryMetric("heap", heapMemoryUsage.getMax()));
        result.add(newMemoryMetric("nonheap.committed", nonHeapMemoryUsage.getCommitted()));
        result.add(newMemoryMetric("nonheap.init", nonHeapMemoryUsage.getInit()));
        result.add(newMemoryMetric("nonheap.used", nonHeapMemoryUsage.getUsed()));
        result.add(newMemoryMetric("nonheap", nonHeapMemoryUsage.getMax()));
    }

    private void addManagementMetrics(Collection<Metric<?>> result) {
        try {
            result.add(new Metric<>("uptime", ManagementFactory.getRuntimeMXBean().getUptime()));
            result.add(new Metric<>("systemload.average", ManagementFactory.getOperatingSystemMXBean().getSystemLoadAverage()));
            for (GarbageCollectorMXBean garbageCollectorMXBean : garbageCollectorMxBeans) {
                String name = beautifyGcName(garbageCollectorMXBean.getName());
                result.add(new Metric<>("gc." + name + ".count", garbageCollectorMXBean.getCollectionCount()));
                result.add(new Metric<>("gc." + name + ".time", garbageCollectorMXBean.getCollectionTime()));
            }
            result.add(new Metric<>("processors", runtime.availableProcessors()));
        }
        catch (NoClassDefFoundError ex) {
            // Expected on Google App Engine
        }
    }


    private void addThreadMetrics(Collection<Metric<?>> result) {
        result.add(new Metric<>("threads.peak", (long) threadMxBean.getPeakThreadCount()));
        result.add(new Metric<>("threads.daemon", (long) threadMxBean.getDaemonThreadCount()));
        result.add(new Metric<>("threads.totalStarted", threadMxBean.getTotalStartedThreadCount()));
        result.add(new Metric<>("threads", (long) threadMxBean.getThreadCount()));
    }


    /**
     * Turn GC names like 'PS Scavenge' or 'PS MarkSweep' into something that is more
     * metrics friendly.
     * @param name the source name
     * @return a metric friendly name
     */
    private String beautifyGcName(String name) {
        return StringUtils.replace(name, " ", ".").toLowerCase();
    }

    private long getTotalNonHeapMemoryIfPossible() {
        try {
            return ManagementFactory.getMemoryMXBean().getNonHeapMemoryUsage().getUsed();
        }
        catch (Throwable ex) {
            return 0;
        }
    }

}
