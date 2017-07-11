package presidio.ade.domain.record.scored.enriched_scored;

import fortscale.domain.feature.score.FeatureScore;
import org.springframework.data.annotation.Transient;
import org.springframework.data.mongodb.core.mapping.Document;
import presidio.ade.domain.record.enriched.AdeEnrichedDlpFileContext;
import presidio.ade.domain.record.enriched.DlpFileRecord;
import presidio.ade.domain.record.enriched.EnrichedDlpFileRecord;
import presidio.ade.domain.record.enriched.EnrichedRecord;
import presidio.ade.domain.record.util.AdeRecordMetadata;

import java.time.Instant;
import java.util.Collections;
import java.util.List;

/**
 * Created by YaronDL on 6/13/2017.
 */
@Document
@AdeRecordMetadata(adeEventType =DlpFileRecord.DLP_FILE_STR)
public class AdeScoredDlpFileRecord extends AdeScoredEnrichedRecord {

    AdeEnrichedDlpFileContext context;

    public AdeScoredDlpFileRecord(Instant date_time, String featureName, Double score, List<FeatureScore> featureScoreList){
        super(date_time,featureName,score,featureScoreList);
    }

    public AdeScoredDlpFileRecord(Instant date_time, String featureName, Double score, List<FeatureScore> featureScoreList, EnrichedDlpFileRecord enrichedDlpFileRecord) {
        super(date_time, featureName, score, featureScoreList);
        this.context = enrichedDlpFileRecord.getContext();
    }


    public AdeEnrichedDlpFileContext getContext(){
        return context;
    }

    public void setContext(AdeEnrichedDlpFileContext context) {
        this.context = context;
    }

    @Override
    public List<String> getDataSources() {
        return Collections.singletonList(DlpFileRecord.DLP_FILE_STR);
    }

    @Override
    public void fillContext(EnrichedRecord enrichedRecord) {
        EnrichedDlpFileRecord enrichedDlpFileRecord = (EnrichedDlpFileRecord) enrichedRecord;
        setContext(enrichedDlpFileRecord.getContext());
    }

}
