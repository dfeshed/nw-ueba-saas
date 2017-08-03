package fortscale.ml.scorer.enriched_events;

import fortscale.domain.feature.score.FeatureScore;
import presidio.ade.domain.record.enriched.EnrichedRecord;
import presidio.ade.domain.record.enriched.AdeScoredEnrichedRecord;

import java.time.Instant;
import java.util.Collections;
import java.util.List;

/**
 * Created by YaronDL on 6/18/2017.
 */
public class AdeScoredEnrichedTestingRecord extends AdeScoredEnrichedRecord {
    private EnrichedRecord enrichedRecord;

    public AdeScoredEnrichedTestingRecord(Instant date_time, String featureName, String featureEventType, Double score, List<FeatureScore> featureScoreList) {
        super(date_time, featureName, featureEventType, score, featureScoreList);
    }

    @Override
    public void fillContext(EnrichedRecord enrichedRecord) {
        this.enrichedRecord = enrichedRecord;
    }

    @Override
    public EnrichedRecord getContext() {
        return enrichedRecord;
    }

    @Override
    public List<String> getDataSources() {
        return Collections.singletonList("testds");
    }
}
