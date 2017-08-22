package presidio.connector.manager.impl.spring;

import fortscale.utils.mongodb.util.ToCollectionNameTranslator;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.PropertySource;
import org.springframework.context.support.PropertySourcesPlaceholderConfigurer;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.repository.config.EnableMongoRepositories;
import org.springframework.validation.beanvalidation.LocalValidatorFactoryBean;
import presidio.connector.manager.impl.repositories.DataSourceRepository;
import presidio.connector.manager.impl.repositories.DataSourceRepositoryImpl;
import presidio.connector.manager.impl.services.DataServiceImpl;
import presidio.connector.manager.impl.services.PresidioInputPersistencyServiceMongoImpl;
import presidio.connector.manager.impl.validators.ValidationManager;
import presidio.sdk.api.services.DataService;
import presidio.sdk.api.services.PresidioInputPersistencyService;
import presidio.sdk.api.utils.InputToCollectionNameTranslator;


@Configuration
@EnableMongoRepositories(basePackages = "presidio.input.sdk.impl.repositories")
@PropertySource("classpath:inputSdk.properties")
public class PresidioInputPersistencyServiceConfig {

    @Autowired
    private MongoTemplate mongoTemplate;

    @Bean
    public static PropertySourcesPlaceholderConfigurer propertySourcesPlaceholderConfigurer() {
        return new PropertySourcesPlaceholderConfigurer();
    }

    @Bean
    public ToCollectionNameTranslator toCollectionNameTranslator(){
        return new InputToCollectionNameTranslator();
    }

    @Bean
    public DataSourceRepository dataSourceRepository(){
        return new DataSourceRepositoryImpl(mongoTemplate);
    }

    @Bean
    public LocalValidatorFactoryBean localValidatorFactoryBean(){
        return new LocalValidatorFactoryBean();
    }

    @Bean
    public PresidioInputPersistencyService presidioInputPersistencyService() {
        return new PresidioInputPersistencyServiceMongoImpl(dataService());
    }

    @Bean
    public ValidationManager validationManager(){
        return new ValidationManager(localValidatorFactoryBean().getValidator());
    }

    @Bean
    public DataService dataService() {
        return new DataServiceImpl(dataSourceRepository(), toCollectionNameTranslator(), validationManager());
    }

}
