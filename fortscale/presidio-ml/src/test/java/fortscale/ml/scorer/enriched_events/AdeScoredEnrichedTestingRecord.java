package fortscale.ml.scorer.enriched_events;

import fortscale.domain.feature.score.FeatureScore;
import org.springframework.data.mongodb.core.mapping.Document;
import presidio.ade.domain.record.enriched.AdeScoredEnrichedRecord;
import presidio.ade.domain.record.enriched.EnrichedRecord;

import java.time.Instant;
import java.util.Collections;
import java.util.List;

/**
 * @author Yaron DL
 */
@Document
public class AdeScoredEnrichedTestingRecord extends AdeScoredEnrichedRecord<AdeScoredEnrichedTestingRecordContext> {
    public AdeScoredEnrichedTestingRecord(Instant date_time, String featureName, String featureEventType, Double score, List<FeatureScore> featureScoreList, EnrichedRecord enrichedRecord) {
        super(date_time, featureName, featureEventType, score, featureScoreList, enrichedRecord);
    }

    @Override
    public void fillContext(EnrichedRecord enrichedRecord) {
        setContext(new AdeScoredEnrichedTestingRecordContext(enrichedRecord.getEventId()));
    }

    @Override
    public List<String> getDataSources() {
        return Collections.singletonList("testds");
    }
}
