package fortscale.ml.model;

import com.fasterxml.jackson.annotation.JsonAutoDetect;
import com.fasterxml.jackson.annotation.JsonAutoDetect.Visibility;
import org.springframework.util.Assert;

@JsonAutoDetect(fieldVisibility = Visibility.ANY, getterVisibility = Visibility.NONE, setterVisibility = Visibility.NONE)
public class PersonalThresholdModel implements Model {
	private double normalizationFactor;

	public PersonalThresholdModel(int numOfContexts, int numOfOrganizationScores, double organizationThreshold) {
		Assert.isTrue(numOfContexts > 0);
		Assert.isTrue(organizationThreshold > 0 && organizationThreshold < 1);
		Assert.isTrue(numOfOrganizationScores > 0);
		double expectedNumOfIndicators = (1 - organizationThreshold) * numOfOrganizationScores;
		this.normalizationFactor = expectedNumOfIndicators / numOfContexts;
	}

	public double calcThreshold(int numOfSamples) {
		return 1 - normalizationFactor / numOfSamples;
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
