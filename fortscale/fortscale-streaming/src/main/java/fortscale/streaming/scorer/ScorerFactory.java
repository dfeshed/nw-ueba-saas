package fortscale.streaming.scorer;

import org.apache.samza.config.Config;

public interface ScorerFactory {

	public Scorer getScorer(String name, Config config, ScorerContext context);
}
