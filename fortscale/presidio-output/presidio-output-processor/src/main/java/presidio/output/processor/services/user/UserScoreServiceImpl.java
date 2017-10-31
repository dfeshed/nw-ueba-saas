package presidio.output.processor.services.user;

import org.apache.commons.math3.stat.descriptive.rank.Percentile;
import org.slf4j.Logger;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import presidio.output.domain.records.alerts.Alert;
import presidio.output.domain.records.alerts.AlertEnums;
import presidio.output.domain.records.alerts.AlertQuery;
import presidio.output.domain.records.users.User;
import presidio.output.domain.records.users.UserQuery;
import presidio.output.domain.records.users.UserSeverity;
import presidio.output.domain.services.alerts.AlertPersistencyService;
import presidio.output.domain.services.users.UserPersistencyService;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.TreeMap;
import java.util.concurrent.atomic.AtomicInteger;


/**
 * Created by shays on 27/08/2017.
 */
public class UserScoreServiceImpl implements UserScoreService {

    private UserPersistencyService userPersistencyService;

    private int percentThresholdCritical;

    private int percentThresholdHigh;

    private int percentThresholdMedium;

    private Map<AlertEnums.AlertSeverity, Double> alertSeverityToScoreContribution;

    private AlertPersistencyService alertPersistencyService;

    private Logger log = org.slf4j.LoggerFactory.getLogger(this.getClass());

    private int defaultAlertsBatchSize;
    public int defaultUsersBatchSize;

    public UserScoreServiceImpl(UserPersistencyService userPersistencyService,
                                AlertPersistencyService alertPersistencyService,
                                int defaultAlertsBatchSize,
                                int defaultUsersBatchSize,
                                int percentThresholdCritical,
                                int percentThresholdHigh,
                                int percentThresholdMedium,
                                double alertContributionCritical,
                                double alertContributionHigh,
                                double alertContributionMedium,
                                double alertContributionLow) {
        this.userPersistencyService = userPersistencyService;
        this.alertPersistencyService = alertPersistencyService;

        this.defaultAlertsBatchSize = defaultAlertsBatchSize;
        this.defaultUsersBatchSize = defaultUsersBatchSize;

        this.percentThresholdCritical = percentThresholdCritical;
        this.percentThresholdHigh = percentThresholdHigh;
        this.percentThresholdMedium = percentThresholdMedium;

        alertSeverityToScoreContribution = new TreeMap<>();
        alertSeverityToScoreContribution.put(AlertEnums.AlertSeverity.CRITICAL, alertContributionCritical);
        alertSeverityToScoreContribution.put(AlertEnums.AlertSeverity.HIGH, alertContributionHigh);
        alertSeverityToScoreContribution.put(AlertEnums.AlertSeverity.MEDIUM, alertContributionMedium);
        alertSeverityToScoreContribution.put(AlertEnums.AlertSeverity.LOW, alertContributionLow);


    }

    @Override
    public void increaseUserScoreWithoutSaving(AlertEnums.AlertSeverity alertSeverity, User user) {
        double userScoreContribution = this.alertSeverityToScoreContribution.get(alertSeverity);
        double userScore = user.getScore();
        userScore += userScoreContribution;
        user.setScore(userScore);
    }

    /**
     * Calculate severities map
     *
     * @param userScores
     * @return map from score to severity
     */
    private UserScoreToSeverity getSeveritiesMap(double[] userScores) {
        Percentile p = new Percentile();

        p.setData(userScores);

        double ceilScoreForLowSeverity = p.evaluate(percentThresholdMedium); //The maximum score that user score still considered low
        double ceilScoreForMediumSeverity = p.evaluate(percentThresholdHigh);//The maximum score that user score still considered medium
        double ceilScoreForHighSeverity = p.evaluate(percentThresholdCritical); //The maximum score that user score still considered high

        UserScoreToSeverity userScoreToSeverity = new UserScoreToSeverity(ceilScoreForLowSeverity, ceilScoreForMediumSeverity, ceilScoreForHighSeverity);


        return userScoreToSeverity;

    }


