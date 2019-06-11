package presidio.output.commons.services.spring;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Import;
import presidio.output.commons.services.entity.EntityMappingService;
import presidio.output.commons.services.entity.EntityMappingServiceImpl;
import presidio.output.domain.translator.OutputToCollectionNameTranslator;

@Configuration
@Import(EventPersistencyServiceTestConfig.class)
public class EntityMappingServiceTestConfig {

    @Bean
    public EntityMappingService entityMappingService() {
        return new EntityMappingServiceImpl();
    }

    @Bean
    public OutputToCollectionNameTranslator outputToCollectionNameTranslator() {
        return new OutputToCollectionNameTranslator();
    }
}
