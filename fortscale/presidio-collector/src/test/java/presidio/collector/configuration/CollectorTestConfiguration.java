package presidio.collector.configuration;

import com.github.fakemongo.Fongo;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Import;
import org.springframework.context.support.PropertySourcesPlaceholderConfigurer;
import org.springframework.data.mongodb.MongoDbFactory;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.SimpleMongoDbFactory;
import presidio.collector.spring.CollectorConfig;

import java.util.Properties;

/**
 * Created by shays on 26/06/2017.
 */
@Configuration
@Import(CollectorConfig.class)
public class CollectorTestConfiguration {


    private static final String FORTSCALE_TEST_DB = "fortscaleTestDb";


    private MongoDbFactory mongoDbFactory() {
        Fongo fongo = new Fongo(FORTSCALE_TEST_DB);

        return new SimpleMongoDbFactory(fongo.getMongo(), FORTSCALE_TEST_DB);
    }


    @Bean
    public MongoTemplate mongoTemplate() {
        return new MongoTemplate(mongoDbFactory());
    }

    @Bean
    public PropertySourcesPlaceholderConfigurer properties() throws Exception {
        final PropertySourcesPlaceholderConfigurer pspc = new PropertySourcesPlaceholderConfigurer();

        Properties properties = new Properties();
        properties.setProperty("fortscale.collector.csvfetcher.csvfilesfolderpath", "file_path");
        pspc.setProperties(properties);
        return pspc;
    }


}
