package presidio.ade.domain.store.scored;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Import;
import org.springframework.data.mongodb.core.MongoTemplate;

/**
 * Created by YaronDL on 6/13/2017.
 */

@Configuration
@Import({
        ScoredDataToCollectionNameTranslatorConfig.class
})
public class ScoredDataStoreMongoConfig {
    @Autowired
    private MongoTemplate mongoTemplate;
    @Autowired
    private ScoredDataToCollectionNameTranslator translator;

    @Bean
    public ScoredDataStore scoredDataStore() { return new ScoredDataStoreMongoImpl(mongoTemplate, translator);}
}
