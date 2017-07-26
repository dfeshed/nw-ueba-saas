package presidio.ade.domain.record.scored.feature_aggregation_scored;

import org.springframework.data.mongodb.core.mapping.Field;
import presidio.ade.domain.record.aggregated.AdeAggregationRecord;
import presidio.ade.domain.record.aggregated.AggregatedFeatureType;

import java.time.Instant;
import java.util.Map;

/**
 * Created by mariad on 7/11/2017.
 */
public class ScoredFeatureAggregatedRecord extends AdeAggregationRecord {

    public static final String SCORE = "score";

    @Field(SCORE)
    private Double score;

    public ScoredFeatureAggregatedRecord(Double score, Instant startInstant, Instant endInstant, String aggregatedFeatureName, Double aggregatedFeatureValue, String bucketConfName, Map<String, String> context, AggregatedFeatureType aggregatedFeatureType){
        super(startInstant, endInstant, aggregatedFeatureName, aggregatedFeatureValue, bucketConfName, context, aggregatedFeatureType);
        this.score = score;
    }

    public Double getScore() {
        return score;
    }

    public void setScore(Double score) {
        this.score = score;
    }
}
