package fortscale.monitoring.external.stats.collector.scheduler;

import fortscale.monitoring.external.stats.collector.parsers.ExternalStatsProcFileSingleValueParser;
import fortscale.monitoring.external.stats.collector.parsers.exceptions.ProcFileParserException;
import org.junit.Test;

/**
 * Created by galiar on 17/04/2016.
 */
public class ExternalStatsProcParserUtilsTest {


    @Test
    public void testParseFileAsMapOfSingleValue() throws Exception {

        /*String meminfoFilename = "src/test/resources/fortscale/monitoring/external/stats/collector/parser/proc/files/meminfo";
        String meminfoSeparator = ":";
        ExternalStatsProcFileParser externalStatsProcFileParser = new ExternalStatsProcFileParser();

        Map<String,String> meminfoMap = externalStatsProcFileParser.parseFileAsMapOfSingleValue(meminfoFilename,meminfoSeparator);
        Assert.assertEquals(meminfoMap.get("MemTotal"),32880764L);
        Assert.assertEquals(meminfoMap.get("DirectMap2M"),33544192L);
        Assert.assertEquals(meminfoMap.size(),42);

        String vmstatFilename = "src/test/resources/fortscale/monitoring/external/stats/collector/parser/proc/files/vmstat";
        String vmstatSeparator = " ";
        Map<String,String> vmstatMap = externalStatsProcFileParser.parseFileAsMapOfSingleValue(vmstatFilename,vmstatSeparator);
        Assert.assertEquals(vmstatMap.get("thp_fault_alloc"),275708L);
        Assert.assertEquals(vmstatMap.size(),66);
*/

        String meminfoFilename = "src/test/resources/fortscale/monitoring/external/stats/collector/parser/proc/files/meminfo";
        String meminfoSeparator = ":";
        ExternalStatsProcFileSingleValueParser singleValueParser = new ExternalStatsProcFileSingleValueParser(meminfoFilename,meminfoSeparator);
    }

    @Test(expected = ProcFileParserException.class)
    public void testParseFileAsMapOfSingleValueBad() throws Exception{

  /*      String meminfoFilenameBad = "src/test/resources/fortscale/monitoring/external/stats/collector/parser/proc/files/meminfo_bad";
        String meminfoSeparator = ":";
        ExternalStatsProcFileParser externalStatsProcFileParser = new ExternalStatsProcFileParser();

        Map<String,String> meminfoMapBad = externalStatsProcFileParser.parseFileAsMapOfSingleValue(meminfoFilenameBad,meminfoSeparator);*/
    }


    @Test
    public void testParseFileAsMapOfMultipleValues() throws Exception{
/*
        String statFilename = "src/test/resources/fortscale/monitoring/external/stats/collector/parser/proc/files/stat";
        String statSeparator = " ";
        ExternalStatsProcFileParser externalStatsProcFileParser = new ExternalStatsProcFileParser();

        Map<String,ArrayList<String>> statMap = externalStatsProcFileParser.parseFileAsMapOfMultipleValues(statFilename,statSeparator);
        Assert.assertEquals(statMap.get("cpu0").get(0),9023080L);
        Assert.assertEquals(statMap.get("cpu3").get(3),67717334L);
        Assert.assertEquals(statMap.get("softirq").get(10),624768346L);*/
    }

    @Test(expected = ProcFileParserException.class)
    public void testParseFileAsMapOfMultipleValueBad() throws Exception {

      /*  String statFilename = "src/test/resources/fortscale/monitoring/external/stats/collector/parser/proc/files/stat";
        String statSeparatorBad = ",";

        ExternalStatsProcFileParser externalStatsProcFileParser = new ExternalStatsProcFileParser();
        Map<String,ArrayList<String>> statMapBad = externalStatsProcFileParser.parseFileAsMapOfMultipleValues(statFilename,statSeparatorBad);*/
    }

}
