package fortscale.acumulator.aggregation.store;

import fortscale.acumulator.AccumulatedFeatureTranslator;
import fortscale.acumulator.aggregation.AccumulatedAggregatedFeatureEvent;
import org.springframework.data.mongodb.core.MongoTemplate;

import java.util.Collection;

/**
 * Created by barak_schuster on 10/6/16.
 */
public class AccumulatedAggregatedFeatureEventStoreImpl implements AccumulatedAggregatedFeatureEventStore {
    private final MongoTemplate mongoTemplate;
    private final AccumulatedFeatureTranslator translator;

    public AccumulatedAggregatedFeatureEventStoreImpl(MongoTemplate mongoTemplate, AccumulatedFeatureTranslator translator)
    {
        this.mongoTemplate = mongoTemplate;
        this.translator = translator;
    }

    @Override
    public void insert(Collection<AccumulatedAggregatedFeatureEvent> events, String featureName) {

        // TODO: 10/6/16 insert logging
        String collectionName = translator.toCollection(featureName);
        // TODO: 10/6/16 replace in bulk operation
        mongoTemplate.insert(events,collectionName);
    }
}
