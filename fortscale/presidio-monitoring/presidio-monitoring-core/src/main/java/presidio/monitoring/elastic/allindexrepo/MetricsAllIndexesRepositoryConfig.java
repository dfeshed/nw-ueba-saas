package presidio.monitoring.elastic.allindexrepo;

import fortscale.utils.elasticsearch.PresidioElasticsearchTemplate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class MetricsAllIndexesRepositoryConfig {
    @Autowired
    private PresidioElasticsearchTemplate elasticsearchTemplate;

    @Bean
    public MetricsAllIndexesRepository metricsAllIndexesRepository()
    {
        return new MetricsAllIndexesRepositoryImpl(elasticsearchTemplate);
    }
}
