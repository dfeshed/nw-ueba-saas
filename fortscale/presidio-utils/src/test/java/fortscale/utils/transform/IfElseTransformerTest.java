package fortscale.utils.transform;

import com.fasterxml.jackson.core.JsonProcessingException;
import fortscale.utils.transform.predicate.JsonObjectKeyExistPredicate;
import org.json.JSONObject;
import org.junit.Test;

import java.util.Collections;

import static fortscale.utils.transform.TransformerUtil.*;

public class IfElseTransformerTest extends TransformerTest{


    @Test
    public void if_match_test() throws JsonProcessingException {
        test(true);
    }

    @Test
    public void if_not_match_test() throws JsonProcessingException {
        test(false);
    }

    public void test(boolean isIfMatch) throws JsonProcessingException {
        String existPredicateKey = "event_type";
        JsonObjectKeyExistPredicate eventTypeKeyExist = new JsonObjectKeyExistPredicate("event-type-exist", existPredicateKey);

        String destinationKey = "destCopy";

        String sourceKeyToCopy1 = "srcCopy1";
        String sourceValue1 = "1";

        CopyValueTransformer copyValueTransformer1 = new CopyValueTransformer("copy-value-1", sourceKeyToCopy1,
                false, Collections.singletonList(destinationKey));
        String sourceKeyToCopy2 = "srcCopy2";
        String sourceValue2 = "2";
        CopyValueTransformer copyValueTransformer2 = new CopyValueTransformer("copy-value-2", sourceKeyToCopy2,
                false, Collections.singletonList(destinationKey));
        IfElseTransformer transformer = new IfElseTransformer("testing-if-else-transformer", eventTypeKeyExist,
                copyValueTransformer1,copyValueTransformer2);

        JSONObject jsonObject = new JSONObject();
        jsonObject.put(sourceKeyToCopy1, sourceValue1);
        jsonObject.put(sourceKeyToCopy2, sourceValue2);
        if(isIfMatch) {
            jsonObject.put(existPredicateKey, "some-value");
        }

        JSONObject retJsonObject = transform(transformer, jsonObject);

        assertNewJsonObjectNotContainsOriginalJsonObject(retJsonObject, jsonObject);
        assertJsonObjectKeyNotAdded(retJsonObject, destinationKey);
        if(isIfMatch) {
            assertWrongValueAddedToKey(retJsonObject, destinationKey, sourceValue1);
        } else {
            assertWrongValueAddedToKey(retJsonObject, destinationKey, sourceValue2);
        }
    }
}
