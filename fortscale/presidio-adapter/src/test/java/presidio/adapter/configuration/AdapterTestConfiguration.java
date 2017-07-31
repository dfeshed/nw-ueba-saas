package presidio.adapter.configuration;

import com.github.fakemongo.Fongo;
import fortscale.common.shell.config.ShellableApplicationConfig;
import fortscale.utils.spring.TestPropertiesPlaceholderConfigurer;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Import;
import org.springframework.data.mongodb.MongoDbFactory;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.SimpleMongoDbFactory;
import presidio.adapter.spring.AdapterConfig;

import java.util.Properties;

/**
 * Created by shays on 26/06/2017.
 */
@Configuration
@Import({AdapterConfig.class, ShellableApplicationConfig.class})
public class AdapterTestConfiguration {


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
    public TestPropertiesPlaceholderConfigurer properties() throws Exception {

        Properties properties = new Properties();
        properties.put("fortscale.adapter.csvfetcher.csvfilesfolderpath", "file_path");

        return new TestPropertiesPlaceholderConfigurer(properties);
    }


}
