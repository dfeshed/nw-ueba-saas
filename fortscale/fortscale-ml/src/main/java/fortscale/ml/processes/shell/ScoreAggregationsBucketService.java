package fortscale.ml.processes.shell;

import presidio.ade.domain.record.scored.AdeScoredRecord;

import java.util.List;

/**
 * Created by barak_schuster on 6/12/17.
 */
public interface ScoreAggregationsBucketService {
    public void updateBuckets(List<AdeScoredRecord> adeScoredRecords);

    void closeBuckets();
}
