package fortscale.utils.test.mongodb;

import com.github.fakemongo.Fongo;
import com.mongodb.Mongo;
import fortscale.utils.mongodb.config.MongoConfig;
import fortscale.utils.spring.TestPropertiesPlaceholderConfigurer;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.Properties;

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
public class MongodbTestConfig extends MongoConfig {

    @Override
    protected String getDatabaseName() {
        return mongoDBName;
    }

    @Override
    public Mongo mongo() throws Exception {
        Fongo fongo = new Fongo(mongoDBName);

        return fongo.getMongo();
    }
}
