package fortscale.ml.model;

import com.fasterxml.jackson.annotation.JsonAutoDetect;
import com.fasterxml.jackson.annotation.JsonAutoDetect.Visibility;
import org.apache.commons.lang3.tuple.ImmutablePair;
import org.springframework.util.Assert;

import java.util.Arrays;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;
import java.util.stream.Stream;

@JsonAutoDetect(fieldVisibility = Visibility.ANY, getterVisibility = Visibility.NONE, setterVisibility = Visibility.NONE)
public class GaussianPriorModel implements Model {
	public static class SegmentPrior implements Comparable<SegmentPrior> {
		public double mean;
		public double priorAtMean;
		public double supportFromLeftOfMean;
		public double supportFromRightOfMean;

		public SegmentPrior(double mean, double priorAtMean, double supportFromLeftOfMean, double supportFromRightOfMean) {
			this.mean = mean;
			this.supportFromLeftOfMean = supportFromLeftOfMean;
			this.supportFromRightOfMean = supportFromRightOfMean;
			this.priorAtMean = priorAtMean;
		}

		public SegmentPrior(double mean, double priorAtMean, double supportRadiusAroundMean) {
			this(mean, priorAtMean, supportRadiusAroundMean, supportRadiusAroundMean);
		}

		@Override
		public int hashCode() {
			return new Double(mean).hashCode();
		}

		@Override
		public boolean equals(Object obj) {
			if (obj == null || !(obj instanceof SegmentPrior)) {
				return false;
			}
			SegmentPrior o = (SegmentPrior) obj;
			return o.mean == mean &&
					o.priorAtMean == priorAtMean &&
					o.supportFromLeftOfMean == supportFromLeftOfMean &&
					o.supportFromRightOfMean == supportFromRightOfMean;
		}

		@Override
		public int compareTo(SegmentPrior o) {
			if (mean < o.mean) {
				return -1;
			} else if (mean > o.mean) {
				return 1;
			} else {
				return 0;
			}
		}

		@Override
		public String toString() {
			return String.format("<SegmentPrior: mean=%f, priorAtMean=%f, supportFromLeftOfMean=%f, supportFromRightOfMean=%f>",
					mean, priorAtMean, supportFromLeftOfMean, supportFromRightOfMean);
		}
	}

	private SegmentPrior[] segmentPriors;

	public GaussianPriorModel() {
		this.segmentPriors = new SegmentPrior[]{};
	}

	public GaussianPriorModel init(List<SegmentPrior> segmentPriors) {
		Assert.notNull(segmentPriors);
		Set<Double> means = new HashSet<>();
		segmentPriors.forEach(segmentPrior -> Assert.isTrue(means.add(segmentPrior.mean)));
		this.segmentPriors = segmentPriors.toArray(new SegmentPrior[]{});
		Arrays.sort(this.segmentPriors);
		return this;
	}

	public SegmentPrior[] getSegmentPriors() {
		return segmentPriors;
	}

	//TODO: move this function to the scorer
	public Double getPrior(double mean) {
		Set<SegmentPrior> containingSegmentPriors = findNearestContainingSegmentPriorsFromEachSide(mean);
		if (containingSegmentPriors.size() == 0) {
			return null;
		} else if (containingSegmentPriors.size() == 1) {
			return containingSegmentPriors.iterator().next().priorAtMean;
		}
		List<ImmutablePair<Double, Double>> priorsAndDistances = containingSegmentPriors.stream()
				.map(segmentPrior -> new ImmutablePair<>(
						segmentPrior.priorAtMean,
						Math.abs(mean - segmentPrior.mean)
				))
				.collect(Collectors.toList());
		double distancesSum = priorsAndDistances.stream()
				.mapToDouble(priorAndDistance -> priorAndDistance.right)
				.sum();
		return priorsAndDistances.stream()
				.mapToDouble(priorAndDistance -> priorAndDistance.left * (distancesSum - priorAndDistance.right) / distancesSum)
				.sum();
	}

	private Set<SegmentPrior> findNearestContainingSegmentPriorsFromEachSide(double mean) {
		int closestSegmentPriorIndexFromRight =
				Arrays.binarySearch(segmentPriors, new SegmentPrior(mean, 0, 0));
		if (closestSegmentPriorIndexFromRight < 0) {
			closestSegmentPriorIndexFromRight = -closestSegmentPriorIndexFromRight - 1;
		}
		int closestSegmentPriorIndexFromLeft = closestSegmentPriorIndexFromRight - 1;
		closestSegmentPriorIndexFromRight = Math.min(segmentPriors.length - 1, closestSegmentPriorIndexFromRight);
		closestSegmentPriorIndexFromLeft = Math.max(0, closestSegmentPriorIndexFromLeft);
		return Stream.of(closestSegmentPriorIndexFromLeft, closestSegmentPriorIndexFromRight)
				.map(segmentIndex -> segmentPriors[segmentIndex])
				.filter(segmentPrior -> isInsideSegmentSupport(mean, segmentPrior))
				.collect(Collectors.toSet());
	}

	private boolean isInsideSegmentSupport(double mean, SegmentPrior segmentPrior) {
		return segmentPrior.mean - segmentPrior.supportFromLeftOfMean <= mean &&
				mean <= segmentPrior.mean + segmentPrior.supportFromRightOfMean;
	}

	@Override
	public long getNumOfSamples() {
		return 0;
	}
}
