package fortscale.smart;

import presidio.ade.domain.record.aggregated.AdeAggregationRecord;
import presidio.ade.domain.record.aggregated.AggregatedFeatureType;
import presidio.ade.domain.record.aggregated.ScoredFeatureAggregationRecord;

public class SmartUtil {

    public static double getAdeAggregationRecordScore(AdeAggregationRecord adeAggregationRecord){
        double score = 0;
        AggregatedFeatureType aggregatedFeatureType = adeAggregationRecord.getAggregatedFeatureType();
        if (aggregatedFeatureType.equals(AggregatedFeatureType.SCORE_AGGREGATION)) {
            score = adeAggregationRecord.getFeatureValue();
        } else if (aggregatedFeatureType.equals(AggregatedFeatureType.FEATURE_AGGREGATION)) {
            score = ((ScoredFeatureAggregationRecord) adeAggregationRecord).getScore();
        }

        return score;
    }
}
