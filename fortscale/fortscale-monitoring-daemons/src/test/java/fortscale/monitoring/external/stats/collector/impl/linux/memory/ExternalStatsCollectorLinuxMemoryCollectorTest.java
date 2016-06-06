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

    //test the reading from different files is good
    //test the matrices are written properly.
    //test collect(parser)


    @Test
    public void testOSMemoryCollector() throws Exception{

        final long epoch = 1_000_000_000;

        ExternalStatsCollectorLinuxMemoryCollector collector = new ExternalStatsCollectorLinuxMemoryCollector(statsService, TEST_PROC_BASE_PATH);

        collector.collect(epoch);
        ExternalStatsCollectorLinuxMemoryMetrics metrics = collector.getMetrics();

        Assert.assertEquals(10090L,  metrics.bufferInMemory);
        Assert.assertEquals(1117169L,metrics.getBufferOutMemoryMB().longValue());
        Assert.assertEquals(696L,    metrics.getBuffersMemoryMB().longValue());
        Assert.assertEquals(8735L,   metrics.getCacheMemoryMB().longValue());
        Assert.assertEquals(0L,      metrics.getDirtyMemoryMB().longValue());
        Assert.assertEquals(5446L,   metrics.getFreeMemoryMB().longValue());
        Assert.assertEquals(19208L,  metrics.getRealFreeMemoryMB().longValue());
        Assert.assertEquals(76L,     metrics.getSharedMemoryMB().longValue());
        Assert.assertEquals(0L,      metrics.getSwapInMemoryMB().longValue());
        Assert.assertEquals(0L,      metrics.getSwapOutMemoryMB().longValue());
        Assert.assertEquals(32110L,  metrics.getTotalMemoryMB().longValue());
//        Assert.assertEquals(metrics.getTotalMemoryMB().longValue() - metrics.getFreeMemoryMB().longValue() ,metrics.getUsedMemoryMB().longValue());

    }
}
