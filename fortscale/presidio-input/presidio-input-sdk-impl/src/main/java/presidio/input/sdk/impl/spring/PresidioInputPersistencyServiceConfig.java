package presidio.input.sdk.impl.spring;

import fortscale.utils.mongodb.util.ToCollectionNameTranslator;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.repository.config.EnableMongoRepositories;
import presidio.input.sdk.impl.repositories.DataSourceRepository;
import presidio.input.sdk.impl.repositories.DataSourceRepositoryImpl;
import presidio.input.sdk.impl.services.DataServiceImpl;
import presidio.input.sdk.impl.services.PresidioInputPersistencyServiceMongoImpl;
import presidio.sdk.api.domain.DataService;
import presidio.sdk.api.domain.InputToCollectionNameTranslator;
import presidio.sdk.api.services.PresidioInputPersistencyService;


@Configuration
@EnableMongoRepositories(basePackages = "presidio.input.sdk.impl.repositories")
public class PresidioInputPersistencyServiceConfig {

    @Autowired
    private MongoTemplate mongoTemplate;

    @Bean
    public ToCollectionNameTranslator toCollectionNameTranslator(){
        return new InputToCollectionNameTranslator();
    }

    @Bean
    public DataSourceRepository dataSourceRepository(){
        return new DataSourceRepositoryImpl(mongoTemplate);
    }

    @Bean
    public DataService dlpFileDataService() {
        return new DataServiceImpl(dataSourceRepository(), toCollectionNameTranslator());
    }

    @Bean
    public PresidioInputPersistencyService presidioInputPersistencyService() {
        return new PresidioInputPersistencyServiceMongoImpl(dlpFileDataService());
    }

}
