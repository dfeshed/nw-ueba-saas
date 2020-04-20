package fortscale.utils.transform;

import org.apache.commons.lang.StringUtils;
import org.json.JSONObject;
import org.junit.Assert;
import org.junit.Test;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import static fortscale.utils.transform.TransformerUtil.*;



public class JoinTransformerTest {

    @Test
    public void joinSingleStringValueTest(){
        String destinationKey = "joinDestinationField";
        String value = "theValue";
        JoinTransformer joinTransformer =
                new JoinTransformer("just a name", destinationKey,
                        Collections.singletonList(value), ",");
        JSONObject jsonObject = new JSONObject();
        jsonObject.put(value, "anotherValue");

        JSONObject retJsonObject = joinTransformer.transform(jsonObject);

        assertNewJsonObjectNotContainsOriginalJsonObject(retJsonObject, jsonObject);
        assertJsonObjectKeyNotAdded(jsonObject, destinationKey);
        assertWrongValueAddedToKey(jsonObject, destinationKey, value);
    }

    @Test
    public void joinMultiStringValuesTest(){
        String destinationKey = "joinDestinationField";
        String value1 = "theValue1";
        String value2 = "theValue2";
        String seperator = "___";
        List<Object> values = Arrays.asList(value1,value2);
        JoinTransformer joinTransformer =
                new JoinTransformer("just a name", destinationKey, values, seperator);
        JSONObject jsonObject = new JSONObject();
        jsonObject.put(value1, "anotherValue");

        JSONObject retJsonObject = joinTransformer.transform(jsonObject);

        assertNewJsonObjectNotContainsOriginalJsonObject(retJsonObject, jsonObject);
        assertJsonObjectKeyNotAdded(jsonObject, destinationKey);
        assertWrongValueAddedToKey(jsonObject, destinationKey,
                StringUtils.join(values, seperator));
    }

    @Test
    public void joinMultiPointerValuesTest(){
        String destinationKey = "joinDestinationField";
        String prefix = "pretest";
        String suffix = "suffixtest";
        String path1 = "field1";
        String value1 = "val1";
        String path2 = "root.field2";
        String value2 = "val2";
        String seperator = "___";
        List<Object> values = Arrays.asList(prefix,String.format("${%s}", path1),String.format("${%s}", path2),suffix);
        JoinTransformer joinTransformer =
                new JoinTransformer("just a name", destinationKey, values, seperator);
        JSONObject jsonObject = new JSONObject();
        jsonObject.put(path1, value1);
        JSONObject subJsonObject = new JSONObject();
        subJsonObject.put("field2", value2);
        jsonObject.put("root", subJsonObject);
        jsonObject.put(value1, "anotherValue");

        JSONObject retJsonObject = joinTransformer.transform(jsonObject);

        assertNewJsonObjectNotContainsOriginalJsonObject(retJsonObject, jsonObject);
        assertJsonObjectKeyNotAdded(jsonObject, destinationKey);
        assertWrongValueAddedToKey(jsonObject, destinationKey,
                StringUtils.join(Arrays.asList(prefix, value1, value2, suffix), seperator));
    }

    @Test
    public void joinMultiPointerValuesWithOneNonExistingPathTest(){
        String destinationKey = "joinDestinationField";
        String prefix = "pretest";
        String suffix = "suffixtest";
        String path1 = "field1";
        String value1 = "val1";
        String path2 = "root.field2";
        String value2 = "val2";
        String seperator = "___";
        List<Object> values = Arrays.asList(prefix,String.format("${%s}", path1),String.format("${%s}", path2),suffix);
        JoinTransformer joinTransformer =
                new JoinTransformer("just a name", destinationKey, values, seperator);
        JSONObject jsonObject = new JSONObject();
        jsonObject.put(path1, value1);
        JSONObject subJsonObject = new JSONObject();
        subJsonObject.put("field3", value2);
        jsonObject.put("root", subJsonObject);
        jsonObject.put(value1, "anotherValue");

        joinTransformer.transform(jsonObject);

        Assert.assertTrue(String.format("the destination key %s should not exist. event: %s", destinationKey, jsonObject),
                !jsonObject.has(destinationKey));
    }


}
