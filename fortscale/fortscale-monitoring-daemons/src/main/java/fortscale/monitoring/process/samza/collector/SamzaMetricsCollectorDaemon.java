package fortscale.monitoring.process.samza.collector;

import fortscale.monitoring.process.group.MonitoringProcessGroupCommon;
import fortscale.monitoring.process.samza.collector.config.SamzaMetricsCollectorConfig;
import org.springframework.context.annotation.AnnotationConfigApplicationContext;

import java.util.Collections;


public class SamzaMetricsCollectorDaemon extends MonitoringProcessGroupCommon {

    public static void main(String[] args) throws InterruptedException {
        SamzaMetricsCollectorDaemon daemon = new SamzaMetricsCollectorDaemon();
        daemon.main(args, Collections.singletonList(SamzaMetricsCollectorConfig.class));
        daemon.contextInit();
    }

    @Override
    protected AnnotationConfigApplicationContext editAppContext(AnnotationConfigApplicationContext springContext) {
        return springContext;
    }

    @Override
    protected void contextInit() {
        groupContextInit(Collections.singletonList(SamzaMetricsCollectorConfig.class));
    }
}
