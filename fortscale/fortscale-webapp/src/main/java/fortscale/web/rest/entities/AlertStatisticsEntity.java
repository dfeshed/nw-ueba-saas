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


    private final static String OPENED = "Open";
    private final static String CLOSED = "Closed";
    private String TIME_RANGE = "timeRange";

    private List<Map<String,Integer>> alertStatus = new ArrayList<>();
    private List<Map<String,Integer>> alertOpenSeverity = new ArrayList<>();


    /**
     * Add new statuses per time range.
     * @param statusMap
     * @param timeRange
     * @return
     */
    public Map<String,Integer> addAlertStatus(Map<String,Integer> statusMap, int timeRange){
        statusMap.put(TIME_RANGE, timeRange);
        statusMap = updateKeysToLowerCase(statusMap);

        alertStatus.add(statusMap);
        return  statusMap;
    }

    /**
     * Add new sevirities per time range
     * @param sevirityMap
     * @param timeRange
     * @return
     */
    public Map<String,Integer> addAlertSeverityMap(Map<String,Integer> sevirityMap, int timeRange){


        sevirityMap.put(TIME_RANGE, timeRange);

        sevirityMap = updateKeysToLowerCase(sevirityMap);
        alertOpenSeverity.add(sevirityMap);
        return sevirityMap;
    }

    @JsonProperty("alert_status")
    public List<Map<String, Integer>> getAlertStatus() {
        return alertStatus;
    }

    public void setAlertStatus(List<Map<String, Integer>> alertStatus) {
        this.alertStatus = alertStatus;
    }

    @JsonProperty("alert_open_severity")
    public List<Map<String, Integer>> getAlertOpenSeverity() {
        return alertOpenSeverity;
    }

    public void setAlertOpenSeverity(List<Map<String, Integer>> alertOpenSeverity) {
        this.alertOpenSeverity = alertOpenSeverity;
    }

    /**
     * This methos transofrm all the key to be lower case
     * @param originalMap
     * @return
     */
    private Map<String, Integer> updateKeysToLowerCase(Map<String, Integer> originalMap){
        Map<String, Integer> newMap = new HashMap<>();
        for (Map.Entry<String, Integer> entry : originalMap.entrySet() ){
            newMap.put(entry.getKey().toLowerCase(), entry.getValue());
        }
        return newMap;

    }




}
