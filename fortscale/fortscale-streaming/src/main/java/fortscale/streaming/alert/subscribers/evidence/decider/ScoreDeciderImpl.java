package fortscale.streaming.alert.subscribers.evidence.decider;

import fortscale.streaming.alert.event.wrappers.EnrichedFortscaleEvent;
import fortscale.streaming.alert.subscribers.AlertDeciderPriorityTable;
import fortscale.streaming.alert.subscribers.FeatureInPriorityTable;

import java.util.*;

/**
 * Created by rans on 14/03/16.
 */
public class ScoreDeciderImpl implements DeciderCommand{


    @Override
    public String getName(List<EnrichedFortscaleEvent> pQueue, List<DeciderCommand> deciderCommands) {
        //Sort events by their score
        Map<Integer, List<EnrichedFortscaleEvent>> scoresMap = new HashMap<>();
        Integer score = 0;
        for (EnrichedFortscaleEvent eventMap : pQueue) {
            score = (Integer)eventMap.getScore();
            if (!scoresMap.containsKey(score)) {
                scoresMap.put(score, new ArrayList<EnrichedFortscaleEvent>());
            }
            scoresMap.get(score).add(eventMap);
        }
        //find highest score
        //retrieve keys in sorted order
        TreeSet<Integer> sortedKeys = new TreeSet<Integer>(scoresMap.keySet());
        List<EnrichedFortscaleEvent> evidences = scoresMap.get(sortedKeys.last());
        if (evidences.size() == 1){
            return (String)evidences.get(0).getAnomalyTypeFieldName();
        } else if (evidences.size() == 0){
            return null;
        } else { //there are more than one evidence per highest score
            if (deciderCommands.size() <= 1) {
                //this is the last decider. choose randomly the first event in the list
                return (String)evidences.get(0).getAnomalyTypeFieldName();

            } else {
                //iterate to next deciderImpl, passing the list of events to decide from and the list of decider to iterate on it
                return deciderCommands.get(1).getName(scoresMap.get(score), deciderCommands.subList(1, deciderCommands.size()));
            }
        }
    }

    @Override
    public Integer getScore(List<EnrichedFortscaleEvent> pQueue, List<DeciderCommand> deciderCommands) {
        //Sort events by their score
        Map<Integer, List<EnrichedFortscaleEvent>> scoresMap = new HashMap<>();
        Integer score = 0;
        for (EnrichedFortscaleEvent eventMap : pQueue) {
            score = (Integer)eventMap.getScore();
            if (!scoresMap.containsKey(score)) {
                scoresMap.put(score, new ArrayList<EnrichedFortscaleEvent>());
            }
            scoresMap.get(score).add(eventMap);
        }
        //find highest score
        //retrieve keys in sorted order
        TreeSet<Integer> sortedKeys = new TreeSet<Integer>(scoresMap.keySet());
        List<EnrichedFortscaleEvent> evidences = scoresMap.get(sortedKeys.last());
        if (evidences.size() == 1){
            return (Integer)evidences.get(0).getScore();
        } else if (evidences.size() == 0){
            return null;
        } else { //there are more than one evidence per highest score
            if (deciderCommands.size() <= 1) {
                //this is the last decider. choose randomly the first event in the list
                return (Integer)evidences.get(0).getScore();

            } else {
                //iterate to next deciderImpl, passing the list of events to decide from and the list of decider to iterate on it
                return deciderCommands.get(1).getScore(scoresMap.get(score), deciderCommands.subList(1, deciderCommands.size()));
            }
        }
    }


}
