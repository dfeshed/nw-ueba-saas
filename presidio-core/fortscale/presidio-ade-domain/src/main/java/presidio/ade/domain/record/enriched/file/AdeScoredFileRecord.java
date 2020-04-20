package presidio.ade.domain.record.enriched.file;

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
@AdeScoredEnrichedMetadata(enrichedRecord = EnrichedFileRecord.class)
public class AdeScoredFileRecord extends AdeScoredEnrichedRecord<AdeEnrichedFileContext> {
    public AdeScoredFileRecord() {
        super();
    }

    public AdeScoredFileRecord(Instant startInstant, String featureName, String featureEventType, Double score, List<FeatureScore> featureScoreList, EnrichedRecord enrichedRecord) {
        super(startInstant, featureName, featureEventType, score, featureScoreList, enrichedRecord);
    }

    @Override
    public List<String> getDataSources() {
        return Collections.singletonList(Schema.FILE.getName());
    }

    @Override
    public void fillContext(EnrichedRecord enrichedRecord) {
        EnrichedFileRecord enrichedFileRecord = (EnrichedFileRecord) enrichedRecord;
        setContext(enrichedFileRecord.getContext());
    }
}
