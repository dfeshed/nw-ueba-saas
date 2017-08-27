package presidio.output.processor.spring;

import fortscale.utils.mongodb.config.MongoConfig;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Import;
import presidio.ade.sdk.common.AdeManagerSdk;
import presidio.ade.sdk.common.AdeManagerSdkConfig;
import presidio.output.domain.services.users.UserPersistencyService;
import presidio.output.processor.services.OutputExecutionService;
import presidio.output.processor.services.OutputExecutionServiceImpl;
import presidio.output.processor.services.alert.AlertService;
import presidio.output.processor.services.user.UserScoreService;
import presidio.output.processor.services.user.UserScoreServiceImpl;

/**
 * Created by shays on 17/05/2017.
 */
@Configuration
@Import({MongoConfig.class, AdeManagerSdkConfig.class, AlertServiceElasticConfig.class})
public class OutputProcessorConfiguration {

    @Autowired
    public UserPersistencyService userPersistencyService;

    @Value("${USER.SEVERITIES.BATCH.SIZE:1000}")
    public int defaultUsersBatchFile;


    @Value("${USER.SEVERITIES.PERCENT.THRESHOLD.CRITICAL:75}")
    private int percentThresholdCritical;

    @Value("${USER.SEVERITIES.PERCENT.THRESHOLD.HIGH:50}")
    private int percentThresholdHigh;

    @Value("${USER.SEVERITIES.PERCENT.THRESHOLD.HIGH:25}")
    private int percentThresholdMedium;

    @Autowired
    private AdeManagerSdk adeManagerSdk;

    @Autowired
    private AlertService alertService;

    @Bean
    public OutputExecutionService outputProcessService() {
        return new OutputExecutionServiceImpl(adeManagerSdk, alertService);
    }

    @Bean
    public UserScoreService userScoreService(){
        return new UserScoreServiceImpl(userPersistencyService,defaultUsersBatchFile,percentThresholdCritical,percentThresholdHigh,percentThresholdMedium);
    }
}
