package fortscale.monitoring.external.stats.collector.impl.linux.process;

import fortscale.utils.monitoring.stats.StatsService;
import org.junit.Assert;
import org.junit.Test;

/**
 * Created by gaashh on 6/6/16.
 */
public class LinuxProcessCollectorTest {

    // PIDs
    final long PID = 123;

    // Where test /proc files are
    final String TEST_PROC_BASE_PATH = "src/test/resources/fortscale/monitoring/external/stats/collector/impl/linux/proc/files";
    final String PID_PROC_PATH       = String.format("%s/%d", TEST_PROC_BASE_PATH, PID);
    final String PID_COMMAND_LINE    = "xterm%-bg%#009966%-title%Green4%-geometry% 45x115+111+119%-sl%5000%-rightbar%-aw%".replace("%"," ");


    // Measurement EPOCH
    final long EPOCH = 1_234_000_000;


    // We don't use the real stats service
    StatsService statsService = null;


    void checkMetrics(LinuxProcessCollectorImplMetrics metrics, String commandLine ) {

        Assert.assertEquals(PID,        metrics.pid);
        Assert.assertEquals(304 * 4096, metrics.memoryRSS);
        Assert.assertEquals(181116928,  metrics.memoryVSize);
        Assert.assertEquals(17,         metrics.threads);

        Assert.assertEquals(1140  * 10, metrics.userTimeMiliSec);
        Assert.assertEquals(346   * 10, metrics.kernelTimeMiliSec);
        Assert.assertEquals((7+9) * 10, metrics.childrenWaitTimeMiliSec);

        Assert.assertEquals(commandLine, metrics.commandLine);

    }


    @Test
    public void testLinuxMemoryCollectorCollector() {


        LinuxProcessCollectorImpl collector = new LinuxProcessCollectorImpl("linuxProcess", statsService, "proc123", "group123");

        LinuxProcessCollectorImplMetrics metrics = collector.getMetrics();

        // Step 1 (with cmd line)
        collector.collect(EPOCH, PID, PID_PROC_PATH);
        checkMetrics(metrics, PID_COMMAND_LINE);

        // Step 2 (without cmd line)
        collector.collect(EPOCH + 60, PID, PID_PROC_PATH);
        checkMetrics(metrics, null);

        // Step 3 (with cmd line)
        collector.collect(EPOCH + 61 * 60, PID, PID_PROC_PATH);
        checkMetrics(metrics, PID_COMMAND_LINE);

        // Step 4 (without cmd line)
        collector.collect(EPOCH + 62 * 60, PID, PID_PROC_PATH);
        checkMetrics(metrics, null);

        // Step 5 (without cmd line)
        collector.collect(EPOCH + 63 * 60, PID, PID_PROC_PATH);
        checkMetrics(metrics, null);

        // Step 6 (with cmd line)
        collector.collect(EPOCH + 122 * 60, PID, PID_PROC_PATH);
        checkMetrics(metrics, PID_COMMAND_LINE);



    }

//    @Test
//    public void testLinuxMemoryCollectorService() {
//
//
//        // Create the collector service
//        boolean isTickThreadEnabled = false;
//        long tickPeriodSeconds      = 60;
//        long tickSlipWarnSeconds    = 30;
//
//        LinuxMemoryCollectorImplService service = new LinuxMemoryCollectorImplService(
//                statsService, TEST_PROC_BASE_PATH, isTickThreadEnabled, tickPeriodSeconds, tickSlipWarnSeconds);
//
//        LinuxMemoryCollectorImplMetrics metrics = service.getMetrics();
//
//        // Do it
//
//        // tick 1
//        long epoch = EPOCH;
//        service.tick(epoch);
//        checkMetrics(metrics);
//
//        // From this point tests are manual (and not very interesting)
//        // tick 2
//        epoch += 60;
//        service.tick(epoch);
//        checkMetrics(metrics);
//
//        // tick 3 - delay
//        epoch += 115;
//        service.tick(epoch);
//        checkMetrics(metrics);
//
//        // tick 4 - too fast, dropped
//        epoch += 5;
//        service.tick(epoch);
//        checkMetrics(metrics);
//
//        // tick 5 - back to normal
//        epoch += 60;
//        service.tick(epoch);
//        checkMetrics(metrics);
//    }

}
