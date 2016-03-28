package fortscale.streaming.alert.subscribers.evidence.applicable;

import fortscale.domain.core.EvidenceType;
import fortscale.services.cache.CacheHandler;
import fortscale.streaming.alert.event.wrappers.EnrichedFortscaleEvent;
import fortscale.streaming.alert.subscribers.alert.creator.AlertContextKey;
import fortscale.streaming.alert.subscribers.evidence.decider.DeciderConfiguration;
import org.apache.commons.lang3.tuple.ImmutablePair;
import org.apache.commons.lang3.tuple.Pair;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * Created by shays on 16/03/2016.
 */
public class LimitNotificationAlertAmountCreation implements PreAlertDeciderFilter {

    int maxAmountOfSameAlertInDay =10; //Should be configurable

    @Autowired
    DeciderConfiguration deciderConfiguration;

    //private ConcurrentMap<Pair<String, Long>, AtomicInteger> counterPerType = new ConcurrentHashMap<>();
    @Autowired
    private CacheHandler<AlertContextKey, AtomicInteger> counsPerAlertsContextKey;

    public boolean canCreateAlert(EnrichedFortscaleEvent evidencesOrEntityEvents, Long startDate, Long endDate){
        long startOfDay = evidencesOrEntityEvents.getDailyStartDate();

        Pair<String, Long> key = new ImmutablePair<>(evidencesOrEntityEvents.getAnomalyTypeFieldName(), startOfDay);
        AtomicInteger numberOfInstances = new AtomicInteger(1);
        int previousAmountOfTimes = 0;

        String title = deciderConfiguration.getAlertNameByAnonalyType(evidencesOrEntityEvents.getAnomalyTypeFieldName());
        AlertContextKey alertContextKey = new AlertContextKey(title, startDate.longValue(), endDate.longValue());

        numberOfInstances = counsPerAlertsContextKey.get(alertContextKey);
        if (numberOfInstances!=null){
            //key was already found and numberOfInstances was already taken from the map
            previousAmountOfTimes = numberOfInstances.get();
        }

        return maxAmountOfSameAlertInDay>=previousAmountOfTimes;
    }
    public boolean filterMatch(String anomalyType, EvidenceType evidenceType){
        return EvidenceType.Notification.equals(evidenceType);
    }



}
