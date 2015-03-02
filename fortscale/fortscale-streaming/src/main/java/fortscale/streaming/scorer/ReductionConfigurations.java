package fortscale.streaming.scorer;

import java.util.ArrayList;
import java.util.List;

public class ReductionConfigurations {
	private List<ReductionConfiguration> reductionConfigs = new ArrayList<>();

	public List<ReductionConfiguration> getReductionConfigs() {
		return reductionConfigs;
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

		public ReductionConfiguration(String reducingValueName, double reductionFactor, double maxValueForFullReduction, double minValueForNoReduction) {
			this.reducingValueName = reducingValueName;
			this.reductionFactor = reductionFactor;
			this.maxValueForFullReduction = maxValueForFullReduction;
			this.minValueForNoReduction = minValueForNoReduction;
		}

		public String getReducingValueName() {
			return reducingValueName;
		}

		public double getReductionFactor() {
			return reductionFactor;
		}

		public double getMaxValueForFullReduction() {
			return maxValueForFullReduction;
		}

		public double getMinValueForNoReduction() {
			return minValueForNoReduction;
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
