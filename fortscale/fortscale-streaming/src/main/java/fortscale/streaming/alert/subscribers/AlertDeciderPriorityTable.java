/*package fortscale.streaming.alert.subscribers;

import org.springframework.beans.factory.annotation.Configurable;

import java.util.HashMap;
import java.util.Map;

/**
 * Created by rans on 14/03/16.
 */
/*@Configurable
public class AlertDeciderPriorityTable {
    Map<FeatureInPriorityTable, Map<String, Object>> priorityMap= new HashMap<>();

    public AlertDeciderPriorityTable() {
        load();
    }

    private void load(){
        //TODO: default implementation, should be replaced by MongoDB
        Map<String, Object> namingPriority = new HashMap<>();
        namingPriority.put("smart", 1);
        namingPriority.put("BruteForce", 4);
        namingPriority.put("vpn_geo_hopping", 2);
        priorityMap.put(FeatureInPriorityTable.NamingPriority, namingPriority);
        Map<String, Object> scorePriority = new HashMap<>();
        scorePriority.put("smart", 5);
        scorePriority.put("BruteForce", 4);
        scorePriority.put("vpn_geo_hopping", 2);
        priorityMap.put(FeatureInPriorityTable.ScorePriority, scorePriority);
        Map<String, Object> alertName = new HashMap<>();
        alertName.put("smart", "Suspicious Daily User activity");
        alertName.put("BruteForce", "Potential Brute Force");
        alertName.put("vpn_geo_hopping", "Suspicious Geo Hopping");
        priorityMap.put(FeatureInPriorityTable.AlertName, alertName);
        Map<String, Object> indicatorsFilterClassName = new HashMap<>();
        indicatorsFilterClassName.put("smart", "FilterSmartImpl");
        indicatorsFilterClassName.put("BruteForce", "FilterBFImpl");
        indicatorsFilterClassName.put("vpn_geo_hopping", "FilterGHImpl");
        priorityMap.put(FeatureInPriorityTable.IndicatorsFilterClassName, indicatorsFilterClassName);
    }


    /**
     *
     * @param feature
     * @param indicatorSemantic
     * @return an object that is mapped for the indicator Semantic in the Feature
     */
  /*  public Object getPriorityObject(FeatureInPriorityTable feature, String indicatorSemantic){
        return priorityMap.get(feature).get(indicatorSemantic);
    }

    /**
     *
     * @param feature
     * @return the map that is mapped to the Feature
     */
    /*public Map<String, Object> getPriorityMap(FeatureInPriorityTable feature){
        return priorityMap.get(feature);
    }


}*/
