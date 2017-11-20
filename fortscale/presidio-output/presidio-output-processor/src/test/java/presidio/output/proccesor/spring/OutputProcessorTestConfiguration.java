package presidio.output.proccesor.spring;

import fortscale.utils.shell.BootShimConfig;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Import;
import presidio.ade.sdk.common.AdeManagerSdk;
import presidio.monitoring.aspect.MonitoringAspects;
import presidio.monitoring.aspect.MonitroingAspectSetup;
import presidio.monitoring.endPoint.PresidioMetricEndPoint;
import presidio.monitoring.endPoint.PresidioSystemMetricsFactory;
import presidio.monitoring.services.MetricCollectingService;
import presidio.monitoring.services.MetricCollectingServiceImpl;
import presidio.output.processor.OutputShellCommands;
import presidio.output.processor.services.OutputExecutionService;
import presidio.output.processor.services.OutputExecutionServiceImpl;
import presidio.output.processor.services.alert.AlertService;
import presidio.output.processor.services.user.UserScoreService;
import presidio.output.processor.services.user.UserService;
import presidio.output.processor.spring.AlertServiceElasticConfig;

/**
 * Created by shays on 17/05/2017.
 */
@Configuration
@Import({
        AlertServiceElasticConfig.class,
        OutputShellCommands.class,
        BootShimConfig.class})
public class OutputProcessorTestConfiguration {

    private String applicationName = "output-core";

    @Bean
    public MetricCollectingService metricCollectingService() {
        return new MetricCollectingServiceImpl(presidioMetricEndPoint());
    }

    @Bean
    public PresidioMetricEndPoint presidioMetricEndPoint() {
        return new PresidioMetricEndPoint(new PresidioSystemMetricsFactory(applicationName), applicationName);
    }

    @Bean
    public MonitoringAspects monitoringAspects() {
        return new MonitoringAspects();
    }

    @Bean
    public MonitroingAspectSetup monitroingAspectSetup() {
        return new MonitroingAspectSetup(presidioMetricEndPoint());
    }

    @Autowired
    private AdeManagerSdk adeManagerSdk;

    @Autowired
    private AlertService alertService;

    @Autowired
    private UserService userService;

    @Value("${smart.threshold.score}")
    private int smartThreshold;

    @Value("${smart.page.size}")
    private int smartPageSize;

    @Autowired
    private UserScoreService userScoreService;

    @Bean
    public OutputExecutionService outputProcessService() {
        return new OutputExecutionServiceImpl(adeManagerSdk, alertService, userService, userScoreService, smartThreshold, smartPageSize);
    }
}
