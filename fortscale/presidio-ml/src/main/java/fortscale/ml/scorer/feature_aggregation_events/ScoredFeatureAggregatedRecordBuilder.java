package fortscale.ml.scorer.feature_aggregation_events;

import fortscale.domain.feature.score.FeatureScore;
import fortscale.utils.logging.Logger;
import presidio.ade.domain.record.aggregated.AdeAggregationRecord;
import presidio.ade.domain.record.scored.feature_aggregation.ScoredFeatureAggregationRecord;


import java.util.List;


public class ScoredFeatureAggregatedRecordBuilder {
    private static final Logger logger = Logger.getLogger(ScoredFeatureAggregatedRecordBuilder.class);

    public void fill(List<ScoredFeatureAggregationRecord> scoredFeatureAggregationRecords, AdeAggregationRecord featureAdeAggrRecord, List<FeatureScore> featureScoreList) {

        if (featureScoreList.size() == 0) {
            //TODO: add metrics.
            logger.error("after calculating an feature aggregate record we got an empty feature score list while expecting for a list of size 1. the feature aggregate record: {}", featureAdeAggrRecord);
            return;
        }

        //expect to get as a root feature score which hold the event score and inside it all the relevant features.
        if (featureScoreList.size() > 1) {
            //TODO: add metrics.
            logger.error("after calculating an feature aggregate record we got feature score list of size > 1 while expecting to get a root which hold the event score and inside it all the relevant features. the feature aggregate record: {}", featureAdeAggrRecord);
            return;
        }

        FeatureScore eventScore = featureScoreList.get(0);

        if (eventScore.getFeatureScores().size() == 0) {
            //TODO: add metrics.
            logger.error("after calculating an feature aggregate record we got an empty feature score list!!! the feature aggregate record: {}", featureAdeAggrRecord);
            return;
        }

        for (FeatureScore featureScore : eventScore.getFeatureScores()) {
            ScoredFeatureAggregationRecord scoredFeatureAggregationRecord = new ScoredFeatureAggregationRecord(featureScore.getScore(), featureScore.getFeatureScores(),
                    featureAdeAggrRecord.getStartInstant(), featureAdeAggrRecord.getEndInstant(), featureAdeAggrRecord.getFeatureName(), featureAdeAggrRecord.getFeatureValue(), featureAdeAggrRecord.getFeatureBucketConfName(), featureAdeAggrRecord.getContext(), featureAdeAggrRecord.getAggregatedFeatureType());
            scoredFeatureAggregationRecords.add(scoredFeatureAggregationRecord);
        }

    }

}
