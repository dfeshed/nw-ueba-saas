package fortscale.monitoring.external.stats.collector.impl.linux.fileSystem;


import fortscale.monitoring.external.stats.collector.impl.ExternalStatsCollectorMetrics;
import fortscale.utils.system.operatingSystem.OperatingSystemUtils;
import org.junit.Assert;
import org.junit.Assume;
import org.junit.Before;
import org.junit.Test;

import java.util.Map;

public class LinuxFileSystemCollectorTest {

    private OperatingSystemUtils operatingSystemUtils;
    private boolean isWinOperatingSystem;

    public LinuxFileSystemCollectorTest() {
        isWinOperatingSystem = OperatingSystemUtils.isWinOperatingSystem();
    }

    @Before
    public void beforeMethod() {
        Assume.assumeFalse(isWinOperatingSystem);
    }

    @Test
    public void shouldCollectHomeDirDiskStats()
    {
        String diskPath = "/home";
        String [] disks = {diskPath};
        ExternalStatsCollectorMetrics selfMetrics = new ExternalStatsCollectorMetrics(null,"test");

        LinuxFileSystemCollectorImpl collector = new LinuxFileSystemCollectorImpl(null,disks,selfMetrics);
        collector.collect(0);

        LinuxFileSystemCollectorImplMetrics metrics = collector.getMetricsMap().get(diskPath);

        Assert.assertNotEquals(0,metrics.totalSize);
        Assert.assertNotEquals(0,metrics.freeSpace);
        Assert.assertNotEquals(0,metrics.usedSpace);
    }

    @Test
    public void shouldCollectNothing()
    {
        ExternalStatsCollectorMetrics selfMetrics = new ExternalStatsCollectorMetrics(null,"test");

        String [] disks = {};
        LinuxFileSystemCollectorImpl collector = new LinuxFileSystemCollectorImpl(null,disks,selfMetrics);
        collector.collect(0);

        Map metricsMap = collector.getMetricsMap();

        Assert.assertEquals(0,metricsMap.size());
    }
}

