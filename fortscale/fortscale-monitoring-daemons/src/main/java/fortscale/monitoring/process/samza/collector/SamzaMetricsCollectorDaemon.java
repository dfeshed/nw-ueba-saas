package fortscale.monitoring.process.samza.collector;

import fortscale.monitoring.process.samza.collector.config.SamzaMetricsCollectorConfig;
import fortscale.monitoring.process.group.MonitoringProcessGroupCommon;
import org.springframework.context.annotation.AnnotationConfigApplicationContext;

import java.util.Arrays;

/**
 * Created by cloudera on 5/22/16.
 */
public class SamzaMetricsCollectorDaemon extends MonitoringProcessGroupCommon {

    public static void main(String[] args) throws InterruptedException {
        SamzaMetricsCollectorDaemon daemon = new SamzaMetricsCollectorDaemon();
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
