package fortscale.domain.core;

import com.fasterxml.jackson.annotation.JsonTypeName;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jsonorg.JsonOrgModule;
import org.json.JSONObject;

/**
 * supporting information for notification evidences - map of keys and values changing based on the type of notification
 * Created by Amir Keren on 02/09/2015.
 */

@JsonTypeName("notificationSupportingInformation")
public class NotificationSupportingInformation extends EntitySupportingInformation {

    private JsonNode data;

    public void setData(JSONObject json) {
        ObjectMapper mapper = new ObjectMapper();
        mapper.registerModule(new JsonOrgModule());
        data = mapper.valueToTree(json);
    }

    public JsonNode getData() {
        return data;
    }

}