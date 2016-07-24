package fortscale.ml.model.builder.gaussian.prior;

import com.fasterxml.jackson.annotation.JsonAutoDetect;
import com.fasterxml.jackson.annotation.JsonAutoDetect.Visibility;
import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;
import fortscale.ml.model.builder.IModelBuilderConf;
import org.springframework.util.Assert;

@JsonAutoDetect(fieldVisibility = Visibility.NONE, getterVisibility = Visibility.ANY, setterVisibility = Visibility.ANY)
public class GaussianPriorModelBuilderConf implements IModelBuilderConf {
	public static final String GAUSSIAN_PRIOR_MODEL_BUILDER = "gaussian_prior_model_builder";

	private double distanceBetweenSegmentCenters;
	private int numberOfNeighbours;
	private double maxRatioBetweenSegmentSizeToCenter;
	private double maxSegmentWidthToNotDiscardBecauseOfBadRatio;
	private double padding;
	private int minNumOfSamplesToLearnFrom;
	private double quantile;
	private Double minMaxValue;

	@JsonCreator
	public GaussianPriorModelBuilderConf(@JsonProperty("distanceBetweenSegmentCenters") double distanceBetweenSegmentCenters,
										 @JsonProperty("numberOfNeighbours") int numberOfNeighbours,
										 @JsonProperty("maxRatioBetweenSegmentSizeToCenter") double maxRatioBetweenSegmentSizeToCenter,
										 @JsonProperty("maxSegmentWidthToNotDiscardBecauseOfBadRatio") double maxSegmentWidthToNotDiscardBecauseOfBadRatio,
										 @JsonProperty("padding") double padding,
										 @JsonProperty("minNumOfSamplesToLearnFrom") int minNumOfSamplesToLearnFrom,
										 @JsonProperty("quantile") double quantile,
										 @JsonProperty("minMaxValue") Double minMaxValue) {
		setDistanceBetweenSegmentCenters(distanceBetweenSegmentCenters);
		setNumberOfNeighbours(numberOfNeighbours);
		setMaxRatioBetweenSegmentSizeToCenter(maxRatioBetweenSegmentSizeToCenter);
		setMaxSegmentWidthToNotDiscardBecauseOfBadRatio(maxSegmentWidthToNotDiscardBecauseOfBadRatio);
		setPadding(padding);
		setMinNumOfSamplesToLearnFrom(minNumOfSamplesToLearnFrom);
		setQuantile(quantile);
		setMinMaxValue(minMaxValue);
	}

	@Override
	public String getFactoryName() {
		return GAUSSIAN_PRIOR_MODEL_BUILDER;
	}

	private void setDistanceBetweenSegmentCenters(double distanceBetweenSegmentCenters) {
		Assert.isTrue(distanceBetweenSegmentCenters > 0, "distanceBetweenSegmentCenters is mandatory and must be a positive double.");
		this.distanceBetweenSegmentCenters = distanceBetweenSegmentCenters;
	}

	private void setNumberOfNeighbours(int numberOfNeighbours) {
		Assert.isTrue(numberOfNeighbours > 0, "numberOfNeighbours is mandatory and must be a positive double.");
		this.numberOfNeighbours = numberOfNeighbours;
	}

	private void setMaxRatioBetweenSegmentSizeToCenter(double maxRatioBetweenSegmentSizeToCenter) {
		Assert.isTrue(maxRatioBetweenSegmentSizeToCenter > 0, "maxRatioBetweenSegmentSizeToCenter is mandatory and must be a positive double.");
		this.maxRatioBetweenSegmentSizeToCenter = maxRatioBetweenSegmentSizeToCenter;
	}

	private void setMaxSegmentWidthToNotDiscardBecauseOfBadRatio(double maxSegmentWidthToNotDiscardBecauseOfBadRatio) {
		Assert.isTrue(maxSegmentWidthToNotDiscardBecauseOfBadRatio >= 0, "maxSegmentWidthToNotDiscardBecauseOfBadRatio is mandatory and must be a non-negative double.");
		this.maxSegmentWidthToNotDiscardBecauseOfBadRatio = maxSegmentWidthToNotDiscardBecauseOfBadRatio;
	}

	private void setPadding(double padding) {
		Assert.isTrue(padding >= 0, "padding is mandatory and must be a non-negative double.");
		this.padding = padding;
	}

	private void setMinNumOfSamplesToLearnFrom(int minNumOfSamplesToLearnFrom) {
		Assert.isTrue(minNumOfSamplesToLearnFrom >= 0, "minNumOfSamplesToLearnFrom is mandatory and must be a non-negative double.");
		this.minNumOfSamplesToLearnFrom = minNumOfSamplesToLearnFrom;
	}

	private void setQuantile(double quantile) {
		Assert.isTrue(quantile >= 0 && quantile <= 1, "quantile is mandatory and must be between 0 to 1.");
		this.quantile = quantile;
	}

	private void setMinMaxValue(Double minMaxValue) {
		this.minMaxValue = minMaxValue;
	}

	public double getDistanceBetweenSegmentCenters() {
		return distanceBetweenSegmentCenters;
	}

	public int getNumberOfNeighbours() {
		return numberOfNeighbours;
	}

	public double getMaxRatioBetweenSegmentSizeToCenter() {
		return maxRatioBetweenSegmentSizeToCenter;
	}

	public double getMaxSegmentWidthToNotDiscardBecauseOfBadRatio() {
		return maxSegmentWidthToNotDiscardBecauseOfBadRatio;
	}

	public double getPadding() {
		return padding;
	}

	public int getMinNumOfSamplesToLearnFrom() {
		return minNumOfSamplesToLearnFrom;
	}

	public double getQuantile() {
		return quantile;
	}

	public Double getMinMaxValue() {
		return minMaxValue;
	}
}
