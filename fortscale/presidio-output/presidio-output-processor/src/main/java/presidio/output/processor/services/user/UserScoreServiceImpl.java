package presidio.output.processor.services.user;

import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.collections.SetUtils;
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
import org.apache.commons.math3.stat.descriptive.rank.Percentile;

import java.time.LocalDate;
import java.util.*;
import java.util.concurrent.atomic.AtomicInteger;


/**
 * Created by shays on 27/08/2017.
 */
public class UserScoreServiceImpl implements UserScoreService{
    private UserPersistencyService userPersistencyService;

    public int defaultUsersBatchSize;

    private int percentThresholdCritical;

    private int percentThresholdHigh;

    private int percentThresholdMedium;

    private Map<AlertEnums.AlertSeverity, Double> alertSeverityToScoreContribution;

    private AlertPersistencyService alertPersistencyService;

    private int alertEffectiveDurationInDays;//How much days an alert can affect on the user score
    private int defaultAlertsBatchSize;

    public UserScoreServiceImpl(UserPersistencyService userPersistencyService,
                                AlertPersistencyService alertPersistencyService,
                                int defaultUsersBatchSize,
                                int defaultAlertsBatchSize,
                                int alertEffectiveDurationInDays,
                                int percentThresholdCritical,
                                int percentThresholdHigh,
                                int percentThresholdMedium,
                                double alertContributionCritical,
                                double alertContributionHigh,
                                double alertContributionMedium,
                                double alertContributionLow
                                ) {
        this.userPersistencyService = userPersistencyService;
        this.alertPersistencyService = alertPersistencyService;
        this.alertEffectiveDurationInDays = alertEffectiveDurationInDays;

        this.defaultUsersBatchSize = defaultUsersBatchSize;
        this.defaultAlertsBatchSize = defaultAlertsBatchSize;

        this.percentThresholdCritical = percentThresholdCritical;
        this.percentThresholdHigh = percentThresholdHigh;
        this.percentThresholdMedium = percentThresholdMedium;

        alertSeverityToScoreContribution=new TreeMap<>();
        alertSeverityToScoreContribution.put(AlertEnums.AlertSeverity.CRITICAL,alertContributionCritical);
        alertSeverityToScoreContribution.put(AlertEnums.AlertSeverity.HIGH,alertContributionHigh);
        alertSeverityToScoreContribution.put(AlertEnums.AlertSeverity.MEDIUM,alertContributionMedium);
        alertSeverityToScoreContribution.put(AlertEnums.AlertSeverity.LOW,alertContributionLow);


    }

    @Override
    public void increaseUserScoreWithoutSaving(Alert alert, User user){
        AlertEnums.AlertSeverity alertSeverity = alert.getSeverity();
        double userScoreContribution = this.alertSeverityToScoreContribution.get(alertSeverity);

        double userScore = user.getUserScore();
        userScore+=userScoreContribution;
        user.setUserScore(userScore);

    }

    /**
     * Calculate severities map
     * @param userScores
     * @return map from score to severity
     */
    private UserScoreToSeverity getSeveritiesMap(double[] userScores){


        double value = 20;
        Percentile p =new Percentile();

        p.setData(userScores);


        TreeMap<Double, UserSeverity> severitiesMap = new TreeMap<>();

        double ceilScoreForLowSeverity = p.evaluate(percentThresholdMedium); //The maximum score that user score still considered low
        double ceilScoreForMediumSeverity = p.evaluate(percentThresholdHigh);//The maximum score that user score still considered medium
        double ceilScoreForHighSeverity = p.evaluate(percentThresholdCritical); //The maximum score that user score still considered high

        UserScoreToSeverity userScoreToSeverity = new UserScoreToSeverity(ceilScoreForLowSeverity,ceilScoreForMediumSeverity,ceilScoreForHighSeverity);


        return userScoreToSeverity;

    }


    @Override
    public void updateSeverities(){
       final double[] scores= getScoresArray();
       final  UserScoreToSeverity severitiesMap = getSeveritiesMap(scores);
       UserQuery.UserQueryBuilder userQueryBuilder = new UserQuery.UserQueryBuilder().pageNumber(0).pageSize(defaultUsersBatchSize);
       Page<User> page = userPersistencyService.find(userQueryBuilder.build());

        while (page != null && page.hasContent()) {
            updateSeveritiesForUsersList(severitiesMap, page.getContent(),true);
            page = getNextUserPage(userQueryBuilder,page);

        }
    }

