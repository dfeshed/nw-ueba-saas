package fortscale.utils.transform;

import org.json.JSONArray;
import org.json.JSONObject;
import org.junit.Test;

import static fortscale.utils.transform.TransformerUtil.*;

public class TopLevelDomainTransformerTest {

    private static final String SOURCE_KEY = "src";
    private static final String TARGET_KEY = "tgt";

    @Test
    public void test1(){
        TopLevelDomainTransformer transformer =
                new TopLevelDomainTransformer(
                        "source-domain-to-top-level-domain",
                        SOURCE_KEY,
                        false,
                        TARGET_KEY);
        JSONObject jsonObject = new JSONObject();
        jsonObject.put(SOURCE_KEY, "www.developer.something.salesforce.com");
        jsonObject.put("anotherField", "www.google.com");

        transformer.transform(jsonObject);

        assertJsonObjectKeyNotAdded(jsonObject, TARGET_KEY);
        assertWrongValueAddedToKey(jsonObject, TARGET_KEY, "salesforce.com");
    }

    @Test
    public void test2(){
        TopLevelDomainTransformer transformer =
                new TopLevelDomainTransformer(
                        "source-domain-to-top-level-domain",
                        SOURCE_KEY+"[0]",
                        false,
                        TARGET_KEY);
        JSONObject jsonObject = new JSONObject();
        jsonObject.put(SOURCE_KEY, new JSONArray("[\"salesforce.co.il\"]"));
        jsonObject.put("anotherField", "www.google.com");

        transformer.transform(jsonObject);

        assertJsonObjectKeyNotAdded(jsonObject, TARGET_KEY);
        assertWrongValueAddedToKey(jsonObject, TARGET_KEY, "salesforce.co.il");
    }
}
