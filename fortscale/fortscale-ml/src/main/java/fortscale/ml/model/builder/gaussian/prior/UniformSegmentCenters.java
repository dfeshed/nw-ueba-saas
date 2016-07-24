package fortscale.ml.model.builder.gaussian.prior;

import fortscale.ml.model.ContinuousDataModel;
import org.springframework.util.Assert;

import java.util.Iterator;
import java.util.List;

/**
 * Generate candidates for segment centers uniformly from 0 to the maximal ContinuousDataModel mean available.
 */
public class UniformSegmentCenters implements SegmentCenters {
	private double distanceBetweenSegmentsCenter;

	public UniformSegmentCenters(double distanceBetweenSegmentsCenter) {
		Assert.isTrue(distanceBetweenSegmentsCenter > 0, "distanceBetweenSegmentsCenter must be positive");
		this.distanceBetweenSegmentsCenter = distanceBetweenSegmentsCenter;
	}

	@Override
	public Iterator<Double> iterate(List<ContinuousDataModel> models) {
		Assert.notNull(models, "models can't be null");
		double maxMean = models.stream()
				.mapToDouble(ContinuousDataModel::getMean)
				.max()
				.orElse(-1);
		return new Iterator<Double>() {
			private double nextSegmentCenter = 0;

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
		};
	}
}