    @Override
    public void updateSeverities() {
        final double[] scores = getScoresArray();
        final UserScoreToSeverity severitiesMap = getSeveritiesMap(scores);
        UserQuery.UserQueryBuilder userQueryBuilder = new UserQuery.UserQueryBuilder().pageNumber(0).pageSize(defaultUsersBatchSize).sortField(new Sort(new Sort.Order(Sort.Direction.ASC, User.SCORE_FIELD_NAME)));
        Page<User> page = userPersistencyService.find(userQueryBuilder.build());

        while (page != null && page.hasContent()) {
            log.info("Updating severity for page: " + page.toString());
            updateSeveritiesForUsersList(severitiesMap, page.getContent(), true);
            page = getNextUserPage(userQueryBuilder, page);

        }
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
                if (!excludedUsersIds.contains(user.getUserId())) {
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
                        AlertEnums.AlertSeverity severity = alert.getSeverity();
                        double userScoreContribution = this.alertSeverityToScoreContribution.get(severity);
                        if (aggregatedUserScore.containsKey(userId)) {
                            UsersAlertData usersAlertData = aggregatedUserScore.get(userId);
                            usersAlertData.incrementUserScore(userScoreContribution);
                            usersAlertData.incrementAlertsCount();
                        } else {
                            aggregatedUserScore.put(userId, new UsersAlertData(userScoreContribution, 1));
                        }

                    });
                    alertsPage = getNextAlertPage(alertQueryBuilder, alertsPage);
                }
            }
        }
        return aggregatedUserScore;
    }

    @Override
    public Double getUserScoreContributionFromSeverity(AlertEnums.AlertSeverity severity) {
        return this.alertSeverityToScoreContribution.get(severity);
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

    public void updateSeveritiesForUsersList(List<User> users, boolean persistChanges) {
        final double[] scores = getScoresArray();
        final UserScoreToSeverity severitiesMap = getSeveritiesMap(scores);
        updateSeveritiesForUsersList(severitiesMap, users, persistChanges);

    }

    private void updateSeveritiesForUsersList(UserScoreToSeverity severitiesMap, List<User> users, boolean persistChanges) {
        List<User> updatedUsers = new ArrayList<>();
        if (users == null) {
            return;
        }
        users.forEach(user -> {
            double userScore = user.getScore();
            UserSeverity newUserSeverity = severitiesMap.getSeverity(userScore);

            log.debug("Updating user severity for userId: " + user.getUserId());
            if (!newUserSeverity.equals(user.getSeverity())) {
                user.setSeverity(newUserSeverity);
                updatedUsers.add(user); //Update user only if severity changes
            }
        });

        if (updatedUsers.size() > 0 && persistChanges) {
            userPersistencyService.save(updatedUsers);
        }
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

    /**
     * This function load all users' score and store it in a double array
     * Only for user scores above 0
     */
    private double[] getScoresArray() {


        Sort sort = new Sort(Sort.Direction.ASC, User.SCORE_FIELD_NAME);
        UserQuery.UserQueryBuilder userQueryBuilder = new UserQuery.UserQueryBuilder().minScore(1).pageNumber(0).pageSize(this.defaultUsersBatchSize).sort(sort);
        Page<User> page = userPersistencyService.find(userQueryBuilder.build());
        int numberOfElements = new Long(page.getTotalElements()).intValue();
        double[] scores = new double[numberOfElements];
        AtomicInteger courser = new AtomicInteger(0);


        while (page != null && page.hasContent()) {
            page.getContent().forEach(user -> {
                scores[courser.getAndAdd(1)] = user.getScore();
            });
            page = getNextUserPage(userQueryBuilder, page);

        }

        return scores;
    }


    public static class UserScoreToSeverity {
        private double ceilScoreForLowSeverity;
        private double ceilScoreForMediumSeverity;
        private double ceilScoreForHighSeverity;

        public UserScoreToSeverity(double ceilScoreForLowSeverity, double ceilScoreForMediumSeverity, double ceilScoreForHighSeverity) {
            this.ceilScoreForLowSeverity = ceilScoreForLowSeverity;
            this.ceilScoreForMediumSeverity = ceilScoreForMediumSeverity;
            this.ceilScoreForHighSeverity = ceilScoreForHighSeverity;
        }

        public UserSeverity getSeverity(double score) {
            if (score <= ceilScoreForLowSeverity) {
                return UserSeverity.LOW;
            } else if (score <= ceilScoreForMediumSeverity) {
                return UserSeverity.MEDIUM;
            } else if (score <= ceilScoreForHighSeverity) {
                return UserSeverity.HIGH;
            } else {
                return UserSeverity.CRITICAL;
            }
        }
    }

}
