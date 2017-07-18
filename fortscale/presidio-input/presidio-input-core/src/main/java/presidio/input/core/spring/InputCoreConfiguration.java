package presidio.input.core.spring;


import fortscale.common.exporter.exporters.FileMetricsExporter;
import fortscale.common.exporter.exporters.MetricsExporter;
import fortscale.common.exporter.PresidioSystemPublicMetrics;
import fortscale.common.shell.PresidioExecutionService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.actuate.endpoint.MetricsEndpoint;
import org.springframework.boot.actuate.endpoint.PublicMetrics;
import org.springframework.context.annotation.*;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Import;
import presidio.ade.sdk.executions.common.ADEManagerSDK;
import presidio.input.core.services.impl.InputExecutionServiceImpl;
import presidio.input.sdk.impl.spring.PresidioInputPersistencyServiceConfig;
import presidio.sdk.api.services.PresidioInputPersistencyService;

@Configuration
@Import({ PresidioInputPersistencyServiceConfig.class, AdeDataServiceConfig.class})
@EnableAspectJAutoProxy
@ComponentScan(basePackages= {"fortscale.utils.monitoring.aspect","fortscale.common.exporter"})
public class InputCoreConfiguration {

    @Autowired
    private PresidioInputPersistencyService presidioInputPersistencyService;

    @Autowired
    private ADEManagerSDK adeManagerSDK;

    @Bean
    public PublicMetrics publicMetrics(){
        return new PresidioSystemPublicMetrics();
    }

    @Bean
    public MetricsEndpoint metricsEndpoint(){
        return  new MetricsEndpoint(publicMetrics());
    }

    @Bean
    public MetricsExporter fileMetricsExporter() {
        return new FileMetricsExporter(metricsEndpoint());
    }


    @Bean
    public PresidioExecutionService inputExecutionService() {
        return new InputExecutionServiceImpl(presidioInputPersistencyService, adeManagerSDK);
    }

}
