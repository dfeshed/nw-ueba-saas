package fortscale.utils.transform.stringformat;

import com.fasterxml.jackson.databind.ObjectMapper;
import fortscale.utils.transform.IJsonObjectTransformer;
import org.json.JSONArray;
import org.json.JSONObject;
import org.junit.Assert;
import org.junit.Test;

public class StringFormatTransformerTest {
    private static final ObjectMapper objectMapper = new ObjectMapper();
    private static final Scenario[] scenarios = new Scenario[]{
            // From lower underscore
            new Scenario("LOWER_UNDERSCORE", "LOWER_UNDERSCORE", "hello_world", "hello_world"),
            new Scenario("LOWER_UNDERSCORE", "UPPER_UNDERSCORE", "hello_world", "HELLO_WORLD"),
            new Scenario("LOWER_UNDERSCORE", "LOWER_CAMEL", "hello_world", "helloWorld"),
            new Scenario("LOWER_UNDERSCORE", "UPPER_CAMEL", "hello_world", "HelloWorld"),
            new Scenario("LOWER_UNDERSCORE", "LOWER_HYPHEN", "hello_world", "hello-world"),
            new Scenario("LOWER_UNDERSCORE", "UPPER_HYPHEN", "hello_world", "HELLO-WORLD"),
            new Scenario("LOWER_UNDERSCORE", "LOWER_SPACE", "hello_world", "hello world"),
            new Scenario("LOWER_UNDERSCORE", "UPPER_SPACE", "hello_world", "HELLO WORLD"),
            // From upper underscore
            new Scenario("UPPER_UNDERSCORE", "LOWER_UNDERSCORE", "HELLO_WORLD", "hello_world"),
            new Scenario("UPPER_UNDERSCORE", "UPPER_UNDERSCORE", "HELLO_WORLD", "HELLO_WORLD"),
            new Scenario("UPPER_UNDERSCORE", "LOWER_CAMEL", "HELLO_WORLD", "helloWorld"),
            new Scenario("UPPER_UNDERSCORE", "UPPER_CAMEL", "HELLO_WORLD", "HelloWorld"),
            new Scenario("UPPER_UNDERSCORE", "LOWER_HYPHEN", "HELLO_WORLD", "hello-world"),
            new Scenario("UPPER_UNDERSCORE", "UPPER_HYPHEN", "HELLO_WORLD", "HELLO-WORLD"),
            new Scenario("UPPER_UNDERSCORE", "LOWER_SPACE", "HELLO_WORLD", "hello world"),
            new Scenario("UPPER_UNDERSCORE", "UPPER_SPACE", "HELLO_WORLD", "HELLO WORLD"),
            // From lower camel
            new Scenario("LOWER_CAMEL", "LOWER_UNDERSCORE", "helloWorld", "hello_world"),
            new Scenario("LOWER_CAMEL", "UPPER_UNDERSCORE", "helloWorld", "HELLO_WORLD"),
            new Scenario("LOWER_CAMEL", "LOWER_CAMEL", "helloWorld", "helloWorld"),
            new Scenario("LOWER_CAMEL", "UPPER_CAMEL", "helloWorld", "HelloWorld"),
            new Scenario("LOWER_CAMEL", "LOWER_HYPHEN", "helloWorld", "hello-world"),
            new Scenario("LOWER_CAMEL", "UPPER_HYPHEN", "helloWorld", "HELLO-WORLD"),
            new Scenario("LOWER_CAMEL", "LOWER_SPACE", "helloWorld", "hello world"),
            new Scenario("LOWER_CAMEL", "UPPER_SPACE", "helloWorld", "HELLO WORLD"),
            // From upper camel
            new Scenario("UPPER_CAMEL", "LOWER_UNDERSCORE", "HelloWorld", "hello_world"),
            new Scenario("UPPER_CAMEL", "UPPER_UNDERSCORE", "HelloWorld", "HELLO_WORLD"),
            new Scenario("UPPER_CAMEL", "LOWER_CAMEL", "HelloWorld", "helloWorld"),
            new Scenario("UPPER_CAMEL", "UPPER_CAMEL", "HelloWorld", "HelloWorld"),
            new Scenario("UPPER_CAMEL", "LOWER_HYPHEN", "HelloWorld", "hello-world"),
            new Scenario("UPPER_CAMEL", "UPPER_HYPHEN", "HelloWorld", "HELLO-WORLD"),
            new Scenario("UPPER_CAMEL", "LOWER_SPACE", "HelloWorld", "hello world"),
            new Scenario("UPPER_CAMEL", "UPPER_SPACE", "HelloWorld", "HELLO WORLD"),
            // From lower hyphen
            new Scenario("LOWER_HYPHEN", "LOWER_UNDERSCORE", "hello-world", "hello_world"),
            new Scenario("LOWER_HYPHEN", "UPPER_UNDERSCORE", "hello-world", "HELLO_WORLD"),
            new Scenario("LOWER_HYPHEN", "LOWER_CAMEL", "hello-world", "helloWorld"),
            new Scenario("LOWER_HYPHEN", "UPPER_CAMEL", "hello-world", "HelloWorld"),
            new Scenario("LOWER_HYPHEN", "LOWER_HYPHEN", "hello-world", "hello-world"),
            new Scenario("LOWER_HYPHEN", "UPPER_HYPHEN", "hello-world", "HELLO-WORLD"),
            new Scenario("LOWER_HYPHEN", "LOWER_SPACE", "hello-world", "hello world"),
            new Scenario("LOWER_HYPHEN", "UPPER_SPACE", "hello-world", "HELLO WORLD"),
            // From upper hyphen
            new Scenario("UPPER_HYPHEN", "LOWER_UNDERSCORE", "HELLO-WORLD", "hello_world"),
            new Scenario("UPPER_HYPHEN", "UPPER_UNDERSCORE", "HELLO-WORLD", "HELLO_WORLD"),
            new Scenario("UPPER_HYPHEN", "LOWER_CAMEL", "HELLO-WORLD", "helloWorld"),
            new Scenario("UPPER_HYPHEN", "UPPER_CAMEL", "HELLO-WORLD", "HelloWorld"),
            new Scenario("UPPER_HYPHEN", "LOWER_HYPHEN", "HELLO-WORLD", "hello-world"),
            new Scenario("UPPER_HYPHEN", "UPPER_HYPHEN", "HELLO-WORLD", "HELLO-WORLD"),
            new Scenario("UPPER_HYPHEN", "LOWER_SPACE", "HELLO-WORLD", "hello world"),
            new Scenario("UPPER_HYPHEN", "UPPER_SPACE", "HELLO-WORLD", "HELLO WORLD"),
            // From lower space
            new Scenario("LOWER_SPACE", "LOWER_UNDERSCORE", "hello world", "hello_world"),
            new Scenario("LOWER_SPACE", "UPPER_UNDERSCORE", "hello world", "HELLO_WORLD"),
            new Scenario("LOWER_SPACE", "LOWER_CAMEL", "hello world", "helloWorld"),
            new Scenario("LOWER_SPACE", "UPPER_CAMEL", "hello world", "HelloWorld"),
            new Scenario("LOWER_SPACE", "LOWER_HYPHEN", "hello world", "hello-world"),
            new Scenario("LOWER_SPACE", "UPPER_HYPHEN", "hello world", "HELLO-WORLD"),
            new Scenario("LOWER_SPACE", "LOWER_SPACE", "hello world", "hello world"),
            new Scenario("LOWER_SPACE", "UPPER_SPACE", "hello world", "HELLO WORLD"),
            // From upper space
            new Scenario("UPPER_SPACE", "LOWER_UNDERSCORE", "HELLO WORLD", "hello_world"),
            new Scenario("UPPER_SPACE", "UPPER_UNDERSCORE", "HELLO WORLD", "HELLO_WORLD"),
            new Scenario("UPPER_SPACE", "LOWER_CAMEL", "HELLO WORLD", "helloWorld"),
            new Scenario("UPPER_SPACE", "UPPER_CAMEL", "HELLO WORLD", "HelloWorld"),
            new Scenario("UPPER_SPACE", "LOWER_HYPHEN", "HELLO WORLD", "hello-world"),
            new Scenario("UPPER_SPACE", "UPPER_HYPHEN", "HELLO WORLD", "HELLO-WORLD"),
            new Scenario("UPPER_SPACE", "LOWER_SPACE", "HELLO WORLD", "hello world"),
            new Scenario("UPPER_SPACE", "UPPER_SPACE", "HELLO WORLD", "HELLO WORLD")
    };

