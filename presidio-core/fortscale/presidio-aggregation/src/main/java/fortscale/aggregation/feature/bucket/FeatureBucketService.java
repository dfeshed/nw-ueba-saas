package fortscale.aggregation.feature.bucket;

import fortscale.aggregation.feature.bucket.strategy.FeatureBucketStrategyData;
import presidio.ade.domain.record.AdeRecord;

import java.util.List;

/**
 * @author Barak Schuster
 * @author Lior Govrin
 */
public interface FeatureBucketService<T extends AdeRecord> {
    /**
     * Update the feature buckets according to the given ADE records.
     *
     * @param adeRecords                 The ADE records.
     * @param contextFieldName           Feature buckets that include this context field name should be updated.
     * @param contextFieldNamesToExclude Feature buckets that include these context field names should not be updated.
     * @param featureBucketStrategyData  The feature bucket strategy data.
     */
    void updateFeatureBuckets(
            List<T> adeRecords,
            String contextFieldName,
            List<String> contextFieldNamesToExclude,
            FeatureBucketStrategyData featureBucketStrategyData);

    /**
     * Close and return the feature buckets that are currently open.
     * Once the feature buckets are closed, they cannot be updated again.
     *
     * @return The closed feature buckets.
     */
    List<FeatureBucket> closeFeatureBuckets();
}
