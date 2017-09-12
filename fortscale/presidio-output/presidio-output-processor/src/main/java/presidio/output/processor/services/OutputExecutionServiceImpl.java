package presidio.output.processor.services;

import fortscale.common.general.CommonStrings;
import fortscale.utils.pagination.PageIterator;
import fortscale.utils.time.TimeRange;
import org.apache.commons.collections.CollectionUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import presidio.ade.domain.record.aggregated.AdeAggregationRecord;
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

    private final UserScoreService userScoreService;
    private final AdeManagerSdk adeManagerSdk;
    private final AlertService alertService;
    private final UserService userService;
    private final int smartThreshold;
    private final int smartPageSize;

    public OutputExecutionServiceImpl(AdeManagerSdk adeManagerSdk,
                                      AlertService alertService,
                                      UserService userService,
                                      UserScoreService userScoreService,
                                      int smartThreshold, int smartPageSize) {
        this.adeManagerSdk = adeManagerSdk;
        this.alertService = alertService;
        this.userService = userService;
        this.userScoreService = userScoreService;
        this.smartPageSize = smartPageSize;
        this.smartThreshold = smartThreshold;
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
        PageIterator<SmartRecord> smartPageIterator = adeManagerSdk.getSmartRecords(smartPageSize, smartPageSize, new TimeRange(startDate, endDate), smartThreshold);

        List<Alert> alerts = new ArrayList<Alert>();
        List<User> users = new ArrayList<User>();
        while (smartPageIterator.hasNext()) {
            List<SmartRecord> smarts = smartPageIterator.next();
            for (SmartRecord smart : smarts) {
                AdeAggregationRecord indicators = smart.getAggregationRecords().get(0);
                if (indicators == null) {
                    logger.error("Failed to retrieve user id from smart because indicators list is empty for smart {}. skipping to next smart", smart.getId());
                    continue;
                }
                String userId = indicators.getContext().get("userId");//TODO- temporary fix, ADE team should provide user id on the smart pojo
                User userEntity = userService.findUserById(userId);
                if (userEntity == null) {
                    userEntity = userService.createUserEntity(userId);
                }
                if(userEntity == null) {
                    logger.error("Failed to process user details for smart {}, skipping to next smart in the batch", smart.getId());
                    continue;
                }

                Alert alertEntity = alertService.generateAlert(smart, userEntity);
                if (alertEntity != null) {
                    userService.setUserAlertData(userEntity, alertEntity.getClassifications(), null);//TODO:change null to indicators when alert pojo will have indicators
                    alerts.add(alertEntity);
                }

                users.add(userEntity);
            }
        }

        storeAlerts(alerts);
        storeUsers(users);
        this.userScoreService.updateSeveritiesForUsersList(users, true);
        logger.info("output process application completed for start date {}:{}, end date {}:{}.", CommonStrings.COMMAND_LINE_START_DATE_FIELD_NAME, startDate, CommonStrings.COMMAND_LINE_END_DATE_FIELD_NAME, endDate);

    }

    public void recalculateUserScore() throws Exception{
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

    private void storeUsers(List<User> users) {
        if (CollectionUtils.isNotEmpty(users)) {
            userService.save(users);
            this.userScoreService.updateSeveritiesForUsersList(users,true);
        }
        logger.info("{} output users were generated", users.size());
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
