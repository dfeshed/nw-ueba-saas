package fortscale.ml.scorer.config;

import com.fasterxml.jackson.annotation.JsonAutoDetect;
import com.fasterxml.jackson.annotation.JsonAutoDetect.Visibility;
import org.springframework.util.Assert;

@JsonAutoDetect(fieldVisibility = Visibility.NONE, getterVisibility = Visibility.ANY, setterVisibility = Visibility.ANY)
public class ReductionConfiguration {
	private String reducingFeatureName;
	private double reducingFactor;
	private double maxValueForFullyReduce;
	private double minValueForNotReduce;

	// TODO: Json creator?

	public String getReducingFeatureName() {
		return reducingFeatureName;
	}

	public void setReducingFeatureName(String reducingFeatureName) {
		Assert.hasText(reducingFeatureName, "reducingFeatureName cannot be blank");
		this.reducingFeatureName = reducingFeatureName;
	}

	public double getReducingFactor() {
		return reducingFactor;
	}

	public void setReducingFactor(double reducingFactor) {
		Assert.isTrue(0 <= reducingFactor && reducingFactor <= 1, "reducingFactor must be in the range of [0,1]");
		this.reducingFactor = reducingFactor;
	}

	public double getMaxValueForFullyReduce() {
		return maxValueForFullyReduce;
	}

	public void setMaxValueForFullyReduce(double maxValueForFullyReduce) {
		this.maxValueForFullyReduce = maxValueForFullyReduce;
	}

	public double getMinValueForNotReduce() {
		return minValueForNotReduce;
	}

	public void setMinValueForNotReduce(double minValueForNotReduce) {
		this.minValueForNotReduce = minValueForNotReduce;
	}

	@Override
	public boolean equals(Object o) {
		if (this == o) return true;
		if (o == null || getClass() != o.getClass()) return false;
		ReductionConfiguration that = (ReductionConfiguration)o;
		return (reducingFeatureName.equals(that.reducingFeatureName) &&
				reducingFactor == that.reducingFactor &&
				maxValueForFullyReduce == that.maxValueForFullyReduce &&
				minValueForNotReduce == that.minValueForNotReduce);
	}

	@Override
	public int hashCode() {
		return reducingFeatureName.hashCode();
	}
}
