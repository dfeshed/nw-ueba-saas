package fortscale.utils.transform;

import com.fasterxml.jackson.core.JsonProcessingException;
import org.json.JSONObject;
import org.junit.Assert;
import org.junit.Test;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import static fortscale.utils.transform.TransformerUtil.*;

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
        copyToDestinations(true, Arrays.asList("dest1","dest2","dest3"));
    }

    @Test
    public void testHierarchyCreatedForTarget() throws Exception {
        assertHierarchy("sslSubject.name", "sourceKey", "sourceValue", "sslSubject");
    }

    @Test
    public void testHierarchyCreatedForTargetOnExistingSource() throws Exception {
        assertHierarchy("ja3.name", "ja3", "sourceValue", "ja3");
    }

    private void assertHierarchy(String destinationKey, String sourceKey, String sourceValue, String nestedObjectName) throws JsonProcessingException {
        JSONObject jsonObject = new JSONObject();
        jsonObject.put(sourceKey, sourceValue);

        IJsonObjectTransformer transformer = buildTransformer(sourceKey, true,
                Collections.singletonList(destinationKey));
        JSONObject retJsonObject = transform(transformer, jsonObject);
        Object actualObj = retJsonObject.get(nestedObjectName);
        JSONObject expectedObj = new JSONObject().put("name", sourceValue);
        Assert.assertEquals(expectedObj.toString(), actualObj.toString());
    }
}
