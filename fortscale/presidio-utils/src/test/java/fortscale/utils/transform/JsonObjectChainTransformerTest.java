package fortscale.utils.transform;


import com.fasterxml.jackson.core.JsonProcessingException;
import org.json.JSONObject;
import org.junit.Assert;
import org.junit.Test;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import static fortscale.utils.transform.TransformerUtil.*;

public class JsonObjectChainTransformerTest extends TransformerTest{

    private CopyValueTransformer buildCopyValueTransformer(String sourceKey, boolean isRemoveSourceKey, List<String> destinationKeys) {
        return new CopyValueTransformer("testName",sourceKey,isRemoveSourceKey,destinationKeys);
    }

    private IJsonObjectTransformer buildJsonObjectConditionalChainTransformer(List<IJsonObjectTransformer> transformerList) {
        return new JsonObjectChainTransformer("testName", transformerList);
    }


    @Test
    public void copyMultiSourceToMultiDestinationsTest() throws JsonProcessingException {
        List<IJsonObjectTransformer> transformerList = new ArrayList<>();
        String sourceKey1 = "orig1";
        String sourceKey2 = "orig2";

        transformerList.add(buildCopyValueTransformer(sourceKey1, false, Arrays.asList("dest1","dest2","dest3")));
        transformerList.add(buildCopyValueTransformer(sourceKey2, true, Arrays.asList("dest4","dest5","dest6")));
        IJsonObjectTransformer transformer = buildJsonObjectConditionalChainTransformer(transformerList);

        JSONObject jsonObject = new JSONObject();
        jsonObject.put(sourceKey1, "svalue1");
        jsonObject.put(sourceKey2, "svalue2");

        JSONObject retJsonObject = transform(transformer, jsonObject);

        for ( IJsonObjectTransformer copyValueTransformer: transformerList){
            assertCopyValueTransformerResults(retJsonObject, jsonObject, (CopyValueTransformer) copyValueTransformer);
        }
    }

    private void assertCopyValueTransformerResults(JSONObject transformedJsonObject, JSONObject origJsonObject, CopyValueTransformer copyValueTransformer){
        String sourceKey = copyValueTransformer.getSourceKey();
        Object sourceValue = origJsonObject.get(copyValueTransformer.getSourceKey());
        if(copyValueTransformer.isRemoveSourceKey()){
            Assert.assertTrue(String.format("The following key should have been removed. key value: %s, event: %s",
                    copyValueTransformer.getSourceKey(), transformedJsonObject),!transformedJsonObject.has(copyValueTransformer.getSourceKey()));
        } else {
            assertJsonObjectValueRemovedOrModified(transformedJsonObject, sourceKey, sourceValue);
        }
        for(String destinationKey: copyValueTransformer.getDestinationKeys()) {
            assertJsonObjectKeyNotAdded(transformedJsonObject, destinationKey);
            assertWrongValueAddedToKey(transformedJsonObject, destinationKey, sourceValue);
        }
    }

    @Test
    public void chain_single_copy_test() throws JsonProcessingException {
        JSONObject jsonObject = new JSONObject();

        String sourceKey = "orig";

        CopyValueTransformer copyValueTransformer = buildCopyValueTransformer(sourceKey, false, Arrays.asList("dest1","dest2","dest3"));
        IJsonObjectTransformer transformer = buildJsonObjectConditionalChainTransformer(Collections.singletonList(copyValueTransformer));

        jsonObject.put(sourceKey, "svalue1");

        JSONObject retJsonObject = transform(transformer, jsonObject);

        assertNewJsonObjectNotContainsOriginalJsonObject(retJsonObject, jsonObject);

        assertCopyValueTransformerResults(retJsonObject, jsonObject, copyValueTransformer);
    }
}
