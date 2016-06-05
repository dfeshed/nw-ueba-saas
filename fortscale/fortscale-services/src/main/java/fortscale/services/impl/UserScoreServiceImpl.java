package fortscale.services.impl;

import fortscale.domain.core.*;
import fortscale.domain.core.dao.AlertsRepository;
import fortscale.domain.core.dao.UserRepository;
import fortscale.domain.core.dao.UserScorePercentilesRepository;
import fortscale.services.AlertsService;
import fortscale.services.UserScoreService;
import fortscale.services.cache.CacheHandler;
import fortscale.services.configuration.Impl.UserScoreConfiguration;
import fortscale.utils.time.TimestampUtils;
import org.apache.commons.lang3.tuple.ImmutablePair;
import org.apache.commons.lang3.tuple.Pair;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;
import fortscale.utils.logging.Logger;

import javax.annotation.PostConstruct;
import java.lang.reflect.InvocationTargetException;
import java.util.*;
import java.util.concurrent.atomic.AtomicInteger;

@Service("userScoreService")
public class UserScoreServiceImpl implements UserScoreService {

    /**
     * Default values that each alert severity contributes to the user score depnded on the alert severity
     */
    public static final double LOW_ALERT_SEVERITY_COINTRIBUTION_DEFAULT = (double) 10;
    public static final double MEDIUM_SEVERITY_COINTRIBUTION_DEFAULT = (double) 20;
    public static final double HIGH_SEVERITY_COINTRIBUTION_DEFAULT = (double) 30;
    public static final double CRITICAL_SEVERITY_COINTRIBUTION_DEFAULT = (double) 40;
    public static final int DAYS_RELEVENT_FOR_UNRESOLVED_ALERTS_DEFAULT = 90;

    /**
     * Default values  of mapiing percentiles to user severity.
     * Users with user score between - MIN_PERCENTIL_USER_SEVERITY_LOW_DEFAULT - MIN_PERCENTIL_USER_SEVERITY_MEDIUM_DEFAULT will get low severity
     * Users with user score between - MIN_PERCENTIL_USER_SEVERITY_MEDIUM_DEFAULT - MIN_PERCENTIL_USER_SEVERITY_HIGH_DEFAULT will get medium severity
     * Users with user score between - MIN_PERCENTIL_USER_SEVERITY_HIGH_DEFAULT - MIN_PERCENTIL_USER_SEVERITY_CRITICAL_DEFAULT will get high severity
     * Users with user score between - MIN_PERCENTIL_USER_SEVERITY_CRITICAL_DEFAULT - 100 will get critical severity
     */
    public static final double MIN_PERCENTIL_USER_SEVERITY_LOW_DEFAULT = (double) 0;
    public static final double MIN_PERCENTIL_USER_SEVERITY_MEDIUM_DEFAULT = (double) 50;
    public static final double MIN_PERCENTIL_USER_SEVERITY_HIGH_DEFAULT = (double) 80;
    public static final double MIN_PERCENTIL_USER_SEVERITY_CRITICAL_DEFAULT = (double) 95;


    public static final String APP_CONF_PREFIX = "user.socre.conf";
    private static final String SCORE_SEVERITIES_CACHE = "SCORE_SEVERITIES_CACHE";





    private Logger logger = Logger.getLogger(this.getClass());

    /*
    TODO: consider split this service 2 services: UserScoreService and UserScoreUpdate service. The cache should be only in UserScoreService
     */
    @Autowired
    @Qualifier("userScoreSeveritiesCache")
    private CacheHandler<String, NavigableMap<Double, Severity>> userScoreSeveritiesCache;

    @Autowired
    private UserScorePercentilesRepository userScorePercentilesRepository;


    @Autowired
    private UserRepository userRepository;

    @Autowired
    private AlertsService alertsService;

    @Autowired
    private AlertsRepository alertsRepository;


    @Autowired
    private ApplicationConfigurationHelper applicationConfigurationHelper;

    private UserScoreConfiguration userScoreConfiguration;

