package presidio.output.forwarder.services;

import fortscale.utils.logging.Logger;
import presidio.output.domain.records.alerts.Alert;
import presidio.output.domain.records.alerts.Indicator;
import presidio.output.domain.records.alerts.IndicatorEvent;
import presidio.output.domain.records.users.User;
import presidio.output.domain.services.alerts.AlertPersistencyService;
import presidio.output.domain.services.users.UserPersistencyService;
import presidio.output.forwarder.handlers.EventsHandler;
import presidio.output.forwarder.shell.OutputForwarderApplication;

import java.time.Instant;
import java.util.List;
import java.util.stream.Stream;

public class PresidioOutputForwardService {

    private static final Logger logger = Logger.getLogger(OutputForwarderApplication.class);

    AlertPersistencyService alertPersistencyService;

    UserPersistencyService userPersistencyService;

    EventsHandler eventsHandler;

    public PresidioOutputForwardService(AlertPersistencyService alertPersistencyService, UserPersistencyService userPersistencyService, EventsHandler eventsHandler) {
        this.userPersistencyService = userPersistencyService;
        this.alertPersistencyService = alertPersistencyService;
        this.eventsHandler = eventsHandler;
    }

    public void forward(Instant startDate, Instant endDate) {
        logger.info(String.format("about to forward data from %s to %s", startDate, endDate));

        // forward user changed events
        eventsHandler.onUserStartStreaming(startDate, endDate);
        try (Stream<User> users = userPersistencyService.findUsersByUpdatedDate(startDate, endDate)) {
            users.forEach(user -> {
                eventsHandler.onUserChanged(user);
            });
        }
        eventsHandler.onUserEndStreaming(startDate, endDate);



        eventsHandler.onAlertStartStreaming(startDate, endDate);

        // forward alert changed events
        try (Stream<Alert> alerts = alertPersistencyService.findAlertsByDate(startDate, endDate)) {
            alerts.forEach(alert -> {
                eventsHandler.onAlertChanged(alert);
            });
        }

        // forward indicator changed events
        try (Stream<Indicator> indicators = alertPersistencyService.findIndicatorByDate(startDate, endDate)) {
            indicators.forEach(indicator -> {
                List<IndicatorEvent> events = alertPersistencyService.findIndicatorEventByIndicatorId(indicator.getId());
                indicator.setEvents(events);
                eventsHandler.onIndicatorChanged(indicator);
            });
        }

        eventsHandler.onAlertEndStreaming(startDate, endDate);

        logger.info("finish to forward data");

    }

}
