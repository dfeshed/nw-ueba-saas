package fortscale.services.impl;

import fortscale.domain.core.Alert;
import fortscale.domain.core.ClassifierScore;
import fortscale.domain.core.ScoreInfo;
import fortscale.domain.core.User;
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
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.util.*;

@Service("userScoreService")
public class UserScoreServiceImpl implements UserScoreService{
//	private static Logger logger = Logger.getLogger(UserScoreServiceImpl.class);
	
	private static final int MAX_NUM_OF_HISTORY_DAYS = 21;
    @Autowired
    private AlertsService alertsService;

	@Autowired
	private UserRepository userRepository;


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
        return 0;
    }
	
	
}
