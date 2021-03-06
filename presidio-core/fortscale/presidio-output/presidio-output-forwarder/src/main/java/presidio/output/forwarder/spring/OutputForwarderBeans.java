package presidio.output.forwarder.spring;


import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Import;
import presidio.monitoring.services.MetricCollectingService;
import presidio.monitoring.spring.PresidioMonitoringConfiguration;
import presidio.output.domain.services.alerts.AlertPersistencyService;
import presidio.output.domain.services.entities.EntityPersistencyService;
import presidio.output.domain.spring.PresidioOutputPersistencyServiceConfig;
import presidio.output.forwarder.AlertsForwarder;
import presidio.output.forwarder.IndicatorsForwarder;
import presidio.output.forwarder.EntitiesForwarder;
import presidio.output.forwarder.services.OutputForwardService;
import presidio.output.forwarder.shell.OutputForwarderExecutionService;
import presidio.output.forwarder.strategy.ForwarderConfiguration;
import presidio.output.forwarder.strategy.ForwarderStrategyFactory;

@Configuration
@Import({
        PresidioOutputPersistencyServiceConfig.class, PresidioMonitoringConfiguration.class
})
public class OutputForwarderBeans {


    @Autowired
    EntityPersistencyService entityPersistencyService;

    @Autowired
    AlertPersistencyService alertPersistencyService;

    @Autowired
    ForwarderConfiguration forwarderStrategyConfiguration;

    @Autowired
    ForwarderStrategyFactory forwarderStrategyFactory;

    @Autowired
    MetricCollectingService metricCollectingService;

    @Bean
    public EntitiesForwarder entitiesForwarder() {
        return new EntitiesForwarder(entityPersistencyService, forwarderStrategyConfiguration, forwarderStrategyFactory);
    }

    @Bean
    public AlertsForwarder alertsForwarder() {
        return new AlertsForwarder(alertPersistencyService, forwarderStrategyConfiguration, forwarderStrategyFactory);
    }

    @Bean
    public IndicatorsForwarder indicatorsForwarder() {
        return new IndicatorsForwarder(alertPersistencyService, forwarderStrategyConfiguration, forwarderStrategyFactory);
    }

    @Bean
    public OutputForwardService presidioOutputForwardService() {
        return new OutputForwardService(entitiesForwarder(), alertsForwarder(), indicatorsForwarder(), metricCollectingService);
    }

    @Bean
    OutputForwarderExecutionService outputForwarderExecutionService() {
        return new OutputForwarderExecutionService(presidioOutputForwardService());
    }

}
