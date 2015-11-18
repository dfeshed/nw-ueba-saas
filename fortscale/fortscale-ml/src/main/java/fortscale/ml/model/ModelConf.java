package fortscale.ml.model;

import com.fasterxml.jackson.annotation.JsonAutoDetect;
import com.fasterxml.jackson.annotation.JsonAutoDetect.Visibility;
import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;
import fortscale.ml.model.builder.IModelBuilderConf;
import fortscale.ml.model.retriever.IDataRetrieverConf;
import fortscale.ml.model.selector.ContextSelectorConf;
import org.springframework.util.Assert;

@JsonAutoDetect(fieldVisibility = Visibility.ANY, getterVisibility = Visibility.NONE, setterVisibility = Visibility.NONE)
public class ModelConf {
    @JsonProperty("name")
    private String name;
    @JsonProperty("buildIntervalInSeconds")
    private long buildIntervalInSeconds;
    @JsonProperty("selector")
    private ContextSelectorConf contextSelectorConf;
    @JsonProperty("retriever")
    private IDataRetrieverConf dataRetrieverConf;
    @JsonProperty("builder")
    private IModelBuilderConf modelBuilderConf;

    @JsonCreator
    public ModelConf(@JsonProperty("name") String name,
                     @JsonProperty("buildIntervalInSeconds") long buildIntervalInSeconds,
                     @JsonProperty("retriever") IDataRetrieverConf dataRetrieverConf,
                     @JsonProperty("builder") IModelBuilderConf modelBuilderConf) {

        Assert.hasText(name);
        Assert.isTrue(buildIntervalInSeconds > 0);
        Assert.notNull(dataRetrieverConf);
        Assert.notNull(modelBuilderConf);

        this.name = name;
        this.buildIntervalInSeconds = buildIntervalInSeconds;
        this.dataRetrieverConf = dataRetrieverConf;
        this.modelBuilderConf = modelBuilderConf;
    }

    public String getName() {
        return name;
    }

    public long getBuildIntervalInSeconds() {
        return buildIntervalInSeconds;
    }

    public ContextSelectorConf getContextSelectorConf() {
        return contextSelectorConf;
    }

    public IDataRetrieverConf getDataRetrieverConf() {
        return dataRetrieverConf;
    }

    public IModelBuilderConf getModelBuilderConf() {
        return modelBuilderConf;
    }
}
