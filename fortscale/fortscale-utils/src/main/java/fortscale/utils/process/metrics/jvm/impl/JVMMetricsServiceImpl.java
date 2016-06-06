package fortscale.utils.process.metrics.jvm.impl;

import com.google.common.util.concurrent.ThreadFactoryBuilder;
import fortscale.utils.logging.Logger;
import fortscale.utils.monitoring.stats.StatsService;
import fortscale.utils.process.metrics.jvm.JVMMetricsService;
import fortscale.utils.process.metrics.jvm.stats.JVMMetrics;

import java.lang.management.GarbageCollectorMXBean;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

import static java.lang.management.ManagementFactory.getGarbageCollectorMXBeans;
import static java.lang.management.ManagementFactory.getMemoryMXBean;

/**
 * Service is used for jvm metrics statistics updates
 */
public class JVMMetricsServiceImpl implements JVMMetricsService, Runnable {

    private static final Logger logger = Logger.getLogger(JVMMetricsServiceImpl.class);
    private long tickSeconds;
    private JVMMetrics jvmMetrics;

    /**
     * ctor
     * @param statsService
     * @param tickSeconds
     * @param pid
     */
    public JVMMetricsServiceImpl(StatsService statsService, long tickSeconds, long pid) {
        jvmMetrics = new JVMMetrics(statsService);
        this.tickSeconds=tickSeconds;
        this.jvmMetrics.pid=pid;
        logger.info("JVM metrics service was created, pid: {} tickSeconds: {}",pid,tickSeconds);
        // Create tick thread if enabled
        if (tickSeconds > 0) {
            // Create the periodic tick thread
            ScheduledExecutorService executor = Executors.newScheduledThreadPool(1,
                    new ThreadFactoryBuilder()
                            .setDaemon(true)
                            .setNameFormat("JVMMetricsServiceImpl-tick[%s]")
                            .build());

            int initialDelay = 0;
            executor.scheduleAtFixedRate(this, initialDelay, this.tickSeconds, TimeUnit.SECONDS);
        }
        else {
            logger.info("jvm metrics disabled");
        }
    }

    /**
     * getter
     * @return jvm metrics
     */
    public JVMMetrics getJvmMetrics() {
        return jvmMetrics;
    }
    /**
     * update memory statistics such as free memory etc.
     */
    @Override
    public void collectMemoryStats() {
        jvmMetrics.heapCommittedMemory = getMemoryMXBean().getHeapMemoryUsage().getCommitted();
        jvmMetrics.heapInitMemory = getMemoryMXBean().getHeapMemoryUsage().getInit();
        jvmMetrics.heapMaxMemory = getMemoryMXBean().getHeapMemoryUsage().getMax();
        jvmMetrics.heapUsedMemory = getMemoryMXBean().getHeapMemoryUsage().getUsed();
        jvmMetrics.nonHeapCommittedMemory = getMemoryMXBean().getNonHeapMemoryUsage().getCommitted();
        jvmMetrics.nonHeapInitMemory = getMemoryMXBean().getNonHeapMemoryUsage().getInit();
        jvmMetrics.nonHeapMaxMemory = getMemoryMXBean().getNonHeapMemoryUsage().getMax();
        jvmMetrics.nonHeapUsedMemory = getMemoryMXBean().getNonHeapMemoryUsage().getUsed();
    }

    /**
     * update garbage collectors statistics
     */
    @Override
    public void collectGarbageCollectorsStats() {
        double garbageCollectionTimeInMillis = getGarbageCollectorMXBeans().stream().mapToDouble(GarbageCollectorMXBean::getCollectionTime).sum();
        jvmMetrics.garageCollectorsTimeUtilization = garbageCollectionTimeInMillis / 1000;
    }

    /**
     * scheduled operation to run
     */
    @Override
    public void run() {
        collectMemoryStats();
        collectGarbageCollectorsStats();
        jvmMetrics.manualUpdate();
    }
}
