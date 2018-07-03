package fortscale.utils.json;

import org.apache.commons.lang3.Validate;
import org.json.JSONArray;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

public class JsonPointer {

    private List<IJsonPointerElement> pointerElements = new ArrayList<>();

    public JsonPointer(String path){
        Validate.notBlank(path);
        for(String key: path.split("\\.")){
            if(key.endsWith("]")){
                Validate.isTrue(key.contains("["));
                int arrayStartIndexOf = key.indexOf("[");
                pointerElements.add(new JsonObjectPointerElement(key.substring(0,arrayStartIndexOf)));
                int arrayIndex = Integer.parseInt(key.substring(arrayStartIndexOf+1, key.indexOf("]")).trim());
                pointerElements.add(new JsonArrayPointerElement(arrayIndex));
            } else {
                pointerElements.add(new JsonObjectPointerElement(key));
            }
        }
    }

    public Object get(JSONObject jsonObject, boolean createPathIfNotExist){
        Object ret = jsonObject;
        for(IJsonPointerElement pointerElement: pointerElements){
            ret = pointerElement.get(ret, createPathIfNotExist);
        }

        return ret;
    }

    public Object get(JSONObject jsonObject){
        return get(jsonObject, false);
    }

    public void set(JSONObject jsonObject, String key, Object value, boolean createPathIfNotExist){
        Object obj = get(jsonObject, createPathIfNotExist);
        if(obj != null && obj instanceof JSONObject){
            ((JSONObject)obj).put(key, value);
        }
    }


    private interface IJsonPointerElement{
        Object get(Object obj, boolean createPathIfNotExist);
    }

    private static class JsonObjectPointerElement implements IJsonPointerElement{
        private String key;

        public JsonObjectPointerElement(String key){
            this.key = key;
        }

        @Override
        public Object get(Object obj, boolean createPathIfNotExist) {
            Object ret = null;
            if(obj != null && obj instanceof JSONObject) {
                JSONObject jsonObject = (JSONObject) obj;
                ret = jsonObject.opt(key);
                if(createPathIfNotExist) {
                    if(ret == null){
                        ret = new JSONObject();
                        jsonObject.put(key, ret);
                    }
                }
            }
            return ret;
        }
    }

    private static class JsonArrayPointerElement implements IJsonPointerElement{
        private int index;

        public JsonArrayPointerElement(int index){
            this.index = index;
        }

        @Override
        public Object get(Object obj, boolean createPathIfNotExist) {
            if(obj == null || !(obj instanceof JSONArray)) {
                return null;
            } else {
                return ((JSONArray) obj).get(index);
            }
        }
    }
}
