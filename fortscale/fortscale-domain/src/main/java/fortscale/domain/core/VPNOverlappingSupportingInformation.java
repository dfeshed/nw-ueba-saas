package fortscale.domain.core;

import fortscale.domain.events.VpnSession;
import java.util.List;
import com.fasterxml.jackson.annotation.JsonTypeName;

/**
 * supporting information for notification evidences - map of keys and values changing based on the type of notification
 * Created by Amir Keren on 02/09/2015.
 */

@JsonTypeName("notificationSupportingInformation")
public class VPNOverlappingSupportingInformation extends NotificationSupportingInformation {

    private List<VpnSession> raw_events;

    public List<VpnSession> getRaw_events() {
        return raw_events;
    }

    public void setRaw_events(List<VpnSession> raw_events) {
        this.raw_events = raw_events;
    }

}