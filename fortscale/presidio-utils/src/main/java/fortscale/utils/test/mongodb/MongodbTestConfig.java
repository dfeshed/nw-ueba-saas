package fortscale.utils.test.mongodb;

import com.github.fakemongo.async.FongoAsync;
import com.mongodb.Mongo;
import fortscale.utils.mongodb.config.MongoConfig;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Import;

/**
 * TL;DR configuration class that should be used in test that enables work with the popular object {@link org.springframework.data.mongodb.core.MongoTemplate}
 *
 * Fongo is an in-memory java implementation of MongoDB.
 * It intercepts calls to the standard mongo-java-driver for finds, updates, inserts, removes and other methods.
 * The primary use is for lightweight unit testing where you don't want to spin up a mongod process.
 *
 * PAY ATTENTION: that not all of of mongod features are implemented in Fongo. (Exceptions & Indexes might act differently)
 *
 * Created by barak_schuster on 12/22/16.
 */
@Configuration
@Import(MongodbTestPropertiesConfig.class)
public class MongodbTestConfig extends MongoConfig {

    @Bean FongoAsync fongoAsync() {
        FongoAsync fongoAsync = new FongoAsync(mongoDBName);
        return fongoAsync;
    }

    @Bean
    public com.mongodb.async.client.MongoClient asyncClient() throws Exception {
        return fongoAsync().getMongo();
    }

    @Override
    protected String getDatabaseName() {
        return mongoDBName;
    }

    @Override
    public Mongo mongo() throws Exception {
        return fongoAsync().getFongo().getMongo();
    }


}
