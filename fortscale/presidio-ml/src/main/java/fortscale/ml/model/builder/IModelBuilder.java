package fortscale.ml.model.builder;

import fortscale.ml.model.Model;

public interface IModelBuilder {
	public Model build(Object modelBuilderData);
}
