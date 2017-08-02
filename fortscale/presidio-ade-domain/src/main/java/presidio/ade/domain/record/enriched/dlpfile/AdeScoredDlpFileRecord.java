package presidio.ade.domain.record.enriched.dlpfile;

import fortscale.domain.feature.score.FeatureScore;
import org.springframework.data.mongodb.core.mapping.Document;
import presidio.ade.domain.record.enriched.EnrichedRecord;
import presidio.ade.domain.record.enriched.AdeScoredEnrichedRecord;
import presidio.ade.domain.record.util.AdeRecordMetadata;

import java.time.Instant;
import java.util.Collections;
import java.util.List;

/**
 * Created by YaronDL on 6/13/2017.
 */
@Document
@AdeRecordMetadata(adeEventType = AdeDlpFileRecord.DLP_FILE_STR)
public class AdeScoredDlpFileRecord extends AdeScoredEnrichedRecord {

    AdeEnrichedDlpFileContext context;

    public AdeScoredDlpFileRecord(Instant date_time, String featureName, String featureEventType, Double score, List<FeatureScore> featureScoreList){
        super(date_time,featureName, featureEventType,score,featureScoreList);
    }

    public AdeScoredDlpFileRecord(Instant date_time, String featureName, String featureEventType, Double score, List<FeatureScore> featureScoreList, EnrichedDlpFileRecord enrichedDlpFileRecord) {
        super(date_time, featureName, featureEventType, score, featureScoreList);
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
        return Collections.singletonList(AdeDlpFileRecord.DLP_FILE_STR);
    }

    @Override
    public void fillContext(EnrichedRecord enrichedRecord) {
        EnrichedDlpFileRecord enrichedDlpFileRecord = (EnrichedDlpFileRecord) enrichedRecord;
        setContext(enrichedDlpFileRecord.getContext());
    }

}
