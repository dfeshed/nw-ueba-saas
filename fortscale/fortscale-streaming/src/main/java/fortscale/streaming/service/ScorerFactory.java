package fortscale.streaming.service;

import org.apache.samza.config.Config;

import fortscale.ml.service.ModelService;

public interface ScorerFactory {

	public Scorer getScorer(String name, Config config, ModelService modelService);
}
