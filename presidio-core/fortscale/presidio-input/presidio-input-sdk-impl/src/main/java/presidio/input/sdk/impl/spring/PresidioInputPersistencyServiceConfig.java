package presidio.input.sdk.impl.spring;

import fortscale.common.general.Schema;
import fortscale.utils.mongodb.util.MongoDbBulkOpUtil;
import fortscale.utils.mongodb.util.MongoDbBulkOpUtilConfig;
import fortscale.utils.mongodb.util.ToCollectionNameTranslator;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.*;
import org.springframework.context.support.PropertySourcesPlaceholderConfigurer;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.repository.config.EnableMongoRepositories;
import org.springframework.validation.beanvalidation.LocalValidatorFactoryBean;
import presidio.input.sdk.impl.repositories.DataSourceRepository;
import presidio.input.sdk.impl.repositories.DataSourceRepositoryImpl;
import presidio.input.sdk.impl.services.DataServiceImpl;
import presidio.input.sdk.impl.services.PresidioInputPersistencyServiceMongoImpl;
import presidio.input.sdk.impl.validators.ValidationManager;
import presidio.sdk.api.services.DataService;
import presidio.sdk.api.services.PresidioInputPersistencyService;
import presidio.sdk.api.utils.InputToCollectionNameTranslator;


@Configuration
@EnableMongoRepositories(basePackages = "presidio.input.sdk.impl.repositories")
@PropertySources({@PropertySource("classpath:inputSdk.properties"),@PropertySource(value = "file:///etc/netwitness/presidio/configserver/configurations/application.properties", ignoreResourceNotFound=true)})
@Import({MongoDbBulkOpUtilConfig.class})
public class PresidioInputPersistencyServiceConfig {

    @Autowired
    private MongoTemplate mongoTemplate;

    @Autowired
    private MongoDbBulkOpUtil mongoDbBulkOpUtil;

    @Bean
    public static PropertySourcesPlaceholderConfigurer propertySourcesPlaceholderConfigurer() {
        return new PropertySourcesPlaceholderConfigurer();
    }

    @Bean
    public ToCollectionNameTranslator<Schema> toCollectionNameTranslator() {
        return new InputToCollectionNameTranslator();
    }

    @Bean
    public DataSourceRepository dataSourceRepository() {
        return new DataSourceRepositoryImpl(mongoTemplate, mongoDbBulkOpUtil);
    }

    @Bean
    public LocalValidatorFactoryBean localValidatorFactoryBean() {
        return new LocalValidatorFactoryBean();
    }

    @Bean
    public PresidioInputPersistencyService presidioInputPersistencyService() {
        return new PresidioInputPersistencyServiceMongoImpl(dataService());
    }

    @Bean
    public ValidationManager validationManager() {
        return new ValidationManager(localValidatorFactoryBean().getValidator());
    }

    @Bean
    public DataService dataService() {
        return new DataServiceImpl(dataSourceRepository(), toCollectionNameTranslator(), validationManager());
    }

}
