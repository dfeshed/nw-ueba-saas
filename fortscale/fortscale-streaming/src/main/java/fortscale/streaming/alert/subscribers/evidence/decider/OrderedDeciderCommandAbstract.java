package fortscale.streaming.alert.subscribers.evidence.decider;

import fortscale.domain.core.AlertTimeframe;
import fortscale.streaming.alert.event.wrappers.EnrichedFortscaleEvent;

import java.util.ArrayList;
import java.util.List;
import java.util.SortedMap;
import java.util.TreeMap;

/**
 * Created by shays on 22/03/2016.
 */

/**
 * Order decider match to any decider which :
 *  1. get list of EnrichedFortscaleEvent and return list of EnrichedFortscaleEvents
 *  2. aggregated EnrichedFortscaleEvent by some number (time, priority, score etc...)
 *  3. return all EnrichedFortscaleEvent with the same max or min order
 * @param <T>
 */
public abstract class OrderedDeciderCommandAbstract<T extends Number> implements  DeciderCommand{

    public List<EnrichedFortscaleEvent> decide(List<EnrichedFortscaleEvent> enrichedFortscaleEvents, AlertTimeframe alertTimeframe){
        SortedMap<T, List<EnrichedFortscaleEvent>> sortedMap = new TreeMap<>();
        //Create map which map each name order to list of EnrichedFortscaleEvent with have the same order
        for (EnrichedFortscaleEvent evidence : enrichedFortscaleEvents){

            T order = getOrder(evidence, alertTimeframe );
            List<EnrichedFortscaleEvent> evidencesInPriority = sortedMap.get(order);
            if (evidencesInPriority == null){
                evidencesInPriority = new ArrayList<>();
                sortedMap.put(order, evidencesInPriority);
            }
            evidencesInPriority.add(evidence);

        }
        //Get the evidences of the max order
        T relevantOrder = null;
        if (isUseMax()){
            relevantOrder = sortedMap.lastKey();//Key with max value
        } else {
            relevantOrder = sortedMap.firstKey();//Key with min value
        }

        return  sortedMap.get(relevantOrder);
    }

    protected abstract T getOrder(EnrichedFortscaleEvent evidence,AlertTimeframe alertTimeframe);

    protected abstract boolean isUseMax();


}
