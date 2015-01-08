package fortscale.streaming.service;

import fortscale.ml.model.prevalance.PrevalanceModel;
import fortscale.ml.model.prevalance.PrevalanceModelBuilder;
import fortscale.ml.model.prevalance.PrevalanceModelBuilderImpl;

public class NullPrevalanceModelBuilder implements PrevalanceModelBuilder {

	@Override
	public PrevalanceModelBuilderImpl withField(String fieldName,
			String fieldModelClassName, String scoreBoostClassName) {
		throw new PrevalanceModelBuilderWasNotSetException();
	}

	@Override
	public PrevalanceModel build() throws Exception {
		throw new PrevalanceModelBuilderWasNotSetException();
	}

	@Override
	public String getModelName() {
		throw new PrevalanceModelBuilderWasNotSetException();
	}

}
