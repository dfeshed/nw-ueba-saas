package fortscale.monitoring.external.stats.linux.collector;

import fortscale.monitoring.external.stats.linux.collector.collectors.ExternalStatsCPUUtilizationCollector;
import fortscale.monitoring.external.stats.linux.collector.metrics.ExternalStatsCPUUtilizationCollectorMetrics;
import fortscale.monitoring.external.stats.linux.collector.parsers.ExternalStatsProcFileMultipleValueParser;
import fortscale.monitoring.external.stats.linux.collector.parsers.ExternalStatsProcFileParser;
import org.junit.Assert;
import org.junit.Test;

import java.io.File;
import java.util.HashMap;
import java.util.Map;

/**
 * tests the behavour of ExternalStatsCPUUtilizationCollector, and it's corresponding metrics class.
 * Created by galiar on 26/04/2016.
 */
public class ExternalStatsCPUCollectorTest {

    @Test
    public void testExternalStatsCPUUtilizationCollector() throws Exception{

        String cpuName = "cpu3";
        ExternalStatsCPUUtilizationCollector cpuUtilizationCollector = new ExternalStatsCPUUtilizationCollector(cpuName);
        String statFilename = "src/test/resources/fortscale/monitoring/external/stats/linux/collector/parser/proc/files/stat";
        String diskStatsSeparator = " ";
        int indexOfKey = 0;

        ExternalStatsProcFileMultipleValueParser parser = new ExternalStatsProcFileMultipleValueParser(statFilename,diskStatsSeparator,new File(statFilename).getName(),indexOfKey);
        Map<String,ExternalStatsProcFileParser> parserMap = new HashMap<>();
        parserMap.put(parser.getName(),parser);

        cpuUtilizationCollector.collect(parserMap);

        ExternalStatsCPUUtilizationCollectorMetrics cpuMetrics = cpuUtilizationCollector.getUtilizationCollectorMetrics();
        Assert.assertEquals(7304287,cpuMetrics.getUser().longValue());
        Assert.assertEquals(2016854,cpuMetrics.getSystem().longValue());
        Assert.assertEquals(325,cpuMetrics.getNice().longValue());
        Assert.assertEquals(67717334,cpuMetrics.getIdle().longValue());
        Assert.assertEquals(15156,cpuMetrics.getWait().longValue());
        Assert.assertEquals(488,cpuMetrics.getHardwareInterrupts().longValue());
        Assert.assertEquals(194254,cpuMetrics.getSoftwareInterrupts().longValue());
        Assert.assertEquals(0,cpuMetrics.getSteal().longValue());

    }




}
