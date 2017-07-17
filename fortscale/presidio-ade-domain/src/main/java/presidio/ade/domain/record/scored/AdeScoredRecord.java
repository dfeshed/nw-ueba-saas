package presidio.ade.domain.record.scored;

import fortscale.domain.feature.score.FeatureScore;
import org.springframework.data.annotation.Transient;
import org.springframework.data.mongodb.core.index.Indexed;
import presidio.ade.domain.record.enriched.EnrichedRecord;


import java.time.Instant;
import java.util.List;

/**
 * Created by YaronDL on 6/13/2017.
 */
public abstract class AdeScoredRecord extends EnrichedRecord {



    @Indexed
    private String featureName;
    private String featureEventType;
    private Double score;
    List<FeatureScore> featureScoreList;



    public AdeScoredRecord(Instant date_time, String featureName ,String featureEventType, Double score, List<FeatureScore> featureScoreList) {
        super(date_time);
        this.featureName = featureName;
        this.featureEventType = featureEventType;
        this.score = score;
        this.featureScoreList = featureScoreList;
    }

    public abstract <U> U getContext();

    public String getFeatureName() {
        return featureName;
    }

    public void setFeatureName(String featureName) {
        this.featureName = featureName;
    }

    public String getFeatureEventType() {
        return featureEventType;
    }

    public void setFeatureEventType(String featureEventType) {
        this.featureEventType = featureEventType;
    }

    public Double getScore() {
        return score;
    }

    public void setScore(Double score) {
        this.score = score;
    }

    public List<FeatureScore> getFeatureScoreList() {
        return featureScoreList;
    }

    public void setFeatureScoreList(List<FeatureScore> featureScoreList) {
        this.featureScoreList = featureScoreList;
    }
}
