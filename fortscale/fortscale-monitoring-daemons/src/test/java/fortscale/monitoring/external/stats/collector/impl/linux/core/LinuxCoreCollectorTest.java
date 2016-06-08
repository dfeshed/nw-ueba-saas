package fortscale.monitoring.external.stats.collector.impl.linux.core;

import fortscale.monitoring.external.stats.collector.impl.linux.parsers.LinuxProcFileKeyMultipleValueParser;
import fortscale.monitoring.external.stats.collector.impl.linux.process.LinuxProcessCollectorImplMetrics;
import fortscale.utils.monitoring.stats.StatsService;
import org.junit.Assert;
import org.junit.Test;

/**
 * Created by gaashh on 6/8/16.
 */
public class LinuxCoreCollectorTest {

    // Where test files are
    final String TEST_BASE_PATH = "src/test/resources/fortscale/monitoring/external/stats/collector/impl/linux";
    final String TEST_PROC_BASE_PATH = TEST_BASE_PATH + "/proc";
    final String TEST_STAT_PROC_FILE = TEST_PROC_BASE_PATH + "/stat";

    // Measurement EPOCH
    final long EPOCH = 1_234_000_000;

    // Parser core field index
    final int CORE_INDEX = 0;
    // We don't use the real stats service
    StatsService statsService = null;


    void checkMetrics(LinuxCoreCollectorImplMetrics metrics, long userTicks) {

        Assert.assertNotNull(metrics);
        Assert.assertEquals(userTicks * 10, metrics.userMiliSec);
        Assert.assertEquals(2016854L * 10, metrics.systemMiliSec);
        Assert.assertEquals(325L * 10, metrics.niceMiliSec);
        Assert.assertEquals(67717334L * 10, metrics.idleMiliSec);
        Assert.assertEquals(15156L * 10, metrics.waitMiliSec);
        Assert.assertEquals(488L * 10, metrics.hwInterruptsMiliSec);
        Assert.assertEquals(194254L * 10, metrics.swInterruptsMiliSec);
        Assert.assertEquals(0L * 10, metrics.stealMiliSec);

    }

    @Test
    public void testLinuxCoreCollectorCollector() {

        // Create the parser
        LinuxProcFileKeyMultipleValueParser parser = new LinuxProcFileKeyMultipleValueParser(TEST_STAT_PROC_FILE, " ", CORE_INDEX);

        // Create the collector
        String coreName = "ALL";
        String coreKey = "cpu";
        LinuxCoreCollectorImpl collector = new LinuxCoreCollectorImpl("linuxCore", statsService, coreName, coreKey);

        // Collect and check
        collector.collect(EPOCH, parser);
        LinuxCoreCollectorImplMetrics metrics = collector.getMetrics();
        checkMetrics(metrics, 9 * 1111);

    }


    @Test
    public void testCoreCollectorService() {


        // Create the collector service
        boolean isTickThreadEnabled = false;
        long tickPeriodSeconds = 60;
        long tickSlipWarnSeconds = 30;

        LinuxCoreCollectorImplService service = new LinuxCoreCollectorImplService(
                statsService, TEST_PROC_BASE_PATH, isTickThreadEnabled, tickPeriodSeconds, tickSlipWarnSeconds);

        // Collect
        service.collect(EPOCH);

        // Check metrics
        LinuxCoreCollectorImplMetrics metrics;

        // cpu0
        metrics = service.getMetrics("cpu0");
        LinuxCoreCollectorImplMetrics metricsCpu0 = metrics;
        checkMetrics(metrics, 0 * 1111L);

        // cpu1
        metrics = service.getMetrics("cpu1");
        checkMetrics(metrics, 1 * 1111L);

        // cpu2
        metrics = service.getMetrics("cpu2");
        checkMetrics(metrics, 2 * 1111L);

        // cpu3
        metrics = service.getMetrics("cpu3");
        checkMetrics(metrics, 3 * 1111L);

        // all cpu-s
        metrics = service.getMetrics("ALL");
        checkMetrics(metrics, 9 * 1111L);

        // Collect again and check metrics are the same, meaning new collector was not created
        service.collect(EPOCH + 60);
        metrics = service.getMetrics("cpu0");
        Assert.assertEquals(metricsCpu0, metrics);
        checkMetrics(metrics, 0 * 1111L);

        // Again
        service.collect(EPOCH + 120);
        metrics = service.getMetrics("cpu0");
        Assert.assertEquals(metricsCpu0, metrics);
        checkMetrics(metrics, 0 * 1111L);
    }
}
