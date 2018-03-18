package fortscale.utils.mongodb.config;

import com.mongodb.*;
import fortscale.utils.EncryptionUtils;
import fortscale.utils.mongodb.converter.FSMappingMongoConverter;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.FilterType;
import org.springframework.core.convert.converter.Converter;
import org.springframework.data.mongodb.config.AbstractMongoConfiguration;
import org.springframework.data.mongodb.core.convert.CustomConversions;
import org.springframework.data.mongodb.core.convert.DbRefResolver;
import org.springframework.data.mongodb.core.convert.DefaultDbRefResolver;
import org.springframework.data.mongodb.core.convert.MappingMongoConverter;
import org.springframework.data.mongodb.repository.config.EnableMongoRepositories;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by rans on 27/10/15.
 */
@Configuration
@EnableMongoRepositories(basePackages = "fortscale")
// scan converters defined at fortscale domain
@ComponentScan(basePackages = "fortscale.domain",includeFilters = @ComponentScan.Filter(type = FilterType.ASSIGNABLE_TYPE, value = Converter.class),excludeFilters = @ComponentScan.Filter(type = FilterType.REGEX,pattern = ".*(?<!Converter)$"))
public class SpringMongoConfiguration extends AbstractMongoConfiguration {

    @Autowired
    private List<Converter> converters;

    @Value("${mongo.host.name}")
    private String mongoHostName;

    @Value("${mongo.host.port}")
    private int mongoHostPort;

    @Value("${mongo.db.name}")
    private String mongoDBName;

    @Value("${mongo.db.user}")
    private String mongoUserName;

    @Value("${mongo.db.password}")
    private String mongoPassword;

    @Value("${mongo.map.dot.replacement}")
    private String mapKeyDotReplacemant;

    @Value("${mongo.map.dollar.replacement}")
    private String mapKeyDollarReplacemant;

    @Bean
    @Override
    public MappingMongoConverter mappingMongoConverter() throws Exception {

        DbRefResolver dbRefResolver = new DefaultDbRefResolver(mongoDbFactory());
        FSMappingMongoConverter converter = new FSMappingMongoConverter(dbRefResolver, mongoMappingContext());
        converter.setMapKeyDotReplacement(mapKeyDotReplacemant);
        converter.setMapKeyDollarReplacement(mapKeyDollarReplacemant);
        converter.setCustomConversions(customConversions());

        return converter;
    }

    @Override
    protected String getDatabaseName() {
        return mongoDBName;
    }

    @Override
    @Bean
    public Mongo mongo() throws Exception {
        MongoClient client;
        if (StringUtils.isNotBlank(mongoUserName) && StringUtils.isNotBlank(mongoPassword)) {
            ServerAddress address = new ServerAddress(mongoHostName, mongoHostPort);
            List<MongoCredential> credentials = new ArrayList<>();
            credentials.add(
                    MongoCredential.createCredential(
                            mongoUserName,
                            mongoDBName,
                            EncryptionUtils.decrypt(mongoPassword).toCharArray()
                    )
            );

            client = new MongoClient(address, credentials);
        } else {
            client = new MongoClient(mongoHostName,mongoHostPort);
        }

        client.setWriteConcern(WriteConcern.SAFE);
        return client;
    }

    @Override
    protected String getMappingBasePackage() {
        return "fortscale";
    }

    @Bean
    @Override
    public CustomConversions customConversions() {
        return new CustomConversions(converters);
    }
}
