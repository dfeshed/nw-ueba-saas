package fortscale.domain.core;

import com.fasterxml.jackson.annotation.JsonTypeName;
import org.json.JSONArray;

/**
 * supporting information for notification evidences - map of keys and values changing based on the type of notification
 * Created by Amir Keren on 02/09/2015.
 */

@JsonTypeName("notificationSupportingInformation")
public class NotificationSupportingInformation extends EntitySupportingInformation {

    private JSONArray data;

    public void setData(JSONArray data) {
        this.data = data;
    }

    public JSONArray getData() {
        return data;
    }

}