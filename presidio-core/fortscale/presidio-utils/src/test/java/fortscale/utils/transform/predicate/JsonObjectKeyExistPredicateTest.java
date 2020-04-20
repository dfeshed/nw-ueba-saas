package fortscale.utils.transform.predicate;

import org.json.JSONObject;
import org.junit.Assert;
import org.junit.Test;

public class JsonObjectKeyExistPredicateTest {

    @Test
    public void match_pattern_test(){
        String key = "test-key";
        JsonObjectKeyExistPredicate predicate = new JsonObjectKeyExistPredicate("testing-key-exist-predicate", key);

        JSONObject jsonObject = new JSONObject();
        jsonObject.put(key, "just a value");

        Assert.assertTrue("The predicate returned false even though the key exist", predicate.test(jsonObject));
    }

    @Test
    public void no_match_pattern_test(){
        String key = "test-key";
        JsonObjectKeyExistPredicate predicate = new JsonObjectKeyExistPredicate("testing-key-exist-predicate", key);

        JSONObject jsonObject = new JSONObject();
        jsonObject.put("different value", "just a value");

        Assert.assertFalse("The predicate returned false even though the key exist", predicate.test(jsonObject));
    }
}
