package fortscale.utils.json;

import org.json.JSONObject;

public class JsonValueStaticExtractor implements IJsonValueExtractor{
    private Object value;

    public JsonValueStaticExtractor(Object value){
        this.value = value;
    }

    @Override
    public Object getValue(JSONObject jsonObject) {
        return value;
    }
}
