package fortscale.accumulator;

import com.github.fakemongo.Fongo;
import com.mongodb.Mongo;
import fortscale.utils.mongodb.converter.FSMappingMongoConverter;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Profile;
import org.springframework.data.mongodb.config.AbstractMongoConfiguration;
import org.springframework.data.mongodb.core.convert.DbRefResolver;
import org.springframework.data.mongodb.core.convert.DefaultDbRefResolver;
import org.springframework.data.mongodb.core.convert.MappingMongoConverter;
import org.springframework.data.mongodb.repository.config.EnableMongoRepositories;

/**
 * Created by barak_schuster on 10/10/16.
 */
// TODO: 10/10/16 delete this class at fortscale 3.0
@Configuration
@Profile("test")
public class TestMongoConfig extends AbstractMongoConfiguration
{
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

    @Autowired
    MappingMongoConverter mappingMongoConverter;



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