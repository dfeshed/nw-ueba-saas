package fortscale.ml.model.builder.gaussian.prior;

import fortscale.ml.model.IContinuousDataModel;
import org.apache.commons.lang.builder.ToStringBuilder;
import org.apache.commons.lang3.builder.EqualsBuilder;
import org.apache.commons.lang3.builder.HashCodeBuilder;

import java.util.List;

/**
 * Full documentation can be found here: https://fortscale.atlassian.net/wiki/display/FSC/Gaussian+model
 */
public interface Segmentor {
	class Segment {
		public double leftMean;
		public double rightMean;
		public List<IContinuousDataModel> models;

		public Segment(double leftMean,
					   double rightMean,
					   List<IContinuousDataModel> models) {
			this.leftMean = leftMean;
			this.rightMean = rightMean;
			this.models = models;
		}

		@Override
		public boolean equals(Object obj) {
			if (obj == null || !(obj instanceof Segment)) {
				return false;
			}
			Segment o = (Segment) obj;
			return new EqualsBuilder()
					.append(o.leftMean, leftMean)
					.append(o.rightMean, rightMean)
					.append(o.models, models)
					.isEquals();
		}

		@Override
		public int hashCode() {
			return new HashCodeBuilder()
					.append(leftMean)
					.append(rightMean)
					.append(models)
  					.hashCode();
		}

		/**
		 * @return ToString you know...
		 */
		@Override
		public String toString() {
			return ToStringBuilder.reflectionToString(this);
		}
	}

	Segment createSegment(List<IContinuousDataModel> sortedModels, double segmentCenter);
}
