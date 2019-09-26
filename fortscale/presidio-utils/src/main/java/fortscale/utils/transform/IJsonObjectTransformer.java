package fortscale.utils.transform;

import com.fasterxml.jackson.annotation.JsonTypeInfo;
import org.json.JSONObject;

@JsonTypeInfo(use = JsonTypeInfo.Id.NAME, include = JsonTypeInfo.As.PROPERTY, property = "type")
public interface IJsonObjectTransformer extends GenericTransformer<JSONObject> {
    JSONObject transform(JSONObject jsonObject);
    String getName();
}
