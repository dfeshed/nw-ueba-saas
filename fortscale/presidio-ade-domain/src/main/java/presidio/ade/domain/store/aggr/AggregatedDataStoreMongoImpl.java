package presidio.ade.domain.store.aggr;

import fortscale.utils.logging.Logger;
import fortscale.utils.mongodb.util.MongoDbBulkOpUtil;
import org.springframework.data.mongodb.core.MongoTemplate;
import presidio.ade.domain.record.aggregated.AdeAggregationRecord;
import presidio.ade.domain.store.AdeDataStoreCleanupParams;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * Created by barak_schuster on 7/10/17.
 */
public class AggregatedDataStoreMongoImpl implements AggrgatedDataStore {
    private static final Logger logger = Logger.getLogger(AggregatedDataStoreMongoImpl.class);

    private final MongoTemplate mongoTemplate;
    private final AggrDataToCollectionNameTranslator translator;
    private final MongoDbBulkOpUtil mongoDbBulkOpUtil;

    public AggregatedDataStoreMongoImpl(MongoTemplate mongoTemplate, AggrDataToCollectionNameTranslator translator, MongoDbBulkOpUtil mongoDbBulkOpUtil) {
        this.mongoTemplate = mongoTemplate;
        this.translator = translator;
        this.mongoDbBulkOpUtil = mongoDbBulkOpUtil;
    }

    @Override
    public void store(List<? extends AdeAggregationRecord> records) {
        Map<String, ? extends List<? extends AdeAggregationRecord>> featureToAggrList = records.stream().collect(Collectors.groupingBy(AdeAggregationRecord::getFeatureName));

        featureToAggrList.keySet().forEach(
                feature ->
                {
                    AggrRecordsMetadata metadata = new AggrRecordsMetadata(feature);
                    String collectionName = getCollectionName(metadata);
                    List<? extends AdeAggregationRecord> aggrRecords = featureToAggrList.get(feature);
                    mongoDbBulkOpUtil.insertUnordered(aggrRecords,collectionName);
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
