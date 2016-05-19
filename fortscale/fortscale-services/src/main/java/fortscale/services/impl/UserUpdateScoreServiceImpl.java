package fortscale.services.impl;

import fortscale.domain.analyst.ScoreWeight;
import fortscale.domain.core.Alert;
import fortscale.domain.core.ClassifierScore;
import fortscale.domain.core.ScoreInfo;
import fortscale.domain.core.User;
import fortscale.domain.core.dao.UserRepository;
import fortscale.domain.dto.AlertWithUserScore;
import fortscale.services.AlertsService;
import fortscale.services.UserScoreService;
import fortscale.services.UserService;
import fortscale.services.UserUpdateScoreService;
import fortscale.services.analyst.ConfigurationService;
import fortscale.services.classifier.Classifier;
import fortscale.utils.logging.Logger;
import org.joda.time.DateTime;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.mongodb.core.query.Update;
import org.springframework.stereotype.Service;

import java.util.*;

@Service("userUpdateScoreService")
public class UserUpdateScoreServiceImpl implements UserUpdateScoreService {

    private static Logger logger = Logger.getLogger(UserUpdateScoreServiceImpl.class);
	

	@Autowired
	private UserRepository userRepository;
	
	@Autowired
	private UserService userService;

    @Autowired
    private UserScoreService userScoreService;


	@Autowired
    private AlertsService alertsService;
	
	public double recalculateUserScore(String userName){

        User user = userRepository.findByUsername(userName);
        List<AlertWithUserScore> alerts = userScoreService.getAlertsWithUserScore(userName);
        double userScore = 0;
        for (AlertWithUserScore alert : alerts){
            userScore += alert.getScore();
        }
        user.setScore(userScore);

        userRepository.save(user);
        return userScore;
    }



}
