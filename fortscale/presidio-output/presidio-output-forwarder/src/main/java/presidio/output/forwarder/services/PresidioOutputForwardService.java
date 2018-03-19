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
import java.util.UUID;
import java.util.concurrent.atomic.AtomicInteger;
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

    public int forward(Instant startDate, Instant endDate) {
        logger.info(String.format("about to forward data from %s to %s", startDate, endDate));

        forwardUsers(startDate, endDate);

        forwardAlerts(startDate, endDate);

        logger.info("finish to forward data");

        return 0;
    }

    private void forwardUsers(Instant startDate, Instant endDate) {
        // forward user changed events
        String syslogEventId = UUID.randomUUID().toString();
        final AtomicInteger forwardedUsers = new AtomicInteger();
        eventsHandler.onUserStartStreaming(startDate, endDate, syslogEventId);
        try (Stream<User> users = userPersistencyService.findUsersByUpdatedDate(startDate, endDate)) {
            users.forEach(user -> {
                    eventsHandler.onUserChanged(user);
                forwardedUsers.incrementAndGet();
            });
        }
        eventsHandler.onUserEndStreaming(startDate, endDate, syslogEventId);

        logger.info(String.format("%d users were sent to syslog", forwardedUsers.get()));
    }


    private void forwardAlerts(Instant startDate, Instant endDate) {
        final AtomicInteger forwardedAlerts = new AtomicInteger();
        final AtomicInteger forwardedIndicators = new AtomicInteger();
        String syslogEventId = UUID.randomUUID().toString();

        eventsHandler.onAlertStartStreaming(startDate, endDate, syslogEventId);

        // forward alert changed events
        try (Stream<Alert> alerts = alertPersistencyService.findAlertsByDate(startDate, endDate)) {
            alerts.forEach(alert -> {
                eventsHandler.onAlertChanged(alert);
                forwardedAlerts.incrementAndGet();
            });
        }

        // forward indicator changed events
        try (Stream<Indicator> indicators = alertPersistencyService.findIndicatorByDate(startDate, endDate)) {
            indicators.forEach(indicator -> {
                List<IndicatorEvent> events = alertPersistencyService.findIndicatorEventByIndicatorId(indicator.getId());
                indicator.setEvents(events);
                eventsHandler.onIndicatorChanged(indicator);
                forwardedIndicators.incrementAndGet();
            });
        }

        eventsHandler.onAlertEndStreaming(startDate, endDate, syslogEventId);

        logger.info(String.format("%d alerts and %d indicators were sent to syslog", forwardedAlerts.get(), forwardedIndicators.get()));
    }

}
