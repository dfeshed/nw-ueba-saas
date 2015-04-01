package fortscale.streaming.scorer;

import org.springframework.util.Assert;

import java.util.List;

public class ReductionConfigurations {
	private List<ReductionConfiguration> reductionConfigs;

	public List<ReductionConfiguration> getReductionConfigs() {
		return reductionConfigs;
	}

	public void setReductionConfigs(List<ReductionConfiguration> reductionConfigs) {
		this.reductionConfigs = reductionConfigs;
	}

	@Override
	public boolean equals(Object o) {
		if (this == o) return true;
		if (o == null || getClass() != o.getClass()) return false;

		ReductionConfigurations that = (ReductionConfigurations)o;

		if (!reductionConfigs.equals(that.reductionConfigs)) return false;

		return true;
	}

	@Override
	public int hashCode() {
		return reductionConfigs.hashCode();
	}

	protected static final class ReductionConfiguration {
		private String reducingFeatureName;
		private double reducingFactor;
		private double maxValueForFullyReduce;
		private double minValueForNotReduce;

		public String getReducingFeatureName() {
			return reducingFeatureName;
		}

		public void setReducingFeatureName(String reducingFeatureName) {
			Assert.notNull(reducingFeatureName, "reducingValueName cannot be null");
			this.reducingFeatureName = reducingFeatureName;
		}

		public double getReducingFactor() {
			return reducingFactor;
		}

		public void setReducingFactor(double reducingFactor) {
			Assert.isTrue(0 <= reducingFactor && reducingFactor <= 1, "reductionFactor must be in the range of [0,1]");
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

			if (!reducingFeatureName.equals(that.reducingFeatureName)) return false;
			if (Double.compare(that.reducingFactor, reducingFactor) != 0) return false;
			if (Double.compare(that.maxValueForFullyReduce, maxValueForFullyReduce) != 0) return false;
			if (Double.compare(that.minValueForNotReduce, minValueForNotReduce) != 0) return false;

			return true;
		}

		@Override
		public int hashCode() {
			return reducingFeatureName.hashCode();
		}
	}
}
