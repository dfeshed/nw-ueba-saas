package fortscale.ml.processes.shell;

import presidio.ade.domain.record.scored.AdeScoredRecord;

import java.util.List;

/**
 * Created by barak_schuster on 6/14/17.
 */
public class ScoreAggregationsBucketServiceImpl implements ScoreAggregationsBucketService{
    @Override
    public void updateBuckets(List<AdeScoredRecord> adeScoredRecords) {

    }

    @Override
    public List<Object> closeBuckets() {
        return null;
    }
}
