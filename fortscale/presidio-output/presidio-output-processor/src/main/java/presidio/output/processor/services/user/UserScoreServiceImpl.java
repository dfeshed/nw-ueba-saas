package presidio.output.processor.services.user;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import presidio.output.domain.records.alerts.Alert;
import presidio.output.domain.records.alerts.AlertEnums;
import presidio.output.domain.records.users.User;
import presidio.output.domain.records.users.UserQuery;
import presidio.output.domain.records.users.UserSeverity;
import presidio.output.domain.services.users.UserPersistencyService;
import org.apache.commons.math3.stat.descriptive.rank.Percentile;

import java.util.*;
import java.util.concurrent.atomic.AtomicInteger;


/**
 * Created by shays on 27/08/2017.
 */
public class UserScoreServiceImpl implements UserScoreService{
    private UserPersistencyService userPersistencyService;

    public int defaultUsersBatchFile;

    private int percentThresholdCritical;

    private int percentThresholdHigh;

    private int percentThresholdMedium;

    private Map<AlertEnums.AlertSeverity, Double> alertSeverityToScoreContribution;

    public UserScoreServiceImpl(UserPersistencyService userPersistencyService,
                                int defaultUsersBatchFile,
                                int percentThresholdCritical,
                                int percentThresholdHigh,
                                int percentThresholdMedium,
                                double alertContributionCritical,
                                double alertContributionHigh,
                                double alertContributionMedium,
                                double alertContributionLow
                                ) {
        this.userPersistencyService = userPersistencyService;
        this.defaultUsersBatchFile = defaultUsersBatchFile;
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

       Page<User> page = userPersistencyService.find(new UserQuery.UserQueryBuilder().pageNumber(0).pageSize(defaultUsersBatchFile).build());

        while (page != null && page.hasContent()) {
            updateSeveritiesForUsersList(severitiesMap, page.getContent(),true);
            page = getNextUserPage(page);

        }
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

    private Page<User> getNextUserPage(Page<User> page) {
        if (page.hasNext()) {
            Pageable pageable = page.nextPageable();

            UserQuery.UserQueryBuilder userQueryBuilder = new UserQuery.UserQueryBuilder().
                    pageNumber(pageable.getPageNumber()).
                    pageSize(pageable.getPageSize());

            if (pageable.getSort()!=null){
                userQueryBuilder.sort(pageable.getSort());
            }
            page = userPersistencyService.find(userQueryBuilder.build());

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
        Page<User> page = userPersistencyService.find(new UserQuery.UserQueryBuilder().minScore(1).pageNumber(0).pageSize(this.defaultUsersBatchFile).sort(sort).build());
        int numberOfElements = new Long(page.getTotalElements()).intValue();
        double[] scores = new double[numberOfElements];
        AtomicInteger courser = new AtomicInteger(0);


        while (page != null && page.hasContent()) {
            page.getContent().forEach(user -> {
                scores[courser.getAndAdd(1)] = user.getUserScore();
            });
            page = getNextUserPage(page);

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
