package fortscale.entity.event;

import fortscale.aggregation.feature.event.AggrFeatureEventBuilderService;
import fortscale.aggregation.feature.event.config.AggrFeatureEventBuilderServiceConfig;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Import;

/**
 * Created by YaronDL on 7/17/2017.
 */
@Configuration
@Import({AggrFeatureEventBuilderServiceConfig.class})
public class EntityEventBuilderFactoryConfig {
    @Value("${fortscale.entity.event.retrieving.page.size}")
    private int retrievingPageSize;
    @Value("${fortscale.entity.event.store.page.size}")
    private int storePageSize;

    @Value("${streaming.event.field.type}")
    private String eventTypeFieldName;
    @Value("${streaming.event.field.type.entity_event}")
    private String eventTypeFieldValue;
    @Value("${streaming.entity_event.field.entity_event_type}")
    private String entityEventTypeFieldName;
    @Value("${impala.table.fields.epochtime}")
    private String epochtimeFieldName;

    @Autowired
    private EntityEventDataStore entityEventDataStore;

    @Autowired
    private AggrFeatureEventBuilderService aggrFeatureEventBuilderService;



    @Bean
    public EntityEventBuilderFactory getEntityEventBuilderFactory(){
        return new EntityEventBuilderFactory(entityEventDataStore, aggrFeatureEventBuilderService,
                retrievingPageSize, storePageSize, eventTypeFieldName, eventTypeFieldValue, entityEventTypeFieldName, epochtimeFieldName);
    }
}
