package fortscale.monitoring.external.stats.collector.impl.linux.memory;

import fortscale.utils.monitoring.stats.StatsService;
import org.junit.Assert;
import org.junit.Test;

/**
 * Created by galiar & gaash on 21/04/2016.
 */
public class ExternalStatsCollectorLinuxMemoryCollectorTest {

    final String TEST_PROC_BASE_PATH = "src/test/resources/fortscale/monitoring/external/stats/collector/impl/linux/proc/files";

    // We don't use the real stats service
    StatsService statsService = null;

    @Test
    public void testOSMemoryCollector() throws Exception{

        final long epoch = 1_000_000_000;

        ExternalStatsCollectorLinuxMemoryCollector collector = new ExternalStatsCollectorLinuxMemoryCollector(statsService, TEST_PROC_BASE_PATH);

        collector.collect(epoch);
        ExternalStatsCollectorLinuxMemoryMetrics metrics = collector.getMetrics();

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
}
