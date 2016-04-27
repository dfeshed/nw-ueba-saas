package fortscale.monitoring.external.stats.linux.collector;

import fortscale.monitoring.external.stats.linux.collector.parsers.ExternalStatsProcFileMultipleValueParser;
import fortscale.monitoring.external.stats.linux.collector.parsers.ExternalStatsProcFileSingleValueParser;
import fortscale.monitoring.external.stats.linux.collector.parsers.exceptions.ProcFileParserException;
import org.junit.Assert;
import org.junit.Test;

import java.io.File;

/**
 * Created by galiar on 17/04/2016.
 */
public class ExternalStatsProcParserTest {


    @Test
    public void testExternalStatsProcFileSingleValueParser() throws Exception {

        String meminfoFilename = "src/test/resources/fortscale/monitoring/external/stats/linux/collector/parser/proc/files/meminfo";
        String meminfoSeparator = ":";
        ExternalStatsProcFileSingleValueParser meminfoValueParser = new ExternalStatsProcFileSingleValueParser(meminfoFilename,meminfoSeparator,new File(meminfoFilename).getName());
        Assert.assertEquals(meminfoValueParser.getValue("MemTotal").longValue(),32880764L);
        Assert.assertEquals(meminfoValueParser.getValue("DirectMap2M").longValue(),33544192L);

        String vmstatFilename = "src/test/resources/fortscale/monitoring/external/stats/linux/collector/parser/proc/files/vmstat";
        String vmstatSeparator = " ";
        ExternalStatsProcFileSingleValueParser vmstatValueParser = new ExternalStatsProcFileSingleValueParser(vmstatFilename,vmstatSeparator,new File(vmstatFilename).getName());
        Assert.assertEquals(vmstatValueParser.getValue("thp_fault_alloc").longValue(),275708L);

    }

    @Test(expected = ProcFileParserException.class)
    public void testParseFileAsMapOfSingleValueBad() throws Exception{

        String meminfoFilenameBad = "src/test/resources/fortscale/monitoring/external/stats/collector/parser/proc/files/meminfo_bad";
        String meminfoSeparator = ":";
        ExternalStatsProcFileSingleValueParser badParser = new ExternalStatsProcFileSingleValueParser(meminfoFilenameBad,meminfoSeparator,new File(meminfoFilenameBad).getName());
        badParser.getValue("MemTotal");
    }


    @Test
    public void testExternalStatsProcFileMultipleValueParser() throws Exception{

        String statFilename = "src/test/resources/fortscale/monitoring/external/stats/linux/collector/parser/proc/files/stat";
        String statSeparator = " ";
        ExternalStatsProcFileMultipleValueParser statParser = new ExternalStatsProcFileMultipleValueParser(statFilename,statSeparator,new File(statFilename).getName(),0);
        Assert.assertEquals(statParser.getValue("cpu0").get(0).longValue(),9023080L);
        Assert.assertEquals(statParser.getValue("cpu3").get(3).longValue(),67717334L);
        Assert.assertEquals(statParser.getValue("softirq").get(10).longValue(),624768346L);
    }

    @Test(expected = ProcFileParserException.class)
    public void testExternalStatsProcFileMultipleValueParserBad() throws Exception {

        String statFilename = "src/test/resources/fortscale/monitoring/external/stats/linux/collector/parser/proc/files/stat";
        String statSeparatorBad = ",";
        ExternalStatsProcFileMultipleValueParser badParser = new ExternalStatsProcFileMultipleValueParser(statFilename,statSeparatorBad,new File(statFilename).getName(),0);
        badParser.getValue("softirq");


    }

}
