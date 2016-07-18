package fortscale.ml.model.builder.gaussian.prior;

import fortscale.ml.model.ContinuousDataModel;
import org.springframework.util.Assert;

import java.util.Iterator;
import java.util.List;

/**
 * Generate candidates for segment centers uniformly from 0 to the maximal ContinuousDataModel mean available.
 */
public class UniformSegmentCentersIterator implements Iterator<Double> {
	private double distanceBetweenSegmentsCenter;
	private double maxMean;
	private double nextSegmentCenter;

	public UniformSegmentCentersIterator(List<ContinuousDataModel> models, double distanceBetweenSegmentsCenter) {
		Assert.notNull(models, "models can't be null");
		Assert.isTrue(distanceBetweenSegmentsCenter > 0, "distanceBetweenSegmentsCenter must be positive");
		this.distanceBetweenSegmentsCenter = distanceBetweenSegmentsCenter;
		nextSegmentCenter = 0;
		maxMean = models.stream()
				.mapToDouble(ContinuousDataModel::getMean)
				.max()
				.orElse(-1);
	}

	@Override
	public boolean hasNext() {
		return maxMean >= 0 && nextSegmentCenter < maxMean + distanceBetweenSegmentsCenter;
	}

	@Override
	public Double next() {
		double next = nextSegmentCenter;
		nextSegmentCenter += distanceBetweenSegmentsCenter;
		return next;
	}
}
