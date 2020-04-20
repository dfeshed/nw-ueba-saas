package fortscale.ml.model.builder;

import fortscale.ml.model.ContextModel;
import fortscale.ml.model.Model;

public class ContextModelBuilder implements IModelBuilder {
    private ContextModelBuilderConf config;

    public ContextModelBuilder(ContextModelBuilderConf config) {
        this.config = config;
    }

    @Override
    public Model build(Object modelBuilderData) {
        long numOfContexts = (long) modelBuilderData;
        return new ContextModel(numOfContexts);
    }

}
