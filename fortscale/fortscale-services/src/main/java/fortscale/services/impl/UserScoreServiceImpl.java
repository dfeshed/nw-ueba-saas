package fortscale.services.impl;

import fortscale.domain.core.*;
import fortscale.domain.core.dao.UserRepository;
import fortscale.domain.core.dao.UserScorePercentilesRepository;
import fortscale.services.AlertsService;
import fortscale.services.UserScoreService;
import fortscale.utils.time.TimestampUtils;
import org.apache.commons.lang3.tuple.ImmutablePair;
import org.apache.commons.lang3.tuple.Pair;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import fortscale.utils.logging.Logger;

import javax.annotation.PostConstruct;
import java.lang.reflect.InvocationTargetException;
import java.util.*;
import java.util.concurrent.atomic.AtomicInteger;

@Service("userScoreService")
public class UserScoreServiceImpl implements UserScoreService{

    public static final double LOW_ALERT_SEVERITY_COINTRIBUTION_DEFAULT = (double) 10;
    public static final double MEDIUM_SEVERITY_COINTRIBUTION_DEFAULT = (double) 20;
    public static final double HIGH_SEVERITY_COINTRIBUTION_DEFAULT = (double) 30;
    public static final double CRITICAL_SEVERITY_COINTRIBUTION_DEFAULT = (double) 40;
    public static final int DAYS_RELEVENT_FOR_UNRESOLVED_ALERTS_DEFAULT = 90;

    public static final String APP_CONF_PREFIX = "user.socre.conf";

    private Logger logger = Logger.getLogger(this.getClass());


    @Autowired
    private UserScorePercentilesRepository userScorePercentilesRepository;

   /// private Map<Severity, Double> alertSeverityToUserScoreContribution;

    @Autowired
    private UserRepository userRepository;
	
	@Autowired
    private AlertsService alertsService;


    @Autowired
    private  ApplicationConfigurationHelper applicationConfigurationHelper;

    private UserScoreConfiguration userScoreConfiguration;

    @PostConstruct
    public void init() throws IllegalAccessException, NoSuchMethodException, InvocationTargetException {


        userScoreConfiguration = new UserScoreConfiguration();
        userScoreConfiguration.setContributionOfCriticalSeverityAlert(CRITICAL_SEVERITY_COINTRIBUTION_DEFAULT);
        userScoreConfiguration.setContributionOfHighSeverityAlert(HIGH_SEVERITY_COINTRIBUTION_DEFAULT);
        userScoreConfiguration.setContributionOfMediumSeverityAlert(MEDIUM_SEVERITY_COINTRIBUTION_DEFAULT);
        userScoreConfiguration.setContributionOfLowSeverityAlert(LOW_ALERT_SEVERITY_COINTRIBUTION_DEFAULT);
        userScoreConfiguration.setDaysRelevantForUnresolvedAlerts(DAYS_RELEVENT_FOR_UNRESOLVED_ALERTS_DEFAULT);

        //Update mongo or get from mongo
        applicationConfigurationHelper.syncWithConfiguration(APP_CONF_PREFIX, userScoreConfiguration, Arrays.asList(

                new ImmutablePair("daysRelevantForUnresolvedAlerts", "daysRelevantForUnresolvedAlerts"),
                new ImmutablePair("contributionOfLowSeverityAlert", "contributionOfLowSeverityAlert"),
                new ImmutablePair("contributionOfMediumSeverityAlert", "contributionOfMediumSeverityAlert"),
                new ImmutablePair("contributionOfHighSeverityAlert", "contributionOfHighSeverityAlert"),
                new ImmutablePair("contributionOfCriticalSeverityAlert", "contributionOfCriticalSeverityAlert")

        ));

    }



    /**
     * Get all the alerts of user with the contribution of each alert to the total score,
     * and sum all the points. Save the score to the alert and return the new score.
     * @param userName
     * @return the new user socre
     */
    public double recalculateUserScore(String userName){


        Set<Alert> alerts = alertsService.getAlertsRelevantToUserScore(userName);
        double userScore = 0;
        for (Alert alert : alerts){
            double updatedUserScoreContributionForAlert = getUserScoreContributionForAlertSeverity(alert.getSeverity(), alert.getFeedback(), alert.getStartDate());
            boolean userScoreContributionFlag = isAlertAffectingUserScore(alert.getFeedback(), alert.getStartDate());

            //Update alert
            if (!userScoreContributionFlag) {//Alert stop affecting only because time became too old
                alert.setUserSocreContributionFlag(userScoreContributionFlag);
                alertsService.saveAlertInRepository(alert);
            } else if (updatedUserScoreContributionForAlert != alert.getUserSocreContribution()){
                alert.setUserSocreContributionFlag(userScoreContributionFlag);
                alert.setUserSocreContribution(updatedUserScoreContributionForAlert);
                alertsService.saveAlertInRepository(alert);
            }


            userScore += alert.getUserSocreContribution();
        }
        User user = userRepository.findByUsername(userName);
        user.setScore(userScore);


        userRepository.save(user);
        return userScore;
    }


