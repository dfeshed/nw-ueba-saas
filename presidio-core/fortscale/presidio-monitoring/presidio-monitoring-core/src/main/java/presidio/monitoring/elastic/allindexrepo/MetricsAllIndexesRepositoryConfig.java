package presidio.monitoring.elastic.allindexrepo;

import fortscale.utils.elasticsearch.PresidioElasticsearchTemplate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.elasticsearch.core.ElasticsearchOperations;

@Configuration
public class MetricsAllIndexesRepositoryConfig {
    @Autowired
    private ElasticsearchOperations elasticsearchTemplate;

    @Bean
    public MetricsAllIndexesRepository metricsAllIndexesRepository()
    {
        return new MetricsAllIndexesRepositoryImpl(elasticsearchTemplate);
    }
}
