package fortscale.utils.elasticsearch.config;


import fortscale.utils.elasticsearch.PresidioElasticsearchTemplate;
import fortscale.utils.elasticsearch.services.TemplateAnnotationExtractor;
import fortscale.utils.elasticsearch.services.TemplateExtractor;
import org.elasticsearch.client.Client;
import org.elasticsearch.common.settings.Settings;
import org.elasticsearch.common.transport.InetSocketTransportAddress;
import org.elasticsearch.transport.client.PreBuiltTransportClient;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Import;
import org.springframework.data.elasticsearch.core.ElasticsearchOperations;

import java.net.InetAddress;

@Configuration
@Import(EmbeddedElasticsearchConfig.class)
public class ElasticsearchTestConfig {

    @Value("${elasticsearch.host}")
    private String EsHost;

    @Value("${elasticsearch.port}")
    private int EsPort;

    @Value("${elasticsearch.clustername}")
    private String EsClusterName;

    @Autowired
    public EmbeddedElasticsearchInitialiser embeddedElasticsearchInitialiser;

    @Bean
    public Client client() throws Exception {
        Settings esSettings = Settings.builder().put("cluster.name", EsClusterName).build();
        return new PreBuiltTransportClient(esSettings).addTransportAddress(new InetSocketTransportAddress(InetAddress.getByName(EsHost), EsPort));
    }

    @Bean
    public TemplateExtractor fileToStringCreating() {
        return new TemplateAnnotationExtractor();
    }

    @Bean
    public ElasticsearchOperations elasticsearchTemplate() throws Exception {
        return new PresidioElasticsearchTemplate(client(), fileToStringCreating());
    }


}