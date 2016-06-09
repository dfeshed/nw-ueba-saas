package fortscale.monitoring.external.stats.collector.impl.linux.process;

import fortscale.monitoring.external.stats.collector.impl.linux.parsers.LinuxProcFileKeyMultipleValueParser;
import fortscale.monitoring.external.stats.collector.impl.linux.parsers.LinuxProcFileSingleValueParser;
import fortscale.utils.logging.Logger;
import fortscale.utils.monitoring.stats.StatsService;

import java.io.File;

/**
 * collects data of a single Linux process of the system.
 * Created by galiar & gaash on 27/04/2016.
 */
public class LinuxProcessCollectorImpl {

    private static Logger logger = Logger.getLogger(LinuxProcessCollectorImpl.class);

    // parse /proc/nnn/stats field indexes
    private static final int USER_TIME_INDEX = 13;
    private static final int KERNEL_TIME_INDEX = 14;
    private static final int USER_WAIT_FOR_CHILDREN_TIME_INDEX = 15;
    private static final int KERNEL_WAIT_FOR_CHILDREN_TIME_INDEX = 16;
    private static final int NUM_THREADS_INDEX = 19;
    private static final int VSIZE_INDEX = 22;
    private static final int RSS_INDEX = 23;


    private static final long KB_TO_BYTES = 1024;
    private static final long PAGES_TO_BYTES = 4 * KB_TO_BYTES;
    private static final long KERNEL_TICK_TO_MSEC = 10;  // Tick is 10mSec

    // Command like is updated periodically at low rate. This is the period in seconds
    protected static final long COMMAND_LINE_UPDATE_PERIOD = 60 * 60;

    // Collector name - mainly used for logging
    String collectorName;

    // Stats service metrics
    protected LinuxProcessCollectorImplMetrics metrics;

    // The last epoch time command line was update
    protected long lastCommandLineUpdateEpoch = 0;

    // Last command line update PID
    protected long lastCommandLinePid = 0;

    /**
     *
     * ctor
     *
     * Creates the metrics group
     *
     * Note: the collector is associated with process name and process group. It does not associated with PID (which might change)
     *
     * @param collectorServiceName
     * @param statsService
     * @param processName
     * @param processGroupName
     */
    public LinuxProcessCollectorImpl(String collectorServiceName, StatsService statsService,
                                     String processName, String processGroupName) {

        // Save params while doing some calculations
        this.collectorName    = String.format("%s[%s.%s]", collectorServiceName, processGroupName, processName);

        logger.debug("Creating Linux process collector instance {} ", collectorName);

        // Create metrics
        metrics = new LinuxProcessCollectorImplMetrics(statsService, processName, processGroupName);
    }

    /**
     * Collect the data from the /proc files and updates the metrics.
     *
     * Note: The caller should verify the PID is valid. However, the process might die while processing it, in this
     * case some errors and warning will be issued. This is OK.
     *
     * Note: command line field is update every hour or on PID change
     *
     * @param epoch
     */

    public void collect(long epoch, long pid, String procPidDir) {

        logger.debug("Collecting {} at {} for PID {} at {}", collectorName, epoch, pid, procPidDir);

        try {

            // Create the parses
            String pidStatFilename = new File(procPidDir, "stat").toString();
            LinuxProcFileKeyMultipleValueParser statParser = new LinuxProcFileKeyMultipleValueParser(pidStatFilename, " ");

            // Collect the values
            metrics.pid = pid;

            metrics.memoryRSS = statParser.getLongValue(RSS_INDEX) * PAGES_TO_BYTES;
            metrics.memoryVSize = statParser.getLongValue(VSIZE_INDEX);
            metrics.threads   = statParser.getLongValue(NUM_THREADS_INDEX);
            metrics.kernelTimeMiliSec       = statParser.getLongValue(KERNEL_TIME_INDEX)                 * KERNEL_TICK_TO_MSEC;
            metrics.userTimeMiliSec         = statParser.getLongValue(USER_TIME_INDEX)                   * KERNEL_TICK_TO_MSEC;
            metrics.childrenWaitTimeMiliSec = (statParser.getLongValue(KERNEL_WAIT_FOR_CHILDREN_TIME_INDEX) +
                                               statParser.getLongValue(USER_WAIT_FOR_CHILDREN_TIME_INDEX) ) * KERNEL_TICK_TO_MSEC;

            // Command line is updated periodically. Do we need to update it?
            if (lastCommandLineUpdateEpoch == 0 ||
                epoch > lastCommandLineUpdateEpoch + COMMAND_LINE_UPDATE_PERIOD ||
                lastCommandLinePid == 0 ||
                lastCommandLinePid != pid ) {
                // Update the command line

                // Update the last update epoch to now and the PID
                lastCommandLineUpdateEpoch = epoch;
                lastCommandLinePid         = pid;

                // Create a parser and get its value
                String pidCommandLineFilename = new File(procPidDir, "cmdline").toString();
                LinuxProcFileSingleValueParser commandLineParser = new LinuxProcFileSingleValueParser(pidCommandLineFilename);
                String rawCommandLine = commandLineParser.getData();

                // Convert the \0 separated command line to space sperated string
                metrics.commandLine = rawCommandLine.replace("\0", " ");

            }
            else {
                // No need to update, make sure command line is empty
                metrics.commandLine = null;
            }

            metrics.manualUpdate(epoch);

        }
        catch (Exception e) {
            String msg = String.format("Error collecting %s at %d for PID {} at {}. Ignored",
                                       collectorName, epoch, pid, procPidDir);
            logger.warn(msg, e);
        }


    }

    public LinuxProcessCollectorImplMetrics getMetrics() {
        return metrics;
    }
}
