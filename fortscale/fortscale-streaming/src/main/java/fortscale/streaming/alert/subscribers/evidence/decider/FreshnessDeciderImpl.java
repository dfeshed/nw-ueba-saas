package fortscale.streaming.alert.subscribers.evidence.decider;

import fortscale.streaming.alert.event.wrappers.EnrichedFortscaleEvent;

import java.util.*;

/**
 * Created by rans on 14/03/16.
 */
public class FreshnessDeciderImpl extends OrderedDeciderCommandAbstract<Long>{

    private boolean useMaxTime = true; //If true - return the List<EnrichedFortscaleEvent> with max score
    //If false - return the List<EnrichedFortscaleEvent> with min score

    /*public List<EnrichedFortscaleEvent> decide(List<EnrichedFortscaleEvent> enrichedFortscaleEvents){
        SortedMap<Long, List<EnrichedFortscaleEvent>> sortedMap = new TreeMap<>();
        //Create map which map each name priority to list of EnrichedFortscaleEvent with have the same priority
        for (EnrichedFortscaleEvent evidence : enrichedFortscaleEvents){
            Long time = evidence.getStartTimeUnix();
            List<EnrichedFortscaleEvent> evidencesInPriority = sortedMap.get(time);
            if (evidencesInPriority == null){
                evidencesInPriority = new ArrayList<>();
                sortedMap.put(time, evidencesInPriority);
            }
            evidencesInPriority.add(evidence);

        }
        //Get the evidences of the max priority
        Long relevantPriority = null;
        if (useMaxTime){
            relevantPriority = sortedMap.lastKey();//Key with max value
        } else {
            relevantPriority = sortedMap.firstKey();//Key with min value
        }

        return  sortedMap.get(relevantPriority);
    }
*/
    protected  Long getOrder(EnrichedFortscaleEvent evidence){
        Long time = evidence.getStartTimeUnix();
        return time;
    }

    protected boolean isUseMax(){
        return useMaxTime;
    }


    public boolean isUseMaxTime() {
        return useMaxTime;
    }

    public void setUseMaxTime(boolean useMaxTime) {
        this.useMaxTime = useMaxTime;
    }

 /*   @Override
    public String getName(List<EnrichedFortscaleEvent> pQueue, List<DeciderCommand> deciderCommands) {
        //Sort events by their score
        Map<Long, List<EnrichedFortscaleEvent>> freshnessMap = new HashMap<>();

        Long timestamp = 0L;
        for (EnrichedFortscaleEvent eventMap : pQueue) {
            timestamp = eventMap.getStartTimeUnix();
            if (!freshnessMap.containsKey(timestamp)) {
                freshnessMap.put(timestamp, new ArrayList<EnrichedFortscaleEvent>());
            }
            freshnessMap.get(timestamp).add(eventMap);
        }
        //find freshest timestamp
        //retrieve keys in sorted order
        TreeSet<Long> sortedKeys = new TreeSet<Long>(freshnessMap.keySet());
        List<EnrichedFortscaleEvent> evidences = freshnessMap.get(sortedKeys.last());
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
                return deciderCommands.get(1).getName(freshnessMap.get(timestamp), deciderCommands.subList(1, deciderCommands.size()));
            }
        }
    }

    @Override
    public Long getScore(List<EnrichedFortscaleEvent> pQueue, List<DeciderCommand> deciderCommands) {
        //Sort events by their score
        Map<Long, List<EnrichedFortscaleEvent>> scoresMap = new HashMap<>();
        Long timestamp = 0L;
        for (EnrichedFortscaleEvent eventMap : pQueue) {
            timestamp = eventMap.getStartTimeUnix();
            if (!scoresMap.containsKey(timestamp)) {
                scoresMap.put(timestamp, new ArrayList<EnrichedFortscaleEvent>());
            }
            scoresMap.get(timestamp).add(eventMap);
        }
        //find highest score
        //retrieve keys in sorted order
        TreeSet<Long> sortedKeys = new TreeSet<Long>(scoresMap.keySet());
        List<EnrichedFortscaleEvent> evidences = scoresMap.get(sortedKeys.last());
        if (evidences.size() == 1){
            return (Long)evidences.get(0).getScore();
        } else if (evidences.size() == 0){
            return null;
        } else { //there are more than one evidence per highest score
            if (deciderCommands.size() <= 1) {
                //this is the last decider. choose randomly the first event in the list
                return (Long)evidences.get(0).getScore();

            } else {
                //iterate to next deciderImpl, passing the list of events to decide from and the list of decider to iterate on it
                return deciderCommands.get(1).getScore(scoresMap.get(timestamp), deciderCommands.subList(1, deciderCommands.size()));
            }
        }
    }*/
}
