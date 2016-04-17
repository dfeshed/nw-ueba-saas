package fortscale.monitoring.external.stats.collector.scheduler;

import fortscale.monitoring.external.stats.collector.parsers.ExternalStatsProcFileParserUtils;
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

        String meminfoFilename = "src/test/resources/procFiles/proc/meminfo";
        String meminfoSeparator = ":";

        Map<String,String> meminfoMap = ExternalStatsProcFileParserUtils.parseFileAsMapOfSingleValue(meminfoFilename,meminfoSeparator);
        Assert.assertEquals(meminfoMap.get("MemTotal"),"32880764 kB");
        Assert.assertEquals(meminfoMap.get("DirectMap2M"),"33544192 kB");
        Assert.assertEquals(meminfoMap.size(),42);

        String meminfoFilenameBad = "src/test/resources/procFiles/proc/meminfo_bad";
        Map<String,String> meminfoMapBad = ExternalStatsProcFileParserUtils.parseFileAsMapOfSingleValue(meminfoFilenameBad,meminfoSeparator);
        Assert.assertNull(meminfoMapBad);

        String vmstatFilename = "src/test/resources/procFiles/proc/vmstat";
        String vmstatSeparator = " ";
        Map<String,String> vmstatMap = ExternalStatsProcFileParserUtils.parseFileAsMapOfSingleValue(vmstatFilename,vmstatSeparator);
        Assert.assertEquals(vmstatMap.get("thp_fault_alloc"),"275708");
        Assert.assertEquals(vmstatMap.size(),66);

    }
    @Test
    public void testParseFileAsMapOfMultipleValues() throws Exception{

        String statFilename = "src/test/resources/procFiles/proc/stat";
        String statSeparator = " ";

        Map<String,ArrayList<String>> statMap = ExternalStatsProcFileParserUtils.parseFileAsMapOfMultipleValues(statFilename,statSeparator);
        Assert.assertEquals(statMap.get("cpu0").get(0),"9023080");
        Assert.assertEquals(statMap.get("cpu3").get(3),"67717334");
        Assert.assertEquals(statMap.get("softirq").get(10),"624768346");

        String statSeparatorBad = ",";
        Map<String,ArrayList<String>> statMapBad = ExternalStatsProcFileParserUtils.parseFileAsMapOfMultipleValues(statFilename,statSeparatorBad);
        Assert.assertNull(statMapBad);
    }


}
