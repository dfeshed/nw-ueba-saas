package presidio.ade.domain.record.aggregated;

import fortscale.domain.feature.score.FeatureScore;
import org.springframework.data.mongodb.core.index.CompoundIndex;
import org.springframework.data.mongodb.core.index.CompoundIndexes;
import org.springframework.data.mongodb.core.mapping.Document;
import org.springframework.data.mongodb.core.mapping.Field;
import presidio.ade.domain.record.AdeScoredRecord;

import java.time.Instant;
import java.util.List;
import java.util.Map;

/**
 * Created by mariad on 7/11/2017.
 */
@Document
@CompoundIndexes({
        @CompoundIndex(name = "score_threshold_query", def = "{'contextId': -1, 'startInstant': -1, 'score': -1}")
})
public class ScoredFeatureAggregationRecord extends AdeAggregationRecord implements AdeScoredRecord {

    public static final String SCORE_FIELD_NAME = "score";

    @Field(SCORE_FIELD_NAME)
    private Double score;
    private List<FeatureScore> featureScoreList;

    public ScoredFeatureAggregationRecord() {
        super();
    }

    public ScoredFeatureAggregationRecord(Double score, List<FeatureScore> featureScoreList, Instant startInstant, Instant endInstant, String aggregatedFeatureName, Double aggregatedFeatureValue, String bucketConfName, Map<String, String> context, AggregatedFeatureType aggregatedFeatureType) {
        super(startInstant, endInstant, aggregatedFeatureName, aggregatedFeatureValue, bucketConfName, context, aggregatedFeatureType);
        this.score = score;
        this.featureScoreList = featureScoreList;
    }

    @Override
    public Double getScore() {
        return score;
    }

    @Override
    public List<FeatureScore> getFeatureScoreList() {
        return featureScoreList;
    }

    public void setScore(Double score) {
        this.score = score;
    }

    public void setFeatureScoreList(List<FeatureScore> featureScoreList) {
        this.featureScoreList = featureScoreList;
    }
}
