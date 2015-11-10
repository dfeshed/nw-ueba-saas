package fortscale.ml.model;

import org.springframework.util.Assert;

import com.fasterxml.jackson.annotation.JsonAutoDetect;
import com.fasterxml.jackson.annotation.JsonAutoDetect.Visibility;
import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;

import fortscale.ml.model.builder.IModelBuilder;
import fortscale.ml.model.retriever.ModelBuilderDataRetriever;
import fortscale.ml.model.selector.ContextSelectorConf;

@JsonAutoDetect(fieldVisibility = Visibility.ANY, getterVisibility = Visibility.NONE, setterVisibility = Visibility.NONE)
public class ModelConf {
    private String name;
    private long buildIntervalInSeconds;
    @JsonProperty("selector")
    private ContextSelectorConf contextSelectorConf;
    private IModelBuilder modelBuilder;
    private ModelBuilderDataRetriever modelBuilderDataRetriever;
    private ModelStore modelStore;

    @JsonCreator
    public ModelConf(@JsonProperty("name") String name,
                     @JsonProperty("buildIntervalInSeconds") long buildIntervalInSeconds,
                     @JsonProperty("builder") IModelBuilder modelBuilder,
                     @JsonProperty("retriever") ModelBuilderDataRetriever modelBuilderDataRetriever,
                     @JsonProperty("store") ModelStore modelStore) {

        Assert.hasText(name);
        Assert.isTrue(buildIntervalInSeconds > 0);
        Assert.notNull(modelBuilder);
        Assert.notNull(modelBuilderDataRetriever);
        Assert.notNull(modelStore);

        this.name = name;
        this.buildIntervalInSeconds = buildIntervalInSeconds;
        this.modelBuilder = modelBuilder;
        this.modelBuilderDataRetriever = modelBuilderDataRetriever;
        this.modelStore = modelStore;
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

    public IModelBuilder getModelBuilder() {
        return modelBuilder;
    }

    public ModelBuilderDataRetriever getModelBuilderDataRetriever() {
        return modelBuilderDataRetriever;
    }

    public ModelStore getModelStore() {
        return modelStore;
    }
}
