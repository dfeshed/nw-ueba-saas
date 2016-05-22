package fortscale.monitoring.external.stats.samza.collector;

import fortscale.monitoring.MonitoringProcessGroupCommon;
import fortscale.monitoring.config.MonitoringDaemonConfig;
import fortscale.monitoring.external.stats.samza.collector.config.SamzaMetricsCollectorConfig;
import fortscale.monitoring.metricAdapter.config.MetricAdapterConfig;
import org.springframework.context.annotation.AnnotationConfigApplicationContext;

import java.util.Arrays;

/**
 * Created by cloudera on 5/22/16.
 */
public class SamzaMetricsCollector  extends MonitoringProcessGroupCommon {

    public static void main(String[] args) throws InterruptedException {
        SamzaMetricsCollector daemon = new SamzaMetricsCollector();
        daemon.main(args, Arrays.asList(SamzaMetricsCollectorConfig.class));
        daemon.contextInit();
    }

    @Override
    protected AnnotationConfigApplicationContext editAppContext(AnnotationConfigApplicationContext springContext) {
        return springContext;
    }

    @Override
    protected void contextInit() {
        groupContextInit(Arrays.asList(SamzaMetricsCollectorConfig.class));
    }
}
