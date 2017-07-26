package fortscale.utils.elasticsearch;

import org.elasticsearch.client.Client;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.elasticsearch.core.ElasticsearchOperations;
import org.springframework.data.elasticsearch.core.convert.ElasticsearchConverter;
import org.springframework.data.elasticsearch.core.convert.MappingElasticsearchConverter;
import org.springframework.data.elasticsearch.core.mapping.SimpleElasticsearchMappingContext;

/**
 * Created by julien on 23/06/2016.
 */
@Configuration
public class ElasticsearchPresidioDataAutoConfiguration {

    @Bean
    @ConditionalOnMissingBean
    public ElasticsearchOperations elasticsearchTemplate(Client client) {
        return new PresidioElasticsearchTemplate(client);
    }

    @Bean
    @ConditionalOnMissingBean
    public ElasticsearchConverter elasticsearchConverter(SimpleElasticsearchMappingContext mappingContext) {
        return new MappingElasticsearchConverter(mappingContext);
    }

    @Bean
    @ConditionalOnMissingBean
    public SimpleElasticsearchMappingContext mappingContext() {
        return new SimpleElasticsearchMappingContext();
    }


}