    @Test
    public void test_string_format_transformer() {
        Assert.assertEquals(StringFormat.values().length, Math.sqrt(scenarios.length), 0);
        JSONObject configuration = new JSONObject();
        configuration.put("type", "string_format");
        configuration.put("name", "test-string-format-transformer");
        configuration.put("sourceKey", "before");
        configuration.put("targetKey", "after");
        JSONObject jsonObject = new JSONObject();

        for (Scenario scenario : scenarios) {
            configuration.put("sourceStringFormat", scenario.sourceStringFormat);
            configuration.put("targetStringFormat", scenario.targetStringFormat);
            IJsonObjectTransformer jsonObjectTransformer = deserializeConfiguration(configuration);
            jsonObject.put("before", scenario.sourceValue);
            jsonObject = jsonObjectTransformer.transform(jsonObject);
            Assert.assertEquals(scenario.targetValue, jsonObject.get("after"));
        }
    }

    @Test
    public void test_string_format_transformer_when_source_value_is_null() {
        JSONObject configuration = new JSONObject();
        configuration.put("type", "string_format");
        configuration.put("name", "test-string-format-transformer");
        configuration.put("sourceKey", "before");
        configuration.put("targetKey", "after");
        configuration.put("sourceStringFormat", "LOWER_UNDERSCORE");
        configuration.put("targetStringFormat", "UPPER_UNDERSCORE");
        IJsonObjectTransformer jsonObjectTransformer = deserializeConfiguration(configuration);
        JSONObject jsonObject = new JSONObject();
        jsonObject.put("before", JSONObject.NULL);
        jsonObject = jsonObjectTransformer.transform(jsonObject);
        Assert.assertEquals(JSONObject.NULL, jsonObject.get("after"));
    }

