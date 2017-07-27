package fortscale.ml.scorer.feature_aggregation_events;

import presidio.ade.domain.record.aggregated.AdeAggregationRecord;
import presidio.ade.domain.record.scored.feature_aggregation.ScoredFeatureAggregationRecord;

import java.util.List;


public interface FeatureAggregationScoringService {

    void scoreEvents(List<ScoredFeatureAggregationRecord> scoredFeatureAggregationRecords, List<AdeAggregationRecord> featureAdeAggrRecords);
}
