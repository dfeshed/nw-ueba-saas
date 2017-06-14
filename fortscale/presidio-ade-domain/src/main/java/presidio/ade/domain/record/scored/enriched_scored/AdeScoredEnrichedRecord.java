package presidio.ade.domain.record.scored.enriched_scored;

import fortscale.domain.feature.score.FeatureScore;
import org.springframework.data.annotation.Transient;
import presidio.ade.domain.record.scored.AdeScoredRecord;

import java.time.Instant;
import java.util.List;

/**
 * Created by YaronDL on 6/14/2017.
 */
public abstract class AdeScoredEnrichedRecord extends AdeScoredRecord {


    public AdeScoredEnrichedRecord(Instant date_time, String featureName, Double score, List<FeatureScore> featureScoreList) {
        super(date_time, featureName, score, featureScoreList);
    }

    @Transient
    public abstract String getDataSource();
}
