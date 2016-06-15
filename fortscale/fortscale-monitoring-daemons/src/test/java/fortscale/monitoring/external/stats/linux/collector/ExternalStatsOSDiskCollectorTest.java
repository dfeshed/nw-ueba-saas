package fortscale.monitoring.external.stats.linux.collector;

import fortscale.monitoring.external.stats.linux.collector.collectors.ExternalStatsOSDiskCollector;
import fortscale.monitoring.external.stats.linux.collector.metrics.ExternalStatsOSDiskCollectorMetrics;
import fortscale.monitoring.external.stats.collector.impl.linux.parsers.LinuxProcFileKeyMultipleValueParser;
import fortscale.monitoring.external.stats.collector.impl.linux.parsers.LinuxProcFileParser;
import org.junit.Assert;
import org.junit.Test;

import java.io.File;
import java.util.HashMap;
import java.util.Map;

/**
 * Created by galiar on 26/04/2016.
 */
public class ExternalStatsOSDiskCollectorTest {
//
//    @Test
//    public void testOSDiskCollector() throws Exception{
//
//        String diskNameSda1 = "sda1";
//        ExternalStatsOSDiskCollector diskCollector = new ExternalStatsOSDiskCollector(diskNameSda1);
//
//        String diskStatsFilename = "src/test/resources/fortscale/monitoring/external/stats/linux/collector/parser/proc/files/diskstats";
//        String diskStatsSeparator = " ";
//        int indexOfKey = 3;
//
//        LinuxProcFileKeyMultipleValueParser parser = new LinuxProcFileKeyMultipleValueParser(diskStatsFilename,diskStatsSeparator,new File(diskStatsFilename).getName(),indexOfKey);
//        Map<String,LinuxProcFileParser> parserMap = new HashMap<>();
//        parserMap.put(parser.getName(),parser);
//
//        diskCollector.collect(parserMap);
//
//        ExternalStatsOSDiskCollectorMetrics diskCollectorMetrics = diskCollector.getDiskCollectorMetrics();
//        Assert.assertEquals(774,diskCollectorMetrics.getReadBytes().longValue());
//        Assert.assertEquals(524,diskCollectorMetrics.getWriteBytes().longValue());
//        Assert.assertEquals(556,diskCollectorMetrics.getUtilization().longValue());
//
//
//        String diskNameSda = "sda";
//        ExternalStatsOSDiskCollector diskCollector2 = new ExternalStatsOSDiskCollector(diskNameSda);
//        diskCollector2.collect(parserMap);
//
//        ExternalStatsOSDiskCollectorMetrics diskCollectorMetrics2 = diskCollector2.getDiskCollectorMetrics();
//
//        Assert.assertEquals(91375,diskCollectorMetrics2.getReadBytes().longValue());
//        Assert.assertEquals(382532,diskCollectorMetrics2.getWriteBytes().longValue());
//        Assert.assertEquals(5803436,diskCollectorMetrics2.getUtilization().longValue());
//
//    }
//

}
