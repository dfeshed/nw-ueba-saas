package fortscale.ml.model.retriever;

import net.minidev.json.JSONObject;
import org.springframework.util.Assert;

import java.util.List;

public abstract class AbstractAggregatedFeatureValueRetrieverConf extends AbstractDataRetrieverConf {
	private String aggregatedFeatureEventConfName;

	public AbstractAggregatedFeatureValueRetrieverConf(long timeRangeInSeconds,
													   List<JSONObject> functions,
													   String aggregatedFeatureEventConfName) {

		super(timeRangeInSeconds, functions);

		Assert.hasText(aggregatedFeatureEventConfName);
		this.aggregatedFeatureEventConfName = aggregatedFeatureEventConfName;
	}

	public String getAggregatedFeatureEventConfName() {
		return aggregatedFeatureEventConfName;
	}
}
