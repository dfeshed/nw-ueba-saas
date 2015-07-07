package fortscale.streaming.task;

import com.espertech.esper.client.EPServiceProvider;
import com.espertech.esper.client.EPStatement;
import fortscale.domain.core.*;
import fortscale.services.AlertsService;
import org.apache.commons.lang.time.DateUtils;
import org.apache.samza.storage.kv.KeyValueStore;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.mongodb.core.MongoOperations;
import parquet.org.slf4j.Logger;
import parquet.org.slf4j.LoggerFactory;

import java.util.*;

/**
 * Wraps Esper Statement and Listener. No dependency on Esper libraries.
 */
public class MonitorAlertSubscriber {

    /**
     * Logger
     */
    private static Logger logger = LoggerFactory.getLogger(MonitorAlertSubscriber.class);

    @Autowired
    private MongoOperations mongoOps;

    public MonitorAlertSubscriber(EPServiceProvider epService, AlertsService alertsService) {
        this.epService = epService;
        this.alertsService = alertsService;
        epService.getEPAdministrator().getConfiguration().addVariable("updateTimestamp", Long.class, maxPrevTime);
        epService.getEPAdministrator().createEPL("on TimestampUpdate set updateTimestamp = minimalTimeStamp");
        EPStatement criticalEventStatement = epService.getEPAdministrator().createEPL(getStatement());
        criticalEventStatement.setSubscriber(this);
    }

    EPServiceProvider epService;
    protected AlertsService alertsService;
    /**
     * {@inheritDoc}
     */

    Long maxPrevTime = 0l;

    public String getStatement() {

        // Example of simple EPL with a Time Window
        return "select id, entityType, entityName, startDate, endDate, type, name, dataSource, score, anomalyValue, severity from EvidenceStream.win:ext_timed_batch(startDate, 2 min, 0L) where startDate > updateTimestamp order by startDate";
    }

    /**
     * Listener method called when Esper has detected a pattern match.
     * Creates an alert and saves it in mongo. this includes the references to its evidences, which are already in mongo.
     */
    public void update(Map[] insertStream, Map[] removeStream) {
        try {

            if (insertStream != null) {
                Long scoreSum = 0L;
                Long firstStartDate = 0L;
                Long lastEndDate = 0L;
                EntityType entityType = EntityType.User;
                String entityName = "";
                boolean isFirst = true;
                List<Evidence> evidences = new ArrayList<>();
                for (Map insertEventMap : insertStream) {

                    String id = (String) insertEventMap.get("id");
                    Long startDate = (Long) insertEventMap.get("startDate");
                    Long endDate = (Long) insertEventMap.get("endDate");
                    entityType = (EntityType) insertEventMap.get("entityType");
                    entityName = (String) insertEventMap.get("entityName");
                    int score = (Integer) insertEventMap.get("score");

                    scoreSum += score;
                    if (isFirst){
                        firstStartDate = startDate;
                    }
                    lastEndDate = endDate;

                    //create new Evidence with the evidence id. it creates reference to the evidence object in mongo.
                    Evidence evidence = new Evidence();
                    evidence.setId(id);
                    evidences.add(evidence);

                }
                Integer average = ((Long)(scoreSum/insertStream.length)).intValue();
                Severity severity = alertsService.getScoreToSeverity().get(average);
                String title = "Suspicious hourly activity";
                Alert alert = new Alert(title, firstStartDate, lastEndDate, entityType, entityName, "", evidences, "", average, severity, AlertStatus.Unread, "");

                //Save alert to mongoDB
                alertsService.saveAlertInRepository(alert);
            }
        } catch (RuntimeException ex){
            logger.error(ex.getMessage(), ex);
        }

    }
}
