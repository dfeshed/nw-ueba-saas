package presidio.ade.domain.record.scored;

import fortscale.domain.feature.score.FeatureScore;
import org.springframework.data.mongodb.core.index.Indexed;
import presidio.ade.domain.record.AdeRecord;


import java.time.Instant;
import java.util.List;

/**
 * Created by YaronDL on 6/13/2017.
 */
public abstract class AdeScoredRecord extends AdeRecord{



    @Indexed
    private String featureName;
    private String adeEventType;
    FeatureScore featureScore;



    public AdeScoredRecord(Instant date_time) {
        super(date_time);
    }

    public abstract <U> U getContext();

    public String getFeatureName() {
        return featureName;
    }

    public void setFeatureName(String featureName) {
        this.featureName = featureName;
    }

    public String getAdeEventType() {
        return adeEventType;
    }

    public void setAdeEventType(String adeEventType) {
        this.adeEventType = adeEventType;
    }

    public FeatureScore getFeatureScore() {
        return featureScore;
    }

    public void setFeatureScore(FeatureScore featureScore) {
        this.featureScore = featureScore;
    }
}
