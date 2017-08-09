package fortscale.ml.model;

import com.fasterxml.jackson.databind.JsonMappingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import fortscale.ml.model.builder.gaussian.ContinuousHistogramModelBuilderConf;
import fortscale.ml.model.retriever.ContextHistogramRetrieverConf;
import fortscale.ml.model.selector.FeatureBucketContextSelectorConf;
import net.minidev.json.JSONArray;
import net.minidev.json.JSONObject;
import org.junit.Assert;
import org.junit.Test;

import java.io.IOException;

public class ModelConfTest {
    private JSONObject buildModelConfJSON(String name, JSONObject selector, JSONObject retriever, JSONObject builder) {

        JSONObject result = new JSONObject();
        if (name != null) {
            result.put("name", name);
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

        return result;
    }

    private JSONObject buildSelectorJSON() {
        JSONObject jsonObject = new JSONObject();
        jsonObject.put("type", FeatureBucketContextSelectorConf.FEATURE_BUCKET_CONTEXT_SELECTOR);
        jsonObject.put(FeatureBucketContextSelectorConf.FEATURE_BUCKET_CONF_NAME_PROPERTY, "featureBucketConfName1");
        return jsonObject;
    }

    private JSONObject buildRetrieverJSON() {
        JSONObject json = new JSONObject();
        json.put("type", ContextHistogramRetrieverConf.CONTEXT_HISTOGRAM_RETRIEVER);
        json.put("timeRangeInSeconds", 1);
        json.put("functions", new JSONArray());
        json.put("featureBucketConfName", "featureBucketConfName1");
        json.put("featureName", "featureName1");
        return json;
    }

    private JSONObject buildBuilderJSON(String type) {
        if (type == null) {
            type = ContinuousHistogramModelBuilderConf.CONTINUOUS_HISTOGRAM_MODEL_BUILDER;
        }

        JSONObject result = new JSONObject();
        result.put("type", type);

        return result;
    }

    @Test
    public void shouldCreateModelConfWithTheGivenName() throws IOException {
        String name = "some name";
        JSONObject modelConfJSON = buildModelConfJSON(
                name,
                buildSelectorJSON(),
                buildRetrieverJSON(),
                buildBuilderJSON(null));
        ModelConf modelConf = (new ObjectMapper()).readValue(modelConfJSON.toJSONString(), ModelConf.class);
        Assert.assertEquals(name, modelConf.getName());
    }

    @Test
    public void shouldCreateModelConfWithTheGivenModelBuilder() throws IOException {
        JSONObject modelConfJSON = buildModelConfJSON(
                "some name",
                buildSelectorJSON(),
                buildRetrieverJSON(),
                buildBuilderJSON(ContinuousHistogramModelBuilderConf.CONTINUOUS_HISTOGRAM_MODEL_BUILDER));
        ModelConf modelConf = (new ObjectMapper()).readValue(modelConfJSON.toJSONString(), ModelConf.class);
        Assert.assertTrue(modelConf.getModelBuilderConf() instanceof ContinuousHistogramModelBuilderConf);
    }

    @Test(expected = Exception.class)
    public void shouldFailIfNameNotGiven() throws IOException {
        JSONObject modelConfJSON = buildModelConfJSON(
                null,
                buildSelectorJSON(),
                buildRetrieverJSON(),
                buildBuilderJSON(null));
        (new ObjectMapper()).readValue(modelConfJSON.toJSONString(), ModelConf.class);
    }

    @Test
    public void shouldNotFailIfSelectorNotGiven() throws IOException {
        JSONObject modelConfJSON = buildModelConfJSON(
                "some name",
                null,
                buildRetrieverJSON(),
                buildBuilderJSON(ContinuousHistogramModelBuilderConf.CONTINUOUS_HISTOGRAM_MODEL_BUILDER));
        (new ObjectMapper()).readValue(modelConfJSON.toJSONString(), ModelConf.class);
    }

    @Test(expected = Exception.class)
    public void shouldFailIfRetrieverNotGiven() throws IOException {
        JSONObject modelConfJSON = buildModelConfJSON(
                "some name",
                buildSelectorJSON(),
                null,
                buildBuilderJSON(null));
        (new ObjectMapper()).readValue(modelConfJSON.toJSONString(), ModelConf.class);
    }

    @Test(expected = Exception.class)
    public void shouldFailIfBuilderNotGiven() throws IOException {
        JSONObject modelConfJSON = buildModelConfJSON(
                "some name",
                buildSelectorJSON(),
                buildRetrieverJSON(),
                null);
        (new ObjectMapper()).readValue(modelConfJSON.toJSONString(), ModelConf.class);
    }

    @Test(expected = JsonMappingException.class)
    public void shouldFailIfUnknownModelBuilderType() throws IOException {
        JSONObject modelConfJSON = buildModelConfJSON(
                "some name",
                buildSelectorJSON(),
                buildRetrieverJSON(),
                buildBuilderJSON("unknown model builder type"));
        (new ObjectMapper()).readValue(modelConfJSON.toJSONString(), ModelConf.class);
    }
}
