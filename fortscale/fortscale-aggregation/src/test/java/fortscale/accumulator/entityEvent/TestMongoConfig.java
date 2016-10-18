package fortscale.accumulator.entityEvent;

import com.github.fakemongo.Fongo;
import com.mongodb.Mongo;
import fortscale.domain.MongoConverterConfigurer;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Profile;
import org.springframework.data.mongodb.config.AbstractMongoConfiguration;
import org.springframework.data.mongodb.core.convert.MappingMongoConverter;

/**
 * Created by barak_schuster on 10/10/16.
 */
// TODO: 10/10/16 delete this class at fortscale 3.0
@Configuration
@Profile("test")
public class TestMongoConfig extends AbstractMongoConfiguration
{
    @Autowired
    MappingMongoConverter mappingMongoConverter;

    private static final String FORTSCALE_TEST_DB = "fortscaleTestDb";

    @Bean
    public MongoConverterConfigurer mongoConverterConfigurer()
    {
        MongoConverterConfigurer converter = new MongoConverterConfigurer();
        converter.setMapKeyDotReplacement("#dot#");
        converter.setMongoConverter(mappingMongoConverter);
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