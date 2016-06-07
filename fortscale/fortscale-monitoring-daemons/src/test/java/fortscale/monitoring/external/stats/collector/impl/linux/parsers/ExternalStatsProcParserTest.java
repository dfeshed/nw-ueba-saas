package fortscale.monitoring.external.stats.collector.impl.linux.parsers;

import fortscale.monitoring.external.stats.collector.impl.linux.parsers.exceptions.ProcFileBadFormatException;
import fortscale.monitoring.external.stats.collector.impl.linux.parsers.exceptions.ProcFileParserException;
import fortscale.monitoring.external.stats.collector.impl.linux.parsers.exceptions.ProcFileReadFailureException;
import org.junit.Assert;
import org.junit.Test;

import java.io.File;

/**
 * Created by galiar on 17/04/2016.
 */
public class ExternalStatsProcParserTest {

    final String TEST_PROC_BASE_PATH = "src/test/resources/fortscale/monitoring/external/stats/collector/impl/linux/proc/files";

    String calcFilepath(String filename) {

        String result = new File(TEST_PROC_BASE_PATH, filename).toString();

        return result;
    }

    @Test
    public void testExternalStatsProcFileSingleValueParser() throws Exception {


        String meminfoFilename = "meminfo";
        String meminfoSeparator = ":";
        LinuxProcFileKeyValueParser meminfoValueParser = new LinuxProcFileKeyValueParser(calcFilepath(meminfoFilename), meminfoSeparator);
        Assert.assertEquals(meminfoValueParser.getValue("MemTotal").longValue(),32880764L);
        Assert.assertEquals(meminfoValueParser.getValue("DirectMap2M").longValue(),33544192L);

        String vmstatFilename = "vmstat";
        String vmstatSeparator = " ";
        LinuxProcFileKeyValueParser vmstatValueParser = new LinuxProcFileKeyValueParser(calcFilepath(vmstatFilename),vmstatSeparator);
        Assert.assertEquals(vmstatValueParser.getValue("thp_fault_alloc").longValue(),275708L);

    }

    @Test( expected = ProcFileReadFailureException.class)
    public void testParseFileAsMapOfSingleValueBad() throws Exception{

        String meminfoFilenameBad = "does-not-exist-file";
        String meminfoSeparator = ":";
        LinuxProcFileKeyValueParser badParser = new LinuxProcFileKeyValueParser(calcFilepath(meminfoFilenameBad), meminfoSeparator);
        badParser.getValue("MemTotal");
    }


    @Test
    public void testPaeseMultipleValueParser() throws Exception{

        String statFilename = "stat";
        String statSeparator = " ";
        LinuxProcFileKeyMultipleValueParser statParser = new LinuxProcFileKeyMultipleValueParser(calcFilepath(statFilename),statSeparator, 0);
        Assert.assertEquals(9023080L  ,statParser.getLongValue("cpu0",  1));
        Assert.assertEquals(67717334L ,statParser.getLongValue("cpu3",  4));
        Assert.assertEquals(624768346L,statParser.getLongValue("softirq", 11));
    }

    @Test(expected = ProcFileBadFormatException.class)
    public void testPaeseMultipleValueParserBad() throws Exception {

        String statFilename = "stat";
        String statSeparatorBad = ",";
        LinuxProcFileKeyMultipleValueParser statParser = new LinuxProcFileKeyMultipleValueParser(calcFilepath(statFilename),statSeparatorBad, 0);
        statParser.getLongValue("cpu0", 10);

    }

    @Test(expected = ProcFileBadFormatException.class)
    public void testPaeseMultipleValueParserBadKeyIndex() throws Exception {

        String statFilename = "stat";
        String statSeparator = " ";
        LinuxProcFileKeyMultipleValueParser statParser = new LinuxProcFileKeyMultipleValueParser(calcFilepath(statFilename), statSeparator, 2);
        statParser.getLongValue("processes", 0);
    }

    @Test(expected = ProcFileBadFormatException.class)
    public void testPaeseMultipleValueParserBadKey() throws Exception {

        String statFilename = "stat";
        String statSeparator = " ";
        LinuxProcFileKeyMultipleValueParser statParser = new LinuxProcFileKeyMultipleValueParser(calcFilepath(statFilename), statSeparator, 0);
        statParser.getLongValue("xxxx", 1);
    }

    @Test(expected = ProcFileBadFormatException.class)
    public void testPaeseMultipleValueParserBadFieldIndex() throws Exception {

        String statFilename = "stat";
        String statSeparator = " ";
        LinuxProcFileKeyMultipleValueParser statParser = new LinuxProcFileKeyMultipleValueParser(calcFilepath(statFilename), statSeparator, 0);
        statParser.getLongValue("processes", 2);
    }

    @Test(expected = ProcFileBadFormatException.class)
    public void testPaeseMultipleValueParserBadValue() throws Exception {

        String statFilename = "stat";
        String statSeparator = " ";
        LinuxProcFileKeyMultipleValueParser statParser = new LinuxProcFileKeyMultipleValueParser(calcFilepath(statFilename), statSeparator, 0);
        statParser.getLongValue("processes", 0);
    }


    @Test
    public void testPaeseMultipleValueParserSingleLine() throws Exception{

        String statFilename = "123/stat";
        String statSeparator = " ";
        LinuxProcFileKeyMultipleValueParser statParser = new LinuxProcFileKeyMultipleValueParser(calcFilepath(statFilename),statSeparator);
        Assert.assertEquals(1679L ,statParser.getLongValue(3));
    }

    @Test
    public void testLinuxProcFileSingleValueParser() throws Exception{

        String cmdlineFilename = "123/cmdline";
        LinuxProcFileSingleValueParser parser = new LinuxProcFileSingleValueParser(calcFilepath(cmdlineFilename));
        String expected = "xterm%-bg%#009966%-title%Green4%-geometry% 45x115+111+119%-sl%5000%-rightbar%-aw%".replace("%","\0");
        Assert.assertEquals(expected ,parser.getData());
    }



}
