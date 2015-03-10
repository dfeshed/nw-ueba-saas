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
		private String reducingValueName;
		private double reductionFactor;
		private double maxValueForFullReduction;
		private double minValueForNoReduction;

		public String getReducingValueName() {
			return reducingValueName;
		}

		public void setReducingValueName(String reducingValueName) {
			Assert.notNull(reducingValueName, "reducingValueName cannot be null");
			this.reducingValueName = reducingValueName;
		}

		public double getReductionFactor() {
			return reductionFactor;
		}

		public void setReductionFactor(double reductionFactor) {
			Assert.isTrue(0 <= reductionFactor && reductionFactor <= 1, "reductionFactor must be in the range of [0,1]");
			this.reductionFactor = reductionFactor;
		}

		public double getMaxValueForFullReduction() {
			return maxValueForFullReduction;
		}

		public void setMaxValueForFullReduction(double maxValueForFullReduction) {
			this.maxValueForFullReduction = maxValueForFullReduction;
		}

		public double getMinValueForNoReduction() {
			return minValueForNoReduction;
		}

		public void setMinValueForNoReduction(double minValueForNoReduction) {
			this.minValueForNoReduction = minValueForNoReduction;
		}

		@Override
		public boolean equals(Object o) {
			if (this == o) return true;
			if (o == null || getClass() != o.getClass()) return false;

			ReductionConfiguration that = (ReductionConfiguration)o;

			if (!reducingValueName.equals(that.reducingValueName)) return false;
			if (Double.compare(that.reductionFactor, reductionFactor) != 0) return false;
			if (Double.compare(that.maxValueForFullReduction, maxValueForFullReduction) != 0) return false;
			if (Double.compare(that.minValueForNoReduction, minValueForNoReduction) != 0) return false;

			return true;
		}

		@Override
		public int hashCode() {
			int result;
			long temp;
			result = reducingValueName.hashCode();
			temp = Double.doubleToLongBits(reductionFactor);
			result = 31 * result + (int)(temp ^ (temp >>> 32));
			temp = Double.doubleToLongBits(maxValueForFullReduction);
			result = 31 * result + (int)(temp ^ (temp >>> 32));
			temp = Double.doubleToLongBits(minValueForNoReduction);
			result = 31 * result + (int)(temp ^ (temp >>> 32));
			return result;
		}
	}
}
