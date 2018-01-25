package presidio.output.processor.spring;

import fortscale.utils.elasticsearch.config.ElasticsearchConfig;
import fortscale.utils.mongodb.config.MongoConfig;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Import;
import presidio.ade.sdk.common.AdeManagerSdk;
import presidio.ade.sdk.common.AdeManagerSdkConfig;
import presidio.monitoring.spring.PresidioMonitoringConfiguration;
import presidio.output.commons.services.user.UserSeverityService;
import presidio.output.domain.services.event.EventPersistencyService;
import presidio.output.domain.services.users.UserPersistencyService;
import presidio.output.domain.spring.EventPersistencyServiceConfig;
import presidio.output.processor.services.OutputExecutionService;
import presidio.output.processor.services.OutputExecutionServiceImpl;
import presidio.output.processor.services.OutputMonitoringService;
import presidio.output.processor.services.alert.AlertService;
import presidio.output.processor.services.user.UserService;

/**
 * Created by shays on 17/05/2017.
 */
@Configuration
@Import({PresidioMonitoringConfiguration.class})
public class OutputMonitoringConfiguration {

    @Bean
    public OutputMonitoringService outputMonitoringService() {
        return new OutputMonitoringService();
    }
}
