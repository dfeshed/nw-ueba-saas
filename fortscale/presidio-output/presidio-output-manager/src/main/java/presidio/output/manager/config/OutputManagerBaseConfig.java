package presidio.output.manager.config;

import fortscale.utils.mongodb.util.MongoDbBulkOpUtilConfig;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Import;
import org.springframework.data.mongodb.core.MongoTemplate;
import presidio.output.domain.repositories.EventMongoRepositoryImpl;
import presidio.output.domain.repositories.EventRepository;
import presidio.output.domain.services.event.EventPersistencyService;
import presidio.output.domain.services.event.EventPersistencyServiceImpl;
import presidio.output.domain.translator.OutputToClassNameTranslator;
import presidio.output.domain.translator.OutputToCollectionNameTranslator;
import presidio.output.manager.services.OutputManagerService;

@Configuration
@Import({
        MongoDbBulkOpUtilConfig.class
})
public class OutputManagerBaseConfig {

    @Autowired
    public MongoTemplate mongoTemplate;

    @Value("${output.enriched.events.retention.in.days}")
    private long retentionEnrichedEventsDays;

    @Autowired
    private EventPersistencyService eventPersistencyService;

    @Bean
    public EventPersistencyService eventPersistencyService() {
        return new EventPersistencyServiceImpl(eventRepository(), new OutputToCollectionNameTranslator(), new OutputToClassNameTranslator());
    }

    @Bean
    public EventRepository eventRepository() {
        return new EventMongoRepositoryImpl(mongoTemplate);
    }

    @Bean
    public OutputManagerService managerApplicationService(){
        return new OutputManagerService(eventPersistencyService, retentionEnrichedEventsDays);
    }
}
