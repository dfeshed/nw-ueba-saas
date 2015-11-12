package fortscale.ml.model;

import com.fasterxml.jackson.databind.JsonMappingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import fortscale.ml.model.builder.ContinuousHistogramModelBuilder;
import fortscale.ml.model.selector.FeatureBucketContextSelectorConf;

import org.junit.Assert;
import net.minidev.json.JSONObject;
import org.junit.Test;

import java.io.IOException;

public class ModelConfTest {
    private JSONObject buildModelConfJSON(String name,
                                          Integer buildIntervalInSeconds,
                                          JSONObject selector,
                                          JSONObject retriever,
                                          JSONObject builder,
                                          JSONObject store) {

        JSONObject result = new JSONObject();
        if (name != null) {
            result.put("name", name);
        }
        if (buildIntervalInSeconds != null) {
            result.put("buildIntervalInSeconds", buildIntervalInSeconds);
        }
        if (selector != null) {
            result.put("selector", selector);
        }
        if (retriever != null) {
            result.put("retriever", retriever);
        }
        if (builder != null) {
            result.put("builder", builder);
        }
        if (store != null) {
            result.put("store", store);
        }

        return result;
    }

    private JSONObject buildSelectorJSON() {
    	JSONObject jsonObject = new JSONObject();
    	jsonObject.put("type", "feature_bucket_context_selector_conf");
    	jsonObject.put(FeatureBucketContextSelectorConf.FEATURE_BUCKET_CONF_NAME_PROPERTY, "featureBucketConfName1");
    	return jsonObject;
    }

    private JSONObject buildRetrieverJSON() {
        JSONObject json = new JSONObject();
        json.put("type", "entity_histogram_retriever");
        return json;
    }

    private JSONObject buildBuilderJSON(String type) {
        if (type == null) {
            type = "continuous_data_histogram";
        }

        JSONObject result = new JSONObject();
        result.put("type", type);

        return result;
    }

    private JSONObject buildStoreJSON() {
        return new JSONObject();
    }

    @Test
    public void shouldCreateModelConfWithTheGivenName() throws IOException {
        String name = "some name";
        JSONObject modelConfJSON = buildModelConfJSON(name,
                1,
                buildSelectorJSON(),
                buildRetrieverJSON(),
                buildBuilderJSON(null),
                buildStoreJSON());
        ModelConf modelConf = (new ObjectMapper()).readValue(modelConfJSON.toJSONString(), ModelConf.class);
        Assert.assertEquals(name, modelConf.getName());
    }

    @Test
    public void shouldCreateModelConfWithTheGivenBuildIntervalInSeconds() throws IOException {
        int buildIntervalInSeconds = 12345;
        JSONObject modelConfJSON = buildModelConfJSON("some name",
                buildIntervalInSeconds,
                buildSelectorJSON(),
                buildRetrieverJSON(),
                buildBuilderJSON(null),
                buildStoreJSON());
        ModelConf modelConf = (new ObjectMapper()).readValue(modelConfJSON.toJSONString(), ModelConf.class);
        Assert.assertEquals(buildIntervalInSeconds, modelConf.getBuildIntervalInSeconds());
    }

    @Test
    public void shouldCreateModelConfWithTheGivenModelBuilder() throws IOException {
        JSONObject modelConfJSON = buildModelConfJSON("some name",
                1,
                buildSelectorJSON(),
                buildRetrieverJSON(),
                buildBuilderJSON("continuous_data_histogram"),
                buildStoreJSON());
        ModelConf modelConf = (new ObjectMapper()).readValue(modelConfJSON.toJSONString(), ModelConf.class);
        Assert.assertTrue(modelConf.getModelBuilder() instanceof ContinuousHistogramModelBuilder);
    }

    @Test(expected = Exception.class)
    public void shouldFailIfNameNotGiven() throws IOException {
        JSONObject modelConfJSON = buildModelConfJSON(null,
                1,
                buildSelectorJSON(),
                buildRetrieverJSON(),
                buildBuilderJSON(null),
                buildStoreJSON());
        (new ObjectMapper()).readValue(modelConfJSON.toJSONString(), ModelConf.class);
    }
    
    @Test
    public void shouldNotFailIfSelectorNotGiven() throws IOException {
        JSONObject modelConfJSON = buildModelConfJSON("some name",
                1,
                null,
                buildRetrieverJSON(),
                buildBuilderJSON("continuous_data_histogram"),
                buildStoreJSON());
        (new ObjectMapper()).readValue(modelConfJSON.toJSONString(), ModelConf.class);
    }

    @Test(expected = Exception.class)
    public void shouldFailIfBuildIntervalInSecondsNotGiven() throws IOException {
        JSONObject modelConfJSON = buildModelConfJSON("some name",
                null,
                buildSelectorJSON(),
                buildRetrieverJSON(),
                buildBuilderJSON(null),
                buildStoreJSON());
        (new ObjectMapper()).readValue(modelConfJSON.toJSONString(), ModelConf.class);
    }

    @Test(expected = Exception.class)
    public void shouldFailIfRetrieverNotGiven() throws IOException {
        JSONObject modelConfJSON = buildModelConfJSON("some name",
                1,
                buildSelectorJSON(),
                null,
                buildBuilderJSON(null),
                buildStoreJSON());
        (new ObjectMapper()).readValue(modelConfJSON.toJSONString(), ModelConf.class);
    }

    @Test(expected = Exception.class)
    public void shouldFailIfBuilderNotGiven() throws IOException {
        JSONObject modelConfJSON = buildModelConfJSON("some name",
                1,
                buildSelectorJSON(),
                buildRetrieverJSON(),
                null,
                buildStoreJSON());
        (new ObjectMapper()).readValue(modelConfJSON.toJSONString(), ModelConf.class);
    }

    @Test(expected = Exception.class)
    public void shouldFailIfStoreNotGiven() throws IOException {
        JSONObject modelConfJSON = buildModelConfJSON("some name",
                1,
                buildSelectorJSON(),
                buildRetrieverJSON(),
                buildBuilderJSON(null),
                null);
        (new ObjectMapper()).readValue(modelConfJSON.toJSONString(), ModelConf.class);
    }

    @Test(expected = Exception.class)
    public void shouldFailIfBuildIntervalInSecondsIsNegative() throws IOException {
        JSONObject modelConfJSON = buildModelConfJSON("some name",
                -1,
                buildSelectorJSON(),
                buildRetrieverJSON(),
                buildBuilderJSON(null),
                buildStoreJSON());
        (new ObjectMapper()).readValue(modelConfJSON.toJSONString(), ModelConf.class);
    }

    @Test(expected = JsonMappingException.class)
    public void shouldFailIfUnknownModelBuilderType() throws IOException {
        JSONObject modelConfJSON = buildModelConfJSON("some name",
                1,
                buildSelectorJSON(),
                buildRetrieverJSON(),
                buildBuilderJSON("unknown model builder type"),
                buildStoreJSON());
        (new ObjectMapper()).readValue(modelConfJSON.toJSONString(), ModelConf.class);
    }
}
