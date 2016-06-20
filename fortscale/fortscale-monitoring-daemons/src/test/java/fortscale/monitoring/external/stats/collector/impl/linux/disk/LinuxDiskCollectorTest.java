package fortscale.monitoring.external.stats.collector.impl.linux.disk;


import org.junit.Assert;
import org.junit.Test;

import java.util.Map;

public class LinuxDiskCollectorTest {

    @Test
    public void shouldCollectHomeDirDiskStats()
    {
        String diskPath = "/home";
        String [] disks = {diskPath};
        LinuxDiskCollectorImpl collector = new LinuxDiskCollectorImpl(null,disks);
        collector.collect(0);

        LinuxDiskCollectorImplMetrics metrics = collector.getMetricsMap().get(diskPath);

        Assert.assertNotEquals(0,metrics.totalFileSystemSize);
        Assert.assertNotEquals(0,metrics.freeSpace);
        Assert.assertNotEquals(0,metrics.usedSpace);
    }

    @Test
    public void shouldCollectNothing()
    {
        String [] disks = {};
        LinuxDiskCollectorImpl collector = new LinuxDiskCollectorImpl(null,disks);
        collector.collect(0);

        Map metricsMap = collector.getMetricsMap();

        Assert.assertEquals(0,metricsMap.size());
    }
}

