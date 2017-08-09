package fortscale.monitoring.external.stats.collector.impl.mongo.config;

import com.mongodb.MongoClient;
import com.mongodb.MongoCredential;
import com.mongodb.ServerAddress;
import fortscale.utils.EncryptionUtils;
import org.apache.commons.lang.StringUtils;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.mongodb.MongoDbFactory;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.SimpleMongoDbFactory;

import java.util.ArrayList;
import java.util.List;

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

    @Value("${mongo.db.user}")
    private String mongoUserName;

    @Value("${mongo.db.password}")
    private String mongoPassword;

    public MongoDbFactory mongoDbFactory() throws Exception {
        if (StringUtils.isNotBlank(mongoUserName) && StringUtils.isNotBlank(mongoPassword)) {
            ServerAddress address = new ServerAddress(mongoHostName, mongoHostPort);
            List<MongoCredential> credentials = new ArrayList<>();
            credentials.add(
                    MongoCredential.createMongoCRCredential(
                            mongoUserName,
                            mongoDBName,
                            EncryptionUtils.decrypt(mongoPassword).toCharArray()
                    )
            );

            return new SimpleMongoDbFactory(new MongoClient(address, credentials), mongoDBName);
        } else {
            return new SimpleMongoDbFactory(new MongoClient(mongoHostName,mongoHostPort), mongoDBName);
        }

    }

    public @Bean MongoTemplate externalStatsMonitoringCollectorMongoTemplate() throws Exception {
        return new MongoTemplate(mongoDbFactory());
    }
}
