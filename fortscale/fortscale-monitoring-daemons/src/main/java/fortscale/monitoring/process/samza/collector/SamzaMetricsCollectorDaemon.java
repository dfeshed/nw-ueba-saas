package fortscale.monitoring.process.samza.collector;

import fortscale.monitoring.process.group.MonitoringProcessGroupCommon;
import fortscale.monitoring.process.samza.collector.config.SamzaMetricsCollectorConfig;
import fortscale.utils.logging.Logger;


public class SamzaMetricsCollectorDaemon extends MonitoringProcessGroupCommon {
    private static final Logger logger = Logger.getLogger(SamzaMetricsCollectorDaemon.class);

    public static void main(String[] args) throws Exception {
        SamzaMetricsCollectorDaemon daemon = new SamzaMetricsCollectorDaemon();
        int returnCode = daemon.mainEntry(args);
        logger.info("Process finished with return code: {}",returnCode);
    }


    @Override
    protected Class getProcessConfigurationClasses() {
        return SamzaMetricsCollectorConfig.class;

    }
}
