package presidio.webapp.service;

import org.springframework.beans.factory.annotation.Autowired;
import presidio.output.commons.services.alert.AlertSeverityService;
import presidio.output.commons.services.entity.EntitySeverityService;
import presidio.output.domain.records.alerts.Alert;
import presidio.output.domain.records.alerts.AlertEnums;
import presidio.output.domain.records.entity.Entity;
import presidio.output.domain.records.entity.EntitySeverity;
import presidio.output.domain.services.alerts.AlertPersistencyService;
import presidio.output.domain.services.entities.EntityPersistencyService;

import java.util.*;

/**
 * Created by efratn on 04/12/2017.
 */
public class FeedbackServiceImpl implements FeedbackService {

    @Autowired
    private EntityPersistencyService entityPersistencyService;

    @Autowired
    private AlertPersistencyService alertPersistencyService;

    @Autowired
    private EntitySeverityService entitySeverityService;

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
     * alert contribution to entity score and entity score (according to new contribution)
     * @param alertIds
     * @param feedback
     */
    @Override
    public void updateAlertFeedback(List<String> alertIds, AlertEnums.AlertFeedback feedback) {
        Iterable<presidio.output.domain.records.alerts.Alert> alerts = alertPersistencyService.findAll(alertIds);


        Map<String, Entity> entitiesToBeUpdated = new HashMap<>();
        alerts.forEach(alert->{
            AlertEnums.AlertFeedback origianlFeedback = alert.getFeedback();
            //1. update alert feedback with the new given feedback
            alert.setFeedback(feedback);

            Double origContribution = alert.getContributionToEntityScore();
            Double contributionDelta = calcContributionToEntityScoreDelta(alert, origianlFeedback, feedback);

            if(!contributionDelta.equals(0D)) {

                //2. update alert contribution to entity score according to new feedback
                alert.setContributionToEntityScore(origContribution + contributionDelta);

                //3. increase\decrease entity score with the updated alert contribution
                Entity updatedEntity = updateEntityScore(alert.getEntityDocumentId(), entitiesToBeUpdated, contributionDelta);
                entitiesToBeUpdated.put(updatedEntity.getId(), updatedEntity);
            }
        });

        //4. update entity severity according to new score (based on already calculated severities percentiles)
        for (Entity entity: entitiesToBeUpdated.values()) {
            EntitySeverity newSeverity = entitySeverityService.getSeveritiesMap(false).getEntitySeverity(entity.getScore());
            entity.setSeverity(newSeverity);
        }

        List<Alert> alertsList = (List<Alert>) alerts;
        if(! alertsList.isEmpty()) {
            alertPersistencyService.save(alertsList);
        }
        if(! entitiesToBeUpdated.isEmpty()) {
            entityPersistencyService.save(new ArrayList<>(entitiesToBeUpdated.values()));
        }
    }

    private Entity updateEntityScore(String entityId, Map<String, Entity> entitiesCache, Double scoreDelta) {
        Entity entity;
        if(entitiesCache.containsKey(entityId)) {
            entity = entitiesCache.get(entityId);
        }
        else {
            entity = entityPersistencyService.findEntityByDocumentId(entityId);
        }
        entity.setScore(entity.getScore() + scoreDelta);
        return entity;
    }

    private Double calcContributionToEntityScoreDelta(Alert alert,
                                                      AlertEnums.AlertFeedback originalFeedback,
                                                      AlertEnums.AlertFeedback newFeedback) {
        Double scoreMultiplier = feedbackToAlertContributionMap.get(originalFeedback).get(newFeedback);
        AlertEnums.AlertSeverity severity = alert.getSeverity();
        Double alertContributionToEntityScore = alertSeverityService.getEntityScoreContributionFromSeverity(severity);
        return scoreMultiplier * alertContributionToEntityScore;
    }
}
