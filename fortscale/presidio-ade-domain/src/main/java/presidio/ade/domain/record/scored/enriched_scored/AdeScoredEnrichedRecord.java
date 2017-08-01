package presidio.ade.domain.record.scored.enriched_scored;

import fortscale.domain.feature.score.FeatureScore;
import org.springframework.data.annotation.Transient;
import org.springframework.data.mongodb.core.index.Indexed;
import presidio.ade.domain.record.enriched.EnrichedRecord;
import presidio.ade.domain.record.scored.AdeScoredRecord;

import java.time.Instant;
import java.util.List;

/**
 * Created by YaronDL on 6/14/2017.
 */
public abstract class AdeScoredEnrichedRecord extends EnrichedRecord implements AdeScoredRecord{
    private static final String EVENT_TYPE_PREFIX = "scored_enriched";

    @Indexed
    private String featureName;
    private String featureEventType;
    private Double score;
    private List<FeatureScore> featureScoreList;

    public AdeScoredEnrichedRecord(Instant date_time, String featureName, String featureEventType, Double score, List<FeatureScore> featureScoreList) {
        super(date_time);
        this.featureName = featureName;
        this.featureEventType = featureEventType;
        this.score = score;
        this.featureScoreList = featureScoreList;
    }

    @Override
    public String getAdeEventType(){
        return EVENT_TYPE_PREFIX + "." + getFeatureEventType() + "." + getFeatureName();
    }

    public abstract void fillContext(EnrichedRecord enrichedRecord);

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
