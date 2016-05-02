package fortscale.monitoring.external.stats.linux.collector;

import fortscale.monitoring.external.stats.linux.collector.parsers.ExternalStatsProcFileKeyValueParser;
import fortscale.monitoring.external.stats.linux.collector.parsers.ExternalStatsProcFileKeyMultipleValueParser;
import fortscale.monitoring.external.stats.linux.collector.parsers.exceptions.ProcFileParserException;
import org.junit.Assert;
import org.junit.Test;

import java.io.File;
import java.util.ArrayList;
import java.util.Arrays;

/**
 * Created by galiar on 17/04/2016.
 */
public class ExternalStatsProcParserTest {


    @Test
    public void testExternalStatsProcFileSingleValueParser() throws Exception {

        String meminfoFilename = "src/test/resources/fortscale/monitoring/external/stats/linux/collector/parser/proc/files/meminfo";
        String meminfoSeparator = ":";
        ExternalStatsProcFileKeyValueParser meminfoValueParser = new ExternalStatsProcFileKeyValueParser(meminfoFilename,meminfoSeparator,new File(meminfoFilename).getName());
        Assert.assertEquals(meminfoValueParser.getValue("MemTotal").longValue(),32880764L);
        Assert.assertEquals(meminfoValueParser.getValue("DirectMap2M").longValue(),33544192L);

        String vmstatFilename = "src/test/resources/fortscale/monitoring/external/stats/linux/collector/parser/proc/files/vmstat";
        String vmstatSeparator = " ";
        ExternalStatsProcFileKeyValueParser vmstatValueParser = new ExternalStatsProcFileKeyValueParser(vmstatFilename,vmstatSeparator,new File(vmstatFilename).getName());
        Assert.assertEquals(vmstatValueParser.getValue("thp_fault_alloc").longValue(),275708L);

    }

    @Test(expected = ProcFileParserException.class)
    public void testParseFileAsMapOfSingleValueBad() throws Exception{

        String meminfoFilenameBad = "src/test/resources/fortscale/monitoring/external/stats/collector/parser/proc/files/meminfo_bad";
        String meminfoSeparator = ":";
        ExternalStatsProcFileKeyValueParser badParser = new ExternalStatsProcFileKeyValueParser(meminfoFilenameBad,meminfoSeparator,new File(meminfoFilenameBad).getName());
        badParser.getValue("MemTotal");
    }


    @Test
    public void testExternalStatsProcFileMultipleValueParser() throws Exception{

        String statFilename = "src/test/resources/fortscale/monitoring/external/stats/linux/collector/parser/proc/files/stat";
        String statSeparator = " ";
        ExternalStatsProcFileKeyMultipleValueParser statParser = new ExternalStatsProcFileKeyMultipleValueParser(statFilename,statSeparator,new File(statFilename).getName(),0,new ArrayList<>(Arrays.asList(0)));
        Assert.assertEquals(9023080L,statParser.getValue("cpu0").get(0).longValue());
        Assert.assertEquals(67717334L,statParser.getValue("cpu3").get(3).longValue());
        Assert.assertEquals(624768346L,statParser.getValue("softirq").get(10).longValue());
    }

    @Test(expected = ProcFileParserException.class)
    public void testExternalStatsProcFileMultipleValueParserBad() throws Exception {

        String statFilename = "src/test/resources/fortscale/monitoring/external/stats/linux/collector/parser/proc/files/stat";
        String statSeparatorBad = ",";
        ExternalStatsProcFileKeyMultipleValueParser badParser = new ExternalStatsProcFileKeyMultipleValueParser(statFilename,statSeparatorBad,new File(statFilename).getName(),0);
        badParser.getValue("softirq");


    }

}
