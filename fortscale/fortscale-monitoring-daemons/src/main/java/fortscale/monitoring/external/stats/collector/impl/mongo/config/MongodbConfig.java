package fortscale.monitoring.external.stats.collector.impl.mongo.config;

import com.mongodb.MongoClient;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.mongodb.MongoDbFactory;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.SimpleMongoDbFactory;

/**
 * mongodb configuration class
 * todo: move to more generic location
 */
@Configuration
public class MongodbConfig {

    @Value("${mongo.host.name}")
    private String mongoHostName;

    @Value("${mongo.host.port}")
    private int mongoHostPort;

    @Value("${mongo.db.name}")
    private String mongoDBName;

    public MongoDbFactory mongoDbFactory() throws Exception {
        return new SimpleMongoDbFactory(new MongoClient(mongoHostName,mongoHostPort), mongoDBName);
    }

    public @Bean MongoTemplate externalStatsMonitoringCollectorMongoTemplate() throws Exception {
        return new MongoTemplate(mongoDbFactory());
    }
}
