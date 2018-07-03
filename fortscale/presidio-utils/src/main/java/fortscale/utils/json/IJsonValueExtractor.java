package fortscale.utils.json;

import org.json.JSONObject;

public interface IJsonValueExtractor {

    Object getValue(JSONObject jsonObject);
}
