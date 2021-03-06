package fortscale.utils.elasticsearch.config;


import fortscale.utils.elasticsearch.PresidioElasticsearchMappingContext;
import fortscale.utils.elasticsearch.PresidioElasticsearchTemplate;
import fortscale.utils.elasticsearch.PresidioResultMapper;
import fortscale.utils.elasticsearch.services.TemplateAnnotationExtractor;
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
import org.springframework.data.elasticsearch.core.ResultsMapper;
import org.springframework.data.elasticsearch.core.convert.MappingElasticsearchConverter;

import java.net.InetAddress;

@Configuration
@Import(EmbeddedElasticsearchConfig.class)
public class ElasticsearchTestConfig {

    @Value("${elasticsearch.host}")
    private String esHost;

    @Value("${elasticsearch.port}")
    private int esPort;

    @Value("${elasticsearch.clustername}")
    private String esClusterName;

    @Value("${elasticsearch.embedded:true}")
    private boolean isEsEmbedded;

    @Autowired
    public EmbeddedElasticsearchInitialiser embeddedElasticsearchInitialiser;

    @Bean
    public Client client() throws Exception {
        Settings esSettings = Settings.builder().put("cluster.name", esClusterName).build();
        int transportTcpPort = isEsEmbedded ? embeddedElasticsearchInitialiser.getTransportTcpPort(): esPort;
        return new PreBuiltTransportClient(esSettings).addTransportAddress(
                new InetSocketTransportAddress(InetAddress.getByName(esHost), transportTcpPort));
    }


    @Bean
    public MappingElasticsearchConverter mappingElasticsearchConverter() {
        return new MappingElasticsearchConverter(new PresidioElasticsearchMappingContext());
    }

    @Bean
    public ResultsMapper resultsMapper() {
        return new PresidioResultMapper(mappingElasticsearchConverter().getMappingContext());
    }

    @Bean
    public ElasticsearchOperations elasticsearchTemplate() throws Exception {
        return new PresidioElasticsearchTemplate(client(), new TemplateAnnotationExtractor(), resultsMapper());
    }
}