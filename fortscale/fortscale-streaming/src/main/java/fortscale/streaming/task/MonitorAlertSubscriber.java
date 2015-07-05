package fortscale.streaming.task;

import com.espertech.esper.client.EPServiceProvider;
import com.espertech.esper.client.EPStatement;
import fortscale.domain.core.*;
import fortscale.services.AlertsService;
import org.apache.commons.lang.time.DateUtils;
import org.apache.samza.storage.kv.KeyValueStore;
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

    public MonitorAlertSubscriber(EPServiceProvider epService, KeyValueStore<String, Alert> store, AlertsService alertsService) {
        this.epService = epService;
        this.store = store;
        this.alertsService = alertsService;
        epService.getEPAdministrator().getConfiguration().addVariable("updateTimestamp", Long.class, maxPrevTime);
        epService.getEPAdministrator().createEPL("on TimestampUpdate set updateTimestamp = minimalTimeStamp");
        EPStatement criticalEventStatement = epService.getEPAdministrator().createEPL(getStatement());
        criticalEventStatement.setSubscriber(this);
    }

    EPServiceProvider epService;
    protected KeyValueStore<String, Alert> store;
    protected AlertsService alertsService;
    /**
     * {@inheritDoc}
     */

    int i = 0;
    Long maxPrevTime = 0l;
    int counter = 0;

    public String getStatement() {

        // Example of simple EPL with a Time Window
        return "select id, entityType, entityName, startDate, endDate, type, name, dataSource, score, severity from EvidenceStream.win:ext_timed_batch(startDate, 2 min, 0L) where startDate > updateTimestamp order by startDate";
    }

    /**
     * Listener method called when Esper has detected a pattern match.
     */
    public void update(Map[] insertStream, Map[] removeStream) {
        try {
            Long maxTime = (Long) insertStream[insertStream.length - 1].get("startDate");
            maxPrevTime = DateUtils.ceiling(new Date(maxTime), Calendar.MINUTE).getTime();


            i++;
            if (insertStream != null) {
                List<Map> filterInsertStream = new ArrayList<>();
                Long scoreSum = 0L;
                Long firstStartDate = 0L;
                Long lastEndDate = 0L;
                String evidenceId = "";
                EntityType entityType = EntityType.User;
                String entityName = "";
                boolean isFirst = true;
                List<Evidence> evidences = new ArrayList<>();
                for (Map insertEventMap : insertStream) {
                    Long startDate = (Long) insertEventMap.get("startDate");
                    Long endDate = (Long) insertEventMap.get("endDate");
                    evidenceId = (String) insertEventMap.get("id");
                    entityType = (EntityType) insertEventMap.get("entityType");
                    entityName = (String) insertEventMap.get("entityName");
                    int score = (Integer) insertEventMap.get("score");
                    String type = (String) insertEventMap.get("type");
                    String dataSource = (String) insertEventMap.get("dataSource");
                    Severity severity = (Severity) insertEventMap.get("severity");
                    StringBuilder sb = new StringBuilder();
                    sb.append("received time frame:" + i + "evidenceId: " + evidenceId + " startDate: " + new Date(startDate) + " score: " + score + " counter " + ++counter);

                    logger.info(sb.toString());
                    scoreSum += score;
                    if (isFirst){
                        firstStartDate = startDate;
                    }
                    lastEndDate = endDate;
                    evidences.add(new Evidence(entityType, entityName, startDate, endDate, type, entityName, dataSource, score, severity));


                }
                Integer average = ((Long)(scoreSum/insertStream.length)).intValue();
                Severity severity = alertsService.getScoreToSeverity().get(average);
                String title = "Alert Title";
                Alert alert = new Alert(title, firstStartDate, lastEndDate, entityType, entityName, "", evidences, "", average, severity, AlertStatus.Unread, "");
                //Store alert in mongoDB
                alertsService.saveAlertInRepository(alert);
            }
        } catch (RuntimeException ex){
            logger.error(ex.getMessage(), ex);
        }

    }
}