        @Override
    public double getUserScoreContributionForAlertSeverity(Severity severity, AlertFeedback feedback, long alertStartDate){

        alertStartDate = TimestampUtils.normalizeTimestamp(alertStartDate);

        if (isAlertAffectingUserScore(feedback, alertStartDate)) {
            return calculateContributionBySeverity(severity);
        }

        return  0;
    }

    private boolean isAlertAffectingUserScore(AlertFeedback feedback, long alertStartDate){
        if (AlertFeedback.Rejected.equals(feedback)){ //Alert which has feedback different from None can't be too old to affect.
            return  false;
        }
        if (AlertFeedback.Approved.equals(feedback)){ //Alert which has feedback different from None can't be too old to affect.
            return  true;
        }
        //Else - unresolved
        long alertAgeInDays = (System.currentTimeMillis() - alertStartDate)/1000 / 3600 / 24;
        return alertAgeInDays < userScoreConfiguration.getDaysRelevantForUnresolvedAlerts();
    }

    public double calculateContributionBySeverity(Severity severity){
        switch(severity){
            case Critical: return userScoreConfiguration.getContributionOfCriticalSeverityAlert();
            case High: return userScoreConfiguration.getContributionOfHighSeverityAlert();
            case Medium: return userScoreConfiguration.getContributionOfMediumSeverityAlert();
            case Low: return userScoreConfiguration.getContributionOfLowSeverityAlert();
            default: throw new RuntimeException("Severity is not legal");
        }

    }



    public void setUserScoreConfiguration(UserScoreConfiguration userScoreConfiguration) {
        this.userScoreConfiguration = userScoreConfiguration;
    }

    /**
     *   Get histogram of counts per score, sort it, and build list of UserSingleScorePercentile (100 percentiles list,
     *  for each percentile we have the max value of the percentile and the min value
     *
     * @return
     */

    public List<UserSingleScorePercentile> getOrderdPercentiles(List<Pair<Double, Integer>> scoresToScoreCount, int p){

        //Sort by score
        Collections.sort(scoresToScoreCount, new Comparator<Pair<Double, Integer>>() {
            @Override
            public int compare(Pair<Double, Integer> o1, Pair<Double, Integer> o2) {
                return  o1.getKey().compareTo(o2.getKey());
            }
        });

        //Map<Integer, UserSingleScorePercentile> percentileMap = new HashMap<>();
        List<UserSingleScorePercentile> percentileList = new ArrayList<>(p);
        int totalUsers = 0;
        for (Pair<Double, Integer> scoreToScoreCount: scoresToScoreCount){
            totalUsers +=scoreToScoreCount.getValue();
        }

        //Init percentile map
        for (int i=1; i<p+1; i++){
            UserSingleScorePercentile u = new UserSingleScorePercentile();
            u.setPercentile(i);
            percentileList.add(i-1,u);
        }

        if (totalUsers > 0){
            long numberOfPeopleInPercentile = new Double(Math.ceil(totalUsers*1.0 / p)).longValue();

            int currentPercentile = 1;
            int usersCountedUntilNow = 0;
            int minPercentileValue = 0;

            for (Pair<Double, Integer>  scoreToCount : scoresToScoreCount){
                usersCountedUntilNow += scoreToCount.getValue();
                if (currentPercentile * numberOfPeopleInPercentile <= usersCountedUntilNow){

                    UserSingleScorePercentile u = percentileList.get(currentPercentile-1);
                    u.setMinScoreInPerecentile(minPercentileValue);
                    int topValuesForPercentile = new Double(scoreToCount.getKey()).intValue();
                    u.setMaxScoreInPercentile(topValuesForPercentile);
                    minPercentileValue = topValuesForPercentile;
                    currentPercentile++;
                }

            }


        }
        return percentileList;

    }

