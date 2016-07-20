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

	private double distanceBetweenSegmentsCenter;
	private int numberOfNeighbours;
	private double maxRatioBetweenSegmentSizeToCenter;
	private double maxSegmentWidthToNotDiscardBecauseOfBadRatio;
	private double padding;

	@JsonCreator
	public GaussianPriorModelBuilderConf(@JsonProperty("distanceBetweenSegmentsCenter") double distanceBetweenSegmentsCenter,
										 @JsonProperty("numberOfNeighbours") int numberOfNeighbours,
										 @JsonProperty("maxRatioBetweenSegmentSizeToCenter") double maxRatioBetweenSegmentSizeToCenter,
										 @JsonProperty("maxSegmentWidthToNotDiscardBecauseOfBadRatio") double maxSegmentWidthToNotDiscardBecauseOfBadRatio,
										 @JsonProperty("padding") double padding) {
		setDistanceBetweenSegmentsCenter(distanceBetweenSegmentsCenter);
		setNumberOfNeighbours(numberOfNeighbours);
		setMaxRatioBetweenSegmentSizeToCenter(maxRatioBetweenSegmentSizeToCenter);
		setMaxSegmentWidthToNotDiscardBecauseOfBadRatio(maxSegmentWidthToNotDiscardBecauseOfBadRatio);
		setPadding(padding);
	}

	@Override
	public String getFactoryName() {
		return GAUSSIAN_PRIOR_MODEL_BUILDER;
	}

	public void setDistanceBetweenSegmentsCenter(double distanceBetweenSegmentsCenter) {
		Assert.isTrue(distanceBetweenSegmentsCenter > 0, "distanceBetweenSegmentsCenter is mandatory and must be a positive double.");
		this.distanceBetweenSegmentsCenter = distanceBetweenSegmentsCenter;
	}

	public void setNumberOfNeighbours(int numberOfNeighbours) {
		Assert.isTrue(numberOfNeighbours > 0, "numberOfNeighbours is mandatory and must be a positive double.");
		this.numberOfNeighbours = numberOfNeighbours;
	}

	public void setMaxRatioBetweenSegmentSizeToCenter(double maxRatioBetweenSegmentSizeToCenter) {
		Assert.isTrue(maxRatioBetweenSegmentSizeToCenter > 0, "maxRatioBetweenSegmentSizeToCenter is mandatory and must be a positive double.");
		this.maxRatioBetweenSegmentSizeToCenter = maxRatioBetweenSegmentSizeToCenter;
	}

	public void setMaxSegmentWidthToNotDiscardBecauseOfBadRatio(double maxSegmentWidthToNotDiscardBecauseOfBadRatio) {
		Assert.isTrue(maxSegmentWidthToNotDiscardBecauseOfBadRatio >= 0, "maxSegmentWidthToNotDiscardBecauseOfBadRatio is mandatory and must be a non-negative double.");
		this.maxSegmentWidthToNotDiscardBecauseOfBadRatio = maxSegmentWidthToNotDiscardBecauseOfBadRatio;
	}

	public void setPadding(double padding) {
		Assert.isTrue(padding >= 0, "padding is mandatory and must be a non-negative double.");
		this.padding = padding;
	}

	public double getDistanceBetweenSegmentsCenter() {
		return distanceBetweenSegmentsCenter;
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
}
