package fortscale.accumulator;

import com.github.fakemongo.Fongo;
import com.mongodb.Mongo;
import fortscale.domain.MongoConverterConfigurer;
import fortscale.utils.mongodb.converter.FSMappingMongoConverter;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Profile;
import org.springframework.data.mongodb.config.AbstractMongoConfiguration;
import org.springframework.data.mongodb.core.convert.DbRefResolver;
import org.springframework.data.mongodb.core.convert.DefaultDbRefResolver;
import org.springframework.data.mongodb.core.convert.MappingMongoConverter;

/**
 * Created by barak_schuster on 10/10/16.
 */
// TODO: 10/10/16 delete this class at fortscale 3.0
@Configuration
@Profile("test")
public class TestMongoConfig extends AbstractMongoConfiguration
{
    @Bean
    @Override
    public MappingMongoConverter mappingMongoConverter() throws Exception {

        DbRefResolver dbRefResolver = new DefaultDbRefResolver(mongoDbFactory());
        MappingMongoConverter converter = new FSMappingMongoConverter(dbRefResolver, mongoMappingContext());
        converter.setCustomConversions(customConversions());

        return converter;
    }

    @Autowired
    MappingMongoConverter mappingMongoConverter;

    private static final String FORTSCALE_TEST_DB = "fortscaleTestDb";

    @Bean
    public MongoConverterConfigurer mongoConverterConfigurer()
    {
        MongoConverterConfigurer converter = new MongoConverterConfigurer();
        converter.setMapKeyDotReplacement("#dot#");
        converter.setMapKeyDollarReplacement("#dol#");
        converter.setMongoConverter((FSMappingMongoConverter)mappingMongoConverter);
        converter.init();
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