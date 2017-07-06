package fortscale.ml.processes.shell.scoring.aggregation;

import java.util.List;

/**
 * Created by barak_schuster on 6/12/17.
 */
public interface ScoreAggregationsCreator {
    void createScoreAggregations(List<Object> closedBuckets);
}
