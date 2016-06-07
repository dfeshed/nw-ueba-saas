package fortscale.monitoring.external.stats.collector.impl.linux.process;

import fortscale.utils.monitoring.stats.StatsService;
import org.junit.Assert;
import org.junit.Test;

/**
 * Created by gaashh on 6/6/16.
 */
public class LinuxProcessCollectorTest {

    // Where test files are
    final String TEST_BASE_PATH              = "src/test/resources/fortscale/monitoring/external/stats/collector/impl/linux";
    final String TEST_PROC_BASE_PATH         = TEST_BASE_PATH + "/proc";
    final String TEST_FORTSCALE_PIDS_PATH    = TEST_BASE_PATH + "/var/run/fortscale";
    final String TEST_EXTERNAL_PIDFILES_LIST = TEST_BASE_PATH + "/var/run/foo/foo1.pid" + ":" + TEST_BASE_PATH + "/var/run/goo/goooo/goo-goo.pid";

    // Primary process info
    final String PROCESS_NAME       = "p123";
    final String PROCESS_GROUP_NAME = "g1";
    final long   PID                = 123;
    final String PID_PROC_PATH       = String.format("%s/%d", TEST_PROC_BASE_PATH, PID);
    final String PID_COMMAND_LINE    = "xterm%-bg%#009966%-title%Green4%-geometry% 45x115+111+119%-sl%5000%-rightbar%-aw%".replace("%"," ");

    // Secondary
    final String SECONDARY_PROCESS_NAME       = "p222";
    final String SECONDARY_PROCESS_GROUP_NAME = "g2";
    final long   SECONDARY_PID                = 222;

    // Measurement EPOCH
    final long EPOCH = 1_234_000_000;


    // We don't use the real stats service
    StatsService statsService = null;


    void checkMetrics(LinuxProcessCollectorImplMetrics metrics, long pid, String commandLine ) {

        Assert.assertEquals(pid,        metrics.pid);
        Assert.assertEquals(304 * 4096, metrics.memoryRSS);
        Assert.assertEquals(181116928,  metrics.memoryVSize);
        Assert.assertEquals(pid * 10,   metrics.threads);

        Assert.assertEquals(1140  * 10, metrics.userTimeMiliSec);
        Assert.assertEquals(346   * 10, metrics.kernelTimeMiliSec);
        Assert.assertEquals((7+9) * 10, metrics.childrenWaitTimeMiliSec);

        Assert.assertEquals(commandLine, metrics.commandLine);

    }


    @Test
    public void testLinuxProcessCollectorCollector() {


        LinuxProcessCollectorImpl collector = new LinuxProcessCollectorImpl("linuxProcess", statsService,
                                                                            PROCESS_NAME, PROCESS_GROUP_NAME);

        LinuxProcessCollectorImplMetrics metrics = collector.getMetrics();

        // Step 1 (with cmd line)
        collector.collect(EPOCH, PID, PID_PROC_PATH);
        checkMetrics(metrics, PID, PID_COMMAND_LINE);

        // Step 2 (without cmd line)
        collector.collect(EPOCH + 60, PID, PID_PROC_PATH);
        checkMetrics(metrics, PID, null);

        // Step 3 (with cmd line)
        collector.collect(EPOCH + 61 * 60, PID, PID_PROC_PATH);
        checkMetrics(metrics, PID, PID_COMMAND_LINE);

        // Step 4 (without cmd line)
        collector.collect(EPOCH + 62 * 60, PID, PID_PROC_PATH);
        checkMetrics(metrics, PID, null);

        // Step 5 (without cmd line)
        collector.collect(EPOCH + 63 * 60, PID, PID_PROC_PATH);
        checkMetrics(metrics, PID, null);

        // Step 6 (with cmd line)
        collector.collect(EPOCH + 122 * 60, PID, PID_PROC_PATH);
        checkMetrics(metrics, PID, PID_COMMAND_LINE);



    }

