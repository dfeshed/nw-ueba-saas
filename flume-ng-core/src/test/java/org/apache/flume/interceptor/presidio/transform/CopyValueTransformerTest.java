package org.apache.flume.interceptor.presidio.transform;

import org.json.JSONObject;
import org.junit.Assert;
import org.junit.Test;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import static org.apache.flume.interceptor.presidio.transform.TransformerUtil.*;

public class CopyValueTransformerTest extends TransformerTest{




    private IJsonObjectTransformer buildTransformer(String sourceKey, boolean isRemoveSourceKey, List<String> destinationKeys) {
        return new CopyValueTransformer("testName",sourceKey,isRemoveSourceKey,destinationKeys);
    }

    private void copyToDestinations(boolean isRemoveSourceKey, List<String> destinationKeys) throws Exception {
        String sourceKey = "orig";

        String sourceValue = "svalue";

        JSONObject jsonObject = new JSONObject();
        jsonObject.put(sourceKey, sourceValue);

        IJsonObjectTransformer transformer = buildTransformer(sourceKey, isRemoveSourceKey, destinationKeys);
        JSONObject retJsonObject = transform(transformer, jsonObject);

        if(isRemoveSourceKey){
            Assert.assertTrue(String.format("The following key should have been removed. key value: %s, event: %s",
                    sourceKey, retJsonObject),!retJsonObject.has(sourceKey));
        } else {
            assertNewJsonObjectNotContainsOriginalJsonObject(retJsonObject, jsonObject);
        }
        for(String destinationKey: destinationKeys) {
            assertJsonObjectKeyNotAdded(retJsonObject, destinationKey);
            assertWrongValueAddedToKey(retJsonObject, destinationKey, sourceValue);
        }
    }

    @Test
    public void copyToSingleDestination() throws Exception {
        String destinationKey = "dest";
        copyToDestinations(false, Collections.singletonList(destinationKey));
    }

    @Test
    public void copyToSingleDestinationAndRemoveSourceKey() throws Exception {
        String destinationKey = "dest";
        copyToDestinations(true, Collections.singletonList(destinationKey));
    }

    @Test
    public void copyToMultiDestinations() throws Exception {
        copyToDestinations(false, Arrays.asList("dest1","dest2","dest3"));
    }

    @Test
    public void copyToMultiDestinationsAndRemoveSourceKey() throws Exception {
        String destinationKey = "dest";
        copyToDestinations(true, Arrays.asList("dest1","dest2","dest3"));
    }
}