    /**
     * Build the percentiles tables and update mongo collection
     * @param scoresHistogram - map of how many users has any of the scores.

      */
    @Override
    public void calculateUserSeverities(List<Pair<Double, Integer>> scoresHistogram){

        logger.info("Build percentiles table from user score histogram");
        Collection<UserSingleScorePercentile> percentiles = getOrderdPercentiles(scoresHistogram, 100);



        long newTimestamp = System.currentTimeMillis();
        logger.info("Save new percentiles table from user score histogram. timestamp is {} ",newTimestamp);
        UserScorePercentiles newUserSocorePercentiles = new UserScorePercentiles(newTimestamp, percentiles,true);
        List<UserScorePercentiles> activeRecords = userScorePercentilesRepository.findByActive(true);

        userScorePercentilesRepository.save(newUserSocorePercentiles);

        logger.info("Deactive old percentiles table from user score histogram. ");
        for (UserScorePercentiles previous:activeRecords){
            previous.setActive(false);
            if (previous.getExpirationTime() == null){
                previous.setExpirationTime(newTimestamp);
            }
            userScorePercentilesRepository.save(previous);
        }


    }

    /**
     * Calculate user score for each user,
     * Update each user with the new score and build histogram of how many users has each score
     * @return histogram of how many users has each score
     *                       Pay attantion that the list is not sorted.
     */
    @Override
    public List<Pair<Double, Integer>> calculateAllUsersScores() {
        //Step 1 - get all relevant users
        logger.info("Get all relevant users");
        Set<String> userNames = alertsService.getDistinctUserNamesFromAlertsRelevantToUserScore();
        logger.info("Going to update score for {} users"+userNames.size());
        //Step 2 - Update all users
        Map<Double, AtomicInteger> scoresAtomicHistogram = new HashMap<>();
        for (String userName : userNames){
            double score = this.recalculateUserScore(userName);
            //Add to
            AtomicInteger count = scoresAtomicHistogram.get(score);
            if (count == null){
                count = new AtomicInteger(0);
                scoresAtomicHistogram.put(score, count);
            }
            count.incrementAndGet();

        }
        logger.info("Finish updating user score");

        //Convert the atomic map to list of pairs
        List<Pair<Double, Integer>> scoresHistogram = new ArrayList<>();
        scoresAtomicHistogram.forEach((score,count) -> {
            scoresHistogram.add(new ImmutablePair<>(score,count.intValue()));
        });
        return scoresHistogram;
    }

    public UserScoreConfiguration getUserScoreConfiguration() {
        return userScoreConfiguration;
    }
    public static class UserScoreConfiguration{

        private long daysRelevantForUnresolvedAlerts;

        public double contributionOfLowSeverityAlert;
        public double contributionOfMediumSeverityAlert;
        public double contributionOfHighSeverityAlert;
        public double contributionOfCriticalSeverityAlert;

        public UserScoreConfiguration() {
        }



        public UserScoreConfiguration(long daysRelevantForUnresolvedAlerts, double contributionOfLowSeverityAlert, double contributionOfMediumSeverityAlert, double contributionOfHighSeverityAlert, double contributionOfCriticalSeverityAlert) {
            this.daysRelevantForUnresolvedAlerts = daysRelevantForUnresolvedAlerts;
            this.contributionOfLowSeverityAlert = contributionOfLowSeverityAlert;
            this.contributionOfMediumSeverityAlert = contributionOfMediumSeverityAlert;
            this.contributionOfHighSeverityAlert = contributionOfHighSeverityAlert;
            this.contributionOfCriticalSeverityAlert = contributionOfCriticalSeverityAlert;
        }

        public long getDaysRelevantForUnresolvedAlerts() {
            return daysRelevantForUnresolvedAlerts;
        }

        public void setDaysRelevantForUnresolvedAlerts(long daysRelevantForUnresolvedAlerts) {
            this.daysRelevantForUnresolvedAlerts = daysRelevantForUnresolvedAlerts;
        }

        public double getContributionOfLowSeverityAlert() {
            return contributionOfLowSeverityAlert;
        }

        public void setContributionOfLowSeverityAlert(double contributionOfLowSeverityAlert) {
            this.contributionOfLowSeverityAlert = contributionOfLowSeverityAlert;
        }

        public double getContributionOfMediumSeverityAlert() {
            return contributionOfMediumSeverityAlert;
        }

        public void setContributionOfMediumSeverityAlert(double contributionOfMediumSeverityAlert) {
            this.contributionOfMediumSeverityAlert = contributionOfMediumSeverityAlert;
        }

        public double getContributionOfHighSeverityAlert() {
            return contributionOfHighSeverityAlert;
        }

        public void setContributionOfHighSeverityAlert(double contributionOfHighSeverityAlert) {
            this.contributionOfHighSeverityAlert = contributionOfHighSeverityAlert;
        }

        public double getContributionOfCriticalSeverityAlert() {
            return contributionOfCriticalSeverityAlert;
        }

        public void setContributionOfCriticalSeverityAlert(double contributionOfCriticalSeverityAlert) {
            this.contributionOfCriticalSeverityAlert = contributionOfCriticalSeverityAlert;
        }


    }
}
