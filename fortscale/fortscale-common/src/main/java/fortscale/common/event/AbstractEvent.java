package fortscale.common.event;

import net.minidev.json.JSONObject;
import org.springframework.util.Assert;

public class AbstractEvent  implements Event{

    protected JSONObject jsonObject;
    protected String dataSource;

    public AbstractEvent(JSONObject jsonObject, String dataSource) {
        Assert.notNull(jsonObject);
        Assert.notNull(dataSource);
        this.jsonObject = jsonObject;
        this.dataSource = dataSource;
    }

    @Override
    public Object get(String key) {
        return jsonObject.get(key);
    }

    @Override
    public JSONObject getJSONObject() {
        return jsonObject;
    }

    @Override
    public String getDataSource() {
        return dataSource;
    }

}
