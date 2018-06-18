package org.apache.flume.interceptor.presidio.transform;

import com.fasterxml.jackson.core.JsonProcessingException;
import org.json.JSONObject;
import org.junit.Test;

import static org.apache.flume.interceptor.presidio.transform.TransformerUtil.*;

public class EpochTimeToNanoRepresentationTransformerTest extends TransformerTest{


    @Test
    public void epoch_time_in_millis_to_second_test() throws JsonProcessingException {
        String sourceKey = "origTime";
        String destinationKey = "destTime";
        EpochTimeToNanoRepresentationTransformer transformer = new EpochTimeToNanoRepresentationTransformer("testing-epoch-transformer", sourceKey, destinationKey);
        JSONObject jsonObject = new JSONObject();
        int eventTime = 1526980932;
        jsonObject.put(sourceKey,Long.toString(eventTime*1000L));

        JSONObject retJsonObject = transform(transformer, jsonObject);

        assertNewJsonObjectNotContainsOriginalJsonObject(retJsonObject, jsonObject);
        assertJsonObjectKeyNotAdded(retJsonObject, destinationKey);
        assertWrongValueAddedToKey(retJsonObject, destinationKey, new Double(eventTime));
    }
}
