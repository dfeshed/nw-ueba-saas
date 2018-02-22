package presidio.output.forwarder.config;


import fortscale.utils.elasticsearch.config.ElasticsearchConfig;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Import;
import presidio.config.server.spring.ConfigServerClientServiceConfiguration;
import presidio.output.domain.services.alerts.AlertPersistencyService;
import presidio.output.domain.services.users.UserPersistencyService;
import presidio.output.domain.spring.PresidioOutputPersistencyServiceConfig;
import presidio.output.forwarder.handlers.EventsHandler;
import presidio.output.forwarder.services.PresidioOutputForwardService;
import presidio.output.forwarder.shell.OutputForwarderExecutionService;

@Configuration
@Import({
        SyslogConfiguration.class, PresidioOutputPersistencyServiceConfig.class, ElasticsearchConfig.class, ConfigServerClientServiceConfiguration.class
})
public class OutputForwarderConfiguration {

    @Autowired
    EventsHandler syslogEventHandler;

    @Autowired
    AlertPersistencyService alertPersistencyService;

    @Autowired
    UserPersistencyService userPersistencyService;

    @Bean
    public PresidioOutputForwardService presidioOutputForwardService() {
        return new PresidioOutputForwardService(alertPersistencyService, userPersistencyService, syslogEventHandler);
    }

    @Bean
    OutputForwarderExecutionService outputForwarderExecutionService() {
        return new OutputForwarderExecutionService(presidioOutputForwardService());
    }

}
