package fortscale.monitoring.external.stats.collector.impl.linux.device;

import fortscale.monitoring.external.stats.collector.impl.linux.disk.LinuxDiskCollectorImpl;
import fortscale.monitoring.external.stats.collector.impl.linux.disk.LinuxDiskCollectorImplMetrics;
import org.junit.Assert;
import org.junit.Test;


public class LinuxDeviceCollectorTest {

    @Test
    public void shouldCollectSDADeviceStats()
    {
        String device = "sda";
        String [] devices = {device};
        LinuxDeviceCollectorImpl collector = new LinuxDeviceCollectorImpl(null,devices);
        collector.collect(0);

        LinuxDeviceCollectorImplMetrics metrics = collector.getMetricsMap().get(device);

        Assert.assertNotEquals(0,metrics.timeSpentReading);
        Assert.assertNotEquals(0,metrics.timeSpentWriting);
        Assert.assertNotEquals(0,metrics.timeSpentDoingIO);
    }
}
