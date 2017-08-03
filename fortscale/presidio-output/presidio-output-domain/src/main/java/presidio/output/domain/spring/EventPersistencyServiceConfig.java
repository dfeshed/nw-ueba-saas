package presidio.output.domain.spring;

import fortscale.utils.mongodb.config.MongoConfig;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Import;
import org.springframework.data.mongodb.core.MongoTemplate;
import presidio.output.domain.repositories.EventMongoRepositoryImpl;
import presidio.output.domain.repositories.EventRepository;
import presidio.output.domain.services.event.EventPersistencyService;
import presidio.output.domain.services.event.EventPersistencyServiceImpl;
import presidio.output.domain.translator.OutputToCollectionNameTranslator;

/**
 * Created by efratn on 02/08/2017.
 */
@Configuration
@Import(MongoConfig.class)
public class EventPersistencyServiceConfig {

    @Autowired
    private EventRepository eventRepository;

    @Autowired
    private OutputToCollectionNameTranslator toCollectionNameTranslator;

    @Autowired
    private MongoTemplate mongoTemplate;

    @Bean
    public EventPersistencyService eventPersistencyService() {
        return new EventPersistencyServiceImpl(eventRepository, toCollectionNameTranslator);
    }

    @Bean
    public EventRepository eventRepository() {
        return new EventMongoRepositoryImpl(mongoTemplate);
    }

    @Bean
    public OutputToCollectionNameTranslator outputToCollectionNameTranslator() {
        return new OutputToCollectionNameTranslator();
    }
}
