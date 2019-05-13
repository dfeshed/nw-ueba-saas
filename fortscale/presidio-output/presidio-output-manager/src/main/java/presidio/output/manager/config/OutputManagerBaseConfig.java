package presidio.output.manager.config;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import presidio.output.domain.services.event.EventPersistencyService;
import presidio.output.manager.services.OutputManagerService;

@Configuration
public class OutputManagerBaseConfig {

    @Value("${output.enriched.events.retention.in.days}")
    private long retentionEnrichedEventsDays;

    @Autowired
    private EventPersistencyService eventPersistencyService;

    @Bean
    public OutputManagerService managerApplicationService(){
        return new OutputManagerService(eventPersistencyService, retentionEnrichedEventsDays);
    }
}
