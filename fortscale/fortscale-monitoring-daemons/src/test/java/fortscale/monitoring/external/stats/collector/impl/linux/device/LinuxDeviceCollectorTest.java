package fortscale.monitoring.external.stats.collector.impl.linux.device;

import fortscale.monitoring.external.stats.collector.impl.ExternalStatsCollectorMetrics;
import org.junit.Assert;
import org.junit.Test;


public class LinuxDeviceCollectorTest {

    @Test
    public void shouldCollectSDADeviceStats()
    {
        String device = "sda";
        ExternalStatsCollectorMetrics selfMetrics = new ExternalStatsCollectorMetrics(null,"test");

        String [] devices = {device};
        LinuxDeviceCollectorImpl collector = new LinuxDeviceCollectorImpl(null,devices,selfMetrics);
        collector.collect(0);

        LinuxDeviceCollectorImplMetrics metrics = collector.getMetricsMap().get(device);

        Assert.assertNotEquals(0,metrics.timeSpentReadingMilli);
        Assert.assertNotEquals(0,metrics.timeSpentWritingMilli);
        Assert.assertNotEquals(0,metrics.timeSpentDoingIOMilli);
    }
}
