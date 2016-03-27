package fortscale.common.event;

import net.minidev.json.JSONObject;
import org.springframework.util.Assert;


public class MultiContextFieldsEvent extends AbstractEvent{

    private static final String ERR_MSG_NO_CONTEXT_OBJECT = "There is no context ('%s') object in a MultiContextFieldsEvent: %s";
    private static final String ERR_MSG_CONTEXT_PREFIX_NOT_INITIALIZED = "The contextJsonPrefix is not initialized";

    private JSONObject context;

    public MultiContextFieldsEvent(JSONObject jsonObject, String dataSource, String contextJsonPrefix) {
        super(jsonObject, dataSource);
        Assert.hasText(contextJsonPrefix, ERR_MSG_CONTEXT_PREFIX_NOT_INITIALIZED);
        context =  (JSONObject)jsonObject.get(contextJsonPrefix);
        Assert.notNull(context, String.format(ERR_MSG_NO_CONTEXT_OBJECT,contextJsonPrefix, jsonObject.toJSONString()));
    }

    @Override
    public String getContextField(String key) {
        return context.getAsString(key);
    }

}
