package presidio.output.commons.services.spring;

import fortscale.utils.elasticsearch.config.ElasticsearchTestConfig;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Import;
import org.springframework.data.elasticsearch.repository.config.EnableElasticsearchRepositories;
import presidio.output.domain.services.entities.EntityPersistencyService;
import presidio.output.domain.services.entities.EntityPersistencyServiceImpl;
import presidio.output.domain.services.event.EventPersistencyService;


@Configuration
@EnableElasticsearchRepositories(basePackages = "presidio.output.domain.repositories")
@Import({PropertiesConfiguration.class, ElasticsearchTestConfig.class, EventPersistencyServiceTestConfig.class})
public class EntityUpdatePropertiesTestConfiguration {


    @Bean
    public EntityPersistencyService entityPersistencyService() {
        return new EntityPersistencyServiceImpl();
    }

    @Autowired
    public EventPersistencyService eventPersistencyService;

}
