package presidio.ade.domain.store.scored.feature_aggregation;

import presidio.ade.domain.store.aggr.AggrDataToCollectionNameTranslator;
import presidio.ade.domain.store.aggr.AggrRecordsMetadata;


public class ScoredFeaturedDataToCollectionNameTranslator extends AggrDataToCollectionNameTranslator {
    private static final String SCORED_AGGR_COLLECTION_PREFIX = "scored_aggr_feature_";

    @Override
    public String toCollectionName(AggrRecordsMetadata metadata) {
        return String.format("%s%s", SCORED_AGGR_COLLECTION_PREFIX, metadata.getFeatureName());
    }

}
