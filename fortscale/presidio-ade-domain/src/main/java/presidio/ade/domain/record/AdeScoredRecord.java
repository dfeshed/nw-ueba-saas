package presidio.ade.domain.record;

import fortscale.domain.feature.score.FeatureScore;
import org.springframework.data.annotation.Transient;
import org.springframework.data.mongodb.core.index.Indexed;
import presidio.ade.domain.record.enriched.EnrichedRecord;


import java.time.Instant;
import java.util.List;

/**
 * Created by YaronDL on 6/13/2017.
 */
public interface AdeScoredRecord {

    public String getFeatureName();


    public Double getScore();

    public List<FeatureScore> getFeatureScoreList();
}