    public boolean updateAllUsersScores() {

        //Get map of users ids to new score
        Map<String, Double> aggregatedUserScore = calculateUserScores();

        //Get users in batches and update the score only if it changed, and add to changesUsers

        Set<String> usersIDForBatch = new HashSet<>();
        List<User> changedUsers = new ArrayList<>();
        for (Map.Entry<String, Double> entry : aggregatedUserScore.entrySet()){

            usersIDForBatch.add(entry.getKey());
            if (usersIDForBatch.size()<defaultUsersBatchSize){
                continue;
            }
            //Update user score batch
            changedUsers.addAll(updateUserScoreForBatch(aggregatedUserScore, usersIDForBatch));

            //After batch calculation, reset the set
            usersIDForBatch.clear();

        }

        if(!usersIDForBatch.isEmpty()){
            //there is leftover smaller then batch size
            changedUsers.addAll(updateUserScoreForBatch(aggregatedUserScore, usersIDForBatch));
        }

        //Persist users that the score changed
        userPersistencyService.save(changedUsers);

        //Clean users which not have alert in the last 90 days, but still have score
        clearUserScoreForUsersThatShouldNotHaveScore(aggregatedUserScore.keySet());

        return true;

    }

    /**
     * Iterate all users which have score more then 0, and reset the score to 0.
     * Excluded user ids are users which should not be reset.
     * @param excludedUsersIds is the list of users which should
     */
    private void clearUserScoreForUsersThatShouldNotHaveScore(Set<String> excludedUsersIds) {
        UserQuery.UserQueryBuilder userQueryBuilder = new UserQuery.UserQueryBuilder().minScore(1)
                                                                                        .filterByNotHaveAnyOfIds(excludedUsersIds)
                                                                                        .pageSize(defaultUsersBatchSize)
                                                                                        .pageNumber(1);
        Page<User> usersPage = userPersistencyService.find(userQueryBuilder.build());


        List<User> clearedUsersList = new ArrayList<>();
        while (usersPage!=null && usersPage.hasContent()){
            usersPage.getContent().forEach(user-> {
                user.setUserScore(0D);
                clearedUsersList.add(user);
            });
            usersPage = getNextUserPage(userQueryBuilder,usersPage);
        }

        userPersistencyService.save(clearedUsersList);
    }

    /**
     * Iterate on all alerts for users in the last alertEffectiveDurationInDays days,
     * and calculate the user score based on the effective alerts.
     *
     * @return map of each user to his new score
     */
    private Map<String,Double> calculateUserScores() {
        LocalDate fromDate = LocalDate.now().minusDays(this.alertEffectiveDurationInDays);

        Map<String, Double> aggregatedUserScore = new HashMap<>() ;
        //TODO: alsom filter by status >
        AlertQuery.AlertQueryBuilder alertQueryBuilder = new AlertQuery.AlertQueryBuilder().filterByStartDate(fromDate.toEpochDay())
                .sortField(Alert.START_DATE, true)
                .pageSize(this.defaultAlertsBatchSize)
                .pageNumber(0);

        AlertQuery alertQuery = alertQueryBuilder.build();

        Page<Alert> alertsPage = alertPersistencyService.find(alertQuery);
        while (alertsPage != null && alertsPage.hasContent()) {
            alertsPage.getContent().forEach(alert -> {
                String userId = alert.getUserId();
                AlertEnums.AlertSeverity severity = alert.getSeverity();
                double userScoreContribution = this.alertSeverityToScoreContribution.get(severity);
                aggregatedUserScore.compute(userId,(userIdKey,value)->{
                  return value ==null? userScoreContribution:value+userScoreContribution;
                } );

            });
            alertsPage = getNextAlertPage(alertQueryBuilder, alertsPage);
        }

        return  aggregatedUserScore;
    }

