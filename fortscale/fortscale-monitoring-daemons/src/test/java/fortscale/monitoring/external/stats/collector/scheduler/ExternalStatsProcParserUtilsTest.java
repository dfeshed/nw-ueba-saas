package fortscale.monitoring.external.stats.collector.scheduler;

import fortscale.monitoring.external.stats.collector.parsers.ExternalStatsProcFileParserUtils;
import fortscale.monitoring.external.stats.collector.parsers.ProcFileParserException;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.python.antlr.ast.Str;

import java.util.ArrayList;
import java.util.Map;

/**
 * Created by galiar on 17/04/2016.
 */
public class ExternalStatsProcParserUtilsTest {


    @Test
    public void testParseFileAsMapOfSingleValue() throws Exception {

        String meminfoFilename = "src/test/resources/fortscale/monitoring/external/stats/collector/parser/proc/files/meminfo";
        String meminfoSeparator = ":";
        ExternalStatsProcFileParserUtils externalStatsProcFileParserUtils = new ExternalStatsProcFileParserUtils();

        Map<String,String> meminfoMap = externalStatsProcFileParserUtils.parseFileAsMapOfSingleValue(meminfoFilename,meminfoSeparator);
        Assert.assertEquals(meminfoMap.get("MemTotal"),32880764L);
        Assert.assertEquals(meminfoMap.get("DirectMap2M"),33544192L);
        Assert.assertEquals(meminfoMap.size(),42);

        String vmstatFilename = "src/test/resources/fortscale/monitoring/external/stats/collector/parser/proc/files/vmstat";
        String vmstatSeparator = " ";
        Map<String,String> vmstatMap = externalStatsProcFileParserUtils.parseFileAsMapOfSingleValue(vmstatFilename,vmstatSeparator);
        Assert.assertEquals(vmstatMap.get("thp_fault_alloc"),275708L);
        Assert.assertEquals(vmstatMap.size(),66);

    }

    @Test(expected = ProcFileParserException.class)
    public void testParseFileAsMapOfSingleValueBad() throws Exception{

        String meminfoFilenameBad = "src/test/resources/fortscale/monitoring/external/stats/collector/parser/proc/files/meminfo_bad";
        String meminfoSeparator = ":";
        ExternalStatsProcFileParserUtils externalStatsProcFileParserUtils = new ExternalStatsProcFileParserUtils();

        Map<String,String> meminfoMapBad = externalStatsProcFileParserUtils.parseFileAsMapOfSingleValue(meminfoFilenameBad,meminfoSeparator);
    }


    @Test
    public void testParseFileAsMapOfMultipleValues() throws Exception{

        String statFilename = "src/test/resources/fortscale/monitoring/external/stats/collector/parser/proc/files/stat";
        String statSeparator = " ";
        ExternalStatsProcFileParserUtils externalStatsProcFileParserUtils = new ExternalStatsProcFileParserUtils();

        Map<String,ArrayList<String>> statMap = externalStatsProcFileParserUtils.parseFileAsMapOfMultipleValues(statFilename,statSeparator);
        Assert.assertEquals(statMap.get("cpu0").get(0),9023080L);
        Assert.assertEquals(statMap.get("cpu3").get(3),67717334L);
        Assert.assertEquals(statMap.get("softirq").get(10),624768346L);
    }

    @Test(expected = ProcFileParserException.class)
    public void testParseFileAsMapOfMultipleValueBad() throws Exception {

        String statFilename = "src/test/resources/fortscale/monitoring/external/stats/collector/parser/proc/files/stat";
        String statSeparatorBad = ",";

        ExternalStatsProcFileParserUtils externalStatsProcFileParserUtils = new ExternalStatsProcFileParserUtils();
        Map<String,ArrayList<String>> statMapBad = externalStatsProcFileParserUtils.parseFileAsMapOfMultipleValues(statFilename,statSeparatorBad);
    }

}
