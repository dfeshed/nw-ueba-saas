package fortscale.utils.transform;

import fortscale.utils.transform.regexcaptureandformat.CaptureAndFormatConfiguration;
import fortscale.utils.transform.regexcaptureandformat.CapturingGroupConfiguration;
import org.json.JSONArray;
import org.json.JSONObject;
import org.junit.Test;

import java.util.Collections;

import static fortscale.utils.transform.TransformerUtil.*;





public class ExtractFirstValueInAnArrayWhichAnswerRegexTransformerTest {
    private static final String SOURCE_KEY = "src";
    private static final String TARGET_KEY = "tgt";

    @Test
    public void test1(){
        CaptureAndFormatConfiguration srcConfiguration = new CaptureAndFormatConfiguration("(.+) src", "%s",
                Collections.singletonList(new CapturingGroupConfiguration(1, "")));
        ExtractFirstValueInAnArrayWhichAnswerRegexTransformer extractSrc =
                new ExtractFirstValueInAnArrayWhichAnswerRegexTransformer(
                        "extract-src",
                        SOURCE_KEY,
                        TARGET_KEY,
                        srcConfiguration);

        JSONObject jsonObject = new JSONObject();
        jsonObject.put(SOURCE_KEY, new JSONArray("[\"dc src\",\"proxy dst\", \"other src\"]"));
        jsonObject.put("anotherField", new JSONArray("[\"salesforce.co.il\"]"));

        extractSrc.transform(jsonObject);

        assertJsonObjectKeyNotAdded(jsonObject, TARGET_KEY);
        assertWrongValueAddedToKey(jsonObject, TARGET_KEY, "dc");
    }

    @Test
    public void test2(){
        CaptureAndFormatConfiguration srcConfiguration = new CaptureAndFormatConfiguration("(.+) dst", "%s",
                Collections.singletonList(new CapturingGroupConfiguration(1, "")));
        ExtractFirstValueInAnArrayWhichAnswerRegexTransformer extractSrc =
                new ExtractFirstValueInAnArrayWhichAnswerRegexTransformer(
                        "extract-src",
                        SOURCE_KEY,
                        TARGET_KEY,
                        srcConfiguration);

        JSONObject jsonObject = new JSONObject();
        jsonObject.put(SOURCE_KEY, new JSONArray("[\"dc src\",\"proxy dst\", \"other src\"]"));
        jsonObject.put("anotherField", new JSONArray("[\"salesforce.co.il\"]"));

        extractSrc.transform(jsonObject);

        assertJsonObjectKeyNotAdded(jsonObject, TARGET_KEY);
        assertWrongValueAddedToKey(jsonObject, TARGET_KEY, "proxy");
    }
}
