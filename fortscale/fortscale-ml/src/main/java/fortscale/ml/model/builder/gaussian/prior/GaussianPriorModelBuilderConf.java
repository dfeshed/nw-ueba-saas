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

	private static final int NUMBER_OF_NEIGHBOURS_DEFAULT_VALUE = 10;
	private static final double MAX_RATIO_BETWEEN_SEGMENT_SIZE_TO_CENTER_DEFAULT_VALUE = 0.1;
	private static final double MAX_SEGMENT_WIDTH_TO_NOT_DISCARD_BECAUSE_OF_BAD_RATIO_DEFAULT_VALUE = 10;
	private static final double PADDING_DEFAULT_VALUE = 1;
	private static final double QUANTILE_DEFAULT_VALUE = 0.99;
	private static final int MIN_QUANTILE_COMPLEMENT_DEFAULT_VALUE = 30;
	private static final double MIN_MAX_VALUE_DEFAULT_VALUE = 1;

	private double distanceBetweenSegmentCenters;
	private int numberOfNeighbours;
	private double maxRatioBetweenSegmentSizeToCenter;
	private double maxSegmentWidthToNotDiscardBecauseOfBadRatio;
	private double padding;
	private int minNumOfSamplesToLearnFrom;
	private double quantile;
	private int minQuantileComplementSize;
	private double minMaxValue;

	@JsonCreator
	public GaussianPriorModelBuilderConf(@JsonProperty("distanceBetweenSegmentCenters") double distanceBetweenSegmentCenters,
										 @JsonProperty("numberOfNeighbours") Integer numberOfNeighbours,
										 @JsonProperty("maxRatioBetweenSegmentSizeToCenter") Double maxRatioBetweenSegmentSizeToCenter,
										 @JsonProperty("maxSegmentWidthToNotDiscardBecauseOfBadRatio") Double maxSegmentWidthToNotDiscardBecauseOfBadRatio,
										 @JsonProperty("padding") Double padding,
										 @JsonProperty("minNumOfSamplesToLearnFrom") int minNumOfSamplesToLearnFrom,
										 @JsonProperty("quantile") Double quantile,
										 @JsonProperty("minQuantileComplementSize") Integer minQuantileComplementSize,
										 @JsonProperty("minMaxValue") Double minMaxValue) {
		setDistanceBetweenSegmentCenters(distanceBetweenSegmentCenters);
		setNumberOfNeighbours(numberOfNeighbours);
		setMaxRatioBetweenSegmentSizeToCenter(maxRatioBetweenSegmentSizeToCenter);
		setMaxSegmentWidthToNotDiscardBecauseOfBadRatio(maxSegmentWidthToNotDiscardBecauseOfBadRatio);
		setPadding(padding);
		setMinNumOfSamplesToLearnFrom(minNumOfSamplesToLearnFrom);
		setQuantile(quantile);
		setMinQuantileComplementSize(minQuantileComplementSize);
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

	private void setNumberOfNeighbours(Integer numberOfNeighbours) {
		if (numberOfNeighbours == null) {
			numberOfNeighbours = NUMBER_OF_NEIGHBOURS_DEFAULT_VALUE;
		}
		Assert.isTrue(numberOfNeighbours > 0, "numberOfNeighbours must be a positive double.");
		this.numberOfNeighbours = numberOfNeighbours;
	}

	private void setMaxRatioBetweenSegmentSizeToCenter(Double maxRatioBetweenSegmentSizeToCenter) {
		if (maxRatioBetweenSegmentSizeToCenter == null) {
			maxRatioBetweenSegmentSizeToCenter = MAX_RATIO_BETWEEN_SEGMENT_SIZE_TO_CENTER_DEFAULT_VALUE;
		}
		Assert.isTrue(maxRatioBetweenSegmentSizeToCenter > 0, "maxRatioBetweenSegmentSizeToCenter must be a positive double.");
		this.maxRatioBetweenSegmentSizeToCenter = maxRatioBetweenSegmentSizeToCenter;
	}

	private void setMaxSegmentWidthToNotDiscardBecauseOfBadRatio(Double maxSegmentWidthToNotDiscardBecauseOfBadRatio) {
		if (maxSegmentWidthToNotDiscardBecauseOfBadRatio == null) {
			maxSegmentWidthToNotDiscardBecauseOfBadRatio = MAX_SEGMENT_WIDTH_TO_NOT_DISCARD_BECAUSE_OF_BAD_RATIO_DEFAULT_VALUE;
		}
		Assert.isTrue(maxSegmentWidthToNotDiscardBecauseOfBadRatio >= 0, "maxSegmentWidthToNotDiscardBecauseOfBadRatio must be a non-negative double.");
		this.maxSegmentWidthToNotDiscardBecauseOfBadRatio = maxSegmentWidthToNotDiscardBecauseOfBadRatio;
	}

	private void setPadding(Double padding) {
		if (padding == null) {
			padding = PADDING_DEFAULT_VALUE;
		}
		Assert.isTrue(padding >= 0, "padding must be a non-negative double.");
		this.padding = padding;
	}

	private void setMinNumOfSamplesToLearnFrom(int minNumOfSamplesToLearnFrom) {
		Assert.isTrue(minNumOfSamplesToLearnFrom >= 0, "minNumOfSamplesToLearnFrom is mandatory and must be a non-negative double.");
		this.minNumOfSamplesToLearnFrom = minNumOfSamplesToLearnFrom;
	}

	private void setQuantile(Double quantile) {
		if (quantile == null) {
			quantile = QUANTILE_DEFAULT_VALUE;
		}
		Assert.isTrue(quantile >= 0 && quantile <= 1, "quantile must be between 0 to 1.");
		this.quantile = quantile;
	}

	private void setMinQuantileComplementSize(Integer minQuantileComplementSize) {
		if (minQuantileComplementSize == null) {
			minQuantileComplementSize = MIN_QUANTILE_COMPLEMENT_DEFAULT_VALUE;
		}
		Assert.isTrue(minQuantileComplementSize >= 0, "minQuantileComplementSize must be non-negative.");
		this.minQuantileComplementSize = minQuantileComplementSize;
	}

	private void setMinMaxValue(Double minMaxValue) {
		if (minMaxValue == null) {
			minMaxValue = MIN_MAX_VALUE_DEFAULT_VALUE;
		}
		Assert.isTrue(minMaxValue >= 0, "minMaxValue can't be negative");
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

	public int getMinQuantileComplementSize() {
		return minQuantileComplementSize;
	}

	public Double getMinMaxValue() {
		return minMaxValue;
	}
}
