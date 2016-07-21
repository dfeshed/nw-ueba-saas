package fortscale.ml.model.builder.gaussian.prior;

import org.apache.commons.lang3.builder.HashCodeBuilder;

public interface Segmentor {
	class Segment {
		public double leftMean;
		public double rightMean;
		public int leftModelIndex;
		public int rightModelIndex;

		public Segment(double leftMean, double rightMean, int leftModelIndex, int rightModelIndex) {
			this.leftMean = leftMean;
			this.rightMean = rightMean;
			this.leftModelIndex = leftModelIndex;
			this.rightModelIndex = rightModelIndex;
		}

		@Override
		public boolean equals(Object obj) {
			if (obj == null || !(obj instanceof Segment)) {
				return false;
			}
			Segment o = (Segment) obj;
			return o.leftMean == leftMean &&
					o.rightMean == rightMean &&
					o.leftModelIndex == leftModelIndex &&
					o.rightModelIndex == rightModelIndex;
		}

		@Override
		public int hashCode() {
			return new HashCodeBuilder()
					.append(leftMean)
					.append(rightMean)
					.append(leftModelIndex)
					.append(rightModelIndex)
					.hashCode();
		}
	}

	Segment createSegment(double[] sortedMeans, double segmentCenter);
}
