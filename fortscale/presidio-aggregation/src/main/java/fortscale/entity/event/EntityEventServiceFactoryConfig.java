package fortscale.entity.event;

import fortscale.aggregation.feature.event.AggrFeatureEventBuilderService;
import fortscale.aggregation.feature.event.config.AggrFeatureEventBuilderServiceConfig;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Import;

/**
 * Created by YaronDL on 7/17/2017.
 */
@Configuration
@Import({AggrFeatureEventBuilderServiceConfig.class,
        EntityEventBuilderFactoryConfig.class
})
public class EntityEventServiceFactoryConfig {

    @Autowired
    private EntityEventConfService entityEventConfService;
    @Autowired
    private AggrFeatureEventBuilderService aggrFeatureEventBuilderService;
    @Autowired
    private EntityEventBuilderFactory entityEventBuilderFactory;

    @Bean
    public EntityEventServiceFactory getEntityEventServiceFactory(){
        return new EntityEventServiceFactory(entityEventConfService, aggrFeatureEventBuilderService, entityEventBuilderFactory);
    }
}
