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



    private Map<Severity, Double> alertSeverityToUserScoreContribution;

    @Autowired
    private UserRepository userRepository;
	
	@Autowired
    private AlertsService alertsService;


    @Autowired
    private  ApplicationConfigurationHelper applicationConfigurationHelper;

    private UserScoreConfiguration userScoreConfiguration;

    @PostConstruct
    public void init() throws IllegalAccessException, NoSuchMethodException, InvocationTargetException {

        alertSeverityToUserScoreContribution=new HashMap<>();
        alertSeverityToUserScoreContribution.put(Severity.Low, LOW_ALERT_SEVERITY_COINTRIBUTION_DEFAULT);
        alertSeverityToUserScoreContribution.put(Severity.Medium, MEDIUM_SEVERITY_COINTRIBUTION_DEFAULT);
        alertSeverityToUserScoreContribution.put(Severity.High, HIGH_SEVERITY_COINTRIBUTION_DEFAULT);
        alertSeverityToUserScoreContribution.put(Severity.Critical, CRITICAL_SEVERITY_COINTRIBUTION_DEFAULT);

        userScoreConfiguration.setDaysRelevantForUnresolvedAlerts(DAYS_RELEVENT_FOR_UNRESOLVED_ALERTS_DEFAULT);

        applicationConfigurationHelper.syncWithConfiguration(APP_CONF_PREFIX, this, Arrays.asList(

                new ImmutablePair("hostnameDomainMarkersString", "hostnameDomainMarkersString"),
                new ImmutablePair("numberOfConcurrentSessions", "numberOfConcurrentSessions"),

                new ImmutablePair("notificationScoreField", "notificationScoreField"),
                new ImmutablePair("notificationTypeField", "notificationTypeField"),
                new ImmutablePair("notificationValueField", "notificationValueField"),

                new ImmutablePair("notificationStartTimestampField", "notificationStartTimestampField"),
                new ImmutablePair("normalizedUsernameField", "normalizedUsernameField"),
                new ImmutablePair("notificationSupportingInformationField", "notificationSupportingInformationField"),

                new ImmutablePair("notificationDataSourceField", "notificationDataSourceField"),
                new ImmutablePair("fieldManipulatorBeanName", "fieldManipulatorBeanName"),
                new ImmutablePair("notificationFixedScore", "notificationFixedScore")
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


//    public long getDaysRelevantForUnresolvedAlerts() {
//        return daysRelevantForUnresolvedAlerts;
//    }
//
//    public void setDaysRelevantForUnresolvedAlerts(long daysRelevantForUnresolvedAlerts) {
//        this.daysRelevantForUnresolvedAlerts = daysRelevantForUnresolvedAlerts;
//    }

    public Map<Severity, Double> getAlertSeverityToUserScoreContribution() {
        return alertSeverityToUserScoreContribution;
    }

    public void setAlertSeverityToUserScoreContribution(Map<Severity, Double> alertSeverityToUserScoreContribution) {
        this.alertSeverityToUserScoreContribution = alertSeverityToUserScoreContribution;
    }

    private double getContribution(Alert alert){
        if (AlertFeedback.Rejected.equals(alert.getFeedback())){
            return 0;
        }


        if (AlertFeedback.Approved.equals(alert.getFeedback())) {
            return alertSeverityToUserScoreContribution.get(alert.getSeverity());
        }

        //|| alert.getStartDate() > daysRelevantForUnresolvedAlerts * 24 * 3600 * 1000
        long alertAgeInDays = (System.currentTimeMillis() - alert.getStartDate())/1000 / 3600 / 24;
        if (AlertFeedback.None.equals(alert.getFeedback()) && alertAgeInDays < userScoreConfiguration.getDaysRelevantForUnresolvedAlerts()) {
            return alertSeverityToUserScoreContribution.get(alert.getSeverity());
        }

        return  0;
    }


    public static class UserScoreConfiguration{

        private long daysRelevantForUnresolvedAlerts;

        public long getDaysRelevantForUnresolvedAlerts() {
            return daysRelevantForUnresolvedAlerts;
        }

        public void setDaysRelevantForUnresolvedAlerts(long daysRelevantForUnresolvedAlerts) {
            this.daysRelevantForUnresolvedAlerts = daysRelevantForUnresolvedAlerts;
        }
    }
}
