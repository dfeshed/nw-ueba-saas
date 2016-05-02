package fortscale.monitoring.external.stats.linux.collector.collectors;

import fortscale.monitoring.external.stats.linux.collector.metrics.ExternalStatsCPUUtilizationCollectorMetrics;
import fortscale.monitoring.external.stats.linux.collector.parsers.ExternalStatsProcFileKeyMultipleValueParser;
import fortscale.monitoring.external.stats.linux.collector.parsers.ExternalStatsProcFileParser;
import fortscale.utils.monitoring.stats.StatsMetricsGroupAttributes;

import java.util.Map;

/**
 * Created by galiar on 26/04/2016.
 */
public class ExternalStatsCPUUtilizationCollector extends AbstractExternalStatsCollector {

    private static final int USER_INDEX = 1;
    private static final int NICE_INDEX = 2;
    private static final int SYSTEM_INDEX = 3;
    private static final int IDLE_INDEX = 4;
    private static final int WAIT_INDEX = 5;
    private static final int HARDWARE_INTERRUPTS_INDEX = 6;
    private static final int SOFTWARE_INTERRUPTS_INDEX = 7;
    private static final int STEAL_INDEX = 8;

    private static final String CPU_STAT_FILE_NAME = "stat";

    private ExternalStatsCPUUtilizationCollectorMetrics utilizationCollectorMetrics = new ExternalStatsCPUUtilizationCollectorMetrics(new StatsMetricsGroupAttributes()); //TODO real attributes
    private String cpuName;

    public ExternalStatsCPUUtilizationCollector(String cpuName){
        this.cpuName = cpuName;
    }

    @Override
    public void collect(Map<String, ExternalStatsProcFileParser> parsers) {

        ExternalStatsProcFileKeyMultipleValueParser parser = (ExternalStatsProcFileKeyMultipleValueParser) parsers.get(CPU_STAT_FILE_NAME);

        //how much time the CPU operated in user mode, in 10 millis
        Long user = parser.getValue(cpuName).get(USER_INDEX);

        //how much time the CPU operated in kernel mode, in 10 millis
        Long system = parser.getValue(cpuName).get(SYSTEM_INDEX);

        //how much time the CPU operated on prioritized processes in user mode, in 10 millis
        Long nice = parser.getValue(cpuName).get(NICE_INDEX);

        //how much time the CPU was idle, in 10 millis
        Long idle = parser.getValue(cpuName).get(IDLE_INDEX);

        //how much time the CPU was idle while waiting for an I/O operation to complete, in 10 millis
        Long wait = parser.getValue(cpuName).get(WAIT_INDEX);

        //how much time the CPU has spent servicing hardware interrupts, in 10 millis
        Long hardwareInterrupts = parser.getValue(cpuName).get(HARDWARE_INTERRUPTS_INDEX);

        //how much time the CPU has spent servicing software interrupts, in 10 millis
        Long softwareInterrupts = parser.getValue(cpuName).get(SOFTWARE_INTERRUPTS_INDEX);

        //how long the virtual CPU has spent waiting for the hypervisor to service another virtual CPU, in 10 millis
        Long steal = parser.getValue(cpuName).get(STEAL_INDEX);


        utilizationCollectorMetrics.setUser(user);
        utilizationCollectorMetrics.setSystem(system);
        utilizationCollectorMetrics.setNice(nice);
        utilizationCollectorMetrics.setIdle(idle);
        utilizationCollectorMetrics.setWait(wait);
        utilizationCollectorMetrics.setHardwareInterrupts(hardwareInterrupts);
        utilizationCollectorMetrics.setSoftwareInterrupts(softwareInterrupts);
        utilizationCollectorMetrics.setSteal(steal);

    }

    //for testing only
    public ExternalStatsCPUUtilizationCollectorMetrics getUtilizationCollectorMetrics() {
        return utilizationCollectorMetrics;
    }

}