    @PostConstruct
    public void init()  {


        userScoreConfiguration = new UserScoreConfiguration();
        //Set all default value on userScoreConfiguration
        userScoreConfiguration.setContributionOfCriticalSeverityAlert(CRITICAL_SEVERITY_COINTRIBUTION_DEFAULT);
        userScoreConfiguration.setContributionOfHighSeverityAlert(HIGH_SEVERITY_COINTRIBUTION_DEFAULT);
        userScoreConfiguration.setContributionOfMediumSeverityAlert(MEDIUM_SEVERITY_COINTRIBUTION_DEFAULT);
        userScoreConfiguration.setContributionOfLowSeverityAlert(LOW_ALERT_SEVERITY_COINTRIBUTION_DEFAULT);
        userScoreConfiguration.setDaysRelevantForUnresolvedAlerts(DAYS_RELEVENT_FOR_UNRESOLVED_ALERTS_DEFAULT);

        userScoreConfiguration.setMinPercentileForUserSeverityCritical(MIN_PERCENTIL_USER_SEVERITY_CRITICAL_DEFAULT);
        userScoreConfiguration.setMinPercentileForUserSeverityHigh(MIN_PERCENTIL_USER_SEVERITY_HIGH_DEFAULT);
        userScoreConfiguration.setMinPercentileForUserSeverityMedium(MIN_PERCENTIL_USER_SEVERITY_MEDIUM_DEFAULT);
        userScoreConfiguration.setMinPercentileForUserSeverityLow(MIN_PERCENTIL_USER_SEVERITY_LOW_DEFAULT);

        syncConfigurationWithApplicationConfiguration();
    }

    private void syncConfigurationWithApplicationConfiguration()  {
        //Update mongo or get from mongo
        try {
            applicationConfigurationHelper.syncWithConfiguration(APP_CONF_PREFIX, userScoreConfiguration, Arrays.asList(

                    new ImmutablePair("daysRelevantForUnresolvedAlerts", "daysRelevantForUnresolvedAlerts"),
                    new ImmutablePair("contributionOfLowSeverityAlert", "contributionOfLowSeverityAlert"),
                    new ImmutablePair("contributionOfMediumSeverityAlert", "contributionOfMediumSeverityAlert"),
                    new ImmutablePair("contributionOfHighSeverityAlert", "contributionOfHighSeverityAlert"),
                    new ImmutablePair("contributionOfCriticalSeverityAlert", "contributionOfCriticalSeverityAlert"),

                    new ImmutablePair("minPercentileForUserSeverityLow", "minPercentileForUserSeverityLow"),
                    new ImmutablePair("minPercentileForUserSeverityMedium", "minPercentileForUserSeverityMedium"),
                    new ImmutablePair("minPercentileForUserSeverityHigh", "minPercentileForUserSeverityHigh"),
                    new ImmutablePair("minPercentileForUserSeverityCritical", "minPercentileForUserSeverityCritical")


            ));
        } catch(Exception e) {
            logger.error("Cannot sync configuration with application configuration, using old values. Exception {}",e);
        }

    }

    /**
     * Get all the alerts of user with the contribution of each alert to the total score,
     * and sum all the points. Save the score to the alert and return the new score.
     *
     * @param userName
     * @return the new user socre
     */
    public double recalculateUserScore(String userName) {


        Set<Alert> alerts = alertsService.getAlertsRelevantToUserScore(userName);
        double userScore = 0;
        for (Alert alert : alerts) {
            double updatedUserScoreContributionForAlert = getUserScoreContributionForAlertSeverity(alert.getSeverity(), alert.getFeedback(), alert.getStartDate());
            boolean userScoreContributionFlag = isAlertAffectingUserScore(alert.getFeedback(), alert.getStartDate());

            //Update alert
            if (!userScoreContributionFlag) {//Alert stop affecting only because time became too old
                alert.setUserSocreContributionFlag(userScoreContributionFlag);
                alertsRepository.save(alert);
            } else if (updatedUserScoreContributionForAlert != alert.getUserSocreContribution()) {
                alert.setUserSocreContributionFlag(userScoreContributionFlag);
                alert.setUserSocreContribution(updatedUserScoreContributionForAlert);
                alertsRepository.save(alert);
            }


            userScore += alert.getUserSocreContribution();
        }
        User user = userRepository.findByUsername(userName);
        user.setScore(userScore);


        userRepository.save(user);
        return userScore;
    }


