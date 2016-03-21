package fortscale.common.event;

import net.minidev.json.JSONObject;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.util.Assert;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class MultiContextFieldsEvent implements Event {

    @Value("${fortscale.event.context.json.prefix}")
    protected String contextJsonPrefix;

    protected JSONObject jsonObject;
    protected String dataSource;

    public MultiContextFieldsEvent(JSONObject jsonObject, String dataSource) {
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
    public String getContextField(String key) {
        return  ((JSONObject)jsonObject.get(contextJsonPrefix)).getAsString(key);
    }

    @Override
    public Map<String, String> getContextFields(List<String> contextFieldNames) {
        Map<String, String> contextFields = new HashMap<>();
        for(String contextFieldName: contextFieldNames) {
            contextFields.put(contextFieldName, getContextField(contextFieldName));
        }
        return contextFields;
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
