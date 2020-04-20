package com.rsa.netwitness.presidio.automation.domain.config.store;

import com.rsa.netwitness.presidio.automation.domain.config.MongoConfig;
import com.rsa.netwitness.presidio.automation.domain.store.CollectionNameTranslator;
import com.rsa.netwitness.presidio.automation.domain.store.NetwitnessEventStore;
import fortscale.utils.mongodb.util.MongoDbBulkOpUtil;
import fortscale.utils.mongodb.util.MongoDbBulkOpUtilConfig;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Import;
import org.springframework.data.mongodb.core.MongoTemplate;

@Configuration
@Import({MongoConfig.class, MongoDbBulkOpUtilConfig.class})
public class NetwitnessEventStoreConfig {

    @Autowired
    private MongoTemplate mongoTemplate;

    @Autowired
    private CollectionNameTranslator collectionNameTranslator;
    @Autowired
    public MongoDbBulkOpUtil mongoDbBulkOpUtil;


    @Bean
    public CollectionNameTranslator collectionNameTranslator() {
        return new CollectionNameTranslator();
    }

    @Bean
    public NetwitnessEventStore netwitnessEventStore() {
        return new NetwitnessEventStore(mongoTemplate, collectionNameTranslator, mongoDbBulkOpUtil);
    }


}
