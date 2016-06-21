package fortscale.monitoring.external.stats.collector.impl.linux.device;

import fortscale.monitoring.external.stats.collector.impl.ExternalStatsCollectorMetrics;
import fortscale.monitoring.external.stats.collector.impl.linux.parsers.LinuxProcFileKeyMultipleValueParser;
import fortscale.utils.logging.Logger;
import fortscale.utils.monitoring.stats.StatsService;

import java.util.HashMap;
import java.util.Map;

/**
 * collects usage data of Linux excludedDevicesPrefix
 */
public class LinuxDeviceCollectorImpl {

    public static final String PROC_DISKSTATS = "/proc/diskstats";
    private final StatsService statsService;
    private String[] excludedDevicesPrefix;
    private HashMap<String, LinuxDeviceCollectorImplMetrics> deviceMetricsMap;
    private static final Logger logger = Logger.getLogger(LinuxDeviceCollectorImpl.class);
    private LinuxProcFileKeyMultipleValueParser parser;
    private ExternalStatsCollectorMetrics selfMetrics;

    /**
     * C'tor
     *
     * @param statsService - statistics service
     * @param excludedDevicesPrefixes      - excluded devices prefix names
     */
    public LinuxDeviceCollectorImpl(StatsService statsService, String[] excludedDevicesPrefixes) {
        this.deviceMetricsMap = new HashMap<>();
        this.statsService = statsService;
        this.excludedDevicesPrefix = excludedDevicesPrefixes;

        selfMetrics = new ExternalStatsCollectorMetrics(statsService,"linux.device");
        parser = new LinuxProcFileKeyMultipleValueParser(PROC_DISKSTATS, " ", 3);
    }

    /**
     * collect stats from excludedDevicesPrefix
     *
     * @param epochTime metric update time
     */
    public void collect(long epochTime) {

        // get all devices names, that are not in excluded device list
        String regex = (String.format("^(%s).*$", String.join("|", excludedDevicesPrefix)));
        String[] devices = parser.getKeys().stream().filter(x -> x.matches(regex)).toArray(String[]::new);

        // collect metrics for devices
        for (String device : devices) {
            try {
                LinuxDeviceCollectorImplMetrics metrics;

                // create metric if there isn't one
                if (!deviceMetricsMap.containsKey(device)) {
                    logger.debug("Initiated metrics collection for device={}", device);
                    metrics = new LinuxDeviceCollectorImplMetrics(statsService, device);
                    deviceMetricsMap.put(device, metrics);
                }

                // get stats
                metrics = deviceMetricsMap.get(device);
                metrics.readsCompletedSuccessfully = parser.getLongValue(device, 4);
                metrics.readsMerged = parser.getLongValue(device, 5);
                metrics.sectorsRead = parser.getLongValue(device, 6);
                metrics.timeSpentReading = parser.getLongValue(device, 7)/1000;
                metrics.writesCompleted = parser.getLongValue(device, 8);
                metrics.writesMerged = parser.getLongValue(device, 9);
                metrics.sectorsWritten = parser.getLongValue(device, 10);
                metrics.timeSpentWriting = parser.getLongValue(device, 11)/1000;
                metrics.IOCurrentlyInProgress = parser.getLongValue(device, 12);
                metrics.timeSpentDoingIO = parser.getLongValue(device, 13)/1000;
                metrics.weightedTimeSpentDoingIO = parser.getLongValue(device, 14)/1000;

                // update metrics collection time
                metrics.manualUpdate(epochTime);

                selfMetrics.statsCollectionSuccess++;

            } catch (Exception e) {
                selfMetrics.statsCollectionFailure++;
                String msg = String.format("error collecting device %s I/O ", device);
                logger.error(msg, e);
            }
        }
        selfMetrics.manualUpdate(epochTime);
    }

    /**
     * getter
     *
     * @return map of device metrics
     */
    public Map<String, LinuxDeviceCollectorImplMetrics> getMetricsMap() {
        return deviceMetricsMap;
    }
}
