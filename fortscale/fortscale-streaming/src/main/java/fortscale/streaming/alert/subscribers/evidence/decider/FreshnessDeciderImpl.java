package fortscale.streaming.alert.subscribers.evidence.decider;

import java.util.*;

/**
 * Created by rans on 14/03/16.
 */
public class FreshnessDeciderImpl implements DeciderCommand{
    @Override
    public String getName(List<Map> pQueue, List<DeciderCommand> deciderCommands) {
        //Sort events by their score
        Map<Long, List<Map>> freshnessMap = new HashMap<>();
        Long timestamp = 0L;
        for (Map eventMap : pQueue) {
            timestamp = (Long)eventMap.get(EVENT_TIME_FIELD_NAME);
            if (!freshnessMap.containsKey(timestamp)) {
                freshnessMap.put(timestamp, new ArrayList<Map>());
            }
            freshnessMap.get(timestamp).add(eventMap);
        }
        //find freshest timestamp
        //retrieve keys in sorted order
        TreeSet<Long> sortedKeys = new TreeSet<Long>(freshnessMap.keySet());
        List<Map> evidences = freshnessMap.get(sortedKeys.last());
        if (evidences.size() == 1){
            return (String)evidences.get(0).get(ANOMALY_TYPE_FIELD_NAME);
        } else if (evidences.size() == 0){
            return null;
        } else { //there are more than one evidence per highest score
            if (deciderCommands.size() <= 1) {
                //this is the last decider. choose randomly the first event in the list
                return (String)evidences.get(0).get(ANOMALY_TYPE_FIELD_NAME);

            } else {
                //iterate to next deciderImpl, passing the list of events to decide from and the list of decider to iterate on it
                return deciderCommands.get(1).getName(freshnessMap.get(timestamp), deciderCommands.subList(1, deciderCommands.size()));
            }
        }
    }

    @Override
    public Integer getScore(List<Map> pQueue, List<DeciderCommand> deciderCommands) {
        //Sort events by their score
        Map<Long, List<Map>> scoresMap = new HashMap<>();
        Long timestamp = 0L;
        for (Map eventMap : pQueue) {
            timestamp = (Long)eventMap.get(EVENT_TIME_FIELD_NAME);
            if (!scoresMap.containsKey(timestamp)) {
                scoresMap.put(timestamp, new ArrayList<Map>());
            }
            scoresMap.get(timestamp).add(eventMap);
        }
        //find highest score
        //retrieve keys in sorted order
        TreeSet<Long> sortedKeys = new TreeSet<Long>(scoresMap.keySet());
        List<Map> evidences = scoresMap.get(sortedKeys.last());
        if (evidences.size() == 1){
            return (Integer)evidences.get(0).get(SCORE_FIELD_NAME);
        } else if (evidences.size() == 0){
            return null;
        } else { //there are more than one evidence per highest score
            if (deciderCommands.size() <= 1) {
                //this is the last decider. choose randomly the first event in the list
                return (Integer)evidences.get(0).get(SCORE_FIELD_NAME);

            } else {
                //iterate to next deciderImpl, passing the list of events to decide from and the list of decider to iterate on it
                return deciderCommands.get(1).getScore(scoresMap.get(timestamp), deciderCommands.subList(1, deciderCommands.size()));
            }
        }
    }
}
