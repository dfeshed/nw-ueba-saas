package presidio.output.processor.services.user;

import fortscale.utils.logging.Logger;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import presidio.output.commons.services.alert.AlertSeverityService;
import presidio.output.domain.records.alerts.Alert;
import presidio.output.domain.records.alerts.AlertQuery;
import presidio.output.domain.records.users.User;
import presidio.output.domain.records.users.UserQuery;
import presidio.output.domain.services.alerts.AlertPersistencyService;
import presidio.output.domain.services.users.UserPersistencyService;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.*;

/**
 * Created by shays on 27/08/2017.
 */
public class UserScoreServiceImpl implements UserScoreService {
    private static final Logger log = Logger.getLogger(UserScoreServiceImpl.class);

    private UserPersistencyService userPersistencyService;

    private AlertPersistencyService alertPersistencyService;

    private AlertSeverityService alertSeverityService;

    private int defaultAlertsBatchSize;

    public int defaultUsersBatchSize;

    public
    UserScoreServiceImpl(UserPersistencyService userPersistencyService,
                                AlertPersistencyService alertPersistencyService,
                                AlertSeverityService alertSeverityService,
                                int defaultAlertsBatchSize,
                                int defaultUsersBatchSize) {
        this.userPersistencyService = userPersistencyService;
        this.alertPersistencyService = alertPersistencyService;
        this.alertSeverityService = alertSeverityService;
        this.defaultAlertsBatchSize = defaultAlertsBatchSize;
        this.defaultUsersBatchSize = defaultUsersBatchSize;
    }


    /**
     * Iterate all users which have score more then 0, and reset the score to 0.
     * Excluded user ids are users which should not be reset.
     *
     * @param excludedUsersIds is the list of users which should
     */
    @Override
    public void clearUserScoreForUsersThatShouldNotHaveScore(Set<String> excludedUsersIds) {
        log.debug("Check if there are users without relevant alert and score higher then 0");

        UserQuery.UserQueryBuilder userQueryBuilder = new UserQuery.UserQueryBuilder().minScore(1)
                .pageSize(defaultUsersBatchSize)
                .pageNumber(0);
        Page<User> usersPage = userPersistencyService.find(userQueryBuilder.build());

        log.debug("found " + usersPage.getTotalElements() + " users which score that should be reset");
        List<User> clearedUsersList = new ArrayList<>();
        while (usersPage != null && usersPage.hasContent()) {
            usersPage.getContent().forEach(user -> {
                if (!excludedUsersIds.contains(user.getId())) {
                    user.setScore(0D);
                    user.setSeverity(null);
                    clearedUsersList.add(user);
                }
            });

            usersPage = getNextUserPage(userQueryBuilder, usersPage);
        }

        log.info("Reseting " + clearedUsersList.size() + " users scores and severity");
        userPersistencyService.save(clearedUsersList);
    }

    /**
     * Iterate on all alerts for users in the last alertEffectiveDurationInDays days,
     * and calculate the user score based on the effective alerts.
     *
     * @return map of each userId to an object that contains the new score and number of alerts
     */
    @Override
    public Map<String, UsersAlertData> calculateUserScores(int alertEffectiveDurationInDays) {

        List<LocalDateTime> days = getListOfLastXdays(alertEffectiveDurationInDays);

        Map<String, UsersAlertData> aggregatedUserScore = new HashMap<>();
        //TODO: alsom filter by status >

        if (days != null && days.size() > 0) {
            for (LocalDateTime startOfDay : days) {

                log.info("Start Calculate user score for day " + startOfDay + " (Calculation, without persistency");
                long startTime = Date.from(startOfDay.atZone(ZoneId.systemDefault()).toInstant()).getTime();
                LocalDateTime endOfDay = startOfDay.plusDays(1).minusSeconds(1);
                long endTime = Date.from(endOfDay.atZone(ZoneId.systemDefault()).toInstant()).getTime();


                AlertQuery.AlertQueryBuilder alertQueryBuilder = new AlertQuery.AlertQueryBuilder()
                        .filterByStartDate(startTime)
                        .filterByEndDate(endTime)
                        .sortField(Alert.START_DATE, true)
                        .setPageSize(this.defaultAlertsBatchSize)
                        .setPageNumber(0);

                AlertQuery alertQuery = alertQueryBuilder.build();

                Page<Alert> alertsPage = alertPersistencyService.find(alertQuery);
                while (alertsPage != null && alertsPage.hasContent()) {
                    alertsPage.getContent().forEach(alert -> {
                        String userId = alert.getUserId();
                        if (aggregatedUserScore.containsKey(userId)) {
                            UsersAlertData usersAlertData = aggregatedUserScore.get(userId);
                            usersAlertData.incrementUserScore(alert.getContributionToUserScore());
                            usersAlertData.incrementAlertsCount();
                            usersAlertData.addClassifications(alert.getClassifications());
                            usersAlertData.addIndicators(alert.getIndicatorsNames());
                        } else {
                            aggregatedUserScore.put(userId, new UsersAlertData(alert.getContributionToUserScore(), 1, alert.getClassifications(), alert.getIndicatorsNames()));
                        }

                    });
                    alertsPage = getNextAlertPage(alertQueryBuilder, alertsPage);
                }
            }
        }
        return aggregatedUserScore;
    }

    private List<LocalDateTime> getListOfLastXdays(int days) {
        LocalDate endDate = LocalDate.now();
        LocalDate startTime = endDate.minusDays(days);
        List<LocalDateTime> dates = new ArrayList<>();
        for (LocalDate d = startTime; !d.isAfter(endDate); d = d.plusDays(1)) {
            LocalDateTime time = d.atStartOfDay();
            dates.add(time);
        }
        return dates;
    }





    /**
     * Return the next user page or null if no next
     *
     * @param page
     * @return
     */

    private Page<User> getNextUserPage(UserQuery.UserQueryBuilder userQueryBuilder, Page<User> page) {
        if (page.hasNext()) {
            Pageable pageable = page.nextPageable();
            userQueryBuilder.pageNumber(pageable.getPageNumber());
            page = userPersistencyService.find(userQueryBuilder.build());

        } else {
            page = null;
        }
        return page;
    }

    /**
     * Return the next user page or null if no next
     *
     * @param page
     * @return
     */

    private Page<Alert> getNextAlertPage(AlertQuery.AlertQueryBuilder alertQueryBuilder, Page<Alert> page) {
        if (page.hasNext()) {
            Pageable pageable = page.nextPageable();
            alertQueryBuilder.setPageNumber(pageable.getPageNumber());
            page = alertPersistencyService.find(alertQueryBuilder.build());

        } else {
            page = null;
        }
        return page;
    }



}
