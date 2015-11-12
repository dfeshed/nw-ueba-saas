package fortscale.ml.model;

import org.springframework.util.Assert;

import com.fasterxml.jackson.annotation.JsonAutoDetect;
import com.fasterxml.jackson.annotation.JsonAutoDetect.Visibility;
import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;

import fortscale.ml.model.builder.IModelBuilder;
import fortscale.ml.model.retriever.IDataRetriever;
import fortscale.ml.model.selector.ContextSelectorConf;
import fortscale.ml.model.store.ModelStore;


@JsonAutoDetect(fieldVisibility = Visibility.ANY, getterVisibility = Visibility.NONE, setterVisibility = Visibility.NONE)
public class ModelConf {
    private String name;
    private long buildIntervalInSeconds;
    @JsonProperty("selector")
    private ContextSelectorConf contextSelectorConf;
    private IModelBuilder modelBuilder;
    private IDataRetriever dataRetriever;
    private ModelStore modelStore;

    @JsonCreator
    public ModelConf(@JsonProperty("name") String name,
                     @JsonProperty("buildIntervalInSeconds") long buildIntervalInSeconds,
                     @JsonProperty("builder") IModelBuilder modelBuilder,
                     @JsonProperty("retriever") IDataRetriever dataRetriever,
                     @JsonProperty("store") ModelStore modelStore) {

        Assert.hasText(name);
        Assert.isTrue(buildIntervalInSeconds > 0);
        Assert.notNull(modelBuilder);
        Assert.notNull(dataRetriever);
        Assert.notNull(modelStore);

        this.name = name;
        this.buildIntervalInSeconds = buildIntervalInSeconds;
        this.modelBuilder = modelBuilder;
        this.dataRetriever = dataRetriever;
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

    public IDataRetriever getDataRetriever() {
        return dataRetriever;
    }

    public ModelStore getModelStore() {
        return modelStore;
    }
}
