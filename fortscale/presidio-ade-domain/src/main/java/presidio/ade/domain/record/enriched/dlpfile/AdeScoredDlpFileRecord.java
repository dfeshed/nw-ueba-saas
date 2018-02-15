package presidio.ade.domain.record.enriched.dlpfile;

import fortscale.common.general.Schema;
import fortscale.domain.feature.score.FeatureScore;
import org.springframework.data.mongodb.core.mapping.Document;
import presidio.ade.domain.record.enriched.AdeScoredEnrichedRecord;
import presidio.ade.domain.record.enriched.EnrichedRecord;
import presidio.ade.domain.record.util.AdeScoredEnrichedMetadata;

import java.time.Instant;
import java.util.Collections;
import java.util.List;

/**
 * @author Yaron DL
 */
@Document
@AdeScoredEnrichedMetadata(enrichedRecord = EnrichedDlpFileRecord.class)
public class AdeScoredDlpFileRecord extends AdeScoredEnrichedRecord<AdeEnrichedDlpFileContext> {
    public AdeScoredDlpFileRecord(Instant date_time, String featureName, String featureEventType, Double score, List<FeatureScore> featureScoreList, EnrichedRecord enrichedRecord) {
        super(date_time, featureName, featureEventType, score, featureScoreList, enrichedRecord);
    }

    @Override
    public List<String> getDataSources() {
        return Collections.singletonList(Schema.DLPFILE.getName());
    }

    @Override
    public void fillContext(EnrichedRecord enrichedRecord) {
        EnrichedDlpFileRecord enrichedDlpFileRecord = (EnrichedDlpFileRecord) enrichedRecord;
        setContext(enrichedDlpFileRecord.getContext());
    }
}
