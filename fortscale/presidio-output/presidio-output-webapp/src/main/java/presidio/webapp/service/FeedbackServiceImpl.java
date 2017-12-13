package presidio.webapp.service;

import org.springframework.beans.factory.annotation.Autowired;
import presidio.output.commons.services.alert.AlertSeverityService;
import presidio.output.commons.services.user.UserSeverityService;
import presidio.output.domain.records.alerts.Alert;
import presidio.output.domain.records.alerts.AlertEnums;
import presidio.output.domain.records.users.User;
import presidio.output.domain.records.users.UserSeverity;
import presidio.output.domain.services.alerts.AlertPersistencyService;
import presidio.output.domain.services.users.UserPersistencyService;

import java.util.*;

/**
 * Created by efratn on 04/12/2017.
 */
public class FeedbackServiceImpl implements FeedbackService {

    @Autowired
    private UserPersistencyService userPersistencyService;

    @Autowired
    private AlertPersistencyService alertPersistencyService;

    @Autowired
    private UserSeverityService userSeverityService;

    @Autowired
    private AlertSeverityService alertSeverityService;

    //Maps between original feedback to new feedback with the alert contribution delta
    // if alert contribution should be updated (null value), zero value otherwise
    private Map<AlertEnums.AlertFeedback, Map<AlertEnums.AlertFeedback, Double>> feedbackToAlertContributionMap;

    public FeedbackServiceImpl() {
        feedbackToAlertContributionMap = new HashMap<>();
        feedbackToAlertContributionMap.put(AlertEnums.AlertFeedback.NONE, new HashMap<>());
        feedbackToAlertContributionMap.put(AlertEnums.AlertFeedback.RISK, new HashMap<>());
        feedbackToAlertContributionMap.put(AlertEnums.AlertFeedback.NOT_RISK, new HashMap<>());

        //updating feedback from NONE to RISK- alert contribution shouldn't be changed
        feedbackToAlertContributionMap.get(AlertEnums.AlertFeedback.NONE).put(AlertEnums.AlertFeedback.RISK, 0D);
        //updating feedback from NONE to NOT_RISK- alert contribution should be updated
        feedbackToAlertContributionMap.get(AlertEnums.AlertFeedback.NONE).put(AlertEnums.AlertFeedback.NOT_RISK, -1D);

        //updating feedback from RISK to NOT_RISK- alert contribution should be updated
        feedbackToAlertContributionMap.get(AlertEnums.AlertFeedback.RISK).put(AlertEnums.AlertFeedback.NOT_RISK, -1D);
        //updating feedback from RISK to NONE- alert contribution shouldn't be changed
        feedbackToAlertContributionMap.get(AlertEnums.AlertFeedback.RISK).put(AlertEnums.AlertFeedback.NONE, 0D);

        //updating feedback from NOT_RISK to RISK- alert contribution should be changed
        feedbackToAlertContributionMap.get(AlertEnums.AlertFeedback.NOT_RISK).put(AlertEnums.AlertFeedback.RISK, 1D);
        //updating feedback from NOT_RISK to NONE- alert contribution should be changed
        feedbackToAlertContributionMap.get(AlertEnums.AlertFeedback.NOT_RISK).put(AlertEnums.AlertFeedback.NONE, 1D);
    }

    /**
     * Update alert feedback to given alert and update all coresponding alert data-
     * alert contribution to user score and user score (according to new contribution)
     * @param alertIds
     * @param feedback
     */
    @Override
    public void updateAlertFeedback(List<String> alertIds, AlertEnums.AlertFeedback feedback) {
        Iterable<presidio.output.domain.records.alerts.Alert> alerts = alertPersistencyService.findAll(alertIds);


        Map<String, User> usersToBeUpdated = new HashMap<>();
        alerts.forEach(alert->{
            AlertEnums.AlertFeedback origianlFeedback = alert.getFeedback();
            //1. update alert feedback with the new given feedback
            alert.setFeedback(feedback);

            Double origContribution = alert.getContributionToUserScore();
            Double contributionDelta = calcContributionToUserScoreDelta(alert, origianlFeedback, feedback);

            if(!contributionDelta.equals(0D)) {

                //2. update alert contribution to user score according to new feedback
                alert.setContributionToUserScore(origContribution + contributionDelta);

                //3. increase\decrease user score with the updated alert contribution
                User updatedUser = updateUserScore(alert.getUserId(), usersToBeUpdated, contributionDelta);
                usersToBeUpdated.put(updatedUser.getId(), updatedUser);

            }
        });

        //4. update user severity according to new score (based on already calculated severities percentiles)
        for (User user: usersToBeUpdated.values()) {
            UserSeverity newSeverity = userSeverityService.getSeveritiesMap(false).getUserSeverity(user.getScore());
            user.setSeverity(newSeverity);
        }

        List<Alert> alertsList = (List<Alert>) alerts;
        if(! alertsList.isEmpty()) {
            alertPersistencyService.save(alertsList);
        }
        if(! usersToBeUpdated.isEmpty()) {
            userPersistencyService.save(new ArrayList<>(usersToBeUpdated.values()));
        }
    }

    private User updateUserScore(String userId, Map<String, User> usersCache, Double scoreDelta) {
        User user;
        if(usersCache.containsKey(userId)) {
            user = usersCache.get(userId);
        }
        else {
            user = userPersistencyService.findUserById(userId);
        }
        user.setScore(user.getScore() + scoreDelta);
        return user;
    }

    private Double calcContributionToUserScoreDelta(Alert alert,
                                                    AlertEnums.AlertFeedback originalFeedback,
                                                    AlertEnums.AlertFeedback newFeedback) {
        Double scoreMultiplier = feedbackToAlertContributionMap.get(originalFeedback).get(newFeedback);
        AlertEnums.AlertSeverity severity = alert.getSeverity();
        Double alertContributionToUserScore = alertSeverityService.getUserScoreContributionFromSeverity(severity);
        return scoreMultiplier * alertContributionToUserScore;
    }
}
