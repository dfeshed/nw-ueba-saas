package presidio.output.forwarder.services;

import fortscale.utils.logging.Logger;
import presidio.output.forwarder.AlertsForwarder;
import presidio.output.forwarder.IndicatorsForwarder;
import presidio.output.forwarder.UsersForwarder;
import presidio.output.forwarder.shell.OutputForwarderApplication;


import java.time.Instant;

public class PresidioOutputForwardService {

    private static final Logger logger = Logger.getLogger(OutputForwarderApplication.class);

    UsersForwarder usersForwarder;
    AlertsForwarder alertsForwarder;
    IndicatorsForwarder indicatorsForwarder;

    public PresidioOutputForwardService(UsersForwarder usersForwarder, AlertsForwarder alertsForwarder, IndicatorsForwarder indicatorsForwarder) {
        this.usersForwarder = usersForwarder;
        this.alertsForwarder = alertsForwarder;
        this.indicatorsForwarder = indicatorsForwarder;
    }

    public int forward(Instant startDate, Instant endDate) {
        logger.info(String.format("about to forward data from %s to %s", startDate, endDate));

        forwardUsers(startDate, endDate);

        forwardAlerts(startDate, endDate);

        forwardIndicators(startDate, endDate);

        logger.info("finish to forward data");

        return 0;
    }


    public void forwardUsers(Instant startDate, Instant endDate) {
        logger.info(String.format("about to forward users from %s to %s", startDate, endDate));

        usersForwarder.forward(startDate, endDate);

        logger.info("finish to forward users");

    }

    public void forwardAlerts(Instant startDate, Instant endDate) {
        logger.info(String.format("about to forward alerts from %s to %s", startDate, endDate));

        usersForwarder.forward(startDate, endDate);

        logger.info("finish to forward alerts");

    }

    public void forwardIndicators(Instant startDate, Instant endDate) {
        logger.info(String.format("about to forward indicators from %s to %s", startDate, endDate));

        usersForwarder.forward(startDate, endDate);

        logger.info("finish to forward indicators");

    }

}
