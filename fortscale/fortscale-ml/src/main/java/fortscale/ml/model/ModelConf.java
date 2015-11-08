package fortscale.ml.model;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;
import fortscale.ml.model.builder.IModelBuilder;
import fortscale.ml.model.retriever.ModelBuilderDataRetriever;
import fortscale.ml.model.selector.EntitiesSelector;
import org.springframework.util.Assert;

public class ModelConf {
    private String name;
    private long buildIntervalInSeconds;
    private EntitiesSelector entitiesSelector;
    private IModelBuilder modelBuilder;
    private ModelBuilderDataRetriever modelBuilderDataRetriever;
    private ModelStore modelStore;

    @JsonCreator
    public ModelConf(@JsonProperty("name") String name,
                     @JsonProperty("buildIntervalInSeconds") long buildIntervalInSeconds,
                     @JsonProperty("selector") EntitiesSelector entitiesSelector,
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
        this.entitiesSelector = entitiesSelector;
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

    public EntitiesSelector getEntitiesSelector() {
        return entitiesSelector;
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
