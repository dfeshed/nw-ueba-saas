package fortscale.collection.jobs.demo;

import fortscale.domain.core.Evidence;
import fortscale.domain.core.Severity;
import fortscale.domain.core.User;
import fortscale.services.AlertsService;
import fortscale.utils.logging.Logger;
import net.minidev.json.JSONObject;
import org.joda.time.DateTime;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Amir Keren on 17/02/2016.
 */
public class Scenario {

    private static Logger logger = Logger.getLogger(ScenarioGeneratorJob.class);

    private String alertTitle;
    private int alertScore;
    private int indicatorsScore;
    private Severity alertSeverity;
    private String samaccountname;
    private int eventsScore;
    private List<BaseLineEvents> baseLineEvents;
    private List<AnomaliesEvents> anomalyEvents;
    private int minHourForAnomaly;
    private int maxHourForAnomaly;

    public List<BaseLineEvents> getBaseLineEvents() {
        return baseLineEvents;
    }

    public void setBaseLineEvents(List<BaseLineEvents> baseLineEvents) {
        this.baseLineEvents = baseLineEvents;
    }

    public List<AnomaliesEvents> getAnomalyEvents() {
        return anomalyEvents;
    }

    public void setAnomalyEvents(List<AnomaliesEvents> anomalyEvents) {
        this.anomalyEvents = anomalyEvents;
    }

    public String getAlertTitle() {
        return alertTitle;
    }

    public void setAlertTitle(String alertTitle) {
        this.alertTitle = alertTitle;
    }

    public int getAlertScore() {
        return alertScore;
    }

    public void setAlertScore(int alertScore) {
        this.alertScore = alertScore;
    }

    public int getIndicatorsScore() {
        return indicatorsScore;
    }

    public void setIndicatorsScore(int indicatorsScore) {
        this.indicatorsScore = indicatorsScore;
    }

    public Severity getAlertSeverity() {
        return alertSeverity;
    }

    public void setAlertSeverity(Severity alertSeverity) {
        this.alertSeverity = alertSeverity;
    }

    public String getSamaccountname() {
        return samaccountname;
    }

    public void setSamaccountname(String samaccountname) {
        this.samaccountname = samaccountname;
    }

    public int getEventsScore() {
        return eventsScore;
    }

    public void setEventsScore(int eventsScore) {
        this.eventsScore = eventsScore;
    }

    public int getMinHourForAnomaly() {
        return minHourForAnomaly;
    }

    public void setMinHourForAnomaly(int minHourForAnomaly) {
        this.minHourForAnomaly = minHourForAnomaly;
    }

    public int getMaxHourForAnomaly() {
        return maxHourForAnomaly;
    }

    public void setMaxHourForAnomaly(int maxHourForAnomaly) {
        this.maxHourForAnomaly = maxHourForAnomaly;
    }

    /**
     *
     * This method generates the scenario
     *
     * @return
     */
    public List<JSONObject> generateScenario(DemoUtils demoUtils, DateTime anomalyDate, User user,
            AlertsService alertsService) {
        List<JSONObject> records = new ArrayList();
        for (BaseLineEvents baseLineEvent: baseLineEvents) {
            //records.addAll(createEvents(baseLineEvent.getDemoEvent(), baseLineEvent.getDataSource()));
        }
        List<Evidence> indicators = new ArrayList();
        for (AnomaliesEvents anomaliesEvent: anomalyEvents) {
            /*records.addAll(createAnomalies(anomaliesEvent.getDataSource(), anomaliesEvent.getDemoEvent(),
                    anomaliesEvent.getMinNumberOfAnomalies(), anomaliesEvent.getMaxNumberOfAnomalies(),
                    anomaliesEvent.getMinHourForAnomaly(), anomaliesEvent.getMaxHourForAnomaly(),
                    anomaliesEvent.getTimeframe(), anomaliesEvent.getEvidenceType(), indicatorsScore, anomaliesEvent,
                    anomaliesEvent.getAnomalyTypeFieldName(), indicators));*/
        }
        demoUtils.createAlert(alertTitle, anomalyDate.getMillis(), anomalyDate.plusDays(1).minusMillis(1).getMillis(),
                user, indicators, alertScore, alertSeverity, alertsService);
        return records;
    }

}