package fortscale.common.event;

import net.minidev.json.JSONObject;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.util.Assert;

public class MultiContextFieldsEvent extends AbstractEvent {

    private static final String ERR_MSG_NO_CONTEXT_OBJECT = "There is no context object in a MultiContextFieldsEvent: %s";

    @Value("${fortscale.event.context.json.prefix}")
    protected String contextJsonPrefix;
    private JSONObject context;

    public MultiContextFieldsEvent(JSONObject jsonObject, String dataSource) {
        super(jsonObject, dataSource);
        context =  (JSONObject)jsonObject.get(contextJsonPrefix);
        Assert.notNull(context, String.format(ERR_MSG_NO_CONTEXT_OBJECT, jsonObject.toJSONString()));
    }

    @Override
    public String getContextField(String key) {
        return context.getAsString(key);
    }

}
