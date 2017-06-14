package presidio.ade.domain.record.scored.raw_scored;

import org.springframework.data.mongodb.core.mapping.Document;
import presidio.ade.domain.record.scored.AdeScoredRecord;

import java.time.Instant;

/**
 * Created by YaronDL on 6/13/2017.
 */
@Document
public class AdeScoredDlpFileRecord extends AdeScoredRecord {

    AdeScoredDlpFileContext context;

    public AdeScoredDlpFileRecord(Instant date_time, AdeScoredDlpFileContext context) {
        super(date_time);
        this.context = context;
    }

    public AdeScoredDlpFileContext getContext(){
        return context;
    }

    public void setContext(AdeScoredDlpFileContext context) {
        this.context = context;
    }

}
