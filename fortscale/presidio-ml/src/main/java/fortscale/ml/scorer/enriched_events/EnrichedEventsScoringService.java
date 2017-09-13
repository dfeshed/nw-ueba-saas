package fortscale.ml.scorer.enriched_events;

import presidio.ade.domain.record.enriched.EnrichedRecord;
import presidio.ade.domain.record.enriched.AdeScoredEnrichedRecord;

import java.util.List;

/**
 * Created by YaronDL on 6/14/2017.
 */
public interface EnrichedEventsScoringService {

    List<AdeScoredEnrichedRecord> scoreAndStoreEvents(List<EnrichedRecord> enrichedRecordList, boolean isStore);
}
