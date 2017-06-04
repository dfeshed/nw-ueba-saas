package presidio.ade.domain.store.input.store;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Import;
import org.springframework.data.mongodb.core.MongoTemplate;
import presidio.ade.domain.store.translators.ADEInputDataToCollectionNameTranslator;
import presidio.ade.domain.store.translators.ADEInputDataToCollectionNameTranslatorConfig;

@Configuration
@Import(
        {
                ADEInputDataToCollectionNameTranslatorConfig.class
        })
public class ADEInputDataStoreConfig {

    @Autowired
    private MongoTemplate mongoTemplate;
    @Autowired
    private ADEInputDataToCollectionNameTranslator translator;

    @Bean
    public ADEInputDataStore ADEInputDataStoreImpl() {
        return new ADEInputDataStoreImplMongo(mongoTemplate, translator);
    }
}
