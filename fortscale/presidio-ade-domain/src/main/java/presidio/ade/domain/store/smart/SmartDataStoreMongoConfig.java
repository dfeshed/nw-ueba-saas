package presidio.ade.domain.store.smart;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.mongodb.core.MongoTemplate;

/**
 * Created by efratn on 23/07/2017.
 */
@Configuration
public class SmartDataStoreMongoConfig {

    @Autowired
    private MongoTemplate mongoTemplate;

    @Bean
    public SmartDataStore smartDataStore() {

        return new SmartDataStoreMongoImpl(mongoTemplate);
    }
}
