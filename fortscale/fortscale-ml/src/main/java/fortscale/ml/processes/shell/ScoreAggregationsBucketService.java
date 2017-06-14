package fortscale.ml.processes.shell;

import presidio.ade.domain.record.scored.enriched_scored.AdeScoredEnrichedRecord;

import java.util.List;

/**
 * Created by barak_schuster on 6/12/17.
 */
public interface ScoreAggregationsBucketService {
    public void updateBuckets(List<AdeScoredEnrichedRecord> adeScoredRecords);

    // todo : change to bucket entity
    List<Object> closeBuckets();
}
