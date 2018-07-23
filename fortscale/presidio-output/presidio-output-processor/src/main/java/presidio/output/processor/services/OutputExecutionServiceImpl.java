package presidio.output.processor.services;

import fortscale.common.general.CommonStrings;
import fortscale.common.general.Schema;
import fortscale.utils.logging.Logger;
import fortscale.utils.pagination.PageIterator;
import fortscale.utils.time.TimeRange;
import org.apache.commons.collections.CollectionUtils;
import presidio.ade.domain.record.aggregated.SmartRecord;
import presidio.ade.sdk.common.AdeManagerSdk;
import presidio.monitoring.aspect.annotations.RunTime;
import presidio.output.domain.records.alerts.Alert;
import presidio.output.domain.records.users.User;
import presidio.output.domain.services.event.EventPersistencyService;
import presidio.output.processor.services.alert.AlertService;
import presidio.output.processor.services.user.UserService;
import presidio.output.processor.services.user.UsersAlertData;

import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.*;

/**
 * Created by shays on 17/05/2017.
 * Main output functionality is implemented here
 */
public class OutputExecutionServiceImpl implements OutputExecutionService {
    private static final Logger logger = Logger.getLogger(OutputExecutionServiceImpl.class);

    private final AdeManagerSdk adeManagerSdk;
    private final AlertService alertService;
    private final UserService userService;
    private final EventPersistencyService eventPersistencyService;
    private final OutputMonitoringService outputMonitoringService;
    private final int smartThresholdScoreForCreatingAlert;
    private final int smartPageSize;
    private final long retentionEnrichedEventsDays;
    private final long retentionOutputDataDays;


    private final int SMART_THRESHOLD_FOR_GETTING_SMART_ENTITIES = 0;

    private static final String ADE_SMART_USER_ID = "userId";

    public OutputExecutionServiceImpl(AdeManagerSdk adeManagerSdk,
                                      AlertService alertService,
                                      UserService userService,
                                      EventPersistencyService eventPersistencyService,
                                      OutputMonitoringService outputMonitoringService,
                                      int smartThresholdScoreForCreatingAlert, int smartPageSize, long retentionEnrichedEventsDays, long retentionOutputDataDays) {
        this.adeManagerSdk = adeManagerSdk;
        this.alertService = alertService;
        this.userService = userService;
        this.eventPersistencyService = eventPersistencyService;
        this.smartPageSize = smartPageSize;
        this.smartThresholdScoreForCreatingAlert = smartThresholdScoreForCreatingAlert;
        this.retentionEnrichedEventsDays = retentionEnrichedEventsDays;
        this.retentionOutputDataDays = retentionOutputDataDays;
        this.outputMonitoringService = outputMonitoringService;
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

        List<User> users = new ArrayList<>();
        List<SmartRecord> smarts = null;
        List<Alert> alerts = new ArrayList<>();
        int indicatorsCountHourly = 0;
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
                    UsersAlertData usersAlertData = new UsersAlertData(alertEntity.getContributionToUserScore(), 1, alertEntity.alertPrimaryClassification(), alertEntity.getIndicatorsNames());
                    userService.addUserAlertData(userEntity, usersAlertData);
                    alerts.add(alertEntity);
                    indicatorsCountHourly += alertEntity.getIndicatorsNum();

                    String classification = alertEntity.alertPrimaryClassification();
                    outputMonitoringService.reportTotalAlertCount(1, alertEntity.getSeverity(), classification, startDate);
                }
                if (getCreatedUser(users, userEntity.getUserId()) == null) {
                    users.add(userEntity);
                }

            }
            storeAlerts(alerts);
            outputMonitoringService.reportTotalAnomalyEvents(alerts, startDate);
            alerts.clear();
        }

        storeUsers(users); //Get the generated users with the new elasticsearch ID
        outputMonitoringService.reportTotalUsersCount(users.size(), startDate);
        outputMonitoringService.reportNumericMetric(outputMonitoringService.INDICATORS_COUNT_HOURLY_METRIC_NAME, indicatorsCountHourly, startDate);

        if (CollectionUtils.isNotEmpty(smarts)) {
            outputMonitoringService.reportLastSmartTimeProcessed(smarts.get(smarts.size() - 1).getStartInstant().toEpochMilli(), startDate);
        }
        logger.info("output process application completed for start date {}:{}, end date {}:{}.", CommonStrings.COMMAND_LINE_START_DATE_FIELD_NAME, startDate, CommonStrings.COMMAND_LINE_END_DATE_FIELD_NAME, endDate);
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

    public void updateAllUsersData(Instant startDate, Instant endDate) throws Exception {
        this.userService.updateUserData(endDate);
        logger.info("updating users data completed successfully");

        logger.info("Starting to report daily metrics");
        outputMonitoringService.reportDailyMetrics(startDate, endDate);
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
        logger.debug("Start deleting alerts and updating users score.");
        // delete alerts
        List<Alert> cleanedAlerts = alertService.cleanAlerts(startDate, endDate);

        // update user scores
        updateUsersScoreFromDeletedAlerts(cleanedAlerts);

    }

    @Override
    public void applyRetentionPolicy(Instant endDate) throws Exception {
        List<Schema> schemas = Arrays.asList(Schema.values());

        schemas.forEach(schema -> {
            logger.debug("Start retention clean to mongo for schema {}", schema);
            eventPersistencyService.remove(schema, Instant.EPOCH, endDate.minus(retentionEnrichedEventsDays, ChronoUnit.DAYS));
        });
        clean(Instant.EPOCH, endDate.minus(retentionOutputDataDays, ChronoUnit.DAYS));
    }

    private void updateUsersScoreFromDeletedAlerts(List<Alert> cleanedAlerts) {
        Set<User> usersToUpdate = new HashSet<>();
        cleanedAlerts.forEach(alert -> {
            if (!usersToUpdate.contains(alert.getUserId())) {
                usersToUpdate.add(userService.findUserById(alert.getUserId()));
            }
        });
        logger.info("{} users are going to update score", usersToUpdate.size());
        usersToUpdate.forEach(user -> {
            userService.recalculateUserAlertData(user);
        });
        userService.save(new ArrayList<>(usersToUpdate));
    }


    @Override
    public void cleanAll() throws Exception {
        // TODO: Implement
    }
}
