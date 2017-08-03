package presidio.ade.domain.store.accumulator;

import fortscale.utils.logging.Logger;
import fortscale.utils.mongodb.util.MongoDbBulkOpUtil;
import org.springframework.data.mongodb.core.MongoTemplate;
import presidio.ade.domain.record.accumulator.AccumulatedAggregationFeatureRecord;
import presidio.ade.domain.store.AdeDataStoreCleanupParams;
import presidio.ade.domain.store.aggr.AggrRecordsMetadata;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;


public class AccumulatedDataStoreMongoImpl implements AccumulatedDataStore {
    private static final Logger logger = Logger.getLogger(AccumulatedDataStoreMongoImpl.class);

    private final MongoTemplate mongoTemplate;
    private final AccumulatedDataToCollectionNameTranslator translator;
    private final MongoDbBulkOpUtil mongoDbBulkOpUtil;

    public AccumulatedDataStoreMongoImpl(MongoTemplate mongoTemplate, AccumulatedDataToCollectionNameTranslator translator, MongoDbBulkOpUtil mongoDbBulkOpUtil) {
        this.mongoTemplate = mongoTemplate;
        this.translator = translator;
        this.mongoDbBulkOpUtil = mongoDbBulkOpUtil;
    }

    @Override
    public void store(List<? extends AccumulatedAggregationFeatureRecord> records) {
        Map<String, ? extends List<? extends AccumulatedAggregationFeatureRecord>> featureToAggrList = records.stream().collect(Collectors.groupingBy(AccumulatedAggregationFeatureRecord::getFeatureName));

        featureToAggrList.keySet().forEach(
                feature ->
                {
                    AggrRecordsMetadata metadata = new AggrRecordsMetadata(feature);
                    String collectionName = getCollectionName(metadata);
                    List<? extends AccumulatedAggregationFeatureRecord> aggrRecords = featureToAggrList.get(feature);
                    mongoDbBulkOpUtil.insertUnordered(aggrRecords, collectionName);
                }
        );
    }

    protected String getCollectionName(AggrRecordsMetadata metadata) {
        return translator.toCollectionName(metadata);
    }

    @Override
    public void cleanup(AdeDataStoreCleanupParams cleanupParams) {
        // todo
    }
}
