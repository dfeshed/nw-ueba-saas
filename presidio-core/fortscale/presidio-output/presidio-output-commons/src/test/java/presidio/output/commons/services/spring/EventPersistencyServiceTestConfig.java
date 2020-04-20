package presidio.output.commons.services.spring;

import fortscale.utils.mongodb.util.MongoDbBulkOpUtilConfig;
import fortscale.utils.test.mongodb.MongodbTestConfig;
import org.springframework.beans.factory.annotation.Autowired;
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

@Configuration
@Import({MongodbTestConfig.class, MongoDbBulkOpUtilConfig.class})
public class EventPersistencyServiceTestConfig {


    @Autowired
    public MongoTemplate mongoTemplate;


    @Bean
    public EventPersistencyService eventPersistencyService() {
        return new EventPersistencyServiceImpl(eventRepository(), new OutputToCollectionNameTranslator(), new OutputToClassNameTranslator());
    }

    @Bean
    public EventRepository eventRepository() {
        return new EventMongoRepositoryImpl(mongoTemplate);
    }
}
