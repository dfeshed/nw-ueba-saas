package presidio.output.commons.services.spring;

import fortscale.utils.elasticsearch.config.ElasticsearchTestConfig;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Import;
import org.springframework.data.elasticsearch.repository.config.EnableElasticsearchRepositories;
import presidio.output.commons.services.user.UserPropertiesUpdateService;
import presidio.output.commons.services.user.UserPropertiesUpdateServiceImpl;
import presidio.output.domain.services.event.EventPersistencyService;
import presidio.output.domain.services.users.UserPersistencyService;
import presidio.output.domain.services.users.UserPersistencyServiceImpl;
import presidio.output.domain.translator.OutputToCollectionNameTranslator;


@Configuration
@EnableElasticsearchRepositories(basePackages = "presidio.output.domain.repositories")
@Import({PropertiesConfiguration.class, ElasticsearchTestConfig.class, EventPersistencyServiceTestConfig.class})
public class UserUpdatePropertiesTestConfiguration {


    @Bean
    public UserPersistencyService userPersistencyService() {
        return new UserPersistencyServiceImpl();
    }

    @Bean
    public UserPropertiesUpdateService userPropertiesUpdateService() {
        return new UserPropertiesUpdateServiceImpl(eventPersistencyService, new OutputToCollectionNameTranslator());
    }

    @Autowired
    public EventPersistencyService eventPersistencyService;

}
