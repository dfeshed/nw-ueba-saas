package presidio.input.core.spring;


import fortscale.common.exporter.FileMetricsExporter;
import fortscale.common.shell.PresidioExecutionService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.actuate.endpoint.MetricsEndpoint;
import org.springframework.context.annotation.*;
import presidio.input.core.services.data.AdeDataService;
import presidio.input.core.services.impl.InputExecutionServiceImpl;
import presidio.input.sdk.impl.spring.PresidioInputPersistencyServiceConfig;
import presidio.sdk.api.services.PresidioInputPersistencyService;

@Configuration
@Import({ PresidioInputPersistencyServiceConfig.class, AdeDataServiceConfig.class})
@EnableAspectJAutoProxy
@ComponentScan(basePackages="fortscale.utils.monitoring.aspect")
public class InputCoreConfiguration {

    @Autowired
    private PresidioInputPersistencyService presidioInputPersistencyService;

    @Autowired
    private AdeDataService adeDataService;

    @Autowired
    private MetricsEndpoint metricsEndpoint;

    @Bean
    public FileMetricsExporter fileMetricsExporter() {
        return new FileMetricsExporter(metricsEndpoint);
    }
/*
    @Autowired
    private NodeClient nodeClient;

    @Bean
    public ElasticsearchTemplate elasticsearchTemplate() {
        return new ElasticsearchTemplate(nodeClient);
    }

    @Bean
    public ElasticMetricWriter elasticMetricWriter(){
        return new ElasticMetricWriter(elasticsearchTemplate(),null)
    }
    */
    @Bean
    public PresidioExecutionService inputExecutionService() {
        return new InputExecutionServiceImpl(presidioInputPersistencyService, adeDataService);
    }

}