    /**
     *
     * @param severity
     * @param feedback
     * @param alertStartDate
     * @return the contribution of the user score by the alert of the above attributes (Severity, feedback, stat date)
     */
    @Override
    public double getUserScoreContributionForAlertSeverity(Severity severity, AlertFeedback feedback, long alertStartDate) {

        alertStartDate = TimestampUtils.normalizeTimestamp(alertStartDate);

        if (isAlertAffectingUserScore(feedback, alertStartDate)) {
            return userScoreConfiguration.fetchContributionBySeverity(severity);
        }

        return 0;
    }

    /**
     *
     * @param feedback
     * @param alertStartDate
     * @return
     */
    private boolean isAlertAffectingUserScore(AlertFeedback feedback, long alertStartDate) {
        if (AlertFeedback.Rejected.equals(feedback)) { //Alert which has feedback different from None can't be too old to affect.
            return false;
        }
        if (AlertFeedback.Approved.equals(feedback)) { //Alert which has feedback different from None can't be too old to affect.
            return true;
        }
        //Else - unresolved
        long alertAgeInDays = (System.currentTimeMillis() - alertStartDate) / 1000 / 3600 / 24;
        return alertAgeInDays < userScoreConfiguration.getDaysRelevantForUnresolvedAlerts();
    }


    /**
     * Get histogram of counts per score, sort it, and build list of UserSingleScorePercentile (100 percentiles list,
     * for each percentile we have the max value of the percentile and the min value
     *
     * @return
     */

