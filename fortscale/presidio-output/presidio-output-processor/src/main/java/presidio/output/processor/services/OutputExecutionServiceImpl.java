package presidio.output.processor.services;

import fortscale.common.general.CommonStrings;
import fortscale.utils.pagination.PageIterator;
import fortscale.utils.time.TimeRange;
import org.apache.commons.collections.CollectionUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import presidio.ade.domain.record.aggregated.SmartRecord;
import presidio.ade.sdk.common.AdeManagerSdk;
import presidio.monitoring.aspect.annotations.RunTime;
import presidio.monitoring.aspect.services.MetricCollectingService;
import presidio.output.domain.records.alerts.Alert;
import presidio.output.domain.records.users.User;
import presidio.output.processor.services.alert.AlertService;
import presidio.output.processor.services.user.UserScoreService;
import presidio.output.processor.services.user.UserService;

import java.time.Instant;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Date;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

/**
 * Created by shays on 17/05/2017.
 * Main output functionality is implemented here
 */

public class OutputExecutionServiceImpl implements OutputExecutionService {

    Logger logger = LoggerFactory.getLogger(this.getClass());
    private final UserScoreService userScoreService;
    private final AdeManagerSdk adeManagerSdk;
    private final AlertService alertService;
    private final UserService userService;
    private final int smartThresholdScoreForCreatingAlert;
    private final int smartPageSize;

    private final int SMART_THRESHOLD_FOR_GETTING_SMART_ENTITIES = 0;
    private final String UNIT_TYPE_LONG = "long";
    private final String NUMBER_OF_ALERTS_METRIC_NAME = "number.of.alerts.created";
    private final String ALERT_WITH_SEVERITY_METRIC_NAME = "alert.created.with.severity.";
    private final String LAST_SMART_TIME_METRIC_NAME = "last.smart.time.in.output";
    private final String TYPE_LONG = "long";
    private static final String ADE_SMART_USER_ID = "userId";
    private final String USER_GOT_SMART = "userGotSmart";

    @Autowired
    MetricCollectingService metricCollectingService;

    public OutputExecutionServiceImpl(AdeManagerSdk adeManagerSdk,
                                      AlertService alertService,
                                      UserService userService,
                                      UserScoreService userScoreService,
                                      int smartThresholdScoreForCreatingAlert, int smartPageSize) {
        this.adeManagerSdk = adeManagerSdk;
        this.alertService = alertService;
        this.userService = userService;
        this.userScoreService = userScoreService;
        this.smartPageSize = smartPageSize;
        this.smartThresholdScoreForCreatingAlert = smartThresholdScoreForCreatingAlert;
    }

