package fortscale.ml.model.builder.gaussian.prior;

import fortscale.ml.model.ContinuousDataModel;

import java.util.List;
import java.util.OptionalDouble;

public class GaussianPriorMaxAllowedValue implements GaussianPrior {
	@Override
	public Double calcPrior(List<ContinuousDataModel> models, double mean) {
		OptionalDouble maxValueOverModels = models.stream()
				.mapToDouble(ContinuousDataModel::getMaxValue)
				.max();
		if (!maxValueOverModels.isPresent()) {
			return null;
		}
		return maxValueOverModels.getAsDouble();
	}
}
