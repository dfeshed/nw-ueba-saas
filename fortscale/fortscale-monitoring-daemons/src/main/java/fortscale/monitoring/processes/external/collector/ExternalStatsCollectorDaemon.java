package fortscale.monitoring.processes.external.collector;

import fortscale.monitoring.processes.external.collector.config.ExternalStatsCollectorDaemonConfig;
import fortscale.monitoring.processes.group.MonitoringProcessGroupCommon;
import fortscale.utils.process.processType.ProcessType;

/**
 * The ExternalStatsCollectorDaemon is a process that collects external metrics such as mongo, linux etc.
 */
public class ExternalStatsCollectorDaemon extends MonitoringProcessGroupCommon {

    private static final String PROCESS_NAME="external-stats-collector";

    public static void main(String[] args) throws Exception {
        ExternalStatsCollectorDaemon daemon = new ExternalStatsCollectorDaemon();
        daemon.mainEntry(args);
    }

    @Override
    protected ProcessType getProcessType() {
        return ProcessType.DAEMON;
    }

    @Override
    protected Class getProcessConfigurationClasses() {
        return ExternalStatsCollectorDaemonConfig.class;
    }

    @Override
    protected String getProcessName() {
        return PROCESS_NAME;
    }
}
