package fortscale.utils.mongodb;

import com.mongodb.Mongo;
import com.mongodb.MongoClient;
import com.mongodb.WriteConcern;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.mongodb.config.AbstractMongoConfiguration;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.repository.config.EnableMongoRepositories;

/**
 * Created by rans on 27/10/15.
 */
public class SpringMongoConfiguration extends AbstractMongoConfiguration {

    @Value("${mongo.host.name}")
    private String mongoHost;

    @Value("${mongo.db.name}")
    private String dbname;

    @Override
    protected String getDatabaseName() {
        return dbname;
    }

    @Override
    @Bean
    public Mongo mongo() throws Exception {
        MongoClient client = new MongoClient(mongoHost);
        client.setWriteConcern(WriteConcern.SAFE);
        return client;
    }

    @Override
    protected String getMappingBasePackage() {
        return "fortscale";
    }

}
