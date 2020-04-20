package presidio.ade.domain.record.enriched.process;

import fortscale.common.general.Schema;
import fortscale.domain.feature.score.FeatureScore;
import org.springframework.data.mongodb.core.mapping.Document;
import presidio.ade.domain.record.enriched.AdeScoredEnrichedRecord;
import presidio.ade.domain.record.enriched.EnrichedRecord;
import presidio.ade.domain.record.util.AdeScoredEnrichedMetadata;

import java.time.Instant;
import java.util.Collections;
import java.util.List;

@Document
@AdeScoredEnrichedMetadata(enrichedRecord = EnrichedProcessRecord.class)
public class AdeScoredProcessRecord extends AdeScoredEnrichedRecord<AdeEnrichedProcessContext> {
    public AdeScoredProcessRecord() {
        super();
    }

    public AdeScoredProcessRecord(Instant startInstant, String featureName, String featureEventType, Double score, List<FeatureScore> featureScoreList, EnrichedRecord enrichedRecord) {
        super(startInstant, featureName, featureEventType, score, featureScoreList, enrichedRecord);
    }

    @Override
    public List<String> getDataSources() {
        return Collections.singletonList(Schema.PROCESS.getName());
    }

    @Override
    public void fillContext(EnrichedRecord enrichedRecord) {
        EnrichedProcessRecord enrichedProcessRecord = (EnrichedProcessRecord) enrichedRecord;
        setContext(enrichedProcessRecord.getContext());
    }
}
