package presidio.output.processor.services;

import fortscale.common.general.CommonStrings;
import fortscale.domain.SMART.EntityEvent;
import fortscale.utils.pagination.PageIterator;
import fortscale.utils.time.TimeRange;
import org.apache.commons.collections.CollectionUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import presidio.ade.sdk.common.AdeManagerSdk;
import presidio.output.domain.records.alerts.Alert;
import presidio.output.domain.records.events.EnrichedEvent;
import presidio.output.domain.records.users.User;
import presidio.output.domain.services.alerts.AlertPersistencyService;
import presidio.output.domain.services.event.EventPersistencyService;
import presidio.output.domain.services.users.UserPersistencyService;
import presidio.output.processor.services.alert.AlertService;
import presidio.output.processor.services.user.UserDetails;
import presidio.output.processor.services.user.UserService;

import java.time.Instant;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by shays on 17/05/2017.
 * Main output functionality is implemented here
 */

public class OutputExecutionServiceImpl implements OutputExecutionService {

    Logger logger = LoggerFactory.getLogger(this.getClass());

    public static int SMART_SCORE_THRESHOLD = 50;

    private final AdeManagerSdk adeManagerSdk;
    private final AlertService alertService;
    private final UserService userService;
    private final EventPersistencyService eventPersistencyService;
    private final AlertPersistencyService alertPersistencyService;
    private final UserPersistencyService userPersistencyService;

    public OutputExecutionServiceImpl(AdeManagerSdk adeManagerSdk,
                                      AlertService alertService,
                                      UserService userService,
                                      EventPersistencyService eventPersistencyService,
                                      AlertPersistencyService alertPersistencyService,
                                      UserPersistencyService userPersistencyService) {
        this.adeManagerSdk = adeManagerSdk;
        this.alertService = alertService;
        this.userService = userService;
        this.eventPersistencyService = eventPersistencyService;
        this.alertPersistencyService = alertPersistencyService;
        this.userPersistencyService = userPersistencyService;
    }

    /**
     * Run the output processor main functionality which consist of the following-
     * 1. Get SMARTs from ADE and create Alerts entities for SMARTs with score higher than the threshold
     * 2. Enrich alerts with information from Input component (fields which were not part of the ADE schema)
     * 3. Alerts classification (rule based semantics)
     * 4. Calculates supporting information
     * @param startDate
     * @param endDate
     * @throws Exception
     */
    @Override
    public void run(Instant startDate, Instant endDate) throws Exception {
        logger.debug("Started output process with params: start date {}:{}, end date {}:{}.", CommonStrings.COMMAND_LINE_START_DATE_FIELD_NAME, startDate, CommonStrings.COMMAND_LINE_END_DATE_FIELD_NAME, endDate);

        //1. Get SMARTs from ADE and generate alerts
        //TODO- change page size and score threshold (configurable)
        PageIterator<EntityEvent> smartPageIterator = adeManagerSdk.getSmartRecords(new TimeRange(startDate, endDate), 100, SMART_SCORE_THRESHOLD);

        //2. For each smart: generate Alert entity and User
        List<Alert> alerts = new ArrayList<Alert>();
        List<User> users = new ArrayList<User>();
        while (smartPageIterator.hasNext()) {
            List<EntityEvent> smarts = smartPageIterator.next();

            smarts.stream().forEach(smart -> {
                //TODO change this after new SMART POJO is ready
                //TODO- user id should be taken from indicators
                String userId = smart.getContext().get("normalized_username");

                User userEntity = userService.createUserEntity(getUserDetails(userId));
                Alert alertEntity = alertService.generateAlert(smart, userEntity);

                if (alertEntity != null)
                    alerts.add(alertEntity);

                users.add(userEntity);
            });
            break; //TODO !!! remove this once ADE Team will implement SmartPageIterator.hasNext(). currently only one page is returned.
        }

        storeAlerts(alerts);
        storeUsers(users);
    }

    private void storeAlerts(List<Alert> alerts) {
        if (CollectionUtils.isNotEmpty(alerts)) {
            alertPersistencyService.save(alerts);
        }
        logger.debug("{} output alerts were generated", alerts.size());
    }

    private void storeUsers(List<User> users) {
        if (CollectionUtils.isNotEmpty(users)) {
            userPersistencyService.save(users);
        }
        logger.debug("{} output users were generated", users.size());
    }

    private UserDetails getUserDetails(String userId) {
        EnrichedEvent event = eventPersistencyService.findLatestEventForUser(userId);
        String userDisplayName = event.getUserDisplayName();
        String userName = event.getUserName();
        return new UserDetails(userName, userDisplayName, userId);
    }

    @Override
    public void clean(Instant startDate, Instant endDate) throws Exception {
        // TODO: Implement
    }

    @Override
    public void cleanAll() throws Exception {
        // TODO: Implement
    }
}
