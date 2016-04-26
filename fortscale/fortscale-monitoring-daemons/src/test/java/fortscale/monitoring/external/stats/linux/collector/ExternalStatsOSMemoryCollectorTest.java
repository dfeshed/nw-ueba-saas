package fortscale.monitoring.external.stats.linux.collector;

import fortscale.monitoring.external.stats.linux.collector.collectors.ExternalStatsOSMemoryCollector;
import fortscale.monitoring.external.stats.linux.collector.metrics.ExternalStatsOSMemoryCollectorMetrics;
import fortscale.monitoring.external.stats.linux.collector.parsers.ExternalStatsProcFileParser;
import fortscale.monitoring.external.stats.linux.collector.parsers.ExternalStatsProcFileSingleValueParser;
import org.junit.Assert;
import org.junit.Test;

import java.io.File;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;

/**
 * Created by galiar on 21/04/2016.
 */
public class ExternalStatsOSMemoryCollectorTest {

    //test the reading from different files is good
    //test the metrices are written properly.
    //test collect(parser)

    @Test
    public void testOSMemoryCollector() throws Exception{

        ExternalStatsOSMemoryCollector collector = new ExternalStatsOSMemoryCollector();

        String meminfoFilename = "src/test/resources/fortscale/monitoring/external/stats/linux/collector/parser/proc/files/meminfo";
        String meminfoSeparator = ":";
        ExternalStatsProcFileSingleValueParser meminfoValueParser = new ExternalStatsProcFileSingleValueParser(meminfoFilename,meminfoSeparator,new File(meminfoFilename).getName());
        String vmstatFilename = "src/test/resources/fortscale/monitoring/external/stats/linux/collector/parser/proc/files/vmstat";
        String vmstatSeparator = " ";
        ExternalStatsProcFileSingleValueParser vmstatValueParser = new ExternalStatsProcFileSingleValueParser(vmstatFilename,vmstatSeparator,new File(vmstatFilename).getName());

        Map<String,ExternalStatsProcFileParser> parserMap = new HashMap<>();
        parserMap.put(meminfoValueParser.getName(),meminfoValueParser );
        parserMap.put(vmstatValueParser.getName(),vmstatValueParser );

        collector.collect(parserMap);
        ExternalStatsOSMemoryCollectorMetrics osMemoryCollectorMetrics = collector.getMemoryCollectorMetrics();

        Assert.assertEquals(10090L,osMemoryCollectorMetrics.getBufferInMemoryMB().longValue());
        Assert.assertEquals(1117169L,osMemoryCollectorMetrics.getBufferOutMemoryMB().longValue());
        Assert.assertEquals(696L,osMemoryCollectorMetrics.getBuffersMemoryMB().longValue());
        Assert.assertEquals(8735L,osMemoryCollectorMetrics.getCacheMemoryMB().longValue());
        Assert.assertEquals(0L,osMemoryCollectorMetrics.getDirtyMemoryMB().longValue());
        Assert.assertEquals(5446L,osMemoryCollectorMetrics.getFreeMemoryMB().longValue());
        Assert.assertEquals(19208L,osMemoryCollectorMetrics.getRealFreeMemoryMB().longValue());
        Assert.assertEquals(76L,osMemoryCollectorMetrics.getSharedMemoryMB().longValue());
        Assert.assertEquals(0L,osMemoryCollectorMetrics.getSwapInMemoryMB().longValue());
        Assert.assertEquals(0L,osMemoryCollectorMetrics.getSwapOutMemoryMB().longValue());
        Assert.assertEquals(32110L,osMemoryCollectorMetrics.getTotalMemoryMB().longValue());
        Assert.assertEquals(osMemoryCollectorMetrics.getTotalMemoryMB().longValue() - osMemoryCollectorMetrics.getFreeMemoryMB().longValue() ,osMemoryCollectorMetrics.getUsedMemoryMB().longValue());

    }
}
