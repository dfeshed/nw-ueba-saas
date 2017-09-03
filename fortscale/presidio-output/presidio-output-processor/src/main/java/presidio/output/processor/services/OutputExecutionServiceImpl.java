package presidio.output.processor.services;

import fortscale.common.general.CommonStrings;
import fortscale.utils.pagination.PageIterator;
import fortscale.utils.time.TimeRange;
import org.apache.commons.collections.CollectionUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import presidio.ade.domain.record.aggregated.SmartRecord;
import presidio.ade.sdk.common.AdeManagerSdk;
import presidio.output.domain.records.alerts.Alert;
import presidio.output.domain.records.alerts.AlertEnums;
import presidio.output.domain.records.users.User;
import presidio.output.processor.services.alert.AlertService;
import presidio.output.processor.services.user.UserScoreService;
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
    private final UserScoreService userScoreService;
    private final AdeManagerSdk adeManagerSdk;
    private final AlertService alertService;
    private final UserService userService;

    public OutputExecutionServiceImpl(AdeManagerSdk adeManagerSdk,
                                      AlertService alertService,
                                      UserService userService,
                                      UserScoreService userScoreService) {
        this.adeManagerSdk = adeManagerSdk;
        this.alertService = alertService;
        this.userService = userService;
        this.userScoreService = userScoreService;
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
    public void run(Instant startDate, Instant endDate, AlertEnums.AlertTimeframe timeframe) throws Exception {
        logger.debug("Started output process with params: start date {}:{}, end date {}:{}.", CommonStrings.COMMAND_LINE_START_DATE_FIELD_NAME, startDate, CommonStrings.COMMAND_LINE_END_DATE_FIELD_NAME, endDate);

        //1. Get SMARTs from ADE and generate alerts
        //TODO- change page size and score threshold (configurable)
        PageIterator<SmartRecord> smartPageIterator = adeManagerSdk.getSmartRecords(new TimeRange(startDate, endDate), 100, SMART_SCORE_THRESHOLD);

        //2. For each smart: generate Alert entity and User
        List<Alert> alerts = new ArrayList<Alert>();
        List<User> users = new ArrayList<User>();
        while (smartPageIterator.hasNext()) {
            List<SmartRecord> smarts = smartPageIterator.next();

            smarts.stream().forEach(smart -> {
                //TODO change this after new SMART POJO is ready
                //TODO- user id should be taken from SMART POJO directly
                String userId = smart.getAggregationRecords().get(0).getContext().get("userId");
                User userEntity = userService.findUserById(userId);
                if (userEntity==null) {
                    userEntity = userService.createUserEntity(userId);
                }
                Alert alertEntity = alertService.generateAlert(smart, userEntity);
                userEntity.addAlertClassifications(alertEntity.getClassifications());

                if (alertEntity != null) {
                    alerts.add(alertEntity);
                }

                users.add(userEntity);
            });
            break; //TODO !!! remove this once ADE Team will implement SmartPageIterator.hasNext(). currently only one page is returned.
        }

        storeAlerts(alerts);
        storeUsers(users);
        this.userScoreService.updateSeveritiesForUsersList(users,true);


    }

    public void recalculateUserScore() throws Exception{
        logger.info("Start Recalculating User Score");
        this.userScoreService.updateAllUsersScores();
        logger.info("Finish Recalculating User Score");
        logger.info("Start Updating UserSeverity");
        this.userScoreService.updateSeverities();
        logger.info("Finish Updating Users Severity");
    }

    private void storeAlerts(List<Alert> alerts) {
        if (CollectionUtils.isNotEmpty(alerts)) {
            alertService.save(alerts);
        }
        logger.debug("{} output alerts were generated", alerts.size());
    }

    private void storeUsers(List<User> users) {
        if (CollectionUtils.isNotEmpty(users)) {
            userService.save(users);
        }
        logger.debug("{} output users were generated", users.size());
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
