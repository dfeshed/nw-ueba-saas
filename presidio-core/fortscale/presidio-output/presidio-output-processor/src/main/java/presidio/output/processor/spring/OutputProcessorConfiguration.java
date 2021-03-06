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
import presidio.output.domain.services.entities.EntityPersistencyService;
import presidio.output.domain.services.event.EventPersistencyService;
import presidio.output.domain.spring.EventPersistencyServiceConfig;
import presidio.output.processor.services.OutputExecutionService;
import presidio.output.processor.services.OutputExecutionServiceImpl;
import presidio.output.processor.services.OutputMonitoringService;
import presidio.output.processor.services.alert.AlertService;
import presidio.output.processor.services.entity.EntityService;

/**
 * Created by shays on 17/05/2017.
 */
@Configuration
@Import({
        EventPersistencyServiceConfig.class,
        MongoConfig.class,
        AdeManagerSdkConfig.class,
        AlertServiceElasticConfig.class,
        OutputMonitoringConfiguration.class,
        ElasticsearchConfig.class
})
public class OutputProcessorConfiguration {
    @Autowired
    private AdeManagerSdk adeManagerSdk;

    @Autowired
    private AlertService alertService;

    @Autowired
    private EntityService entityService;

    @Autowired
    private EntityPersistencyService entityPersistencyService;

    @Autowired
    private EventPersistencyService eventPersistencyService;

    @Value("${smart.threshold.score}")
    private int smartThreshold;

    @Value("${smart.page.size}")
    private int smartPageSize;

    @Value("${alert.page.size}")
    private int alertPageSize;

    @Value("${output.data.retention.in.days}")
    private long retentionOutputDataDays;

    @Autowired
    private OutputMonitoringService outputMonitoringService;

    @Bean
    public OutputExecutionService outputProcessService() {
        return new OutputExecutionServiceImpl(adeManagerSdk, alertService, entityService, eventPersistencyService, outputMonitoringService, smartThreshold, smartPageSize, alertPageSize, retentionOutputDataDays);
    }
}
