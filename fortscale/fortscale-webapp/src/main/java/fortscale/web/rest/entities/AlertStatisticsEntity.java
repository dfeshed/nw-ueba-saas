package fortscale.web.rest.entities;


import com.fasterxml.jackson.annotation.JsonProperty;
import fortscale.domain.core.Severity;

import java.util.HashMap;
import java.util.Map;


/**
 * Created by shays on 07/10/2015.
 */
public class AlertStatisticsEntity {


    private String CLOSED_ALERT_FIELD = "closed";
    private String OPENED_ALERT_FIELD = "opened";


    private Map<String,Integer> alertStatusLast7Days;
    private Map<String,Integer> alertStatusLastDay;
    private Map<String,Integer> alertOpenSeverityLast7Days;
    private Map<String,Integer> alertOpenSeverityLastDay;
    

    public AlertStatisticsEntity(int closedAlerts7Days, int openAlerts7Days, int closedAlertsLastDay,
                                 int openAlertsLastDay, int criticalAlertsLast7Days, int highAlertsLast7Days,
                                 int mediumAlertsLast7Days, int lowAlertsLast7Days,
                                 int criticalAlertsLastDay, int highAlertsLastDay,
                                 int mediumAlertsLastDay, int lowAlertsLastDay

    ) {
        this.alertStatusLastDay = getStatusMap(openAlertsLastDay, closedAlertsLastDay);
        this.alertStatusLast7Days = getStatusMap(openAlerts7Days, closedAlerts7Days);
       
        this.alertOpenSeverityLast7Days = getAlertSevirityMap(criticalAlertsLast7Days, highAlertsLast7Days,
                mediumAlertsLast7Days, lowAlertsLast7Days);

        this.alertOpenSeverityLastDay = getAlertSevirityMap(criticalAlertsLastDay, highAlertsLastDay,
                mediumAlertsLastDay, lowAlertsLastDay);


    }



    @JsonProperty("alert_status_last_7_days")
    public Map<String,Integer> getAlertStatusLast7Days() {
        return alertStatusLast7Days;
    }

    public void setAlertStatusLast7Days(Map<String,Integer> alertStatusLast7Days) {
        this.alertStatusLast7Days = alertStatusLast7Days;
    }

    @JsonProperty("alert_status_last_day")
    public Map<String,Integer> getAlertStatusLastDay() {
        return alertStatusLastDay;
    }

    public void setAlertStatusLastDay(Map<String,Integer> alertStatusLastDay) {
        this.alertStatusLastDay = alertStatusLastDay;
    }

    @JsonProperty("alert_open_severity_last_7_days")
    public Map<String, Integer> getAlertOpenSeverityLast7Days() {
        return alertOpenSeverityLast7Days;
    }

    public void setAlertOpenSeverityLast7Days(Map<String, Integer> alertOpenSeverityLast7Days) {
        this.alertOpenSeverityLast7Days = alertOpenSeverityLast7Days;
    }

    @JsonProperty("alert_open_severity_last_day")
    public Map<String, Integer> getAlertOpenSeverityLastDay() {
        return alertOpenSeverityLastDay;
    }

    public void setAlertOpenSeverityLastDay(Map<String, Integer> alertOpenSeverityLastDay) {
        this.alertOpenSeverityLastDay = alertOpenSeverityLastDay;
    }



    private Map<String,Integer> getAlertSevirityMap(int critical, int high, int medium, int low){
        Map<String,Integer> sevirityMap = new HashMap<>();
        sevirityMap.put(Severity.Critical.name().toLowerCase(), critical);
        sevirityMap.put(Severity.High.name().toLowerCase(), high);
        sevirityMap.put(Severity.Medium.name().toLowerCase(), medium);
        sevirityMap.put(Severity.Low.name().toLowerCase(), low);
        return sevirityMap;
    }

    private Map<String, Integer> getStatusMap(int openCount, int closedCount){
        Map<String,Integer> statusMap = new HashMap<>();
        statusMap.put(OPENED_ALERT_FIELD, openCount);
        statusMap.put(CLOSED_ALERT_FIELD, closedCount);
        return  statusMap;
    }



}
