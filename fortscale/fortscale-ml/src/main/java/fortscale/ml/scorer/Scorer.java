package fortscale.ml.scorer;

import fortscale.common.event.Event;

public interface Scorer {
    FeatureScore calculateScore(Event eventMessage, long eventEpochTimeInSec) throws Exception;
    String getName();
}
