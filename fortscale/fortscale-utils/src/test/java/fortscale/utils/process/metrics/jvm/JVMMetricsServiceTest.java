package fortscale.utils.process.metrics.jvm;

import fortscale.utils.process.metrics.jvm.impl.JVMMetricsServiceImpl;
import fortscale.utils.process.metrics.jvm.stats.JVMMetrics;
import org.junit.Assert;
import org.junit.Ignore;
import org.junit.Test;

import static java.lang.Thread.sleep;


public class JVMMetricsServiceTest
{
    @Test
    public void metricsUpdateTest() throws InterruptedException {
        JVMMetricsServiceImpl jvmMetricsService= new JVMMetricsServiceImpl(null,1,2);
        JVMMetrics jvmMetrics= jvmMetricsService.getJvmMetrics();
        sleep(5*1000);
        Assert.assertNotEquals(jvmMetrics.heapCommittedMemory,0);
        Assert.assertNotEquals(jvmMetrics.heapInitMemory,0);
        Assert.assertNotEquals(jvmMetrics.heapMaxMemory,0);
        Assert.assertNotEquals(jvmMetrics.heapUsedMemory,0);
        Assert.assertNotEquals(jvmMetrics.nonHeapCommittedMemory,0);
        Assert.assertNotEquals(jvmMetrics.nonHeapInitMemory,0);
        Assert.assertNotEquals(jvmMetrics.nonHeapUsedMemory,0);
    }
}
