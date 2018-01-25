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
import presidio.monitoring.endPoint.PresidioMetricBucket;
import presidio.monitoring.endPoint.PresidioSystemMetricsFactory;
import presidio.monitoring.services.MetricCollectingService;
import presidio.monitoring.services.MetricCollectingServiceImpl;
import presidio.monitoring.services.MetricConventionApplyer;
import presidio.monitoring.services.PresidioMetricConventionApplyer;
import presidio.output.domain.services.event.EventPersistencyService;
import presidio.output.domain.spring.EventPersistencyServiceConfig;
import presidio.output.processor.OutputShellCommands;
import presidio.output.processor.services.OutputExecutionService;
import presidio.output.processor.services.OutputExecutionServiceImpl;
import presidio.output.processor.services.OutputMonitoringService;
import presidio.output.processor.services.alert.AlertService;
import presidio.output.processor.services.user.UserService;
import presidio.output.processor.spring.AlertServiceElasticConfig;
import presidio.output.processor.spring.OutputMonitoringConfiguration;

/**
 * Created by shays on 17/05/2017.
 */
@Configuration
@Import({EventPersistencyServiceConfig.class,
        AlertServiceElasticConfig.class,
        OutputShellCommands.class,
        BootShimConfig.class,
        OutputMonitoringConfiguration.class})
public class OutputProcessorTestConfiguration {

    private final String APPLICATION_NAME = "output-core";

    @Bean
    public MetricCollectingService metricCollectingService() {
        return new MetricCollectingServiceImpl(presidioMetricEndPoint());
    }

    @Bean
    public MetricConventionApplyer metricConventionApplyer() {
        return new PresidioMetricConventionApplyer(APPLICATION_NAME);
    }

    @Bean
    public PresidioMetricBucket presidioMetricEndPoint() {
        return new PresidioMetricBucket(new PresidioSystemMetricsFactory(APPLICATION_NAME), metricConventionApplyer());
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

    @Autowired
    private EventPersistencyService eventPersistencyService;

    @Value("${smart.threshold.score}")
    private int smartThreshold;

    @Value("${smart.page.size}")
    private int smartPageSize;

    @Value("${output.enriched.events.retention.in.days}")
    private long retentionEnrichedEventsDays;

    @Value("${output.data.retention.in.days}")
    private long retentionOutputDataDays;

    @Autowired
    private OutputMonitoringService outputMonitoringService;

    @Bean
    public OutputExecutionService outputProcessService() {
        return new OutputExecutionServiceImpl(adeManagerSdk, alertService, userService, eventPersistencyService, outputMonitoringService, smartThreshold, smartPageSize, retentionEnrichedEventsDays, retentionOutputDataDays);
    }
}
