package presidio.ade.domain.store.aggr;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Import;
import org.springframework.data.mongodb.core.MongoTemplate;

/**
 * Created by barak_schuster on 7/10/17.
 */
@Configuration
@Import(AggrDataAdeToCollectionNameTranslatorConfig.class)
public class AggrDataStoreConfig {
    @Autowired
    public MongoTemplate mongoTemplate;
    @Autowired
    public AggrDataAdeToCollectionNameTranslator translator;

    @Bean
    public AggrDataStore aggrDataStore()
    {
        return new AggrDataStoreMongoImpl(mongoTemplate,translator);
    }

}
