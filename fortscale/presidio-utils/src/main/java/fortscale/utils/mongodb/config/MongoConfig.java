package fortscale.utils.mongodb.config;

import com.google.common.collect.Lists;
import com.mongodb.*;
import fortscale.utils.EncryptionUtils;
import fortscale.utils.mongodb.converter.FSMappingMongoConverter;
import fortscale.utils.mongodb.index.DynamicIndexApplicationListenerConfig;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Import;
import org.springframework.core.convert.converter.Converter;
import org.springframework.data.mongodb.config.AbstractMongoConfiguration;
import org.springframework.data.mongodb.config.EnableMongoAuditing;
import org.springframework.data.mongodb.core.convert.CustomConversions;
import org.springframework.data.mongodb.core.convert.DbRefResolver;
import org.springframework.data.mongodb.core.convert.DefaultDbRefResolver;
import org.springframework.data.mongodb.core.convert.MappingMongoConverter;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

/**
 * Created by rans on 27/10/15.
 */
@Configuration
@EnableMongoAuditing
@Import(DynamicIndexApplicationListenerConfig.class)
public class MongoConfig extends AbstractMongoConfiguration {

    @Value("${mongo.db.name}")
    protected String mongoDBName;
    @Autowired (required = false)
    private List<Converter> converters;
    @Value("${mongo.host.name}")
    private String mongoHostName;
    @Value("${mongo.host.port}")
    private int mongoHostPort;
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
    protected Collection<String> getMappingBasePackages() {
        return Lists.newArrayList("fortscale","presidio");
    }

    @Bean
    @Override
    public CustomConversions customConversions() {
        if(converters==null)
        {
            return super.customConversions();
        }
        return new CustomConversions(converters);
    }
}
