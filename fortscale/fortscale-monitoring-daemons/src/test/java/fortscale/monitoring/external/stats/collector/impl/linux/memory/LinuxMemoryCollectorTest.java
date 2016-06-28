package fortscale.monitoring.external.stats.collector.impl.linux.memory;

import fortscale.monitoring.external.stats.collector.impl.ExternalStatsCollectorMetrics;
import fortscale.utils.monitoring.stats.StatsService;
import org.junit.Assert;
import org.junit.Test;

/**
 * Created by galiar & gaashh on 21/04/2016.
 */
public class LinuxMemoryCollectorTest {

    // Where test /proc files are
    final String TEST_PROC_BASE_PATH = "src/test/resources/fortscale/monitoring/external/stats/collector/impl/linux/proc";

    // Measurement EPOCH
    final long EPOCH = 1_234_000_000;

    // We don't use the real stats service
    StatsService statsService = null;


    void checkMetrics(LinuxMemoryCollectorImplMetrics metrics ) {
        Assert.assertEquals(32880764L * 1024, metrics.totalMemory);
        Assert.assertEquals( 5577408L * 1024, metrics.freeMemory);
        Assert.assertEquals(  713132L * 1024, metrics.buffersMemory);
        Assert.assertEquals(   78068L * 1024, metrics.sharedMemory);
        Assert.assertEquals( 8945296L * 1024, metrics.cacheMemory);
        Assert.assertEquals(     732L * 1024, metrics.dirtyMemory);
        Assert.assertEquals(19669844L * 1024, metrics.activeMemory);
        Assert.assertEquals( 6624784L * 1024, metrics.inactiveMemory);

        // used = total - free
        Assert.assertEquals((32880764L - 5577408L)          * 1024,metrics.usedMemory);
        // read free = free + buffers + cached
        Assert.assertEquals((5577408L + 713132L + 8945296L) * 1024, metrics.realFreeMemory);

        Assert.assertEquals(     1234L * 4096, metrics.swapInMemory);
        Assert.assertEquals(     5678L * 4096, metrics.swapOutMemory);
        Assert.assertEquals(285995460L * 4096, metrics.bufferOutMemory);
        Assert.assertEquals(  2583072L * 4096, metrics.bufferInMemory);
    }


    @Test
    public void testLinuxMemoryCollectorCollector() {

        ExternalStatsCollectorMetrics selfMetrics = new ExternalStatsCollectorMetrics(null,"test");
        LinuxMemoryCollectorImpl collector =
                new LinuxMemoryCollectorImpl("linuxMemory", statsService, TEST_PROC_BASE_PATH,selfMetrics);

        LinuxMemoryCollectorImplMetrics metrics = collector.getMetrics();

        collector.collect(EPOCH);

        checkMetrics(metrics);

    }

    @Test
    public void testLinuxMemoryCollectorService() {


        // Create the collector service
        boolean isTickThreadEnabled = false;
        long tickPeriodSeconds      = 60;
        long tickSlipWarnSeconds    = 30;

        LinuxMemoryCollectorImplService service = new LinuxMemoryCollectorImplService(
                statsService, TEST_PROC_BASE_PATH, isTickThreadEnabled, tickPeriodSeconds, tickSlipWarnSeconds);

        LinuxMemoryCollectorImplMetrics metrics = service.getMetrics();

        // Do it

        // tick 1
        long epoch = EPOCH;
        service.tick(epoch);
        checkMetrics(metrics);

        // From this point tests are manual (and not very interesting)
        // tick 2
        epoch += 60;
        service.tick(epoch);
        checkMetrics(metrics);

        // tick 3 - delay
        epoch += 115;
        service.tick(epoch);
        checkMetrics(metrics);

        // tick 4 - too fast, dropped
        epoch += 5;
        service.tick(epoch);
        checkMetrics(metrics);

        // tick 5 - back to normal
        epoch += 60;
        service.tick(epoch);
        checkMetrics(metrics);
    }

}
