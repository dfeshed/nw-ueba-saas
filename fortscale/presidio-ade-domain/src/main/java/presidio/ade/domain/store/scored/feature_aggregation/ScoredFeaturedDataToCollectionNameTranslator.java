package presidio.ade.domain.store.scored.feature_aggregation;

import presidio.ade.domain.store.aggr.AggrDataToCollectionNameTranslator;
import presidio.ade.domain.store.aggr.AggrRecordsMetadata;


public class ScoredFeaturedDataToCollectionNameTranslator extends AggrDataToCollectionNameTranslator {
    private static final String SCORED_FEATURE_AGGR_COLLECTION_PREFIX = "scored_feature_aggr_";

    @Override
    public String toCollectionName(AggrRecordsMetadata metadata) {
        return String.format("%s%s", SCORED_FEATURE_AGGR_COLLECTION_PREFIX, metadata.getFeatureName());
    }

}
