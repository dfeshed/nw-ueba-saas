package fortscale.monitoring.external.stats.collector.impl.linux.parsers;

import fortscale.monitoring.external.stats.collector.impl.linux.parsers.exceptions.ProcFileParserException;
import fortscale.monitoring.external.stats.collector.impl.linux.parsers.exceptions.ProcFileReadFailureException;
import org.junit.Assert;
import org.junit.Test;

/**
 * Created by galiar on 17/04/2016.
 */
public class ExternalStatsProcParserTest {

    final String TEST_PROC_BASE_PATH = "src/test/resources/fortscale/monitoring/external/stats/collector/impl/linux/proc/files";

    @Test
    public void testExternalStatsProcFileSingleValueParser() throws Exception {


        String meminfoFilename = "meminfo";
        String meminfoSeparator = ":";
        LinuxProcFileKeyValueParser meminfoValueParser = new LinuxProcFileKeyValueParser(TEST_PROC_BASE_PATH, meminfoFilename, meminfoSeparator);
        Assert.assertEquals(meminfoValueParser.getValue("MemTotal").longValue(),32880764L);
        Assert.assertEquals(meminfoValueParser.getValue("DirectMap2M").longValue(),33544192L);

        String vmstatFilename = "vmstat";
        String vmstatSeparator = " ";
        LinuxProcFileKeyValueParser vmstatValueParser = new LinuxProcFileKeyValueParser(TEST_PROC_BASE_PATH, vmstatFilename,vmstatSeparator);
        Assert.assertEquals(vmstatValueParser.getValue("thp_fault_alloc").longValue(),275708L);

    }

    @Test( expected = ProcFileReadFailureException.class)
    public void testParseFileAsMapOfSingleValueBad() throws Exception{

        String meminfoFilenameBad = "does-not-exist-file";
        String meminfoSeparator = ":";
        LinuxProcFileKeyValueParser badParser = new LinuxProcFileKeyValueParser(TEST_PROC_BASE_PATH, meminfoFilenameBad,meminfoSeparator);
        badParser.getValue("MemTotal");
    }


//    @Test
//    public void testExternalStatsProcFileMultipleValueParser() throws Exception{
//
//        String statFilename = "src/test/resources/fortscale/monitoring/external/stats/linux/collector/parser/proc/files/stat";
//        String statSeparator = " ";
//        LinuxProcFileKeyMultipleValueParser statParser = new LinuxProcFileKeyMultipleValueParser(statFilename,statSeparator,new File(statFilename).getName(),0,new ArrayList<>(Arrays.asList(0)));
//        Assert.assertEquals(9023080L,statParser.getValue("cpu0").get(0).longValue());
//        Assert.assertEquals(67717334L,statParser.getValue("cpu3").get(3).longValue());
//        Assert.assertEquals(624768346L,statParser.getValue("softirq").get(10).longValue());
//    }
//
//    @Test(expected = ProcFileParserException.class)
//    public void testExternalStatsProcFileMultipleValueParserBad() throws Exception {
//
//        String statFilename = "src/test/resources/fortscale/monitoring/external/stats/linux/collector/parser/proc/files/stat";
//        String statSeparatorBad = ",";
//        LinuxProcFileKeyMultipleValueParser badParser = new LinuxProcFileKeyMultipleValueParser(statFilename,statSeparatorBad,new File(statFilename).getName(),0);
//        badParser.getValue("softirq");
//
//
//    }

}
