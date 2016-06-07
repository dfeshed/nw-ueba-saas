package fortscale.monitoring.external.stats.mongo.collector;

import com.github.fakemongo.Fongo;
import com.mongodb.MongoClient;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.mongodb.MongoDbFactory;
import org.springframework.data.mongodb.config.AbstractMongoConfiguration;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.SimpleMongoDbFactory;

@Configuration
public class FakeMongoConfig extends AbstractMongoConfiguration {

    @Override
    protected String getDatabaseName() {
        return "fortscale";
    }

    @Override
    public MongoClient mongo() throws Exception {
        return new Fongo(getDatabaseName()).getMongo();
    }

    public @Bean
    MongoDbFactory mongoDbFactory() throws Exception {
        return new SimpleMongoDbFactory(mongo(),getDatabaseName());
    }

    public @Bean
    MongoTemplate mongoTemplate() throws Exception {
        return new MongoTemplate(mongoDbFactory());
    }

}
