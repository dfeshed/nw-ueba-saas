package presidio.output.domain.spring;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.elasticsearch.repository.config.EnableElasticsearchRepositories;
import presidio.monitoring.elastic.repositories.MetricRepository;
import presidio.monitoring.elastic.services.PresidioMetricPersistencyService;
import presidio.monitoring.elastic.services.PresidioMetricPersistencyServiceImpl;
import presidio.output.domain.services.alerts.AlertPersistencyService;
import presidio.output.domain.services.alerts.AlertPersistencyServiceImpl;
import presidio.output.domain.services.users.UserPersistencyService;
import presidio.output.domain.services.users.UserPersistencyServiceImpl;

@Configuration
@EnableElasticsearchRepositories(basePackages = {"presidio.output.domain.repositories","presidio.monitoring.elastic.repositories"})
public class PresidioOutputPersistencyServiceConfig {

    @Autowired
    private MetricRepository metricRepository;

    @Bean
    public AlertPersistencyService alertPersistencyService() {
        return new AlertPersistencyServiceImpl();
    }

    @Bean
    public UserPersistencyService userPersistencyService() {
        return new UserPersistencyServiceImpl();
    }

    @Bean
    PresidioMetricPersistencyService presidioMetricPersistencyService(){
        return new PresidioMetricPersistencyServiceImpl(metricRepository);
    }

}
