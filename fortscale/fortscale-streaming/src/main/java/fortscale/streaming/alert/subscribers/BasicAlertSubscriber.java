package fortscale.streaming.alert.subscribers;

import fortscale.domain.core.*;
import fortscale.services.AlertsService;
import parquet.org.slf4j.Logger;
import parquet.org.slf4j.LoggerFactory;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/**
 * Wraps Esper Statement and Listener. No dependency on Esper libraries.
 */
public class BasicAlertSubscriber {

    /**
     * Logger
     */
    private static Logger logger = LoggerFactory.getLogger(BasicAlertSubscriber.class);

    public BasicAlertSubscriber(AlertsService alertsService) {
        this.alertsService = alertsService;
    }

    protected AlertsService alertsService;


    /**
     * Listener method called when Esper has detected a pattern match.
     * Creates an alert and saves it in mongo. this includes the references to its evidences, which are already in mongo.
     */
    public void update(Map[] insertStream, Map[] removeStream) {
        try {

            if (insertStream != null) {
                List<Evidence> evidences = new ArrayList<>();
                for (Map insertEventMap : insertStream) {
                    String id = (String) insertEventMap.get("id");
                    //create new Evidence with the evidence id. it creates reference to the evidence object in mongo.
                    Evidence evidence = new Evidence(id);
                    evidences.add(evidence);
                }

                String title = (String) insertStream[0].get("title");
                Long startDate = (Long) insertStream[0].get("startDate");
                Long endDate = (Long) insertStream[0].get("endDate");
                EntityType entityType = (EntityType) insertStream[0].get(Evidence.entityTypeField);
                String entityName = (String) insertStream[0].get(Evidence.entityNameField);
                Double score = (Double) insertStream[0].get("score");
                Integer roundScore = score.intValue();
                Severity severity = alertsService.getScoreToSeverity().floorEntry(roundScore).getValue();
                Alert alert = new Alert(title, startDate, endDate, entityType, entityName, null, evidences, null, roundScore, severity, AlertStatus.Unread, null);

                //Save alert to mongoDB
                alertsService.saveAlertInRepository(alert);
            }
        } catch (RuntimeException ex){
            logger.error(ex.getMessage(), ex);
        }

    }
}
