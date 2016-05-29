package fortscale.services.impl;

import fortscale.domain.core.*;
import fortscale.domain.core.dao.UserRepository;
import fortscale.domain.dto.AlertWithUserScore;
import fortscale.services.AlertsService;
import fortscale.services.UserScoreService;
import org.apache.commons.lang3.tuple.ImmutablePair;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import fortscale.utils.logging.Logger;

import javax.annotation.PostConstruct;
import java.lang.reflect.InvocationTargetException;
import java.util.*;

@Service("userScoreService")
public class UserScoreServiceImpl implements UserScoreService{

    public static final double LOW_ALERT_SEVERITY_COINTRIBUTION_DEFAULT = (double) 10;
    public static final double MEDIUM_SEVERITY_COINTRIBUTION_DEFAULT = (double) 20;
    public static final double HIGH_SEVERITY_COINTRIBUTION_DEFAULT = (double) 30;
    public static final double CRITICAL_SEVERITY_COINTRIBUTION_DEFAULT = (double) 40;
    public static final int DAYS_RELEVENT_FOR_UNRESOLVED_ALERTS_DEFAULT = 90;

    public static final String APP_CONF_PREFIX = "user.socre.conf";

    private Logger logger = Logger.getLogger(this.getClass());



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

    public List<AlertWithUserScore> getAlertsWithUserScore(String userName){
        List<AlertWithUserScore> alertWithUserScoreList = new ArrayList<>();
        List<Alert> alerts = alertsService.getAlertsByUsername(userName);
        alerts.forEach(alert -> {
            double score = getContribution(alert);
            AlertWithUserScore alertWithUserScore = new AlertWithUserScore(alert, score);
            alertWithUserScoreList.add(alertWithUserScore);
        });


        return alertWithUserScoreList;
    };

    /**
     * Get all the alerts of user with the contribution of each alert to the total score,
     * and sum all the points. Save the score to the alert and return the new score.
     * @param userName
     * @return the new user socre
     */
    public double recalculateUserScore(String userName){


        List<AlertWithUserScore> alerts = getAlertsWithUserScore(userName);
        double userScore = 0;
        for (AlertWithUserScore alert : alerts){
            userScore += alert.getScore();
        }
        User user = userRepository.findByUsername(userName);
        user.setScore(userScore);

        userRepository.save(user);
        return userScore;
    }




    private double getContribution(Alert alert){
        if (AlertFeedback.Rejected.equals(alert.getFeedback())){
            return 0;
        }


        if (AlertFeedback.Approved.equals(alert.getFeedback())) {
            return calculateContributionBySeverity(alert.getSeverity());
        }


        long alertAgeInDays = (System.currentTimeMillis() - alert.getStartDate())/1000 / 3600 / 24;
        if (AlertFeedback.None.equals(alert.getFeedback()) && alertAgeInDays < userScoreConfiguration.getDaysRelevantForUnresolvedAlerts()) {
            return calculateContributionBySeverity(alert.getSeverity());
        }

        return  0;
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

    public UserScoreConfiguration getUserScoreConfiguration() {
        return userScoreConfiguration;
    }

    public void setUserScoreConfiguration(UserScoreConfiguration userScoreConfiguration) {
        this.userScoreConfiguration = userScoreConfiguration;
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
