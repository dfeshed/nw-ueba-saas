package fortscale.streaming.alert.subscribers.evidence.decider;

import java.util.HashMap;
import java.util.Map;

/**
 * Created by shays on 22/03/2016.
 * Contain the attributes required to decide about which indicator create the alert
 */
public class DeciderConfiguration {
    private Map<String, Integer> namePriority;
    private Map<String, Integer> scorePriority;
    private Map<String, String> alertName;



    public DeciderConfiguration(){
        //TODO: load from configuration
        namePriority = new HashMap<>();
        scorePriority = new HashMap<>();
        alertName = new HashMap<>();

        alertName.put("smart","smart");
        alertName.put("BruteForce","BruteForce");
        alertName.put("vpn_geo_hopping","vpn_geo_hopping");

        namePriority.put("smart",1);
        namePriority.put("BruteForce",4);
        namePriority.put("vpn_geo_hopping",2);

        scorePriority.put("smart",5);
        scorePriority.put("BruteForce",4);
        scorePriority.put("vpn_geo_hopping",2);


    }

    public Map<String, Integer> getNamePriority() {
        return namePriority;
    }

    public Map<String, Integer> getScorePriority() {
        return scorePriority;
    }

    public Map<String, String> getAlertName() {
        return alertName;
    }

    public String getAlertNameByAnonalyType(String anomalyType){
        return alertName.get(anomalyType);
    }

    public Map<String, Integer> getPriorityMap(PriorityType type) {
        switch (type){
            case NAME_PRIORITY:
                        return this.namePriority;
            case SCORE_PRIORITY:
                        return this.scorePriority;
            default: throw new RuntimeException("Type is not supported");
        }
    }

    public static enum PriorityType{
        NAME_PRIORITY,
        SCORE_PRIORITY
    }
}
