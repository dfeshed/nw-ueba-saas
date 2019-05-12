package presidio.output.manager.config;

import fortscale.utils.mongodb.config.MongoConfig;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Import;
import presidio.output.domain.services.event.EventPersistencyService;
import presidio.output.domain.spring.EventPersistencyServiceConfig;
import presidio.output.manager.OutputManagerService;
import presidio.output.manager.OutputManagerShellCommands;

@Configuration
@Import({
        MongoConfig.class,
        EventPersistencyServiceConfig.class,
        // CLI commands related configurations
        OutputManagerShellCommands.class
})
public class OutputManagerConfiguration {

    @Value("${output.enriched.events.retention.in.days}")
    private long retentionEnrichedEventsDays;

    @Autowired
    private EventPersistencyService eventPersistencyService;

    @Bean
    public OutputManagerService managerApplicationService(){
        return new OutputManagerService(eventPersistencyService, retentionEnrichedEventsDays);
    }
}
