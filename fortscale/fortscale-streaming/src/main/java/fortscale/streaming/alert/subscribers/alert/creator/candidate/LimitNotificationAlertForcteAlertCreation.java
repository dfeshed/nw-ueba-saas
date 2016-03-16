package fortscale.streaming.alert.subscribers.alert.creator.candidate;

import fortscale.domain.core.EvidenceType;
import fortscale.services.AlertsService;
import fortscale.utils.time.TimeUtils;
import fortscale.utils.time.TimestampUtils;
import org.apache.commons.lang3.tuple.ImmutablePair;
import org.apache.commons.lang3.tuple.Pair;

import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * Created by shays on 16/03/2016.
 */
public class LimitNotificationAlertForcteAlertCreation implements AlertCreatorCandidate {

    int maxAmountOfSameAlertInDay =10; //Should be configurable

    private ConcurrentMap<Pair<String, Long>, AtomicInteger> counterPerType = new ConcurrentHashMap<>();

    public boolean canCreateAlert(String anomalyType, EvidenceType evidenceType,Map<String, String> evidence){
        long startOfDay = TimestampUtils.toStartOfDay(Long.parseLong(evidence.get("startOfDay")));

        Pair<String, Long> key = new ImmutablePair<>(anomalyType, startOfDay);
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
