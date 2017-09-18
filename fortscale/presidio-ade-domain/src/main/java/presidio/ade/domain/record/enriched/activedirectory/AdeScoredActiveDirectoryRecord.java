package presidio.ade.domain.record.enriched.activedirectory;

import fortscale.domain.feature.score.FeatureScore;
import org.springframework.data.mongodb.core.index.CompoundIndex;
import org.springframework.data.mongodb.core.index.CompoundIndexes;
import org.springframework.data.mongodb.core.mapping.Document;
import presidio.ade.domain.record.enriched.AdeScoredEnrichedRecord;
import presidio.ade.domain.record.enriched.EnrichedRecord;
import presidio.ade.domain.record.util.AdeScoredEnrichedMetadata;

import java.time.Instant;
import java.util.Collections;
import java.util.List;

@Document
@AdeScoredEnrichedMetadata(erichedRecord = EnrichedActiveDirectoryRecord.class)
@CompoundIndexes({
        @CompoundIndex(def = "{'startInstant': 1}"),
        @CompoundIndex(def = "{'startInstant': 1, 'score': 1}"),
        @CompoundIndex(def = "{'context.eventId': 1, 'score': 1}")
})
public class AdeScoredActiveDirectoryRecord extends AdeScoredEnrichedRecord<AdeEnrichedActiveDirectoryContext> {

    public AdeScoredActiveDirectoryRecord() {
        super();
    }

    public AdeScoredActiveDirectoryRecord(Instant date_time, String featureName, String featureEventType, Double score, List<FeatureScore> featureScoreList, EnrichedRecord enrichedRecord) {
        super(date_time, featureName, featureEventType, score, featureScoreList, enrichedRecord);
    }

    @Override
    public List<String> getDataSources() {
        return Collections.singletonList(AdeActiveDirectoryRecord.ACTIVE_DIRECTORY_STR);
    }


    @Override
    public void fillContext(EnrichedRecord enrichedRecord) {
        EnrichedActiveDirectoryRecord enrichedFileRecord = (EnrichedActiveDirectoryRecord) enrichedRecord;
        setContext(enrichedFileRecord.getContext());
    }
}
