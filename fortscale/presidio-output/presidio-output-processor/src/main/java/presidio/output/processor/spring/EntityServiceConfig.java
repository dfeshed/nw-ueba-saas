package presidio.output.processor.spring;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Import;
import presidio.output.commons.services.alert.AlertSeverityService;
import presidio.output.commons.services.spring.EntitySeverityServiceConfig;
import presidio.output.commons.services.entity.EntitySeverityService;
import presidio.output.domain.services.alerts.AlertPersistencyService;
import presidio.output.domain.services.entities.EntityPersistencyService;
import presidio.output.domain.services.event.EventPersistencyService;
import presidio.output.processor.services.entity.EntityScoreService;
import presidio.output.processor.services.entity.EntityScoreServiceImpl;
import presidio.output.processor.services.entity.EntityService;
import presidio.output.processor.services.entity.EntityServiceImpl;

/**
 * Created by efratn on 22/08/2017.
 */
@Configuration
@Import(EntitySeverityServiceConfig.class)
public class EntityServiceConfig {

    @Value("${entity.batch.size:2000}")
    private int defaultEntitiesBatchSize;


    @Value("${alerts.batch.size:2000}")
    private int defaultAlertsBatchSize;

    @Value("${alert.affect.duration.days:90}")
    private int alertEffectiveDurationInDays;

    @Autowired
    private EventPersistencyService eventPersistencyService;

    @Autowired
    private AlertSeverityService alertSeverityService;

    @Autowired
    private EntityPersistencyService entityPersistencyService;

    @Autowired
    private EntitySeverityService entitySeverityService;

    @Autowired
    private AlertPersistencyService alertPersistencyService;


    @Bean
    public EntityService entityService() {
        return new EntityServiceImpl(eventPersistencyService, entityPersistencyService, alertPersistencyService, entityScoreService(), entitySeverityService, alertEffectiveDurationInDays, defaultAlertsBatchSize);
    }

    @Bean
    public EntityScoreService entityScoreService() {
        return new EntityScoreServiceImpl(entityPersistencyService, alertPersistencyService, alertSeverityService, defaultAlertsBatchSize, defaultEntitiesBatchSize);
    }

}
