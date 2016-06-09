package fortscale.monitoring.external.stats.collector.impl.linux.process;

import fortscale.monitoring.external.stats.collector.impl.AbstractExternalStatsCollectorServiceImpl;
import fortscale.utils.logging.Logger;
import fortscale.utils.monitoring.stats.StatsService;
import org.apache.commons.collections.map.MultiKeyMap;
import org.apache.commons.io.FilenameUtils;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileFilter;
import java.io.FileReader;
import java.util.Arrays;
import java.util.List;

/**
 *
 * A service that collects linux process information from /proc files and writes them to stats metrics group.
 * All fortscale processes (that have pidfile) and the external processes listed are collected
 *
 * Created by gaashh on 6/7/16.
 */
public class LinuxProcessCollectorImplService extends AbstractExternalStatsCollectorServiceImpl {

    private static final Logger logger = Logger.getLogger(LinuxProcessCollectorImplService.class);

    // Collector service name. Used for logging
    final static String COLLECTOR_SERVICE_NAME = "linuxProcess";

    // Fortscale pidfiles extension
    final static String FORTSCALE_PIDFILE_EXTENSION = "pid";

    // External pid file list separator
    final static String EXTERNAL_PIDFILE_SEPARATOR = ":";

    // External pid files process group name
    final static String EXTERNAL_PROCESS_GROUP_NAME = "external";

    // Linux /proc file system base path
    String procBasePath;

    // Fortscale .pid file base dir. dir structure is group/name.pid
    String fortscaleBasePidFilesPath;

    // External (not-fortscale) .pid file list seperated by ':' to collect
    String externalPidfilesList;

    // Map of all collectorsMap: (processName, processGroupName) -> collector
    MultiKeyMap collectorsMap = new MultiKeyMap();


    /**
     * @param statsService              - The stats service. might be null
     * @param procBasePath              - "/proc" base path
     * @param isTickThreadEnabled       - Enable tick thread. Typically true
     * @param tickPeriodSeconds         - Tick thread period
     * @param tickSlipWarnSeconds       - Tick period warning threshold
     * @param fortscaleBasePidFilesPath - fortscale .pid file base dir. dir structure is group/name.pid
     * @param externalPidfilesList      - external (not-fortscale) .pid file list seperated by ':' to collect
     *
     */

    public LinuxProcessCollectorImplService(StatsService statsService, String procBasePath,
                                           boolean isTickThreadEnabled,
                                           long tickPeriodSeconds, long tickSlipWarnSeconds,
                                           String fortscaleBasePidFilesPath,
                                           String externalPidfilesList ) {
        // Call parent ctor
        super(COLLECTOR_SERVICE_NAME, statsService, isTickThreadEnabled, tickPeriodSeconds, tickSlipWarnSeconds);

        // Save vars
        this.procBasePath              = procBasePath;
        this.fortscaleBasePidFilesPath = fortscaleBasePidFilesPath;
        this.externalPidfilesList      = externalPidfilesList;

        // Start doing the real work
        start();

    }


    /**
     * collect the process data for Fortscale processes and external processes
     *
     * This function is typically called from the parent class at the tick
     *
     *
     * @param epoch - the measurement time
     */

    public void collect(long epoch) {

        // Collect Fortscale processes
        collectFortscaleProcesses(epoch);

        // Collect external processes
        collectExternalProcesses(epoch, EXTERNAL_PROCESS_GROUP_NAME, externalPidfilesList);

    }

    /**
     *
     * Collect data for one process.
     *
     * First verify the pid is a valid process
     *
     * 2nd, gets a collector identified by processName and processGroup name. If the collector does not exist it is
     * created
     *
     * 3rd, collect the data for the process
     *
     * @param epoch
     * @param processName
     * @param processGroupName
     * @param pid
     */
    public void collectOneProcess(long epoch, String processName, String processGroupName, long pid) {

        // Calc the PID path
        File processProcDir = new File(procBasePath, Long.toString(pid));

        // Verify process proc dir exists
        boolean exists = processProcDir.exists();
        if ( ! exists) {
            logger.debug("collector {} of process {} of group {} with PID {} does not have a proc directory {}, ignored",
                    collectorServiceName, processName, processGroupName, pid, processProcDir.toString());
            return;
        }

        // Try to get the collector from the collect map
        LinuxProcessCollectorImpl collector = (LinuxProcessCollectorImpl)collectorsMap.get(processName, processGroupName);
        if (collector == null) {

            // Not found, create a new collector
            collector = new LinuxProcessCollectorImpl(collectorServiceName, statsService, processName, processGroupName);

            // Add the new collector to the collectors map
            collectorsMap.put(processName, processGroupName, collector);
        }

        // Collect it
        collector.collect(epoch, pid, processProcDir.getAbsolutePath());

    }

