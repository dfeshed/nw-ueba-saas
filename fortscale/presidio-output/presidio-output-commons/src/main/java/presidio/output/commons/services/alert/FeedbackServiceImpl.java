package presidio.output.commons.services.alert;

import org.springframework.beans.factory.annotation.Autowired;
import presidio.output.domain.records.alerts.Alert;
import presidio.output.domain.records.alerts.AlertEnums;
import presidio.output.domain.records.users.User;
import presidio.output.domain.services.alerts.AlertPersistencyService;
import presidio.output.domain.services.users.UserPersistencyService;

import java.util.HashSet;
import java.util.List;
import java.util.Set;

/**
 * Created by efratn on 04/12/2017.
 */
public class FeedbackServiceImpl implements FeedbackService {

    @Autowired
    private UserPersistencyService userPersistencyService;

    @Autowired
    private AlertPersistencyService alertPersistencyService;

    /**
     * Update alert feedback to given alert and update all coresponding alert data-
     * alert contribution to user score and user score (according to new contribution)
     * @param alertIds
     * @param feedback
     */
    @Override
    public void updateAlertFeedback(List<String> alertIds, AlertEnums.AlertFeedback feedback) {
        Iterable<presidio.output.domain.records.alerts.Alert> alerts = alertPersistencyService.findAll(alertIds);

        Set<User> usersToBeUpdated = new HashSet<>();
        alerts.forEach(alert->{

            //1. update alert feedback with the new given feedback
            alert.setFeedback(feedback);

            //2. update alert contribution to user score according to new feedback
            Double origContribution = alert.getContributionToUserScore();
            Double contributionDelta = calcContributionToUserScoreDelta(alert, feedback);
            alert.setContributionToUserScore(origContribution + contributionDelta);

            //3. increase\decrease user score with the updated alert contribution
            presidio.output.domain.records.users.User user = userPersistencyService.findUserById(alert.getUserId());
            user.setScore(user.getScore() + contributionDelta);
            usersToBeUpdated.add(user);
        });

        alertPersistencyService.save((List<Alert>)alerts);
        userPersistencyService.save((List<User>) usersToBeUpdated);
    }

    private Double calcContributionToUserScoreDelta(Alert alert, AlertEnums.AlertFeedback feedback) {
        if(AlertEnums.AlertFeedback.NOT_RISK.equals(feedback)) {
            return -1 * alert.getContributionToUserScore();
        }

        return 0D;
    }
}
