package fortscale.ml.model.builder.gaussian.prior;

import fortscale.ml.model.ContinuousDataModel;
import org.apache.commons.lang3.builder.EqualsBuilder;
import org.apache.commons.lang3.builder.HashCodeBuilder;

import java.util.List;

public interface Segmentor {
	class Segment {
		public double leftMean;
		public double rightMean;
		public List<ContinuousDataModel> models;

		public Segment(double leftMean,
					   double rightMean,
					   List<ContinuousDataModel> models) {
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
	}

	Segment createSegment(List<ContinuousDataModel> sortedModels, double segmentCenter);
}
