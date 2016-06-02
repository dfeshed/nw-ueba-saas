package fortscale.monitoring.processes.samza.collector;

import fortscale.monitoring.processes.group.MonitoringProcessGroupCommon;
import fortscale.monitoring.processes.samza.collector.config.SamzaMetricsCollectorConfig;
import fortscale.utils.logging.Logger;


public class SamzaMetricsCollectorDaemon extends MonitoringProcessGroupCommon {
    private static final Logger logger = Logger.getLogger(SamzaMetricsCollectorDaemon.class);
    private static final String PROCESS_NAME="samza-metrics-collector";

    public static void main(String[] args) throws Exception {
        SamzaMetricsCollectorDaemon daemon = new SamzaMetricsCollectorDaemon();
        daemon.mainEntry(args);
    }


    @Override
    protected Class getProcessConfigurationClasses() {
        return SamzaMetricsCollectorConfig.class;

    }

    @Override
    protected String getProcessName() {
        return PROCESS_NAME;
    }
}
