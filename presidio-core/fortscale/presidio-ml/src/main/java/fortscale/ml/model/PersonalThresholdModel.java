package fortscale.ml.model;

import com.fasterxml.jackson.annotation.JsonAutoDetect;
import com.fasterxml.jackson.annotation.JsonAutoDetect.Visibility;
import org.springframework.util.Assert;

@JsonAutoDetect(fieldVisibility = Visibility.ANY, getterVisibility = Visibility.NONE, setterVisibility = Visibility.NONE)
public class PersonalThresholdModel implements Model {
	private Double uniformThreshold;
	private Double normalizationFactor;

	public PersonalThresholdModel(int numOfContexts, long numOfOrganizationScores, double uniformThreshold) {
		Assert.isTrue(numOfContexts > 0);
		Assert.isTrue(uniformThreshold > 0 && uniformThreshold < 1);
		Assert.isTrue(numOfOrganizationScores > 0);
		this.uniformThreshold = uniformThreshold;
		double expectedNumOfIndicators = (1 - uniformThreshold) * numOfOrganizationScores;
		this.normalizationFactor = expectedNumOfIndicators / numOfContexts;
	}

	public double calcThreshold(long numOfSamples, double maxRatioFromUniformThreshold) {
		double minAllowedThreshold = 1 - (1 - uniformThreshold) * maxRatioFromUniformThreshold;
		return Math.max(minAllowedThreshold, 1 - normalizationFactor / numOfSamples);
	}

	@Override
	public long getNumOfSamples() {
		return 0;
	}

	@Override
	public boolean equals(Object o) {
		if (!(o instanceof  PersonalThresholdModel)) {
			return false;
		}
		return Math.abs(((PersonalThresholdModel) o).normalizationFactor - normalizationFactor) < 0.00000001;
	}

	@Override
	public int hashCode() {
		return new Double(normalizationFactor).hashCode();
	}
}
