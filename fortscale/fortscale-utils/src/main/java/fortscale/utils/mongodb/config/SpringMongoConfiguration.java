package fortscale.utils.mongodb.config;

import com.mongodb.*;
import fortscale.utils.EncryptionUtils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.mongodb.config.AbstractMongoConfiguration;
import org.springframework.data.mongodb.repository.config.EnableMongoRepositories;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by rans on 27/10/15.
 */
@Configuration
@EnableMongoRepositories(basePackages = "fortscale")
public class SpringMongoConfiguration extends AbstractMongoConfiguration {


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

    @Override
    protected String getDatabaseName() {
        return mongoDBName;
    }

    @Override
    @Bean
    public Mongo mongo() throws Exception {
        MongoClient client;
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

            client = new MongoClient(address, credentials);
        } else {
            client = new MongoClient(mongoHostName,mongoHostPort);
        }

        client.setWriteConcern(WriteConcern.SAFE);
        return client;
    }

    @Override
    protected String getMappingBasePackage() {
        return "fortscale";
    }

}
