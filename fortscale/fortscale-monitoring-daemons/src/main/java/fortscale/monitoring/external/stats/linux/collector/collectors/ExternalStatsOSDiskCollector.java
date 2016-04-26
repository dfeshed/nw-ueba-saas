package fortscale.monitoring.external.stats.linux.collector.collectors;

import fortscale.monitoring.external.stats.linux.collector.metrics.ExternalStatsOSDiskCollectorMetrics;
import fortscale.monitoring.external.stats.linux.collector.metrics.ExternalStatsOSMemoryCollectorMetrics;
import fortscale.monitoring.external.stats.linux.collector.parsers.ExternalStatsProcFileMultipleValueParser;
import fortscale.monitoring.external.stats.linux.collector.parsers.ExternalStatsProcFileParser;
import fortscale.utils.monitoring.stats.StatsMetricsGroupAttributes;

import java.util.Map;

/**
 * collcects statistics regarding a computer's disk: number of read/write bytes per second
 *
 *
 * Created by galiar on 25/04/2016.
 */
public class ExternalStatsOSDiskCollector extends AbstractExternalStatsCollector {

    private static final int READ_BYTES_INDEX = 4;
    private static final int WRITE_BYTES_INDEX = 7;
    private static final int UTILIZATION_INDEX = 14;
    private static final String DISK_STATS = "diskstats";

    private ExternalStatsOSDiskCollectorMetrics diskCollectorMetrics = new ExternalStatsOSDiskCollectorMetrics(new StatsMetricsGroupAttributes()); //TODO real attributes
    private String diskName;


    public ExternalStatsOSDiskCollector(String diskName){
        this.diskName = diskName;
    }


    @Override
    public void collect(Map<String, ExternalStatsProcFileParser> parsers) {

       ExternalStatsProcFileMultipleValueParser diskStatsParser = (ExternalStatsProcFileMultipleValueParser) parsers.get(DISK_STATS);

        Long readBytes = diskStatsParser.getValue(diskName).get(READ_BYTES_INDEX);
        Long writeBytes = diskStatsParser.getValue(diskName).get(WRITE_BYTES_INDEX);
        Long utilization = diskStatsParser.getValue(diskName).get(UTILIZATION_INDEX);

        diskCollectorMetrics.setReadBytes(readBytes);
        diskCollectorMetrics.setWriteBytes(writeBytes);
        diskCollectorMetrics.setUtilization(utilization);

    }

    //for testing only
    public ExternalStatsOSDiskCollectorMetrics getDiskCollectorMetrics() {
        return diskCollectorMetrics;
    }

}
