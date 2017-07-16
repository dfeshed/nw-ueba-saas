package presidio.input.core.spring;


import fortscale.common.elastic.ElasticMetricWriter;
import fortscale.common.exporter.ElasticMetricsExporter;
import fortscale.common.exporter.FileMetricsExporter;
import fortscale.common.exporter.PresidioSystmePublicMetrics;
import fortscale.common.shell.PresidioExecutionService;
import org.elasticsearch.client.node.NodeClient;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.actuate.endpoint.MetricsEndpoint;
import org.springframework.context.annotation.*;
import org.springframework.data.elasticsearch.core.ElasticsearchTemplate;
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


    private MetricsEndpoint metricsEndpoint(){
        return  new MetricsEndpoint(new PresidioSystmePublicMetrics());
    }

    @Bean
    public FileMetricsExporter fileMetricsExporter() {
        return new FileMetricsExporter(metricsEndpoint());
    }

    @Bean
    public ElasticMetricsExporter elasticMetricsExporter(){
        return new ElasticMetricsExporter(metricsEndpoint());
    }

    @Bean
    public PresidioExecutionService inputExecutionService() {
        return new InputExecutionServiceImpl(presidioInputPersistencyService, adeDataService);
    }

}
