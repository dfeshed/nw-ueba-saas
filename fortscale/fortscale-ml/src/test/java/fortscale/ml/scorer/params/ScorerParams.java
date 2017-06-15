package fortscale.ml.scorer.params;

import fortscale.domain.feature.score.FeatureScore;
import fortscale.ml.scorer.Scorer;
import fortscale.ml.scorer.config.IScorerConf;
import presidio.ade.domain.record.AdeRecord;

public interface ScorerParams {
    String getScorerConfJsonString();

    default Scorer getScorer() {
        return new Scorer() {
            @Override
            public FeatureScore calculateScore(AdeRecord record) {
                return null;
            }

            @Override
            public String getName() {
                return "dummy scorer";
            }
        };
    }

    default IScorerConf getScorerConf() {
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
