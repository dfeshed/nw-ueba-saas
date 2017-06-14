package presidio.ade.domain.record.scored.enriched_scored;

import fortscale.domain.feature.score.FeatureScore;
import org.springframework.data.annotation.Transient;
import org.springframework.data.mongodb.core.mapping.Document;
import presidio.ade.domain.record.enriched.DlpFileRecord;

import java.time.Instant;
import java.util.List;

/**
 * Created by YaronDL on 6/13/2017.
 */
@Document
public class AdeScoredDlpFileRecord extends AdeScoredEnrichedRecord {

    AdeScoredDlpFileContext context;

    public AdeScoredDlpFileRecord(Instant date_time, String featureName, Double score, List<FeatureScore> featureScoreList, AdeScoredDlpFileContext context) {
        super(date_time, featureName, score, featureScoreList);
        this.context = context;
    }


    public AdeScoredDlpFileContext getContext(){
        return context;
    }

    public void setContext(AdeScoredDlpFileContext context) {
        this.context = context;
    }

    @Override
    @Transient
    public String getDataSource() {
        return DlpFileRecord.DLP_FILE_STR;
    }

}