    public List<UserSingleScorePercentile> getOrderdPercentiles(List<Pair<Double, Integer>> scoresToScoreCount, int p) {

        //Sort by score
        Collections.sort(scoresToScoreCount, new Comparator<Pair<Double, Integer>>() {
            @Override
            public int compare(Pair<Double, Integer> o1, Pair<Double, Integer> o2) {
                return o1.getKey().compareTo(o2.getKey());
            }
        });

        //Map<Integer, UserSingleScorePercentile> percentileMap = new HashMap<>();
        List<UserSingleScorePercentile> percentileList = new ArrayList<>(p);
        int totalUsers = 0;
        for (Pair<Double, Integer> scoreToScoreCount : scoresToScoreCount) {
            totalUsers += scoreToScoreCount.getValue();
        }

        //Init percentile map
        for (int i = 1; i < p + 1; i++) {
            UserSingleScorePercentile u = new UserSingleScorePercentile();
            u.setPercentile(i);
            percentileList.add(i - 1, u);
        }

        if (totalUsers > 0) {
            long numberOfPeopleInPercentile = new Double(Math.ceil(totalUsers * 1.0 / p)).longValue();

            int currentPercentile = 1;
            int usersCountedUntilNow = 0;
            int minPercentileValue = 0;

            for (Pair<Double, Integer> scoreToCount : scoresToScoreCount) {
                usersCountedUntilNow += scoreToCount.getValue();
                if (currentPercentile * numberOfPeopleInPercentile <= usersCountedUntilNow) {

                    UserSingleScorePercentile u = percentileList.get(currentPercentile - 1);
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
     *
     * @param scoresHistogram - map of how many users has any of the scores.
     */
    @Override
    public void calculateUserSeverities(List<Pair<Double, Integer>> scoresHistogram) {

        logger.info("Build percentiles table from user score histogram");
        Collection<UserSingleScorePercentile> percentiles = getOrderdPercentiles(scoresHistogram, 100);


        long newTimestamp = System.currentTimeMillis();
        logger.info("Save new percentiles table from user score histogram. timestamp is {} ", newTimestamp);
        UserScorePercentiles newUserSocorePercentiles = new UserScorePercentiles(newTimestamp, percentiles, true);
        List<UserScorePercentiles> activeRecords = userScorePercentilesRepository.findByActive(true);

        userScorePercentilesRepository.insert(newUserSocorePercentiles);

        logger.info("Deactive old percentiles table from user score histogram. ");
        if (activeRecords != null) {
            for (UserScorePercentiles previous : activeRecords) {
                previous.setActive(false);
                if (previous.getExpirationTime() == null) {
                    previous.setExpirationTime(newTimestamp);
                }
                userScorePercentilesRepository.save(previous);
            }
        }


    }

    /**
     * Calculate user score for each user,
     * Update each user with the new score and build histogram of how many users has each score
     *
     * @return histogram of how many users has each score
     * Pay attantion that the list is not sorted.
     */
    @Override
    public List<Pair<Double, Integer>> calculateAllUsersScores() {
        //Step 1 - get all relevant users
        logger.info("Get all relevant users");
        Set<String> userNames = alertsService.getDistinctUserNamesFromAlertsRelevantToUserScore();
        logger.info("Going to update score for {} users" + userNames.size());
        //Step 2 - Update all users
        Map<Double, AtomicInteger> scoresAtomicHistogram = new HashMap<>();
        for (String userName : userNames) {
            double score = this.recalculateUserScore(userName);
            //Add to
            AtomicInteger count = scoresAtomicHistogram.get(score);
            if (count == null) {
                count = new AtomicInteger(0);
                scoresAtomicHistogram.put(score, count);
            }
            count.incrementAndGet();

        }
        logger.info("Finish updating user score");

        //Convert the atomic map to list of pairs
        List<Pair<Double, Integer>> scoresHistogram = new ArrayList<>();
        scoresAtomicHistogram.forEach((score, count) -> {
            scoresHistogram.add(new ImmutablePair<>(score, count.intValue()));
        });
        return scoresHistogram;
    }


    /**
     * Translate the user score to severity, using the percentiles table and configuration.
     *      userScoreSeveritiesCache.get(SCORE_SEVERITIES_CACHE) map between score to severity.
     *      So score 30 might return Low and score 80 might return high.
     *      The map stored in the cache (default - hourly cache)
     *      and build each time that the cache too old.
     *      Too build the map I use loadSeveritiesToCache();
     *
     * @param userScore
     * @return
     */
    public Severity getUserSeverityForScore(double userScore) {
        NavigableMap<Double, Severity> severityNavigableMap = userScoreSeveritiesCache.get(SCORE_SEVERITIES_CACHE);
        if (severityNavigableMap == null) {
            severityNavigableMap = loadSeveritiesToCache();
            userScoreSeveritiesCache.put(SCORE_SEVERITIES_CACHE, severityNavigableMap);
        }

        return severityNavigableMap.floorEntry(userScore).getValue();
    }

    /**
     * Load severities of each user score range into navigable map of user score to severity.
     * The values bases on percentiles from mongo and configuration that map the percentiles to severities
     * @return
     */
    private NavigableMap<Double, Severity> loadSeveritiesToCache() {

        //Get changes from application configuration
        syncConfigurationWithApplicationConfiguration();

        NavigableMap<Double, Severity> severityNavigableMap = new TreeMap<>();
        List<UserScorePercentiles> percentiles = userScorePercentilesRepository.findByActive(true);
        if (percentiles.size() != 1) {
            throw new RuntimeException("UserScorePercentiles collection can have only one active document");
        }
        UserScorePercentiles userScorePercentiles = percentiles.get(0);
        for (UserSingleScorePercentile percentile : userScorePercentiles.getUserScorePercentileCollection()) {
            if (percentile.getMaxScoreInPercentile() > percentile.getMaxScoreInPercentile()) {
                severityNavigableMap.put((double) percentile.getMinScoreInPerecentile(), userScoreConfiguration.fetchSeverity(percentile.getPercentile()));
            }
        }
        return severityNavigableMap;
    }


    public void setUserScoreConfiguration(UserScoreConfiguration userScoreConfiguration) {
        this.userScoreConfiguration = userScoreConfiguration;
    }
}
