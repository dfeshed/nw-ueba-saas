package fortscale.utils.elasticsearch.config;


import fortscale.utils.elasticsearch.PresidioElasticsearchTemplate;
import org.elasticsearch.client.Client;
import org.elasticsearch.common.settings.Settings;
import org.elasticsearch.common.transport.InetSocketTransportAddress;
import org.elasticsearch.transport.client.PreBuiltTransportClient;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Profile;
import org.springframework.data.elasticsearch.core.ElasticsearchOperations;

import java.net.InetAddress;

@Configuration
public class EmbeddedElasticsearchConfig {


    //Embedded elasticsearch is used for tests only
    //This bean must be initialized before elasticsearch client bean
    @Bean
    public EmbeddedElasticsearchInitialiser embeddedElasticsearchInitialiser() {
        return new EmbeddedElasticsearchInitialiser();
    }



}