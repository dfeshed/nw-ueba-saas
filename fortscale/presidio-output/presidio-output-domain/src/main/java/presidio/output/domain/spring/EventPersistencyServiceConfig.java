package presidio.output.domain.spring;

import fortscale.utils.mongodb.util.MongoDbBulkOpUtilConfig;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Import;
import org.springframework.data.mongodb.core.MongoTemplate;
import presidio.ade.sdk.common.AdeManagerSdk;
import presidio.ade.sdk.common.AdeManagerSdkConfig;
import presidio.output.domain.repositories.EventMongoRepositoryImpl;
import presidio.output.domain.repositories.EventRepository;
import presidio.output.domain.services.event.EventPersistencyService;
import presidio.output.domain.services.event.EventPersistencyServiceImpl;
import presidio.output.domain.services.event.ScoredEventService;
import presidio.output.domain.services.event.ScoredEventServiceImpl;
import presidio.output.domain.translator.OutputToClassNameTranslator;
import presidio.output.domain.translator.OutputToCollectionNameTranslator;

/**
 * Created by efratn on 02/08/2017.
 */
@Configuration
@Import({MongoDbBulkOpUtilConfig.class,
        AdeManagerSdkConfig.class})
public class EventPersistencyServiceConfig {

    @Autowired
    private MongoTemplate mongoTemplate;

    @Autowired
    private AdeManagerSdk adeManagerSdk;

    @Bean
    public EventPersistencyService eventPersistencyService() {
        return new EventPersistencyServiceImpl(eventRepository(), outputToCollectionNameTranslator(), outputToClassNameTranslator());
    }

    @Bean
    public ScoredEventService scoredEventService() {
        return new ScoredEventServiceImpl(eventPersistencyService(), adeManagerSdk);
    }

    @Bean
    public EventRepository eventRepository() {
        return new EventMongoRepositoryImpl(mongoTemplate);
    }

    @Bean
    public OutputToCollectionNameTranslator outputToCollectionNameTranslator() {
        return new OutputToCollectionNameTranslator();
    }

    @Bean
    public OutputToClassNameTranslator outputToClassNameTranslator() {
        return new OutputToClassNameTranslator();
    }

}
