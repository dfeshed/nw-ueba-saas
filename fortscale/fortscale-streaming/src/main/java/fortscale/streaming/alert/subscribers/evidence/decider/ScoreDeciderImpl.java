package fortscale.streaming.alert.subscribers.evidence.decider;

import fortscale.streaming.alert.event.wrappers.EnrichedFortscaleEvent;
import fortscale.streaming.alert.subscribers.AlertDeciderPriorityTable;
import fortscale.streaming.alert.subscribers.FeatureInPriorityTable;

import java.util.*;

/**
 * Created by rans on 14/03/16.
 */
public class ScoreDeciderImpl extends OrderedDeciderCommandAbstract<Integer>{

    private boolean useMaxScore = true; //If true - return the List<EnrichedFortscaleEvent> with max score
                                        //If false - return the List<EnrichedFortscaleEvent> with min score

    protected  Integer getOrder(EnrichedFortscaleEvent evidence){
        return evidence.getScore();
    }

    protected boolean isUseMax(){
        return useMaxScore;
    }

 /*   public List<EnrichedFortscaleEvent> decide(List<EnrichedFortscaleEvent> enrichedFortscaleEvents){
        SortedMap<Integer, List<EnrichedFortscaleEvent>> sortedMap = new TreeMap<>();
        //Create map which map each name priority to list of EnrichedFortscaleEvent with have the same priority
        for (EnrichedFortscaleEvent evidence : enrichedFortscaleEvents){
            Integer score = evidence.getScore();
            List<EnrichedFortscaleEvent> evidencesInPriority = sortedMap.get(score);
            if (evidencesInPriority == null){
                evidencesInPriority = new ArrayList<>();
                sortedMap.put(score, evidencesInPriority);
            }
            evidencesInPriority.add(evidence);

        }
        //Get the evidences of the max priority
        Integer relevantPriority = null;
        if (useMaxScore){
            relevantPriority = sortedMap.lastKey();//Key with max value
        } else {
            relevantPriority = sortedMap.firstKey();//Key with min value
        }

        return  sortedMap.get(relevantPriority);
    }

    public boolean isUseMaxScore() {
        return useMaxScore;
    }

    public void setUseMaxScore(boolean useMaxScore) {
        this.useMaxScore = useMaxScore;
    }*/

    /*
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
*/

}
