package fortscale.monitoring.external.stats.linux.collector;

import fortscale.monitoring.external.stats.linux.collector.collectors.ExternalStatsOSMemoryCollector;
import fortscale.monitoring.external.stats.linux.collector.parsers.ExternalStatsProcFileSingleValueParser;
import org.junit.Test;

import java.util.ArrayList;
import java.util.Arrays;

/**
 * Created by galiar on 21/04/2016.
 */
public class ExternalStatsOSMemoryCollectorTest {

    //test the reading is good

    //test the metrices are written properly.

    //test collect(parser)

    @Test
    public void testOSMemoryCollector() throws Exception{

        ExternalStatsOSMemoryCollector collector = new ExternalStatsOSMemoryCollector();

        String meminfoFilename = "src/test/resources/fortscale/monitoring/external/stats/linux/collector/parser/proc/files/meminfo";
        String meminfoSeparator = ":";
        ExternalStatsProcFileSingleValueParser meminfoValueParser = new ExternalStatsProcFileSingleValueParser(meminfoFilename,meminfoSeparator);
        String vmstatFilename = "src/test/resources/fortscale/monitoring/external/stats/linux/collector/parser/proc/files/vmstat";
        String vmstatSeparator = " ";
        ExternalStatsProcFileSingleValueParser vmstatValueParser = new ExternalStatsProcFileSingleValueParser(vmstatFilename,vmstatSeparator);

        collector.collect(new ArrayList<>(Arrays.asList(meminfoValueParser,vmstatValueParser)));


    }





}
