package fortscale.web.rest.entities;


import com.fasterxml.jackson.annotation.JsonProperty;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;


/**
 * Created by shays on 07/10/2015.
 *
 * This entity used to present list of alertStatus and alertOpenSevirity per timeRange.
 */
public class AlertStatisticsEntity {


    private Map<String,Integer> alertStatus;
    private Map<String,Integer> alertOpenSeverity;


    
    @JsonProperty("alert_status")
    public Map<String, Integer> getAlertStatus() {
        return alertStatus;
    }

    public void setAlertStatus(Map<String, Integer> alertStatus) {
        this.alertStatus = alertStatus;
    }

    @JsonProperty("alert_open_severity")
    public Map<String, Integer> getAlertOpenSeverity() {
        return alertOpenSeverity;
    }

    public void setAlertOpenSeverity(Map<String, Integer> alertOpenSeverity) {
        this.alertOpenSeverity = alertOpenSeverity;
    }





}
