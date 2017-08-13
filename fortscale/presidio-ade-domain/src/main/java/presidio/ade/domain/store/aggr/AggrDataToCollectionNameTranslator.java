package presidio.ade.domain.store.aggr;

import presidio.ade.domain.record.aggregated.AggregatedFeatureType;
import presidio.ade.domain.store.AdeDataStoreCleanupParams;
import presidio.ade.domain.store.AdeToCollectionNameTranslator;

import java.util.Collection;

/**
 * Created by barak_schuster on 7/10/17.
 */
public class AggrDataToCollectionNameTranslator implements AdeToCollectionNameTranslator<AggrRecordsMetadata> {
    private static final String SCORE_AGGR_COLLECTION_PREFIX = "aggr_of_score";
    private static final String SCORED_FEATURE_AGGR_COLLECTION_PREFIX = "scored_feature_aggr_";

    @Override
    public String toCollectionName(AggrRecordsMetadata metadata) {
        String featureName = metadata.getFeatureName();
        AggregatedFeatureType aggregatedFeatureType = metadata.getAggregatedFeatureType();
        switch (aggregatedFeatureType) {
            case SCORE_AGGREGATION:
                return toCollectionName(featureName,SCORE_AGGR_COLLECTION_PREFIX);
            case FEATURE_AGGREGATION:
                return toCollectionName(featureName,SCORED_FEATURE_AGGR_COLLECTION_PREFIX);
            default:
                throw new RuntimeException(String.format("unsupported aggregatedFeatureType=%s",aggregatedFeatureType.toString()));
        }
    }

    private String toCollectionName(String featureName, String aggrFeatureTypePrefix) {
        return String.format("%s_%s", aggrFeatureTypePrefix,featureName);
    }

    @Override
    public Collection<String> toCollectionNames(AdeDataStoreCleanupParams cleanupParams) {
        //todo
        return null;
    }
}
