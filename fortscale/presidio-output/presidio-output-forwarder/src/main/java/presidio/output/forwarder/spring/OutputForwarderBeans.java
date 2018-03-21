package presidio.output.forwarder.spring;


import fortscale.utils.elasticsearch.config.ElasticsearchConfig;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Import;
import presidio.config.server.spring.ConfigServerClientServiceConfiguration;
import presidio.output.domain.services.alerts.AlertPersistencyService;
import presidio.output.domain.services.users.UserPersistencyService;
import presidio.output.domain.spring.PresidioOutputPersistencyServiceConfig;
import presidio.output.forwarder.AlertsForwarder;
import presidio.output.forwarder.IndicatorsForwarder;
import presidio.output.forwarder.UsersForwarder;
import presidio.output.forwarder.services.PresidioOutputForwardService;
import presidio.output.forwarder.shell.OutputForwarderExecutionService;
import presidio.output.forwarder.strategy.ForwarderConfiguration;
import presidio.output.forwarder.strategy.ForwarderStrategyFactory;

@Configuration
@Import({
        PresidioOutputPersistencyServiceConfig.class
})
public class OutputForwarderBeans {


    @Autowired
    UserPersistencyService userPersistencyService;

    @Autowired
    AlertPersistencyService alertPersistencyService;

    @Autowired
    ForwarderConfiguration forwarderStrategyConfiguration;

    @Autowired
    ForwarderStrategyFactory forwarderStrategyFactory;

    @Bean
    public UsersForwarder usersForwarder() {
        return new UsersForwarder(userPersistencyService, forwarderStrategyConfiguration, forwarderStrategyFactory);
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
    public PresidioOutputForwardService presidioOutputForwardService() {
        return new PresidioOutputForwardService(usersForwarder(), alertsForwarder(), indicatorsForwarder());
    }

    @Bean
    OutputForwarderExecutionService outputForwarderExecutionService() {
        return new OutputForwarderExecutionService(presidioOutputForwardService());
    }

}
