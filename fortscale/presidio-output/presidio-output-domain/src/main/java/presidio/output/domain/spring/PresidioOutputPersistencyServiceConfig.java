package presidio.output.domain.spring;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.elasticsearch.repository.config.EnableElasticsearchRepositories;
import presidio.output.domain.services.alerts.AlertPersistencyService;
import presidio.output.domain.services.alerts.AlertPersistencyServiceImpl;
import org.springframework.context.annotation.Import;
import presidio.output.domain.services.users.UserPersistencyService;
import presidio.output.domain.services.users.UserPersistencyServiceImpl;

@Configuration
@EnableElasticsearchRepositories(basePackages = "presidio.output.domain.repositories")
@Import(fortscale.utils.elasticsearch.config.ElasticsearchConfig.class)
public class PresidioOutputPersistencyServiceConfig {

    @Bean
    public AlertPersistencyService alertPersistencyService() {
        return new AlertPersistencyServiceImpl();
    }

    @Bean
    public UserPersistencyService userPersistencyService() {
        return new UserPersistencyServiceImpl();
    }

}
