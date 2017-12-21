package presidio.output.processor.services;

import fortscale.common.general.CommonStrings;
import fortscale.utils.logging.Logger;
import fortscale.utils.pagination.PageIterator;
import fortscale.utils.time.TimeRange;
import org.apache.commons.collections.CollectionUtils;
import org.springframework.beans.factory.annotation.Autowired;
import presidio.ade.domain.record.aggregated.SmartRecord;
import presidio.ade.sdk.common.AdeManagerSdk;
import presidio.monitoring.aspect.annotations.RunTime;
import presidio.monitoring.records.Metric;
import presidio.monitoring.sdk.api.services.enums.MetricEnums;
import presidio.monitoring.services.MetricCollectingService;
import presidio.output.commons.services.user.UserSeverityService;
import presidio.output.domain.records.alerts.Alert;
import presidio.output.domain.records.users.User;
import presidio.output.processor.services.alert.AlertService;
import presidio.output.processor.services.user.UserService;
import presidio.output.processor.services.user.UsersAlertData;

import java.time.Instant;
import java.util.*;

/**
 * Created by shays on 17/05/2017.
 * Main output functionality is implemented here
 */
public class OutputExecutionServiceImpl implements OutputExecutionService {
    private static final Logger logger = Logger.getLogger(OutputExecutionServiceImpl.class);

    private final UserSeverityService userSeverityService;
    private final AdeManagerSdk adeManagerSdk;
    private final AlertService alertService;
    private final UserService userService;
    private final int smartThresholdScoreForCreatingAlert;
    private final int smartPageSize;

    private final int SMART_THRESHOLD_FOR_GETTING_SMART_ENTITIES = 0;
    private final String NUMBER_OF_ALERTS_METRIC_NAME = "number_of_alerts_created";
    private final String ALERT_WITH_SEVERITY_METRIC_PREFIX = "alert_created_with_severity.";
    private final String LAST_SMART_TIME_METRIC_NAME = "last_smart_time";
    private static final String ADE_SMART_USER_ID = "userId";

    @Autowired
    MetricCollectingService metricCollectingService;

    public OutputExecutionServiceImpl(AdeManagerSdk adeManagerSdk,
                                      AlertService alertService,
                                      UserService userService,
                                      UserSeverityService userSeverityService,
                                      int smartThresholdScoreForCreatingAlert, int smartPageSize) {
        this.adeManagerSdk = adeManagerSdk;
        this.alertService = alertService;
        this.userService = userService;
        this.userSeverityService = userSeverityService;
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
        Map<MetricEnums.MetricTagKeysEnum, String> tags = new HashMap<>();
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
                    userEntity = userService.createUserEntity(userId);
                    users.add(userEntity);
                    if (userEntity == null) {
                        logger.error("Failed to process user details for smart {}, skipping to next smart in the batch", smart.getId());
                        continue;
                    }

                }


                Alert alertEntity = alertService.generateAlert(smart, userEntity, smartThresholdScoreForCreatingAlert);
                if (alertEntity != null) {
                    UsersAlertData usersAlertData = new UsersAlertData(alertEntity.getContributionToUserScore(),1,alertEntity.getPreferredClassification(),alertEntity.getIndicatorsNames());
                    userService.addUserAlertData(userEntity, usersAlertData);
                    alerts.add(alertEntity);
                    metricCollectingService.addMetric(new Metric.MetricBuilder().setMetricName(ALERT_WITH_SEVERITY_METRIC_PREFIX + alertEntity.getSeverity().name()).
                            setMetricValue(1).
                            setMetricTags(tags).
                            setMetricUnit(MetricEnums.MetricUnitType.NUMBER).
                            setMetricLogicTime(startDate).
                            build());
                }
                if (getCreatedUser(users, userEntity.getUserId()) == null) {
                    users.add(userEntity);
                }
            }
        }

        users = storeUsers(users); //Get the generated users with the new elasticsearch ID
        storeAlerts(alerts);

        logger.info("output process application completed for start date {}:{}, end date {}:{}.", CommonStrings.COMMAND_LINE_START_DATE_FIELD_NAME, startDate, CommonStrings.COMMAND_LINE_END_DATE_FIELD_NAME, endDate);
        metricCollectingService.addMetric(new Metric.MetricBuilder().setMetricName(NUMBER_OF_ALERTS_METRIC_NAME).
                setMetricValue(alerts.size()).
                setMetricTags(tags).
                setMetricUnit(MetricEnums.MetricUnitType.NUMBER).
                setMetricLogicTime(startDate).
                build());
        if (CollectionUtils.isNotEmpty(smarts)) {
            tags = new HashMap();
            metricCollectingService.addMetric(new Metric.MetricBuilder().setMetricName(LAST_SMART_TIME_METRIC_NAME).
                    setMetricValue(smarts.get(smarts.size() - 1).getStartInstant().toEpochMilli()).
                    setMetricTags(tags).setMetricUnit(MetricEnums.MetricUnitType.NUMBER).
                    setMetricLogicTime(startDate).
                    build());
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
        this.userSeverityService.updateSeverities();
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

        // delete alerts
        List<Alert> cleanedAlerts = alertService.cleanAlerts(startDate, endDate);

        // update user scores
        Set<User> usersToUpdate = new HashSet<User>();
        cleanedAlerts.forEach(alert -> {
            if (!usersToUpdate.contains(alert.getUserId())) {
                usersToUpdate.add(userService.findUserById(alert.getUserId()));
            }
        });
        usersToUpdate.forEach(user -> {
            userService.recalculateUserAlertData(user);
        });
        userService.save(new ArrayList<User>(usersToUpdate));

    }

    @Override
    public void cleanAll() throws Exception {
        // TODO: Implement
    }
}
