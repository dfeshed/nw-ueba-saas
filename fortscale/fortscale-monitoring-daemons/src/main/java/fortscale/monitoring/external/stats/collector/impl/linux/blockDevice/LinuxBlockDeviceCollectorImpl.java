package fortscale.monitoring.external.stats.collector.impl.linux.blockDevice;

import fortscale.monitoring.external.stats.collector.impl.ExternalStatsCollectorMetrics;
import fortscale.monitoring.external.stats.collector.impl.linux.parsers.LinuxProcFileKeyMultipleValueParser;
import fortscale.utils.logging.Logger;
import fortscale.utils.monitoring.stats.StatsService;

import java.util.HashMap;
import java.util.Map;

/**
 * collects usage data of Linux excludedDevicesPrefix
 */
public class LinuxBlockDeviceCollectorImpl {

    public static final String PROC_DISKSTATS = "/proc/diskstats";
    private final StatsService statsService;
    private String[] excludedDevicesPrefix;
    private HashMap<String, LinuxBlockDeviceCollectorImplMetrics> deviceMetricsMap;
    private static final Logger logger = Logger.getLogger(LinuxBlockDeviceCollectorImpl.class);
    private LinuxProcFileKeyMultipleValueParser parser;
    private ExternalStatsCollectorMetrics selfMetrics;

    /**
     * C'tor
     *
     * @param statsService - statistics service
     * @param excludedDevicesPrefixes      - excluded devices prefix names
     */
    public LinuxBlockDeviceCollectorImpl(StatsService statsService, String[] excludedDevicesPrefixes, ExternalStatsCollectorMetrics selfMetrics) {
        this.deviceMetricsMap = new HashMap<>();
        this.statsService = statsService;
        this.excludedDevicesPrefix = excludedDevicesPrefixes;
        this.selfMetrics = selfMetrics;
    }

    /**
     * collect stats from excludedDevicesPrefix
     *
     * @param epochTime metric update time
     */
    public void collect(long epochTime) {

        // get all devices names, that are not in excluded device list
        String regex = (String.format("^(%s).*$", String.join("|", excludedDevicesPrefix)));
        parser = new LinuxProcFileKeyMultipleValueParser(PROC_DISKSTATS, " ", 3);
        String[] devices = parser.getKeys().stream().filter(x -> !x.matches(regex)).toArray(String[]::new);

        // collect metrics for devices
        for (String device : devices) {
            try {
                LinuxBlockDeviceCollectorImplMetrics metrics;

                // create metric if there isn't one
                if (!deviceMetricsMap.containsKey(device)) {
                    logger.debug("Initiated metrics collection for device={}", device);
                    metrics = new LinuxBlockDeviceCollectorImplMetrics(statsService, device);
                    deviceMetricsMap.put(device, metrics);
                }

//                parsing "/proc/diskstats"
//                4 - reads completed successfully
//                5 - reads merged
//                6 - sectors read
//                7 - time spent reading (ms)
//                8 - writes completed
//                9 - writes merged
//                10 - sectors written
//                11 - time spent writing (ms)
//                12 - I/Os currently in progress
//                13 - time spent doing I/Os (ms)
//                14 - weighted time spent doing I/Os (ms)
                metrics = deviceMetricsMap.get(device);
                metrics.readsCompletedSuccessfully = parser.getLongValue(device, 4);
                metrics.readsMerged = parser.getLongValue(device, 5);
                metrics.sectorsRead = parser.getLongValue(device, 6);
                metrics.timeSpentReadingMilli = parser.getLongValue(device, 7);
                metrics.writesCompleted = parser.getLongValue(device, 8);
                metrics.writesMerged = parser.getLongValue(device, 9);
                metrics.sectorsWritten = parser.getLongValue(device, 10);
                metrics.timeSpentWritingMilli = parser.getLongValue(device, 11);
                metrics.IOCurrentlyInProgress = parser.getLongValue(device, 12);
                metrics.timeSpentDoingIOMilli = parser.getLongValue(device, 13);
                metrics.weightedTimeSpentDoingIOMilli = parser.getLongValue(device, 14);

                // update metrics collection time
                metrics.manualUpdate(epochTime);

            } catch (Exception e) {
                selfMetrics.collectFailures++;
                String msg = String.format("error collecting device %s I/O ", device);
                logger.error(msg, e);
            }
        }
    }

    /**
     * getter
     *
     * @return map of device metrics
     */
    public Map<String, LinuxBlockDeviceCollectorImplMetrics> getMetricsMap() {
        return deviceMetricsMap;
    }
}
