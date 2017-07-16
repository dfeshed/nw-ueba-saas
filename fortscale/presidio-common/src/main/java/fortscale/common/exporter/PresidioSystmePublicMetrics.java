package fortscale.common.exporter;

import org.springframework.boot.actuate.endpoint.PublicMetrics;
import org.springframework.boot.actuate.metrics.Metric;
import org.springframework.core.Ordered;
import org.springframework.util.StringUtils;

import java.lang.management.*;
import java.util.Collection;
import java.util.LinkedHashSet;
import java.util.List;


public class PresidioSystmePublicMetrics  implements PublicMetrics, Ordered {

    private long timestamp;

    public PresidioSystmePublicMetrics() {
        this.timestamp = System.currentTimeMillis();
    }

    private Metric<Long> newMemoryMetric(String name, long bytes) {
        return new Metric<Long>(name, bytes / 1024);
    }


    @Override
    public int getOrder() {
        return Ordered.HIGHEST_PRECEDENCE + 10;
    }

    @Override
    public Collection<Metric<?>> metrics() {
        Collection<Metric<?>> result = new LinkedHashSet<Metric<?>>();
        addBasicMetrics(result);
        return result;
    }

    /**
     * Add basic system metrics.
     * @param result the result
     */
    protected void addBasicMetrics(Collection<Metric<?>> result) {
        // NOTE: ManagementFactory must not be used here since it fails on GAE
        Runtime runtime = Runtime.getRuntime();
        result.add(newMemoryMetric("mem",
                runtime.totalMemory() + getTotalNonHeapMemoryIfPossible()));
        result.add(newMemoryMetric("mem.free", runtime.freeMemory()));
        result.add(new Metric<Integer>("processors", runtime.availableProcessors()));
        result.add(new Metric<Long>("instance.uptime",
                System.currentTimeMillis() - this.timestamp));
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

    private long getTotalNonHeapMemoryIfPossible() {
        try {
            return ManagementFactory.getMemoryMXBean().getNonHeapMemoryUsage().getUsed();
        }
        catch (Throwable ex) {
            return 0;
        }
    }


}
