package presidio.output.domain.spring;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Import;
import org.springframework.data.elasticsearch.repository.config.EnableElasticsearchRepositories;
import presidio.monitoring.elastic.allindexrepo.MetricsAllIndexesRepository;
import presidio.monitoring.elastic.allindexrepo.MetricsAllIndexesRepositoryConfig;
import presidio.monitoring.elastic.repositories.MetricRepository;
import presidio.monitoring.elastic.services.PresidioMetricPersistencyService;
import presidio.monitoring.elastic.services.PresidioMetricPersistencyServiceImpl;
import presidio.output.domain.services.alerts.AlertPersistencyService;
import presidio.output.domain.services.alerts.AlertPersistencyServiceImpl;
import presidio.output.domain.services.entities.EntityPersistencyService;
import presidio.output.domain.services.entities.EntityPersistencyServiceImpl;

@Configuration
@EnableElasticsearchRepositories(basePackages = {"presidio.output.domain.repositories","presidio.monitoring.elastic.repositories"})
@Import(MetricsAllIndexesRepositoryConfig.class)
public class PresidioOutputPersistencyServiceConfig {

    @Autowired
    private MetricRepository metricRepository;

    @Bean
    public AlertPersistencyService alertPersistencyService() {
        return new AlertPersistencyServiceImpl();
    }

    @Bean
    public EntityPersistencyService entityPersistencyService() {
        return new EntityPersistencyServiceImpl();
    }

    @Autowired
    private MetricsAllIndexesRepository metricsAllIndexesRepository;

    @Bean
    PresidioMetricPersistencyService presidioMetricPersistencyService(){
        return new PresidioMetricPersistencyServiceImpl(metricRepository, metricsAllIndexesRepository);
    }

}
