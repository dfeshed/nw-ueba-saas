package fortscale.ml.model.builder;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;
import org.springframework.util.Assert;

public class SMARTValuesPriorModelBuilderConf implements IModelBuilderConf {
	public static final String SMART_VALUES_PRIOR_MODEL_BUILDER = "smart_values_prior_model_builder";
	private static final double QUANTILE_DEFAULT_VALUE = 0.99;

	private double quantile;

	@JsonCreator
	public SMARTValuesPriorModelBuilderConf(@JsonProperty("quantile") Double quantile) {
		if (quantile == null) {
			quantile = QUANTILE_DEFAULT_VALUE;
		}
		Assert.isTrue(quantile >= 0 && quantile <= 1);
		this.quantile = quantile;
	}

	@Override
	public String getFactoryName() {
		return SMART_VALUES_PRIOR_MODEL_BUILDER;
	}

	public double getQuantile() {
		return quantile;
	}
}
