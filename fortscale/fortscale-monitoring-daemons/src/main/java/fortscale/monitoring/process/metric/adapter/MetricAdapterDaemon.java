package fortscale.monitoring.process.metric.adapter;

import fortscale.monitoring.process.group.MonitoringProcessGroupCommon;
import fortscale.monitoring.process.metric.adapter.config.MetricAdapterDaemonConfig;
import fortscale.utils.logging.Logger;

import java.util.Arrays;
import java.util.List;

/**
 * The MetricAdapterDaemon is a process that collects metrics from kafka topic: metrics and write to influxdb using MetricAdapterService
 */
public class MetricAdapterDaemon extends MonitoringProcessGroupCommon {

    private static final Logger logger = Logger.getLogger(MetricAdapterDaemon.class);

    public static void main(String[] args) throws Exception {
        MetricAdapterDaemon metricAdapterDaemon = new MetricAdapterDaemon();
        int returnCode = metricAdapterDaemon.mainEntry(args);
        logger.info("Process finished with return code: {}",returnCode);
    }

    @Override
    protected Class getProcessConfigurationClasses() {
        return MetricAdapterDaemonConfig.class;
    }
}
