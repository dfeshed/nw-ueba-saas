package presidio.input.core.spring;


import fortscale.common.exporter.ElasticMetricsExporter;
import fortscale.common.exporter.FileMetricsExporter;
import fortscale.common.exporter.PresidioSystemPublicMetrics;
import fortscale.common.shell.PresidioExecutionService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.actuate.endpoint.MetricsEndpoint;
import org.springframework.context.annotation.*;
import presidio.input.core.services.data.AdeDataService;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Import;
import presidio.ade.sdk.executions.common.ADEManagerSDK;
import presidio.ade.sdk.executions.online.ADEManagerSDKConfig;
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
    private ADEManagerSDK adeManagerSDK;

    @Bean
    private MetricsEndpoint metricsEndpoint(){
        return  new MetricsEndpoint(new PresidioSystemPublicMetrics());
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
        return new InputExecutionServiceImpl(presidioInputPersistencyService, adeManagerSDK);
    }

}
