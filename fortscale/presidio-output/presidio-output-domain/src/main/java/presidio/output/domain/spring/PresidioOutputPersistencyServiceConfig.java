package presidio.output.domain.spring;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.elasticsearch.repository.config.EnableElasticsearchRepositories;
import presidio.output.domain.repositories.AlertRepository;
import presidio.output.domain.services.AlertService;
import presidio.output.domain.services.AlertServiceImpl;
import org.springframework.context.annotation.Import;

@Configuration
@EnableElasticsearchRepositories(basePackages = "presidio.output.domain.repositories")
@Import(fortscale.utils.elasticsearch.config.ElasticsearchConfig.class)
public class PresidioOutputPersistencyServiceConfig {

    @Bean
    public AlertService alertService() {
        return new AlertServiceImpl();
    }

}
