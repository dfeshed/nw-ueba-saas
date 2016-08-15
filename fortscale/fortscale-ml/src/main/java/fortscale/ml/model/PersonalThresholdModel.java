package fortscale.ml.model;

import com.fasterxml.jackson.annotation.JsonAutoDetect;
import com.fasterxml.jackson.annotation.JsonAutoDetect.Visibility;
import org.springframework.util.Assert;

@JsonAutoDetect(fieldVisibility = Visibility.ANY, getterVisibility = Visibility.NONE, setterVisibility = Visibility.NONE)
public class PersonalThresholdModel implements Model {
	private double normalizationFactor;

	public PersonalThresholdModel(int numOfContexts, int desiredNumOfIndicators) {
		Assert.isTrue(numOfContexts > 0);
		Assert.isTrue(desiredNumOfIndicators > 0);
		this.normalizationFactor = ((double) desiredNumOfIndicators) / numOfContexts;
	}

	public double calcThreshold(int numOfSamples) {
		return 1 - normalizationFactor / numOfSamples;
	}

	@Override
	public long getNumOfSamples() {
		return 0;
	}
}
