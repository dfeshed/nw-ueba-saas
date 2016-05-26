package fortscale.services.impl;

import fortscale.domain.core.*;
import fortscale.domain.core.dao.UserRepository;
import fortscale.domain.dto.AlertWithUserScore;
import fortscale.services.AlertsService;
import fortscale.services.UserScoreService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import fortscale.utils.logging.Logger;

import javax.annotation.PostConstruct;
import java.util.*;

@Service("userScoreService")
public class UserScoreServiceImpl implements UserScoreService{

	private Logger logger = Logger.getLogger(this.getClass());

    private long daysRelevantForUnresolvedAlerts = 90;

    @Autowired
    private UserRepository userRepository;
	
	@Autowired
    private AlertsService alertsService;

  //  @Autowired
//    @Qualifier("alertSeverityToUserScoreContribution")
    private Map<Severity, Double> alertSeverityToUserScoreContribution;


    @PostConstruct
    public void init(){

        alertSeverityToUserScoreContribution=new HashMap<>();
        alertSeverityToUserScoreContribution.put(Severity.Low,(double)10);
        alertSeverityToUserScoreContribution.put(Severity.Medium,(double)20);
        alertSeverityToUserScoreContribution.put(Severity.High,(double)30);
        alertSeverityToUserScoreContribution.put(Severity.Critical,(double)40);

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


    public long getDaysRelevantForUnresolvedAlerts() {
        return daysRelevantForUnresolvedAlerts;
    }

    public void setDaysRelevantForUnresolvedAlerts(long daysRelevantForUnresolvedAlerts) {
        this.daysRelevantForUnresolvedAlerts = daysRelevantForUnresolvedAlerts;
    }

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
        if (AlertFeedback.None.equals(alert.getFeedback()) && alertAgeInDays < daysRelevantForUnresolvedAlerts) {
            return alertSeverityToUserScoreContribution.get(alert.getSeverity());
        }

        return  0;
    }

}
