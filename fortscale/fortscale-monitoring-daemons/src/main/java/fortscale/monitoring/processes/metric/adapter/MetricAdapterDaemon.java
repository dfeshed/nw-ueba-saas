package fortscale.monitoring.processes.metric.adapter;

import fortscale.monitoring.processes.group.MonitoringProcessGroupCommon;
import fortscale.monitoring.processes.metric.adapter.config.MetricAdapterDaemonConfig;
import fortscale.utils.logging.Logger;

/**
 * The MetricAdapterDaemon is a process that collects metrics from kafka topic: metrics and write to influxdb using MetricAdapterService
 */
public class MetricAdapterDaemon extends MonitoringProcessGroupCommon {

    private static final Logger logger = Logger.getLogger(MetricAdapterDaemon.class);

    private static final String PROCESS_NAME="metricAdapter";
    public static void main(String[] args) throws Exception {
        MetricAdapterDaemon metricAdapterDaemon = new MetricAdapterDaemon();
        metricAdapterDaemon.mainEntry(args);
    }

    @Override
    protected Class getProcessConfigurationClasses() {
        return MetricAdapterDaemonConfig.class;
    }

    @Override
    protected String getProcessName() {
        return PROCESS_NAME;
    }
}
