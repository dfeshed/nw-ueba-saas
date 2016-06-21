package fortscale.monitoring.external.stats.collector.impl.linux.disk;

import fortscale.monitoring.external.stats.collector.impl.ExternalStatsCollectorMetrics;
import fortscale.utils.logging.Logger;
import fortscale.utils.monitoring.stats.StatsService;
import fortscale.utils.system.FileSystemUtils;

import java.util.HashMap;
import java.util.Map;

/**
 * collects usage data of Linux disks
 */
public class LinuxDiskCollectorImpl {

    private final StatsService statsService;
    private final String[] disks;
    private HashMap<String, LinuxDiskCollectorImplMetrics> disksMetricsMap;
    private ExternalStatsCollectorMetrics selfMetrics;
    private FileSystemUtils fileSystemUtils;
    private static final Logger logger = Logger.getLogger(LinuxDiskCollectorImpl.class);

    /**
     * C'tor
     *
     * @param statsService - statistics service
     * @param disks        - disks paths
     */
    public LinuxDiskCollectorImpl(StatsService statsService, String[] disks) {
        this.disksMetricsMap = new HashMap<>();
        this.statsService = statsService;
        this.disks = disks;
        this.fileSystemUtils = new FileSystemUtils();
        this.selfMetrics = new ExternalStatsCollectorMetrics(this.statsService, "linux.disk");
    }

    /**
     * collect stats from disks
     *
     * @param epochTime metric update time
     */
    public void collect(long epochTime) {
        for (String disk : disks) {
            try {
                LinuxDiskCollectorImplMetrics metrics;

                // create metric if there isn't one
                if (!disksMetricsMap.containsKey(disk)) {
                    logger.debug("Initiated metrics collection for disk={}", disk);
                    metrics = new LinuxDiskCollectorImplMetrics(statsService, disk);
                    disksMetricsMap.put(disk, metrics);
                }

                // get metric
                metrics = disksMetricsMap.get(disk);

                metrics.freeSpace = fileSystemUtils.getFreeSpace(disk);
                metrics.totalFileSystemSize = fileSystemUtils.getTotalSpace(disk);
                metrics.usedSpace = metrics.totalFileSystemSize - metrics.freeSpace;
                metrics.manualUpdate(epochTime);

                selfMetrics.statsCollectionSuccess++;
            } catch (Exception e) {
                selfMetrics.statsCollectionFailure++;
                String msg = String.format("error collecting file system disk space on path %s", disk);
                logger.error(msg, e);
            }
        }

        selfMetrics.manualUpdate(epochTime);
    }

    /**
     * getter
     *
     * @return map of disk metrics
     */
    public Map<String, LinuxDiskCollectorImplMetrics> getMetricsMap() {
        return disksMetricsMap;
    }
}
