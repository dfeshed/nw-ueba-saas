package fortscale.streaming.alert.subscribers.evidence.decider;

import fortscale.streaming.alert.subscribers.AlertDeciderPriorityTable;
import fortscale.streaming.alert.subscribers.FeatureInPriorityTable;

import java.util.*;

/**
 * Created by rans on 14/03/16.
 */
public class ScoreDeciderImpl implements DeciderCommand{


    @Override
    public String getName(List<Map> pQueue, List<DeciderCommand> deciderCommands) {
        //Sort events by their score
        Map<Integer, List<Map>> scoresMap = new HashMap<>();
        Integer score = 0;
        for (Map eventMap : pQueue) {
            score = (Integer)eventMap.get(SCORE_FIELD_NAME);
            if (!scoresMap.containsKey(score)) {
                scoresMap.put(score, new ArrayList<Map>());
            }
            scoresMap.get(score).add(eventMap);
        }
        //find highest score
        //retrieve keys in sorted order
        TreeSet<Integer> sortedKeys = new TreeSet<Integer>(scoresMap.keySet());
        List<Map> evidences = scoresMap.get(sortedKeys.last());
        if (evidences.size() == 1){
            return (String)evidences.get(0).get(ANOMALY_TYPE_FIELD_NAME);
        } else if (evidences.size() == 0){
            return null;
        } else { //there are more than one evidence per highest score
            if (deciderCommands.get(1) == null) {
                //this is the last decider. choose randomly the first event in the list
                return (String)evidences.get(0).get(ANOMALY_TYPE_FIELD_NAME);

            } else {
                //iterate to next deciderImpl, passing the list of events to decide from and the list of decider to iterate on it
                deciderCommands.get(1).getName(scoresMap.get(score), deciderCommands.subList(1, deciderCommands.size()));
            }
        }
        return null;
    }

    @Override
    public Integer getScore(List<Map> pQueue, List<DeciderCommand> deciderCommands) {
        //Sort events by their score
        Map<Integer, List<Map>> scoresMap = new HashMap<>();
        Integer score = 0;
        for (Map eventMap : pQueue) {
            score = (Integer)eventMap.get(SCORE_FIELD_NAME);
            if (!scoresMap.containsKey(score)) {
                scoresMap.put(score, new ArrayList<Map>());
            }
            scoresMap.get(score).add(eventMap);
        }
        //find highest score
        //retrieve keys in sorted order
        TreeSet<Integer> sortedKeys = new TreeSet<Integer>(scoresMap.keySet());
        List<Map> evidences = scoresMap.get(sortedKeys.last());
        if (evidences.size() == 1){
            return (Integer)evidences.get(0).get(SCORE_FIELD_NAME);
        } else if (evidences.size() == 0){
            return null;
        } else { //there are more than one evidence per highest score
            if (deciderCommands.get(1) == null) {
                //this is the last decider. choose randomly the first event in the list
                return (Integer)evidences.get(0).get(SCORE_FIELD_NAME);

            } else {
                //iterate to next deciderImpl, passing the list of events to decide from and the list of decider to iterate on it
                deciderCommands.get(1).getScore(scoresMap.get(score), deciderCommands.subList(1, deciderCommands.size()));
            }
        }
        return null;
    }


}
