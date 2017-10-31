package fortscale.utils.elasticsearch.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.aspectj.EnableSpringConfigured;

@Configuration
@EnableSpringConfigured
public class EmbeddedElasticTestConfig {

    @Bean
    public EmbeddedElasticsearchInitialiser embeddedElasticsearchInitialiser() {
        return new EmbeddedElasticsearchInitialiser();
    }
}
