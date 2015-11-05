package fortscale.ml.model;

import com.fasterxml.jackson.annotation.JsonAutoDetect;
import com.fasterxml.jackson.annotation.JsonAutoDetect.Visibility;
import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.databind.ObjectMapper;
import fortscale.ml.model.builder.IModelBuilder;
import fortscale.ml.model.retriever.DataRetriever;
import fortscale.ml.model.selector.EntitiesSelector;
import fortscale.utils.logging.Logger;
import net.minidev.json.JSONObject;
import org.eclipse.jdt.internal.core.Assert;

import java.io.IOException;

@JsonAutoDetect(fieldVisibility = Visibility.NONE, getterVisibility = Visibility.ANY, setterVisibility = Visibility.ANY, isGetterVisibility = Visibility.ANY)
public class ModelConf {
    private static final Logger logger = Logger.getLogger(ModelConf.class);

    public String name;
    public long buildIntervalInSeconds;
    public EntitiesSelector entitiesSelector;
    public IModelBuilder modelBuilder;
    public DataRetriever dataRetriever;
    public ModelStore modelStore;

    @JsonCreator
    public ModelConf(@JsonProperty("name") String name,
                     @JsonProperty("buildIntervalInSeconds") long buildIntervalInSeconds,
                     @JsonProperty("selector") JSONObject selector,
                     @JsonProperty("builder") JSONObject builder,
                     @JsonProperty("retriever") JSONObject retriever,
                     @JsonProperty("store") JSONObject store) {
        Assert.isNotNull(name);
        Assert.isTrue(buildIntervalInSeconds > 0);

        this.name = name;
        this.buildIntervalInSeconds = buildIntervalInSeconds;

        String jsonString = null;
        try {
            jsonString = selector.toJSONString();
            entitiesSelector = (new ObjectMapper()).readValue(jsonString, EntitiesSelector.class);
            jsonString = builder.toJSONString();
            modelBuilder = (new ObjectMapper()).readValue(jsonString, IModelBuilder.class);
            jsonString = retriever.toJSONString();
            dataRetriever = (new ObjectMapper()).readValue(jsonString, DataRetriever.class);
            jsonString = store.toJSONString();
            modelStore = (new ObjectMapper()).readValue(jsonString, ModelStore.class);
        } catch (IOException e) {
            String errorMsg = String.format("Failed to deserialize JSON %s", jsonString);
            logger.error(errorMsg, e);
            throw new RuntimeException(errorMsg, e);
        }

    }
}
