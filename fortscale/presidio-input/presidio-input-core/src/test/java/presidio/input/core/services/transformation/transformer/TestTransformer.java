package presidio.input.core.services.transformation.transformer;

import fortscale.utils.transform.AbstractJsonObjectTransformer;
import org.json.JSONObject;

public class TestTransformer extends AbstractJsonObjectTransformer {

    public TestTransformer(String name) {
        super(name);
    }

    @Override
    public JSONObject transform(JSONObject jsonObject) {
        return null;
    }
}
