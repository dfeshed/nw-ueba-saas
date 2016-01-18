package fortscale.ml.scorer;

import fortscale.common.event.EventMessage;

public interface Scorer {
    FeatureScore calculateScore(EventMessage eventMessage, long eventEpochTimeInSec) throws Exception;
}