    @Test
    public void testLinuxProcessCollectorServiceOneProcess() {

        // Create the service
        LinuxProcessCollectorImplService service = new LinuxProcessCollectorImplService(statsService, TEST_PROC_BASE_PATH,
                                                                                        false, 0, 0,
                                                                                        TEST_FORTSCALE_PIDS_PATH,
                                                                                        TEST_EXTERNAL_PIDFILES_LIST);

        // First time, the collector is created
        service.collectOneProcess(EPOCH, PROCESS_NAME, PROCESS_GROUP_NAME, PID);
        LinuxProcessCollectorImplMetrics metrics = service.getMetrics(PROCESS_NAME, PROCESS_GROUP_NAME);
        Assert.assertNotNull(metrics);
        checkMetrics(metrics, PID, PID_COMMAND_LINE);

        // First time for secondary collector, it is created
        service.collectOneProcess(EPOCH, SECONDARY_PROCESS_NAME, SECONDARY_PROCESS_GROUP_NAME, SECONDARY_PID);
        LinuxProcessCollectorImplMetrics secondaryMetrics = service.getMetrics(SECONDARY_PROCESS_NAME, SECONDARY_PROCESS_GROUP_NAME);
        Assert.assertNotNull(secondaryMetrics);
        checkMetrics(secondaryMetrics, SECONDARY_PID, PID_COMMAND_LINE);

        // 2nd time, the collector exist
        service.collectOneProcess(EPOCH + 60, PROCESS_NAME, PROCESS_GROUP_NAME, PID);
        LinuxProcessCollectorImplMetrics metrics2 = service.getMetrics(PROCESS_NAME, PROCESS_GROUP_NAME);
        Assert.assertEquals(metrics, metrics2);
        checkMetrics(metrics2, PID, null);

        // 3nd time, the collector exist
        service.collectOneProcess(EPOCH + 120, PROCESS_NAME, PROCESS_GROUP_NAME, PID);
        LinuxProcessCollectorImplMetrics metrics3 = service.getMetrics(PROCESS_NAME, PROCESS_GROUP_NAME);
        Assert.assertEquals(metrics, metrics3);
        checkMetrics(metrics3, PID, null);

        // 4th time, change the PID to some non existing PID - metrics were not updated hence need to be the same
        service.collectOneProcess(EPOCH + 180, PROCESS_NAME, PROCESS_GROUP_NAME, 999);
        LinuxProcessCollectorImplMetrics metrics4 = service.getMetrics(PROCESS_NAME, PROCESS_GROUP_NAME);
        Assert.assertEquals(metrics, metrics4);
        checkMetrics(metrics3, PID, null);

        // Secondary collector - check again
        service.collectOneProcess(EPOCH + 999, SECONDARY_PROCESS_NAME, SECONDARY_PROCESS_GROUP_NAME, SECONDARY_PID);
        LinuxProcessCollectorImplMetrics secondaryMetrics2 = service.getMetrics(SECONDARY_PROCESS_NAME, SECONDARY_PROCESS_GROUP_NAME);
        Assert.assertEquals(secondaryMetrics, secondaryMetrics2);
        checkMetrics(secondaryMetrics, SECONDARY_PID, null);

    }

    @Test
    public void testLinuxProcessCollectorServiceOneProcess_NotExist() {

        // Create the service
        LinuxProcessCollectorImplService service = new LinuxProcessCollectorImplService(statsService, TEST_PROC_BASE_PATH,
                false, 0, 0,
                TEST_FORTSCALE_PIDS_PATH,
                TEST_EXTERNAL_PIDFILES_LIST);


        // First time, the collector does not exists but process does not exist, hence no collector and null metrics
        service.collectOneProcess(EPOCH, PROCESS_NAME, PROCESS_GROUP_NAME, 999);
        LinuxProcessCollectorImplMetrics metrics = service.getMetrics(PROCESS_NAME, PROCESS_GROUP_NAME);
        Assert.assertNull(metrics);

    }

    @Test
    public void testLinuxProcessCollectorServiceOneProcessFortscaleProcess() {

        // Create the service
        LinuxProcessCollectorImplService service = new LinuxProcessCollectorImplService(statsService, TEST_PROC_BASE_PATH,
                false, 0, 0,
                TEST_FORTSCALE_PIDS_PATH,
                TEST_EXTERNAL_PIDFILES_LIST);


        // Process all processes
        service.collect(EPOCH);

        LinuxProcessCollectorImplMetrics metrics;

        // Check the processes metrics
        metrics = service.getMetrics("p100", "g1");
        checkMetrics(metrics, 100, PID_COMMAND_LINE);

        metrics = service.getMetrics("p101", "g1");
        checkMetrics(metrics, 101, PID_COMMAND_LINE);

        metrics = service.getMetrics("p123", "g1");
        checkMetrics(metrics, 123, PID_COMMAND_LINE);

        metrics = service.getMetrics("p222", "g2");
        checkMetrics(metrics, 222, PID_COMMAND_LINE);

        // Check external processes
        metrics = service.getMetrics("foo1", "external");
        checkMetrics(metrics, 300, PID_COMMAND_LINE);

        metrics = service.getMetrics("goo-goo", "external");
        checkMetrics(metrics, 301, PID_COMMAND_LINE);
    }

}
