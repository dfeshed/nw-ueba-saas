package presidio.input.core.spring;


import fortscale.common.elastic.ElasticMetricWriter;
import fortscale.common.elastic.repository.ElasticRepository;
import fortscale.common.elastic.services.ElasticExportService;
import fortscale.common.shell.PresidioExecutionService;
import org.elasticsearch.client.node.NodeClient;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.actuate.autoconfigure.ExportMetricWriter;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Import;
import org.springframework.data.elasticsearch.core.ElasticsearchTemplate;
import presidio.input.core.services.data.AdeDataService;
import presidio.input.core.services.impl.InputExecutionServiceImpl;
import presidio.input.sdk.impl.spring.PresidioInputPersistencyServiceConfig;
import presidio.sdk.api.services.PresidioInputPersistencyService;

@Configuration
@Import({ PresidioInputPersistencyServiceConfig.class, AdeDataServiceConfig.class})
public class InputCoreConfiguration {

    @Autowired
    private PresidioInputPersistencyService presidioInputPersistencyService;

    @Autowired
    private AdeDataService adeDataService;

    @Autowired
    private NodeClient nodeClient;

    @Bean
    @ExportMetricWriter
    private ElasticMetricWriter elasticMetricWriter() {
        return new ElasticMetricWriter();
    }

    @Bean
    private ElasticsearchTemplate elasticsearchTemplate() {
        return new ElasticsearchTemplate(nodeClient);
    }

    @Bean
    private ElasticRepository elasticRepository() {
        return new ElasticRepository(elasticsearchTemplate());
    }

    @Bean
    private ElasticExportService elasticExportService() {
        return new ElasticExportService(elasticRepository());
    }

    @Bean
    public PresidioExecutionService inputExecutionService() {
        return new InputExecutionServiceImpl(presidioInputPersistencyService, adeDataService);
    }

}
