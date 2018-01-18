package presidio.output.proccesor.spring;

import fortscale.utils.elasticsearch.config.ElasticsearchTestConfig;
import fortscale.utils.test.mongodb.MongodbTestConfig;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.test.context.ContextConfiguration;
import presidio.output.domain.repositories.EventMongoRepositoryImpl;
import presidio.output.domain.repositories.EventRepository;
import presidio.output.domain.services.event.EventPersistencyService;
import presidio.output.domain.services.users.UserPersistencyService;
import presidio.output.domain.services.users.UserPersistencyServiceImpl;
import presidio.output.domain.spring.EventPersistencyServiceConfig;
import presidio.output.domain.translator.OutputToClassNameTranslator;
import presidio.output.domain.translator.OutputToCollectionNameTranslator;
import presidio.output.processor.services.user.UserPropertiesUpdateService;
import presidio.output.processor.services.user.UserPropertiesUpdateServiceImpl;


@Configuration
@ContextConfiguration(classes = {PropertiesConfiguration.class, ElasticsearchTestConfig.class, MongodbTestConfig.class, EventPersistencyServiceConfig.class})
public class UserUpdatePropertiesTestConfiguration {

    //@Value("${batch.size}")
    //private int batchSize;

    @Autowired
    private MongoTemplate mongoTemplate;

    @Bean
    public UserPersistencyService userPersistencyService() {
        return new UserPersistencyServiceImpl();
    }

    @Bean
    public UserPropertiesUpdateService userPropertiesUpdateService() {
        return new UserPropertiesUpdateServiceImpl(eventPersistencyService, userPersistencyService(), 100);
    }

    @Autowired
    public EventPersistencyService eventPersistencyService;

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
