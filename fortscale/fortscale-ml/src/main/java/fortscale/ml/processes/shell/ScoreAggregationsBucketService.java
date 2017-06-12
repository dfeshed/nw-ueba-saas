package fortscale.ml.processes.shell;

/**
 * Created by barak_schuster on 6/12/17.
 */
public interface ScoreAggregationsBucketService {
    public void updateBuckets();

    void closeBuckets();
}
