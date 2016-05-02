package fortscale.monitoring.external.stats.linux.collector.collectors;

import fortscale.monitoring.external.stats.linux.collector.metrics.ExternalStatsFileSystemCollectorMetrics;
import fortscale.monitoring.external.stats.linux.collector.parsers.ExternalStatsProcFileParser;
import fortscale.utils.monitoring.stats.StatsMetricsGroupAttributes;

import java.io.File;
import java.util.Map;

/**
 * collects metrics about the file system: what its size, free space and used space - in MB
 * Created by galiar on 01/05/2016.
 */
public class ExternalStatsFileSystemCollector  extends AbstractExternalStatsCollector {

    private ExternalStatsFileSystemCollectorMetrics fileSystemMetrics = new ExternalStatsFileSystemCollectorMetrics(new StatsMetricsGroupAttributes()); //TODO user real attributes
    private String rootDir;

    public ExternalStatsFileSystemCollector(String rootDir){
        this.rootDir = rootDir;
    }

    @Override
    public void collect(Map<String, ExternalStatsProcFileParser> parsers) {

        //the data returns relates to the entire FS, rather than the directory.
        // e.g. the results when rootDir == D: and when rootDir == D:\subDirectory\verySpecificFile will be identical,
        // since they both reside in D: file system
        // the results would be different (probably) to rootDir = D: and rootDir == C:
        Long freeDiskSpace = convertBytesToMB(new File(rootDir).getFreeSpace());
        Long totalDiskSpace = convertBytesToMB(new File(rootDir).getTotalSpace());
        Long usedDiskSpace = totalDiskSpace - freeDiskSpace;

        fileSystemMetrics.setFreeSpace(freeDiskSpace);
        fileSystemMetrics.setTotalFileSystemSize(totalDiskSpace);
        fileSystemMetrics.setUsedSpace(usedDiskSpace);

    }

    public ExternalStatsFileSystemCollectorMetrics getFileSystemMetrics() {
        return fileSystemMetrics;
    }
}
