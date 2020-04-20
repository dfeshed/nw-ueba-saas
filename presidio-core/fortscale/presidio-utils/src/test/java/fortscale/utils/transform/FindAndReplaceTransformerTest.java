package fortscale.utils.transform;

import com.fasterxml.jackson.core.JsonProcessingException;
import org.json.JSONObject;
import org.junit.Assert;
import org.junit.Test;

public class FindAndReplaceTransformerTest extends TransformerTest{

    @Test
    public void find_and_replace_match_test() throws JsonProcessingException {
        String sourceKey = "operationType";
        FindAndReplaceTransformer transformer = new FindAndReplaceTransformer("find-and-replace-operation-type", sourceKey, "^FILE_", "FOLDER_");

        JSONObject jsonObject = new JSONObject();
        jsonObject.put(sourceKey, "FILE_OPEN");

        JSONObject retJsonObject = transform(transformer, jsonObject);

        Assert.assertEquals("operation type should have been changed to folder operation type",
                "FOLDER_OPEN", retJsonObject.getString(sourceKey));
    }
}
