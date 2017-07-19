package presidio.input.core.spring;


import fortscale.common.exporter.ExporterConfiguration;
import fortscale.common.exporter.exporters.FileMetricsExporter;
import fortscale.common.exporter.exporters.MetricsExporter;
import fortscale.common.exporter.PresidioSystemPublicMetrics;
import fortscale.common.shell.PresidioExecutionService;
import fortscale.utils.monitoring.aspect.CustomMetric;
import fortscale.utils.monitoring.aspect.MonitoringAspects;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.actuate.endpoint.MetricsEndpoint;
import org.springframework.boot.actuate.endpoint.PublicMetrics;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ConfigurableApplicationContext;
import org.springframework.context.annotation.*;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Import;
import presidio.ade.sdk.executions.common.ADEManagerSDK;
import presidio.ade.sdk.executions.online.ADEManagerSDKConfig;
import presidio.input.core.services.impl.InputExecutionServiceImpl;
import presidio.input.sdk.impl.spring.PresidioInputPersistencyServiceConfig;
import presidio.sdk.api.services.PresidioInputPersistencyService;

import javax.naming.Context;

@Configuration
@Import({ PresidioInputPersistencyServiceConfig.class, AdeDataServiceConfig.class,ADEManagerSDKConfig.class,ExporterConfiguration.class})
public class InputCoreConfiguration {

    @Autowired
    private PresidioInputPersistencyService presidioInputPersistencyService;

    @Autowired
    private ADEManagerSDK adeManagerSDK;

    @Bean
    public PresidioExecutionService inputExecutionService() {
        return new InputExecutionServiceImpl(presidioInputPersistencyService, adeManagerSDK);
    }

}
