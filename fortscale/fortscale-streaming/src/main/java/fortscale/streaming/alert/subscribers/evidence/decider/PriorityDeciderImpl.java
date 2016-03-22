package fortscale.streaming.alert.subscribers.evidence.decider;

import fortscale.streaming.alert.event.wrappers.EnrichedFortscaleEvent;
import fortscale.streaming.alert.subscribers.AlertDeciderPriorityTable;
import fortscale.streaming.alert.subscribers.FeatureInPriorityTable;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.*;

/**
 * Created by rans on 14/03/16.
 */
public class PriorityDeciderImpl implements DeciderCommand {

    static AlertDeciderPriorityTable alertDeciderPriorityTable = new AlertDeciderPriorityTable();
    List<String> namesOrderedByPriority = null;
    List<String> scoresOrderedByPriority = null;

    /**
     *
     * @param pQueue array of EnrichedFortscaleEvent's, each holds event from Esper based on EnrichedFortscaleEvent, that are eligible for decider
     * @param deciderCommands List of <DeciderCommand> that can be chained for next decider iteration
     * @return decide and return anomalyTypeFieldName of the main evidence or entity event. The alert name dervied from this type (by configuration)
     */
    @Override
    public String getName(List<EnrichedFortscaleEvent> pQueue, List<DeciderCommand> deciderCommands) {
        //put all events in map by their EvidenceType
        Map<String, List<EnrichedFortscaleEvent>> priorityMap = new HashMap<>();
        for (EnrichedFortscaleEvent eventMap : pQueue) {
            String evidenceType = (String)eventMap.getAnomalyTypeFieldName();
            if (!priorityMap.containsKey(evidenceType)) {
                priorityMap.put(evidenceType, new ArrayList<EnrichedFortscaleEvent>());
            }
            priorityMap.get(evidenceType).add(eventMap);
        }
        //find highest EvidenceType priority
        if (namesOrderedByPriority == null){
            namesOrderedByPriority = getIndicatorTypesSortedByPriority(FeatureInPriorityTable.NamingPriority);
        }
        for (String indicatorType : namesOrderedByPriority){
            List<EnrichedFortscaleEvent> evidences = priorityMap.get(indicatorType);
            if (evidences == null){
                continue;
            }
            if (evidences.size() == 1){
                return (String)evidences.iterator().next().getAnomalyTypeFieldName();
            } else if (evidences.size() == 0){
                return null;
            } else { //there are more than one potential type
                if (deciderCommands.size() <= 1) {
                    //this is the last decider. choose randomly the first event in the list
                    return (String)evidences.get(0).getAnomalyTypeFieldName();

                } else {
                    //iterate to next deciderImpl, passing the list of events to decide from and the list of decider to iterate on it
                    return deciderCommands.get(1).getName(priorityMap.get(indicatorType), deciderCommands.subList(1, deciderCommands.size()));
                }
            }
        }
        return null;
    }

    /**
     *
     * @param pQueue array of EnrichedFortscaleEvent's, each holds event from Esper based on EnrichedFortscaleEvent, that are eligible for decider
     * @param deciderCommands List of <DeciderCommand> that can be chained for next decider iteration
     * @return decide and return anomalyTypeFieldName of the main evidence or entity event. The alert score dervied from this type (by configuration)
     */
    @Override
    public Integer getScore(List<EnrichedFortscaleEvent> pQueue, List<DeciderCommand> deciderCommands) {
        //put all events in map by their EvidenceType
        Map<String, List<EnrichedFortscaleEvent>> priorityMap = new HashMap<>();
        for (EnrichedFortscaleEvent eventMap : pQueue) {
            String evidenceType = (String)eventMap.getAnomalyTypeFieldName();
            if (!priorityMap.containsKey(evidenceType)) {
                priorityMap.put(evidenceType, new ArrayList<EnrichedFortscaleEvent>());
            }
            priorityMap.get(evidenceType).add(eventMap);
        }
        //find highest EvidenceType priority
        if (scoresOrderedByPriority == null){
            scoresOrderedByPriority = getIndicatorTypesSortedByPriority(FeatureInPriorityTable.ScorePriority);
        }
        for (String indicatorType : scoresOrderedByPriority){
            List<EnrichedFortscaleEvent> evidences = priorityMap.get(indicatorType);
            if (evidences == null){
                continue;
            }
            if (evidences.size() == 1){
                return (Integer)evidences.iterator().next().getScore();
            } else if (evidences.size() == 0){
                return null;
            } else { //there are more than one potential type
                if (deciderCommands.size() <= 1) {
                    //this is the last decider. choose randomly the first event in the list
                    return (Integer)evidences.get(0).getScore();

                } else {
                    //iterate to next deciderImpl, passing the list of events to decide from and the list of decider to iterate on it
                    return deciderCommands.get(1).getScore(priorityMap.get(indicatorType), deciderCommands.subList(1, deciderCommands.size()));
                }
            }
        }
        return null;
    }


    /**
     * get highest priority Semantic Indicator name
     * @return
     */
    private List<String> getIndicatorTypesSortedByPriority(FeatureInPriorityTable featureInPriorityTable){
        //init sortedList once
        //TODO: remove all priority '0' from the list!!!
        Map<String, Object> featureMap = alertDeciderPriorityTable.getPriorityMap(featureInPriorityTable);
        Map<Integer, String> reversedMap = new HashMap<>();
        //create a reverse map where key is value and value is key, so we can sort by priority
        //TODO: this will not work if two types have the same priority!!!
        for (String evidenceType : featureMap.keySet()) {
            reversedMap.put((Integer) featureMap.get(evidenceType), evidenceType);
        }
        List<String> sortedList = new ArrayList<>();
        //retrieve keys in sorted order
        TreeSet<Integer> keys = new TreeSet<Integer>(reversedMap.keySet());
        //reverse order of keys
        Set<Integer> descendingKeys = keys.descendingSet();
        for (Integer key : descendingKeys) {
            String value = reversedMap.get(key);
            sortedList.add(value);
        }
        return sortedList;
    }
}
