package fortscale.ml.model.builder.gaussian.prior;

import fortscale.ml.model.ContinuousDataModel;
import org.springframework.util.Assert;

import java.util.Iterator;
import java.util.List;

/**
 * Generate candidates for segment centers uniformly from 0 to the maximal ContinuousDataModel mean available.
 */
public class UniformSegmentCenters implements SegmentCenters {
	private double distanceBetweenSegmentCenters;

	public UniformSegmentCenters(double distanceBetweenSegmentCenters) {
		Assert.isTrue(distanceBetweenSegmentCenters > 0, "distanceBetweenSegmentCenters must be positive");
		this.distanceBetweenSegmentCenters = distanceBetweenSegmentCenters;
	}

	@Override
	public Iterator<Double> iterate(List<ContinuousDataModel> models) {
		Assert.notNull(models, "models can't be null");
		double maxMean = models.stream()
				.mapToDouble(ContinuousDataModel::getMean)
				.max()
				.orElse(-1);
		double minMean = models.stream()
				.mapToDouble(ContinuousDataModel::getMean)
				.min()
				.orElse(-1);
		return new Iterator<Double>() {
			private double nextSegmentCenter = ((int) (minMean / distanceBetweenSegmentCenters)) * distanceBetweenSegmentCenters;

			@Override
			public boolean hasNext() {
				return maxMean >= 0 && nextSegmentCenter < maxMean + distanceBetweenSegmentCenters;
			}

			@Override
			public Double next() {
				double next = nextSegmentCenter;
				nextSegmentCenter += distanceBetweenSegmentCenters;
				return next;
			}
		};
	}
}
