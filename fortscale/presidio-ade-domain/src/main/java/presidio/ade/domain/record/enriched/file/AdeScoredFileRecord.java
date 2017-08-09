package presidio.ade.domain.record.enriched.file;

import fortscale.domain.feature.score.FeatureScore;
import org.springframework.data.mongodb.core.mapping.Document;
import presidio.ade.domain.record.enriched.AdeScoredEnrichedRecord;
import presidio.ade.domain.record.enriched.EnrichedRecord;
import presidio.ade.domain.record.enriched.dlpfile.AdeDlpFileRecord;
import presidio.ade.domain.record.enriched.dlpfile.EnrichedDlpFileRecord;
import presidio.ade.domain.record.util.AdeScoredEnrichedMetadata;

import java.time.Instant;
import java.util.Collections;
import java.util.List;

@Document
@AdeScoredEnrichedMetadata(erichedRecord = EnrichedFileRecord.class)
public class AdeScoredFileRecord  extends AdeScoredEnrichedRecord<AdeEnrichedFileContext> {

    public AdeScoredFileRecord(Instant date_time, String featureName, String featureEventType, Double score, List<FeatureScore> featureScoreList, EnrichedRecord enrichedRecord) {
        super(date_time, featureName, featureEventType, score, featureScoreList, enrichedRecord);
    }

    @Override
    public List<String> getDataSources() {
        return Collections.singletonList(AdeFileRecord.FILE_STR);
    }


    @Override
    public void fillContext(EnrichedRecord enrichedRecord) {
        EnrichedFileRecord enrichedFileRecord = (EnrichedFileRecord) enrichedRecord;
        setContext(enrichedFileRecord.getContext());
    }
}
