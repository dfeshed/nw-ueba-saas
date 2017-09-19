package presidio.ade.domain.record.enriched.dlpfile;

import fortscale.domain.feature.score.FeatureScore;
import org.springframework.data.mongodb.core.index.CompoundIndex;
import org.springframework.data.mongodb.core.index.CompoundIndexes;
import org.springframework.data.mongodb.core.mapping.Document;
import presidio.ade.domain.record.enriched.EnrichedRecord;
import presidio.ade.domain.record.enriched.AdeScoredEnrichedRecord;
import presidio.ade.domain.record.util.AdeScoredEnrichedMetadata;

import java.time.Instant;
import java.util.Collections;
import java.util.List;

/**
 * @author Yaron DL
 */
@Document
@AdeScoredEnrichedMetadata(erichedRecord = EnrichedDlpFileRecord.class)
@CompoundIndexes({
        @CompoundIndex(name = "start", def = "{'startInstant': 1}"),
        @CompoundIndex(name = "scrStart", def = "{'score': 1, 'startInstant': 1}"),
        @CompoundIndex(name = "idScr", def = "{'context.eventId': 1, 'score': 1}")
})
public class AdeScoredDlpFileRecord extends AdeScoredEnrichedRecord<AdeEnrichedDlpFileContext> {

    public AdeScoredDlpFileRecord(Instant date_time, String featureName, String featureEventType, Double score, List<FeatureScore> featureScoreList, EnrichedRecord enrichedRecord){
        super(date_time, featureName, featureEventType,score, featureScoreList, enrichedRecord);
    }

    @Override
    public List<String> getDataSources() {
        return Collections.singletonList(AdeDlpFileRecord.DLP_FILE_STR);
    }

    @Override
    public void fillContext(EnrichedRecord enrichedRecord) {
        EnrichedDlpFileRecord enrichedDlpFileRecord = (EnrichedDlpFileRecord) enrichedRecord;
        setContext(enrichedDlpFileRecord.getContext());
    }
}
