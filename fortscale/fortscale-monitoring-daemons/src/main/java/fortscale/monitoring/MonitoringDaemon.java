package fortscale.monitoring;

import fortscale.monitoring.metricAdapter.config.MetricAdapterConfig;
import org.springframework.context.annotation.AnnotationConfigApplicationContext;

import java.util.Arrays;

/**
 * Created by baraks on 5/1/2016.
 */
public class MonitoringDaemon extends MonitoringProcessGroupCommon {

    public static void main(String [] args) throws InterruptedException {
        MonitoringDaemon monitoringDaemon = new MonitoringDaemon();
        monitoringDaemon.main(args,Arrays.asList(MetricAdapterConfig.class));
        monitoringDaemon.contextInit();

    }

    @Override
    protected AnnotationConfigApplicationContext editAppContext(AnnotationConfigApplicationContext springContext) {
        return springContext;
    }

    @Override
    protected  void contextInit() {
        groupContextInit(Arrays.asList(MetricAdapterConfig.class));
    }

}