    /**
     *
     * @param aggregatedUserScore - all the users which have at least one alert in the last 3 month with the new score
     * @param usersIDForBatch - only the ids in the current handled batch
     * @return  List of updated users
     */
    private List<User> updateUserScoreForBatch(Map<String, Double> aggregatedUserScore, Set<String> usersIDForBatch) {
        List<User> changedUsers=new ArrayList<>();
        UserQuery.UserQueryBuilder userQueryBuilder = new UserQuery.UserQueryBuilder().filterByIds(usersIDForBatch)
                                                                                      .pageNumber(0)
                                                                                      .pageSize(usersIDForBatch.size());
        UserQuery userQuery = userQueryBuilder.build();
        Page<User> users = userPersistencyService.find(userQuery);
        users.forEach(user->{
            double newUserScore = aggregatedUserScore.get(user.getUserId());
            if (user.getUserScore() != newUserScore){
                user.setUserScore(newUserScore);
                changedUsers.add(user);
            }
        });

        return changedUsers;
    }


    public void updateSeveritiesForUsersList(List<User> users, boolean persistChanges) {
        final double[] scores= getScoresArray();
        final  UserScoreToSeverity severitiesMap = getSeveritiesMap(scores);
        updateSeveritiesForUsersList(severitiesMap,users,persistChanges);

    }
    private void updateSeveritiesForUsersList(UserScoreToSeverity severitiesMap, List<User> users, boolean persistChanges) {
        List<User> updatedUsers = new ArrayList<>();
        users.forEach(user -> {
            double userScore = user.getUserScore();
            UserSeverity newUserSeverity =  severitiesMap.getSeverity(userScore);
            if (!newUserSeverity.equals(user.getUserSeverity())){
                user.setUserSeverity(newUserSeverity);
                updatedUsers.add(user); //Update user only if severity changes
            }
        });

        if (updatedUsers.size()>0 && persistChanges){
            userPersistencyService.save(updatedUsers);
        }
    }

    /**
     * Return the next user page or null if no next
     * @param page
     * @return
     */

    private Page<User> getNextUserPage(UserQuery.UserQueryBuilder userQueryBuilder,Page<User> page) {
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
     * @param page
     * @return
     */

    private Page<Alert> getNextAlertPage(AlertQuery.AlertQueryBuilder alertQueryBuilder, Page<Alert> page) {
        if (page.hasNext()) {
            Pageable pageable = page.nextPageable();
            alertQueryBuilder.pageNumber(pageable.getPageNumber());
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


        Sort sort = new Sort(Sort.Direction.ASC,"score");
        UserQuery.UserQueryBuilder userQueryBuilder = new UserQuery.UserQueryBuilder().minScore(1).pageNumber(0).pageSize(this.defaultUsersBatchSize).sort(sort);
        Page<User> page = userPersistencyService.find(userQueryBuilder.build());
        int numberOfElements = new Long(page.getTotalElements()).intValue();
        double[] scores = new double[numberOfElements];
        AtomicInteger courser = new AtomicInteger(0);


        while (page != null && page.hasContent()) {
            page.getContent().forEach(user -> {
                scores[courser.getAndAdd(1)] = user.getUserScore();
            });
            page = getNextUserPage(userQueryBuilder,page);

        }

        return scores;
    }


    public static class UserScoreToSeverity{
        private double ceilScoreForLowSeverity;
        private double ceilScoreForMediumSeverity;
        private double ceilScoreForHighSeverity;

        public UserScoreToSeverity(double ceilScoreForLowSeverity, double ceilScoreForMediumSeverity, double ceilScoreForHighSeverity) {
            this.ceilScoreForLowSeverity = ceilScoreForLowSeverity;
            this.ceilScoreForMediumSeverity = ceilScoreForMediumSeverity;
            this.ceilScoreForHighSeverity = ceilScoreForHighSeverity;
        }

        public UserSeverity getSeverity(double score){
            if (score<=ceilScoreForLowSeverity){
                return UserSeverity.LOW;
            } else if (score<=ceilScoreForMediumSeverity){
                return  UserSeverity.MEDIUM;
            } else if (score<=ceilScoreForHighSeverity){
                return  UserSeverity.HIGH;
            } else {
                return UserSeverity.CRITICAL;
            }
        }
    }

}
