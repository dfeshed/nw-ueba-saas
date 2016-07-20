package fortscale.ml.model;

import com.fasterxml.jackson.annotation.JsonAutoDetect;
import com.fasterxml.jackson.annotation.JsonAutoDetect.Visibility;
import org.springframework.util.Assert;

import java.util.ArrayList;
import java.util.List;

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

	private List<SegmentPrior> priors;

	public GaussianPriorModel() {
		this.priors = new ArrayList<>();
	}

	public void init(List<SegmentPrior> priors) {
		Assert.notNull(priors);
		this.priors = priors;
	}

	public Double getPrior(double mean) {
		return priors.stream()
				.filter(s -> s.mean - s.supportFromLeftOfMean <= mean && mean <= s.mean + s.supportFromRightOfMean)
				.findFirst()
				.map(s -> s.priorAtMean)
				.orElse(null);
	}

	@Override
	public long getNumOfSamples() {
		return 0;
	}
}
