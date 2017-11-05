package fortscale.ml.scorer.feature_aggregation_events;

import presidio.ade.domain.record.aggregated.AdeAggregationRecord;
import presidio.ade.domain.record.aggregated.ScoredFeatureAggregationRecord;

import java.util.List;


public interface FeatureAggregationScoringService {

    List<ScoredFeatureAggregationRecord> scoreEvents(List<AdeAggregationRecord> featureAdeAggrRecords);

    /**
     * Reset model cache
     */
    void resetModelCache();
}
