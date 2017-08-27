package presidio.output.processor.spring;

import fortscale.utils.mongodb.config.MongoConfig;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Import;
import presidio.ade.sdk.common.AdeManagerSdk;
import presidio.ade.sdk.common.AdeManagerSdkConfig;
import presidio.output.domain.services.alerts.AlertPersistencyService;
import presidio.output.domain.services.event.EventPersistencyService;
import presidio.output.domain.services.users.UserPersistencyService;
import presidio.output.processor.services.OutputExecutionService;
import presidio.output.processor.services.OutputExecutionServiceImpl;
import presidio.output.processor.services.alert.AlertService;
import presidio.output.processor.services.user.UserService;

/**
 * Created by shays on 17/05/2017.
 */
@Configuration
@Import({MongoConfig.class, AdeManagerSdkConfig.class, AlertServiceElasticConfig.class, UserServiceConfig.class})
public class OutputProcessorConfiguration {

    @Autowired
    private AdeManagerSdk adeManagerSdk;

    @Autowired
    private AlertService alertService;

    @Autowired
    private UserService userService;

    @Autowired
    private UserPersistencyService userPersistencyService;

    @Bean
    public OutputExecutionService outputProcessService() {
        return new OutputExecutionServiceImpl(adeManagerSdk, alertService, userService);
    }
}
