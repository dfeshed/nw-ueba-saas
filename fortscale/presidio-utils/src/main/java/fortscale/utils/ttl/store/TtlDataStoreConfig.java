package fortscale.utils.ttl.store;

import fortscale.utils.mongodb.util.MongoDbBulkOpUtilConfig;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Import;
import org.springframework.data.mongodb.core.MongoTemplate;

/**
 * Created by maria_dorohin on 8/30/17.
 */
@Configuration
public class TtlDataStoreConfig {
    @Autowired
    private MongoTemplate mongoTemplate;

    @Bean
    public TtlDataStore ttlDataStore() {
        return new TtlDataStoreImpl(mongoTemplate);
    }
}
