package presidio.output.forwarder.handlers;

import presidio.output.domain.records.alerts.Alert;
import presidio.output.domain.records.alerts.Indicator;
import presidio.output.domain.records.alerts.IndicatorEvent;
import presidio.output.domain.records.users.User;

import java.time.Instant;

public interface EventsHandler {

    void onUserChanged(User user);

    void onAlertChanged(Alert alert);

    void onIndicatorChanged(Indicator indicator);

    void onUserStartStreaming(Instant start, Instant end, String syslogEventId);

    void onUserEndStreaming(Instant start, Instant end, String syslogEventId);

    void onAlertStartStreaming(Instant start, Instant end, String syslogEventId);

    void onAlertEndStreaming(Instant start, Instant end, String syslogEventId);

}
