package com.rsa.netwitness.presidio.automation.domain.store;

import fortscale.common.general.Schema;
import fortscale.utils.mongodb.util.MongoDbBulkOpUtil;
import org.bson.Document;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.index.CompoundIndexDefinition;
import presidio.nw.flume.domain.test.NetwitnessStoredData;

import java.util.List;

public class NetwitnessEventStore {

    private final MongoDbBulkOpUtil mongoDbBulkOpUtil;
    private final MongoTemplate mongoTemplate;
    private final CollectionNameTranslator collectionNameTranslator;
    private boolean isEnsuredIndex;

    public NetwitnessEventStore(MongoTemplate mongoTemplate,  CollectionNameTranslator collectionNameTranslator, MongoDbBulkOpUtil mongoDbBulkOpUtil){
        this.mongoDbBulkOpUtil = mongoDbBulkOpUtil;
        this.mongoTemplate = mongoTemplate;
        this.collectionNameTranslator = collectionNameTranslator;
        isEnsuredIndex = false;
    }

    /**
     * Save Events
     * @param netwitnessStoredDataList
     * @param schema
     */
    public void store(List<NetwitnessStoredData> netwitnessStoredDataList, Schema schema){
        String collectionName = collectionNameTranslator.toCollectionName(schema);
        if(!isEnsuredIndex){
            Document indexOptions = new Document();
            indexOptions.put("netwitnessEvent.mongo_source_event_time", 1);
            CompoundIndexDefinition indexDefinition = new CompoundIndexDefinition(indexOptions);
            indexDefinition.named("netwitnessEvent.mongo_source_event_time_1");
            mongoTemplate.indexOps(collectionName).ensureIndex(indexDefinition);
            isEnsuredIndex = true;
        }
        mongoDbBulkOpUtil.insertUnordered(netwitnessStoredDataList, collectionName);
    }


}
