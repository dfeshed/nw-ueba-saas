package presidio.ade.domain.record.scored.enriched_scored;

import org.springframework.data.annotation.Transient;
import presidio.ade.domain.record.scored.AdeScoredRecord;

import java.time.Instant;

/**
 * Created by YaronDL on 6/14/2017.
 */
public abstract class AdeScoredEnrichedRecord extends AdeScoredRecord {
    public AdeScoredEnrichedRecord(Instant date_time) {
        super(date_time);
    }

    @Transient
    public abstract String getDataSource();
}
