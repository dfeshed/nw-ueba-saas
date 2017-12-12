package fortscale.ml.scorer.feature_aggregation_events;

import fortscale.utils.time.TimeRange;
import presidio.ade.domain.record.aggregated.AdeAggregationRecord;
import presidio.ade.domain.record.aggregated.ScoredFeatureAggregationRecord;

import java.util.List;


public interface FeatureAggregationScoringService {

    List<ScoredFeatureAggregationRecord> scoreEvents(List<AdeAggregationRecord> featureAdeAggrRecords, TimeRange timeRange);

    /**
     * Reset model cache
     */
    void resetModelCache();
}
