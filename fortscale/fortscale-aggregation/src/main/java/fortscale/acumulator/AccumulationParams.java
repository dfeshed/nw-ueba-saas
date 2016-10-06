package fortscale.acumulator;

import java.time.Instant;

/**
 * params the {@link Accumulator} should execute by
 * Created by barak_schuster on 10/6/16.
 */
public class AccumulationParams {

    enum timeFrame
    {
        DAILY
    }
    // source data should be accumulated from that date
    Instant from;
    // source data should be accumulated till that date
    Instant to;
    // source collection name to be accumulated
    String sourceCollectionName;
    // destination containing the accumulated documents
    String destinationCollectionName;
}