    /**
     * Collect process metrics for all pidfiles at fortscaleBasePidFilesPath/group/file.pid
     *
     * This function process the top level and calls collectFortscaleGroupProcesses() to process the group
     *
     * @param epoch
     */
    public void collectFortscaleProcesses(long epoch) {

        // Directory file filter
        FileFilter directoryFilter = new FileFilter() {
            @Override
            public boolean accept(File pathname) {
                return pathname.isDirectory() && pathname.canRead();
            }
        };

        // Get a list of all Fortscale group dirs
        File groupsDir = new File(fortscaleBasePidFilesPath);
        File[] groupsDirList = groupsDir.listFiles(directoryFilter);
        if (groupsDirList == null) {
            logger.warn("Collector service {} - no process group files at {}. Ignoring", collectorServiceName, fortscaleBasePidFilesPath);
            return;
        }

        // Process the group list
        for (File groupDir : groupsDirList) {

            // Process group name is the directory name
            String processGroupName = groupDir.getName();

            // Process the group
            collectFortscaleGroupProcesses(epoch, processGroupName, groupDir);
        }

    }

    /**
     * Collect process metrics for all pidfiles at fortscaleBasePidFilesPath/group/file.pid for a group
     *
     * This function process the group level and calls collectPidFile() to process the pidfile
     *
     * @param epoch
     * @param processGroupName - the group name
     * @param groupDir         - the group directory
     */
    public void collectFortscaleGroupProcesses(long epoch, String processGroupName, File groupDir) {

        // File file filter
        FileFilter fileFilter = new FileFilter() {
            @Override
            public boolean accept(File pathname) {
                return pathname.isFile() && pathname.canRead();
            }
        };

        // Get a list of all files within a group
        File[] filesList = groupDir.listFiles(fileFilter);
        if (filesList == null) {
            logger.debug("Collector service {} - no process files at {}. Ignoring", collectorServiceName, groupDir.toString());
            return;
        }

        // Process the files list
        for (File file : filesList) {

            // get file name
            String filename = file.getAbsolutePath();

            // Split file name
            String basename = FilenameUtils.getBaseName(filename);
            String extension = FilenameUtils.getExtension(filename);

            // Ignore all not .pid files
            if ( ! extension.equals(FORTSCALE_PIDFILE_EXTENSION)) {
                logger.debug("Collector service {} - ignoring non-pid file {}", collectorServiceName, filename);
                continue;
            }

            // Process name is the base file name
            String processName = basename;
            collectPidFile(epoch, processName, processGroupName, filename);
        }

    }

    /**
     *
     * Collects the process metrics for a pidfile
     *
     * @param epoch
     * @param processName        - process name
     * @param processGroupName   - process group name
     * @param filename           - PID file name
     */
    public void collectPidFile(long epoch, String processName, String processGroupName, String filename) {

        long pid = -1;
        try {

            // Read the first line
            BufferedReader reader = new BufferedReader(new FileReader(filename));
            String line = reader.readLine();

            // Convert line to pid
            pid = Long.parseLong(line);

        }
        catch (Exception e) {
            logger.warn("Linux process collector service {} - problem parsing pidfile {}. Ignored",
                         collectorServiceName, filename);
            return;
        }

        // Do it!
        collectOneProcess(epoch, processName, processGroupName, pid);

    }

    /**
     * Collect process metrics for all pidfiles listed at pidfilesList. files are separated by ':'
     * The process name is the pidfile basename
     *
     * This function calls collectPidFile to do the real work.
     *
     * @param epoch
     * @param processGroupName - the group name
     * @param pidfilesList     - pidfiles list separated by ':'
     */
    public void collectExternalProcesses(long epoch, String processGroupName, String pidfilesList) {

        // Split the pid files
        List<String> pidFilenamesList = Arrays.asList( pidfilesList.split(EXTERNAL_PIDFILE_SEPARATOR) );

        // Process the files list
        for (String  filename : pidFilenamesList) {

            // Split file name
            String basename = FilenameUtils.getBaseName(filename);

            // Process name is the base file name
            String processName = basename;
            collectPidFile(epoch, processName, processGroupName, filename);
        }

    }



    /**
     * Get the collector metrics object. Used for testing
     *
     * @return - the collector metrics object
     */
    public LinuxProcessCollectorImplMetrics getMetrics(String processName, String processGroupName) {

        // Get the collector
        LinuxProcessCollectorImpl collector;
        collector = (LinuxProcessCollectorImpl)collectorsMap.get(processName, processGroupName);

        // Check not found
        if (collector == null) {
            return null;
        }

        // Get the metrics from the collector
        LinuxProcessCollectorImplMetrics metrics = collector.getMetrics();

        return metrics;
    }

}
