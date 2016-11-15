package fortscale.ml.model;

import com.fasterxml.jackson.annotation.JsonAutoDetect;
import com.fasterxml.jackson.annotation.JsonAutoDetect.Visibility;
import org.apache.commons.lang3.tuple.ImmutablePair;
import org.springframework.util.Assert;

import java.util.*;
import java.util.stream.Collectors;
import java.util.stream.Stream;

@JsonAutoDetect(fieldVisibility = Visibility.ANY, getterVisibility = Visibility.NONE, setterVisibility = Visibility.NONE)
public class GaussianPriorModel implements Model {
	@JsonAutoDetect(fieldVisibility = Visibility.ANY, getterVisibility = Visibility.NONE, setterVisibility = Visibility.NONE)
	public static class SegmentPrior {
		public double mean;
		public double priorAtMean;
		public double supportFromLeftOfMean;
		public double supportFromRightOfMean;

		public SegmentPrior init(double mean, double priorAtMean, double supportFromLeftOfMean, double supportFromRightOfMean) {
			this.mean = mean;
			this.supportFromLeftOfMean = supportFromLeftOfMean;
			this.supportFromRightOfMean = supportFromRightOfMean;
			this.priorAtMean = priorAtMean;
			return this;
		}

		public SegmentPrior init(double mean, double priorAtMean, double supportRadiusAroundMean) {
			return init(mean, priorAtMean, supportRadiusAroundMean, supportRadiusAroundMean);
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
		public String toString() {
			return String.format("<SegmentPrior: mean=%f, priorAtMean=%f, supportFromLeftOfMean=%f, supportFromRightOfMean=%f>",
					mean, priorAtMean, supportFromLeftOfMean, supportFromRightOfMean);
		}
	}

	private SegmentPrior[] segmentPriors;
	private Double minPrior;

	public GaussianPriorModel() {
		this.segmentPriors = new SegmentPrior[]{};
	}

	public GaussianPriorModel init(List<SegmentPrior> segmentPriors) {
		Assert.notNull(segmentPriors);
		Set<Double> means = new HashSet<>();
		segmentPriors.forEach(segmentPrior -> Assert.isTrue(means.add(segmentPrior.mean)));
		this.segmentPriors = segmentPriors.toArray(new SegmentPrior[]{});
		Arrays.sort(this.segmentPriors, Comparator.comparingDouble(segmentPrior -> segmentPrior.mean));
		minPrior = Stream.of(this.segmentPriors)
				.map(segmentPrior -> segmentPrior.priorAtMean)
				.min(Double::compare)
				.orElse(null);
		return this;
	}

	public SegmentPrior[] getSegmentPriors() {
		return segmentPriors;
	}

	//TODO: move this function to the scorer
	public Double getMinPrior() {
		return minPrior;
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
		if (segmentPriors.length == 0) {
			return Collections.emptySet();
		}
		int closestSegmentPriorIndexFromRight =
				Arrays.binarySearch(
						segmentPriors,
						new SegmentPrior().init(mean, 0, 0),
						Comparator.comparingDouble(segmentPrior -> segmentPrior.mean)
				);
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
