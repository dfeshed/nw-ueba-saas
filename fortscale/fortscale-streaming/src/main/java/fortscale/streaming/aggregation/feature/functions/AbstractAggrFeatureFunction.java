package fortscale.streaming.aggregation.feature.functions;

import com.fasterxml.jackson.annotation.JsonProperty;
import fortscale.streaming.aggregation.feature.Feature;
import fortscale.streaming.service.aggregation.AggregatedFeatureConf;

import java.util.Map;

/**
 * Created by orend on 12/07/2015.
 */

public abstract class AbstractAggrFeatureFunction implements AggrFeatureFunction{

	protected AggrFilter aggrFilter;

	public AbstractAggrFeatureFunction(@JsonProperty("filter") AggrFilter aggrFilter) {
		this.aggrFilter = aggrFilter;
	}

	@Override
	public abstract Object updateAggrFeature(AggregatedFeatureConf aggregatedFeatureConf, Map<String, Feature> features, Feature aggrFeature);
}
