package fortscale.monitoring.process.metric.adapter;

import fortscale.monitoring.process.group.MonitoringProcessGroupCommon;
import fortscale.monitoring.process.metric.adapter.config.MetricAdapterDaemonConfig;
import org.springframework.context.annotation.AnnotationConfigApplicationContext;

import java.util.Arrays;
import java.util.Collections;

/**
 * The MetricAdapterDaemon is a process that collects metrics from kafka topic: metrics and write to influxdb using MetricAdapterService
 */
public class MetricAdapterDaemon extends MonitoringProcessGroupCommon {

    public static void main(String[] args) throws InterruptedException {
        MetricAdapterDaemon metricAdapterDaemon = new MetricAdapterDaemon();
        metricAdapterDaemon.main(args, Collections.singletonList(MetricAdapterDaemonConfig.class));
        metricAdapterDaemon.contextInit();

    }

    @Override
    protected AnnotationConfigApplicationContext editAppContext(AnnotationConfigApplicationContext springContext) {
        return springContext;
    }

    @Override
    protected void contextInit() {
        groupContextInit(Collections.singletonList(MetricAdapterDaemonConfig.class));
    }

}
