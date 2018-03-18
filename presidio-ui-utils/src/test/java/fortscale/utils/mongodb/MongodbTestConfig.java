package fortscale.utils.mongodb;

import com.github.fakemongo.Fongo;
import com.mongodb.Mongo;
import fortscale.utils.mongodb.converter.FSMappingMongoConverter;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.mongodb.config.AbstractMongoConfiguration;
import org.springframework.data.mongodb.core.convert.DbRefResolver;
import org.springframework.data.mongodb.core.convert.DefaultDbRefResolver;
import org.springframework.data.mongodb.core.convert.MappingMongoConverter;

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
public class MongodbTestConfig extends AbstractMongoConfiguration {

    private static final String FORTSCALE_TEST_DB = "fortscaleTestDb";

    @Bean
    @Override
    public MappingMongoConverter mappingMongoConverter() throws Exception {

        DbRefResolver dbRefResolver = new DefaultDbRefResolver(mongoDbFactory());
        FSMappingMongoConverter converter = new FSMappingMongoConverter(dbRefResolver, mongoMappingContext());
        converter.setCustomConversions(customConversions());
        converter.setMapKeyDotReplacement("#dot#");
        converter.setMapKeyDollarReplacement("#dlr#");

        return converter;
    }

    @Override
    protected String getDatabaseName() {
        return FORTSCALE_TEST_DB;
    }

    @Override
    public Mongo mongo() throws Exception {
        Fongo fongo = new Fongo(FORTSCALE_TEST_DB);

        return fongo.getMongo();
    }

}
