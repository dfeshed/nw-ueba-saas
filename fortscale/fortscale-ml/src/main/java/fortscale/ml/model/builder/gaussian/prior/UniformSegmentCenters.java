package fortscale.ml.model.builder.gaussian.prior;

import fortscale.ml.model.ContinuousDataModel;
import org.springframework.util.Assert;

import java.util.Iterator;
import java.util.List;

/**
 * Generate candidates for segment centers uniformly from 0 to the maximal ContinuousDataModel mean available.
 */
public class UniformSegmentCenters implements Iterable<Double> {
	private double distanceBetweenSegmentsCenter;
	private double maxMean;

	private class UniformSegmentCentersIterator implements Iterator<Double> {
		private double nextSegmentCenter;

		public UniformSegmentCentersIterator() {
			nextSegmentCenter = 0;
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

	public UniformSegmentCenters(List<ContinuousDataModel> models, double distanceBetweenSegmentsCenter) {
		Assert.notNull(models, "models can't be null");
		Assert.isTrue(distanceBetweenSegmentsCenter > 0, "distanceBetweenSegmentsCenter must be positive");
		this.distanceBetweenSegmentsCenter = distanceBetweenSegmentsCenter;
		maxMean = models.stream()
				.mapToDouble(ContinuousDataModel::getMean)
				.max()
				.orElse(-1);
	}

	@Override
	public Iterator<Double> iterator() {
		return new UniformSegmentCentersIterator();
	}
}
