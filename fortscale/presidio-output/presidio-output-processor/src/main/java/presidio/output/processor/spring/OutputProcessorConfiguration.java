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
import presidio.output.processor.services.alert.AlertService;
import presidio.output.processor.services.user.UserService;

/**
 * Created by shays on 17/05/2017.
 */
@Configuration
@Import({EventPersistencyServiceConfig.class, MongoConfig.class, AdeManagerSdkConfig.class, AlertServiceElasticConfig.class, PresidioMonitoringConfiguration.class, ElasticsearchConfig.class})
public class OutputProcessorConfiguration {

    @Autowired
    private AdeManagerSdk adeManagerSdk;

    @Autowired
    private AlertService alertService;

    @Autowired
    private UserService userService;

    @Autowired
    private UserPersistencyService userPersistencyService;

    @Autowired
    private UserSeverityService userSeverityService;

    @Autowired
    private EventPersistencyService eventPersistencyService;

    @Value("${smart.threshold.score}")
    private int smartThreshold;

    @Value("${smart.page.size}")
    private int smartPageSize;

    @Value("${output.enriched.events.retention.in.days}")
    private long retentionEnrichedEventsDays;

    @Value("${output.result.events.retention.in.days}")
    private long retentionResultEventsDays;

    @Bean
    public OutputExecutionService outputProcessService() {
        return new OutputExecutionServiceImpl(adeManagerSdk, alertService, userService, userSeverityService, eventPersistencyService, smartThreshold, smartPageSize, retentionEnrichedEventsDays, retentionResultEventsDays);
    }
}
