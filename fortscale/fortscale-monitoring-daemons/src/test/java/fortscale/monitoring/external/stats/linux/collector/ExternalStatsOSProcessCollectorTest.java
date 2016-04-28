package fortscale.monitoring.external.stats.linux.collector;

import fortscale.monitoring.external.stats.linux.collector.collectors.ExternalStatsOSProcessCollector;
import fortscale.monitoring.external.stats.linux.collector.metrics.ExternalStatsOSProcessCollectorMetrics;
import fortscale.monitoring.external.stats.linux.collector.parsers.ExternalStatsProcFileKeyMultipleValueParser;
import fortscale.monitoring.external.stats.linux.collector.parsers.ExternalStatsProcFileParser;
import fortscale.monitoring.external.stats.linux.collector.parsers.ExternalStatsProcFileSingleValueParser;
import org.junit.Assert;
import org.junit.Test;

import java.io.File;
import java.util.HashMap;
import java.util.Map;

/**
 * Created by galiar on 27/04/2016.
 */
public class ExternalStatsOSProcessCollectorTest {

    @Test
    public void testExternalOSProcessCollector() throws Exception{

        String pidName = "32120";
        ExternalStatsOSProcessCollector osProcessCollector = new ExternalStatsOSProcessCollector(pidName);
        String statFilename = "src/test/resources/fortscale/monitoring/external/stats/linux/collector/parser/proc/files/pid/stat";
        String cmdFilename = "src/test/resources/fortscale/monitoring/external/stats/linux/collector/parser/proc/files/pid/cmdline";
        String statsSeparator = " ";
        int indexOfKey = 0;

        ExternalStatsProcFileSingleValueParser cmdLineParser = new ExternalStatsProcFileSingleValueParser(cmdFilename,"\0"," ",new File(cmdFilename).getName());
        ExternalStatsProcFileKeyMultipleValueParser statParser = new ExternalStatsProcFileKeyMultipleValueParser(statFilename,statsSeparator,new File(statFilename).getName(),indexOfKey);
        Map<String,ExternalStatsProcFileParser> parserMap = new HashMap<>();
        parserMap.put(cmdLineParser.getName(),cmdLineParser);
        parserMap.put(statParser.getName(),statParser);

        osProcessCollector.collect(parserMap);
        ExternalStatsOSProcessCollectorMetrics processMetrics = osProcessCollector.getProcessMetrics();
        Assert.assertEquals(32120,processMetrics.getPid().longValue());
        Assert.assertEquals(0,processMetrics.getChildrenWaitTime().longValue());
        Assert.assertEquals(391,processMetrics.getKernelTime().longValue());
        Assert.assertEquals(3099,processMetrics.getMemoryRSS().longValue()); //TODO
        Assert.assertEquals(1040838656,processMetrics.getMemoryVSize().longValue()); //TODO
        Assert.assertEquals(1,processMetrics.getNumThreads().longValue());
        Assert.assertEquals(771,processMetrics.getUserTime().longValue());
        Assert.assertEquals(32120,processMetrics.getProcessCommandLine());//TODO

    }

}
