package fortscale.utils.test.mongodb;

import com.github.fakemongo.Fongo;
import com.mongodb.MongoClient;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Import;
import org.springframework.data.mongodb.config.AbstractMongoConfiguration;

/**
 * Created by YaronDL on 7/5/2017.
 */

@Configuration
@Import(MongodbTestPropertiesConfig.class)
public class FongoTestConfig extends AbstractMongoConfiguration {

    @Value("${mongo.db.name}")
    protected String mongoDBName;

    @Override
    protected String getDatabaseName() {
        return mongoDBName;
    }

    
    @Override
    public MongoClient mongoClient() {
        Fongo fongo = new Fongo(getDatabaseName());

        return fongo.getMongo();
    }
}