    /**
     * Run the output processor main functionality which consist of the following-
     * 1. Get SMARTs from ADE and create Alerts entities for SMARTs with score higher than the threshold
     * 2. Enrich alerts with information from Input component (fields which were not part of the ADE schema)
     * 3. Alerts classification (rule based semantics)
     * 4. Calculates supporting information
     *
     * @param startDate
     * @param endDate
     * @throws Exception
     */
    @RunTime
    @Override
    public void run(Instant startDate, Instant endDate) throws Exception {
        logger.debug("Started output process with params: start date {}:{}, end date {}:{}.", CommonStrings.COMMAND_LINE_START_DATE_FIELD_NAME, startDate, CommonStrings.COMMAND_LINE_END_DATE_FIELD_NAME, endDate);
        PageIterator<SmartRecord> smartPageIterator = adeManagerSdk.getSmartRecords(smartPageSize, smartPageSize, new TimeRange(startDate, endDate), SMART_THRESHOLD_FOR_GETTING_SMART_ENTITIES);

        List<Alert> alerts = new ArrayList<>();
        List<User> users = new ArrayList<>();
        Set tags = new HashSet();
        tags.add(startDate.toString());
        List<SmartRecord> smarts = null;
        while (smartPageIterator.hasNext()) {
            smarts = smartPageIterator.next();
            for (SmartRecord smart : smarts) {
                User userEntity;
                String userId = smart.getContext().get(ADE_SMART_USER_ID);

                if (userId == null || userId.isEmpty()) {
                    logger.error("Failed to get user id from smart context, user id is null or empty for smart {}. skipping to next smart", smart.getId());
                    continue;
                }
                if ((userEntity = getCreatedUser(users, userId)) == null && (userEntity = getSingleUserEntityById(userId)) == null) {
                    //Need to create user and add it to about to be created list
                    userEntity = userService.createUserEntity(userId, startDate);
                    users.add(userEntity);
                    if (userEntity == null) {
                        logger.error("Failed to process user details for smart {}, skipping to next smart in the batch", smart.getId());
                        continue;
                    }

                }


                Alert alertEntity = alertService.generateAlert(smart, userEntity, smartThresholdScoreForCreatingAlert);
                if (alertEntity != null) {

                    userService.setUserAlertData(userEntity, alertEntity.getClassifications(), alertEntity.getIndicatorsNames(), alertEntity.getSeverity());
                    alerts.add(alertEntity);
                    metricCollectingService.addMetric(ALERT_WITH_SEVERITY_METRIC_NAME + alertEntity.getSeverity().name(), 1, tags, UNIT_TYPE_LONG);
                }
                if (getCreatedUser(users, userEntity.getUserId()) == null) {
                    users.add(userEntity);
                }
            }
        }

        users = storeUsers(users); //Get the generated users with the new elasticsearch ID
        storeAlerts(alerts);

        //Update the users severities
        if (CollectionUtils.isNotEmpty(users)) {
            this.userScoreService.updateSeveritiesForUsersList(users, true);
        }
        logger.info("output process application completed for start date {}:{}, end date {}:{}.", CommonStrings.COMMAND_LINE_START_DATE_FIELD_NAME, startDate, CommonStrings.COMMAND_LINE_END_DATE_FIELD_NAME, endDate);
        metricCollectingService.addMetric(NUMBER_OF_ALERTS_METRIC_NAME, alerts.size(), tags, UNIT_TYPE_LONG);
        if (CollectionUtils.isNotEmpty(smarts)) {
            metricCollectingService.addMetric(LAST_SMART_TIME_METRIC_NAME, smarts.get(smarts.size() - 1).getStartInstant().toEpochMilli(), new HashSet(Arrays.asList(startDate.toEpochMilli() + "")), TYPE_LONG);
        }
    }

    private User getSingleUserEntityById(String userId) {
        List<User> userEntities = userService.findUserByVendorUserIds(Arrays.asList(userId));
        if (CollectionUtils.isEmpty(userEntities)) {
            return null;
        }
        if (userEntities.size() > 1) {
            logger.error("Cannot have vendor userId more then once {}", userId);
        }
        return userEntities.get(0);
    }

    private User getCreatedUser(List<User> users, String userVendorId) {
        for (User user : users) {
            if (user.getUserId().equals(userVendorId)) {
                return user;
            }

        }
        return null;
    }

    public void recalculateUserScore() throws Exception {
        logger.info("Start Recalculating User Alert Data");
        this.userService.updateAllUsersAlertData();
        logger.info("Finish Recalculating User Score");
        logger.info("Start Updating UserSeverity");
        this.userScoreService.updateSeverities();
        logger.info("Finish Updating Users Severity");
    }

    private void storeAlerts(List<Alert> alerts) {
        if (CollectionUtils.isNotEmpty(alerts)) {
            alertService.save(alerts);
        }
        logger.info("{} output alerts were generated", alerts.size());
    }

    private List<User> storeUsers(List<User> users) {
        List<User> savedUsers = Collections.EMPTY_LIST;
        if (CollectionUtils.isNotEmpty(users)) {
            logger.info("{} output users were generated", users.size());
            savedUsers = userService.save(users);
        }
        return savedUsers;

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
