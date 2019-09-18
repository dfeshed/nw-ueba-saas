package fortscale.utils.reflection;

import fortscale.utils.transform.AbstractJsonObjectTransformer;
import org.json.JSONObject;

public class TestObject extends AbstractJsonObjectTransformer {

    public TestObject(String name) {
        super(name);
    }

    @Override
    public JSONObject transform(JSONObject jsonObject) {
        return null;
    }
}
