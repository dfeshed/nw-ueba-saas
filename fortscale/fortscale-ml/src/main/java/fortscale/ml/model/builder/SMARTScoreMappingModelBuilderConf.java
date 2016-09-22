package fortscale.ml.model.builder;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;
import org.springframework.util.Assert;


public class SMARTScoreMappingModelBuilderConf implements IModelBuilderConf {
	public static final String SMART_SCORE_MAPPING_MODEL_BUILDER = "smart_score_mapping_model_builder";
	private static final double DEFAULT_THRESHOLD_DEFAULT_VALUE = 50;
	private static final double DEFAULT_MAXIMAL_SCORE_DEFAULT_VALUE = 100;
	public static final double DEFAULT_LOW_OUTLIERS_FRACTION = 2.0 / 7; // usually the low outliers will be the weekend
	public static final double DEFAULT_HIGH_OUTLIERS_FRACTION = 1.0 / 7; // usually there will be no more than 1 noisy day per week

	private double defaultThreshold;
	private double defaultMaximalScore;
	private double minThreshold;
	private double minMaximalScore;
	private double lowOutliersFraction;
	private double highOutliersFraction;

	@JsonCreator
	public SMARTScoreMappingModelBuilderConf(
			@JsonProperty("defaultThreshold") Double defaultThreshold,
			@JsonProperty("defaultMaximalScore") Double defaultMaximalScore,
			@JsonProperty("minThreshold") double minThreshold,
			@JsonProperty("minMaximalScore") double minMaximalScore,
			@JsonProperty("lowOutliersFraction") Double lowOutliersFraction,
			@JsonProperty("highOutliersFraction") Double highOutliersFraction) {
		if (defaultThreshold == null) {
			defaultThreshold = DEFAULT_THRESHOLD_DEFAULT_VALUE;
		}
		if (defaultMaximalScore == null) {
			defaultMaximalScore = DEFAULT_MAXIMAL_SCORE_DEFAULT_VALUE;
		}
		if (lowOutliersFraction == null) {
			lowOutliersFraction = DEFAULT_LOW_OUTLIERS_FRACTION;
		}
		if (highOutliersFraction == null) {
			highOutliersFraction = DEFAULT_HIGH_OUTLIERS_FRACTION;
		}
		Assert.isTrue(defaultThreshold >= 0 && defaultThreshold <= 100);
		Assert.isTrue(defaultMaximalScore >= 0 && defaultMaximalScore <= 100);
		Assert.isTrue(defaultMaximalScore >= defaultThreshold);
		Assert.isTrue(minThreshold >= 0 && minThreshold <= 100);
		Assert.isTrue(minMaximalScore >= 0 && minMaximalScore <= 100);
		Assert.isTrue(minMaximalScore >= minThreshold);
		Assert.isTrue(lowOutliersFraction >= 0 && lowOutliersFraction <= 1);
		Assert.isTrue(highOutliersFraction >= 0 && highOutliersFraction <= 1);
		this.defaultThreshold = defaultThreshold;
		this.defaultMaximalScore = defaultMaximalScore;
		this.minThreshold = minThreshold;
		this.minMaximalScore = minMaximalScore;
		this.lowOutliersFraction = lowOutliersFraction;
		this.highOutliersFraction = highOutliersFraction;
	}

	@Override
	public String getFactoryName() {
		return SMART_SCORE_MAPPING_MODEL_BUILDER;
	}

	public double getDefaultThreshold() {
		return defaultThreshold;
	}

	public double getDefaultMaximalScore() {
		return defaultMaximalScore;
	}

	public double getMinThreshold() {
		return minThreshold;
	}

	public double getMinMaximalScore() {
		return minMaximalScore;
	}

	public double getLowOutliersFraction() {
		return lowOutliersFraction;
	}

	public double getHighOutliersFraction() {
		return highOutliersFraction;
	}
}
