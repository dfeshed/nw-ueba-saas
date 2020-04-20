package presidio.input.core.services.transformation.dummies;

import fortscale.utils.transform.AbstractJsonObjectTransformer;
import org.json.JSONObject;

public class TestJsonObjectTransformer extends AbstractJsonObjectTransformer {
    public TestJsonObjectTransformer(String name) {
        super(name);
    }

    @Override
    public JSONObject transform(JSONObject jsonObject) {
        return null;
    }
}
