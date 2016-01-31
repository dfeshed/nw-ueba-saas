package fortscale.ml.scorer.config;

import com.fasterxml.jackson.annotation.JsonAutoDetect;
import com.fasterxml.jackson.annotation.JsonAutoDetect.Visibility;
import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;
import org.springframework.util.Assert;

@JsonAutoDetect(fieldVisibility = Visibility.ANY, getterVisibility = Visibility.NONE, setterVisibility = Visibility.NONE)
public class QuadPolyCalibrationConf {
	public static final double DEFAULT_SENSITIVITY = 1.0;
	public static final boolean DEFAULT_IS_SCORE_FOR_SMALL_VALUES = true;
	public static final boolean DEFAULT_IS_SCORE_FOR_LARGE_VALUES = true;

	private double a1;
	private double a2;
	@JsonProperty("sensitivity")
	private double sensitivity = DEFAULT_SENSITIVITY;
	@JsonProperty("is-score-for-small-values")
	private boolean isScoreForSmallValues = DEFAULT_IS_SCORE_FOR_SMALL_VALUES;
	@JsonProperty("is-score-for-large-values")
	private boolean isScoreForLargeValues = DEFAULT_IS_SCORE_FOR_LARGE_VALUES;

	@JsonCreator
	public QuadPolyCalibrationConf(@JsonProperty("a1") Double a1, @JsonProperty("a2") Double a2) {
		Assert.notNull(a1);
		Assert.notNull(a2);
		this.a1 = a1;
		this.a2 = a2;
	}

	public double getA1() {
		return a1;
	}

	public double getA2() {
		return a2;
	}

	public double getSensitivity() {
		return sensitivity;
	}

	public boolean isScoreForSmallValues() {
		return isScoreForSmallValues;
	}

	public boolean isScoreForLargeValues() {
		return isScoreForLargeValues;
	}
}
