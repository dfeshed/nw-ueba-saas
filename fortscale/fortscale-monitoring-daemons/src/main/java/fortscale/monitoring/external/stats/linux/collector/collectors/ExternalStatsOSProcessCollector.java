package fortscale.monitoring.external.stats.linux.collector.collectors;

import fortscale.monitoring.external.stats.linux.collector.metrics.ExternalStatsOSProcessCollectorMetrics;
import fortscale.monitoring.external.stats.linux.collector.parsers.ExternalStatsProcFileKeyMultipleValueParser;
import fortscale.monitoring.external.stats.linux.collector.parsers.ExternalStatsProcFileParser;
import fortscale.monitoring.external.stats.linux.collector.parsers.ExternalStatsProcFileSingleValueParser;
import fortscale.utils.monitoring.stats.StatsMetricsGroupAttributes;
import fortscale.utils.syslog.SyslogSender;

import java.util.Map;

/**
 * collects data of a single os process of the system.
 * an os process is a process in the classic-university term, as opposed to thread: has its own memory space,
 * managed by the OS etc.
 * Created by galiar on 27/04/2016.
 */
public class ExternalStatsOSProcessCollector extends AbstractExternalStatsCollector {

    private static final String PID_STATS = "stat";
    private static final String PID_CMDLINE = "cmdline";
    private static final int PID_INDEX = 0;
    private static final int USER_TIME_INDEX = 13;
    private static final int KERNEL_TIME_INDEX = 14;
    private static final int USER_WAIT_FOR_CHILDREN_TIME_INDEX = 15;
    private static final int KERNEL_WAIT_FOR_CHILDREN_TIME_INDEX = 16;
    private static final int NUM_THREADS_INDEX = 19;
    private static final int VSIZE_INDEX = 22;
    private static final int RSS_INDEX = 23;
    private static final long HOUR = 60 * 60 * 1000;

    private String pidName;
    private ExternalStatsOSProcessCollectorMetrics processMetrics = new ExternalStatsOSProcessCollectorMetrics(new StatsMetricsGroupAttributes()); //TODO real attributes
    private long cmdLineUpdateTime = 0;

    public ExternalStatsOSProcessCollector(String pid){
        this.pidName = pid;
    }

    @Override
    public void collect(Map<String, ExternalStatsProcFileParser> parsers) {

        ExternalStatsProcFileKeyMultipleValueParser pidStatParser = (ExternalStatsProcFileKeyMultipleValueParser) parsers.get(PID_STATS);
        ExternalStatsProcFileSingleValueParser pidCmdlineParser = (ExternalStatsProcFileSingleValueParser) parsers.get(PID_CMDLINE);

        //process id
        Long pid = pidStatParser.getValue(pidName).get(PID_INDEX);

        //resident set size memory - number of pages the process has in real memory - converted to bytes
        Long memoryRSS = convertPagesToBytes(pidStatParser.getValue(pidName).get(RSS_INDEX));

        //Virtual memory size in bytes
        Long memoryVSize = pidStatParser.getValue(pidName).get(VSIZE_INDEX);

        //Amount of time that this process has been scheduled in kernel mode, in Jiffies (assumed 10 millis)
        Long kernelTime = pidStatParser.getValue(pidName).get(KERNEL_TIME_INDEX);

        //Amount of time that this process has been scheduled in user mode, in Jiffies (assumed 10 millis)
        Long userTime = pidStatParser.getValue(pidName).get(USER_TIME_INDEX);

        //Number of threads in this process
        Long numThreads = pidStatParser.getValue(pidName).get(NUM_THREADS_INDEX);

        //Amount of time that this process's waited-for children have been scheduled in user or kernel mode, in Jiffies (assumed 10 millis)
        Long childrenWaitTime = pidStatParser.getValue(pidName).get(USER_WAIT_FOR_CHILDREN_TIME_INDEX) + pidStatParser.getValue(pidName).get(KERNEL_WAIT_FOR_CHILDREN_TIME_INDEX);

        //the command line which the kernel calls the process - including the process arguments

        String processCommandLine = pidCmdlineParser.getData();

        processMetrics.setPid(pid);
        processMetrics.setMemoryRSS(memoryRSS);
        processMetrics.setMemoryVSize(memoryVSize);
        processMetrics.setKernelTime(kernelTime);
        processMetrics.setUserTime(userTime);
        processMetrics.setNumThreads(numThreads);
        processMetrics.setChildrenWaitTime(childrenWaitTime);
        long timeNow = System.currentTimeMillis();
        if(cmdLineUpdateTime == 0 || timeNow >= cmdLineUpdateTime + HOUR) {
            processMetrics.setProcessCommandLine(processCommandLine);
            cmdLineUpdateTime = timeNow;
        }
    }

    public ExternalStatsOSProcessCollectorMetrics getProcessMetrics() {
        return processMetrics;
    }
}
