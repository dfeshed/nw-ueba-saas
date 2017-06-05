package fortscale.monitoring.external.stats.collector.impl.linux.blockDevice;

import fortscale.monitoring.external.stats.collector.impl.ExternalStatsCollectorMetrics;
import fortscale.utils.system.operatingSystem.OperatingSystemUtils;
import org.junit.Assert;
import org.junit.Assume;
import org.junit.Before;
import org.junit.Test;

public class LinuxDeviceCollectorTest {

    private OperatingSystemUtils operatingSystemUtils;
    private boolean isWinOperatingSystem;

    public LinuxDeviceCollectorTest() {
        isWinOperatingSystem = OperatingSystemUtils.isWinOperatingSystem();
    }

    @Before
    public void beforeMethod() {
        Assume.assumeFalse(isWinOperatingSystem);
    }

    @Test
    public void shouldCollectSDADeviceStats()
    {
        String device = "sda";
        ExternalStatsCollectorMetrics selfMetrics = new ExternalStatsCollectorMetrics(null,"test");

        String [] devices = {"test"};
        LinuxBlockDeviceCollectorImpl collector = new LinuxBlockDeviceCollectorImpl(null,devices,selfMetrics);
        collector.collect(0);

        LinuxBlockDeviceCollectorImplMetrics metrics = collector.getMetricsMap().get(device);

        Assert.assertNotEquals(0,metrics.timeSpentReadingMilli);
        Assert.assertNotEquals(0,metrics.timeSpentWritingMilli);
        Assert.assertNotEquals(0,metrics.timeSpentDoingIOMilli);
    }
}
