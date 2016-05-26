package fortscale.services.impl;

import fortscale.domain.core.*;
import fortscale.domain.core.dao.UserRepository;
import fortscale.domain.dto.AlertWithUserScore;
import fortscale.services.AlertsService;
import fortscale.services.IUserScore;
import fortscale.services.IUserScoreHistoryElement;
import fortscale.services.UserScoreService;
import fortscale.services.classifier.Classifier;
import fortscale.services.classifier.ClassifierHelper;
import fortscale.common.exceptions.UnknownResourceException;
import org.joda.time.DateTime;
import org.joda.time.DateTimeZone;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import fortscale.utils.logging.Logger;
import java.util.*;

@Service("userScoreService")
public class UserScoreServiceImpl implements UserScoreService{

	private Logger logger = Logger.getLogger(this.getClass());

    private final static long DAYS_FOR_UNRESOLVED = 90;
	
	private static final int MAX_NUM_OF_HISTORY_DAYS = 21;
    @Autowired
    private AlertsService alertsService;

    @Autowired
    @Qualifier("alertSeverityToUserScoreContribution")
    private Map<Severity, Double> alertSeverityToUserScoreContribution;



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

    private double getContribution(Alert alert){
        if (AlertFeedback.Rejected.equals(alert.getFeedback())){
            return 0;
        }


        if (AlertFeedback.Approved.equals(alert.getFeedback()) || alert.getStartDate() > DAYS_FOR_UNRESOLVED * 24 * 3600 * 1000) {
            return alertSeverityToUserScoreContribution.get(alert.getSeverity());
        }
        return  0;
    }



	
	
}