    @Test
    public void test_string_format_transformer_when_source_value_is_list() {
        JSONObject configuration = new JSONObject();
        configuration.put("type", "string_format");
        configuration.put("name", "test-string-format-transformer");
        configuration.put("sourceKey", "before");
        configuration.put("targetKey", "after");
        configuration.put("sourceStringFormat", "LOWER_UNDERSCORE");
        configuration.put("targetStringFormat", "UPPER_UNDERSCORE");
        IJsonObjectTransformer jsonObjectTransformer = deserializeConfiguration(configuration);
        JSONArray jsonArray = new JSONArray();
        jsonArray.put("hello_world");
        jsonArray.put("hello_galaxy");
        jsonArray.put("hello_universe");
        JSONObject jsonObject = new JSONObject();
        jsonObject.put("before", jsonArray);
        jsonObject = jsonObjectTransformer.transform(jsonObject);
        Assert.assertEquals("HELLO_WORLD", jsonObject.getJSONArray("after").get(0));
        Assert.assertEquals("HELLO_GALAXY", jsonObject.getJSONArray("after").get(1));
        Assert.assertEquals("HELLO_UNIVERSE", jsonObject.getJSONArray("after").get(2));
    }

    private static IJsonObjectTransformer deserializeConfiguration(JSONObject configuration) {
        try {
            return objectMapper.readValue(configuration.toString(), IJsonObjectTransformer.class);
        } catch (Exception e) {
            throw new IllegalArgumentException("Could not deserialize configuration.", e);
        }
    }

    private static final class Scenario {
        public final String sourceStringFormat;
        public final String targetStringFormat;
        public final String sourceValue;
        public final String targetValue;

        public Scenario(String sourceStringFormat, String targetStringFormat, String sourceValue, String targetValue) {
            this.sourceStringFormat = sourceStringFormat;
            this.targetStringFormat = targetStringFormat;
            this.sourceValue = sourceValue;
            this.targetValue = targetValue;
        }
    }
}
