package fortscale.streaming.alert.subscribers.evidence.applicable;

import fortscale.domain.core.EvidenceType;
import fortscale.streaming.alert.event.wrappers.EnrichedFortscaleEvent;
import org.apache.commons.lang3.tuple.ImmutablePair;
import org.apache.commons.lang3.tuple.Pair;

import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * Created by shays on 16/03/2016.
 */
public class LimitNotificationAlertCreationPerDay implements PreAlertDeciderFilter {

    int maxAmountOfSameAlertInDay =10; //Should be configurable

    private ConcurrentMap<Pair<String, Long>, AtomicInteger> counterPerType = new ConcurrentHashMap<>();

    public boolean canCreateAlert(EnrichedFortscaleEvent evidencesOrEntityEvents){
        long startOfDay = evidencesOrEntityEvents.getDailyStartDate();

        Pair<String, Long> key = new ImmutablePair<>(evidencesOrEntityEvents.getAnomalyTypeFieldName(), startOfDay);
        AtomicInteger numberOfInstances = new AtomicInteger(1);
        int previousAmountOfTimes = 0;
        numberOfInstances = counterPerType.putIfAbsent(key, numberOfInstances);
        if (numberOfInstances!=null){
            //key was already found and numberOfInstances was already taken from the map
            previousAmountOfTimes = numberOfInstances.getAndIncrement();
        }

        return maxAmountOfSameAlertInDay>=previousAmountOfTimes;
    }
    public boolean filterMatch(String anomalyType, EvidenceType evidenceType){
        return EvidenceType.Notification.equals(evidenceType);
    }



}
