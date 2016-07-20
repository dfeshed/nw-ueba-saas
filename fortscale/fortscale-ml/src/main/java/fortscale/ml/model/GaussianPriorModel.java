package fortscale.ml.model;

import com.fasterxml.jackson.annotation.JsonAutoDetect;
import com.fasterxml.jackson.annotation.JsonAutoDetect.Visibility;
import org.apache.commons.lang3.tuple.ImmutablePair;
import org.springframework.util.Assert;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

@JsonAutoDetect(fieldVisibility = Visibility.ANY, getterVisibility = Visibility.NONE, setterVisibility = Visibility.NONE)
public class GaussianPriorModel implements Model {
	public static class SegmentPrior {
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
	}

	private List<SegmentPrior> segmentPriors;

	public GaussianPriorModel() {
		this.segmentPriors = new ArrayList<>();
	}

	public void init(List<SegmentPrior> priors) {
		Assert.notNull(priors);
		Set<Double> means = new HashSet<>();
		priors.forEach(segmentPrior -> Assert.isTrue(means.add(segmentPrior.mean)));
		this.segmentPriors = priors;
	}

	public Double getPrior(double mean) {
		List<ImmutablePair<Double, Double>> priorsAndDistances = segmentPriors.stream()
				.filter(s -> s.mean - s.supportFromLeftOfMean <= mean && mean <= s.mean + s.supportFromRightOfMean)
				.map(s -> new ImmutablePair<>(s.priorAtMean, Math.abs(mean - s.mean)))
				.collect(Collectors.toList());
		if (priorsAndDistances.size() == 0) {
			return null;
		}
		if (priorsAndDistances.size() == 1) {
			return priorsAndDistances.get(0).left;
		}
		double distancesSum = priorsAndDistances.stream()
				.mapToDouble(priorAndDistance -> priorAndDistance.right)
				.sum();
		return priorsAndDistances.stream()
				.mapToDouble(priorAndDistance -> priorAndDistance.left * (distancesSum - priorAndDistance.right) / distancesSum)
				.sum();
	}

	@Override
	public long getNumOfSamples() {
		return 0;
	}
}
