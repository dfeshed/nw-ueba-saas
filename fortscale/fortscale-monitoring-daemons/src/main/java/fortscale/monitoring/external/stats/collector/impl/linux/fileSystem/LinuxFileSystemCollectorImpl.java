package fortscale.monitoring.external.stats.collector.impl.linux.fileSystem;

import fortscale.monitoring.external.stats.collector.impl.ExternalStatsCollectorMetrics;
import fortscale.utils.logging.Logger;
import fortscale.utils.monitoring.stats.StatsService;
import fortscale.utils.system.FileSystemUtils;

import java.util.HashMap;
import java.util.Map;

/**
 * collects usage data of Linux paths
 */
public class LinuxFileSystemCollectorImpl {

    private final StatsService statsService;
    private final String[] paths;
    private HashMap<String, LinuxFileSystemCollectorImplMetrics> disksMetricsMap;
    private ExternalStatsCollectorMetrics selfMetrics;
    private FileSystemUtils fileSystemUtils;
    private static final Logger logger = Logger.getLogger(LinuxFileSystemCollectorImpl.class);

    /**
     * C'tor
     *
     * @param statsService - statistics service
     * @param paths        - file system paths
     */
    public LinuxFileSystemCollectorImpl(StatsService statsService, String[] paths, ExternalStatsCollectorMetrics selfMetrics) {
        this.disksMetricsMap = new HashMap<>();
        this.statsService = statsService;
        this.paths = paths;
        this.fileSystemUtils = new FileSystemUtils();
        this.selfMetrics = selfMetrics;
    }

    /**
     * collect stats from paths
     *
     * @param epochTime metric update time
     */
    public void collect(long epochTime) {
        for (String path : paths) {
            try {
                LinuxFileSystemCollectorImplMetrics metrics;

                // create metric if there isn't one
                if (!disksMetricsMap.containsKey(path)) {
                    logger.debug("Initiated metrics collection for disk={}", path);
                    metrics = new LinuxFileSystemCollectorImplMetrics(statsService, path);
                    disksMetricsMap.put(path, metrics);
                }

                // get metric
                metrics = disksMetricsMap.get(path);

                metrics.freeSpace = fileSystemUtils.getFreeSpace(path);
                metrics.totalSize = fileSystemUtils.getTotalSpace(path);
                metrics.usedSpace = metrics.totalSize - metrics.freeSpace;

                if(metrics.totalSize>0) {
                    metrics.freeSpacePercent = metrics.freeSpacePercent / metrics.totalSize;
                }
                else
                {
                    metrics.freeSpace=0;
                }

                metrics.manualUpdate(epochTime);

            } catch (Exception e) {
                selfMetrics.collectFailures++;
                String msg = String.format("error collecting file system disk space on path %s", path);
                logger.error(msg, e);
            }
        }
    }

    /**
     * getter
     *
     * @return map of disk metrics
     */
    public Map<String, LinuxFileSystemCollectorImplMetrics> getMetricsMap() {
        return disksMetricsMap;
    }
}
