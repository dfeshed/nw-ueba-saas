package fortscale.ml.model;

import com.fasterxml.jackson.annotation.JsonAutoDetect;
import com.fasterxml.jackson.annotation.JsonAutoDetect.Visibility;
import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;
import fortscale.ml.model.builder.IModelBuilderConf;
import fortscale.ml.model.retriever.AbstractDataRetrieverConf;
import fortscale.ml.model.selector.IContextSelectorConf;
import org.springframework.util.Assert;

@JsonAutoDetect(fieldVisibility = Visibility.ANY, getterVisibility = Visibility.NONE, setterVisibility = Visibility.NONE)
public class ModelConf {
    @JsonProperty("name")
    private String name;
    @JsonProperty("selector")
    private IContextSelectorConf contextSelectorConf;
    @JsonProperty("retriever")
    private AbstractDataRetrieverConf dataRetrieverConf;
    @JsonProperty("builder")
    private IModelBuilderConf modelBuilderConf;

    @JsonCreator
    public ModelConf(@JsonProperty("name") String name,
                     @JsonProperty("retriever") AbstractDataRetrieverConf dataRetrieverConf,
                     @JsonProperty("selector") IContextSelectorConf contextSelectorConf,
                     @JsonProperty("builder") IModelBuilderConf modelBuilderConf) {

        Assert.hasText(name);
        Assert.notNull(dataRetrieverConf);
        Assert.notNull(modelBuilderConf);

        this.name = name;
        this.dataRetrieverConf = dataRetrieverConf;
        this.contextSelectorConf = contextSelectorConf;
        this.modelBuilderConf = modelBuilderConf;
    }

    public String getName() {
        return name;
    }

    public IContextSelectorConf getContextSelectorConf() {
        return contextSelectorConf;
    }

    public AbstractDataRetrieverConf getDataRetrieverConf() {
        return dataRetrieverConf;
    }

    public IModelBuilderConf getModelBuilderConf() {
        return modelBuilderConf;
    }
}
