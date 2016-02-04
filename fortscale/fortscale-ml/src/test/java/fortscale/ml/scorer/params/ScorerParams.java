package fortscale.ml.scorer.params;


import fortscale.common.event.Event;
import fortscale.ml.scorer.FeatureScore;
import fortscale.ml.scorer.Scorer;
import fortscale.ml.scorer.config.IScorerConf;

public interface ScorerParams {
    String getScorerConfJsonString();

    default Scorer getScorer() {
        return new Scorer() {
            @Override
            public FeatureScore calculateScore(Event eventMessage, long eventEpochTimeInSec) throws Exception {
                return null;
            }

            @Override
            public String getName() {
                return "dummy scorer";
            }
        };
    }

    default public IScorerConf getScorerConf() {
        return new IScorerConf() {
            @Override
            public String getName() {
                return null;
            }

            @Override
            public String getFactoryName() {
                return null;
            }
        };
    }
}
