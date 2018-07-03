package fortscale.utils.json;

import org.json.JSONObject;

public class JsonPointerValueExtractor implements IJsonValueExtractor{

    private JsonPointer jsonPointer;

    public JsonPointerValueExtractor(String path){
        jsonPointer = new JsonPointer(path);
    }

    @Override
    public Object getValue(JSONObject jsonObject) {
        return jsonPointer.get(jsonObject);
    }
}
