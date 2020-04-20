package presidio.output.processor.spring;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Import;
import presidio.ade.sdk.common.AdeManagerSdk;
import presidio.ade.sdk.common.AdeManagerSdkConfig;
import presidio.monitoring.elastic.services.PresidioMetricPersistencyService;
import presidio.monitoring.spring.PresidioMonitoringConfiguration;
import presidio.output.processor.services.OutputMonitoringService;

/**
 * Created by shays on 17/05/2017.
 */
@Configuration
@Import({PresidioMonitoringConfiguration.class, AdeManagerSdkConfig.class})
public class OutputMonitoringConfiguration {

    @Autowired
    private AdeManagerSdk adeManagerSdk;

    @Autowired
    private PresidioMetricPersistencyService metricPersistencyService;

    @Bean
    public OutputMonitoringService outputMonitoringService() {
        return new OutputMonitoringService(adeManagerSdk, metricPersistencyService);
    }
}
