package source.presidioHttpSource;

import java.util.List;

/**
 * Created by tomerd on 7/4/2017.
 */
public class NotificationEvents {

    private String authorizationId;

    private Data data;

    public String getAuthorizationId() {
        return authorizationId;
    }

    public void setAuthorizationId(String authorizationId) {
        this.authorizationId = authorizationId;
    }

    public Data getData() {
        return data;
    }

    public void setData(Data data) {
        this.data = data;
    }

    public static class Data {

        private String subscriptionId;

        private NotificationEvent[] events;

        public NotificationEvent[] getEvents() {
            return events;
        }

        public void setEvents(NotificationEvent[] events) {
            this.events = events;
        }

        public String getSubscriptionId() {
            return subscriptionId;
        }

        public void setSubscriptionId(String subscriptionId) {
            this.subscriptionId = subscriptionId;
        }
    }

    public static class NotificationEvent {

        private String event;

        private String subsystem;

        private String facility;

        private String severity;

        private String timeDetected;

        public String getEvent() {
            return event;
        }

        public void setEvent(String event) {
            this.event = event;
        }

        public String getSubsystem() {
            return subsystem;
        }

        public void setSubsystem(String subsystem) {
            this.subsystem = subsystem;
        }

        public String getFacility() {
            return facility;
        }

        public void setFacility(String facility) {
            this.facility = facility;
        }

        public String getSeverity() {
            return severity;
        }

        public void setSeverity(String severity) {
            this.severity = severity;
        }

        public String getTimeDetected() {
            return timeDetected;
        }

        public void setTimeDetected(String timeDetected) {
            this.timeDetected = timeDetected;
        }
    }
}
