package com.rsa.netwitness.presidio.automation.common.helpers;

import fortscale.utils.data.Pair;
import presidio.data.domain.event.Event;

import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.List;

/**
 * Created by presidio on 8/21/17.
 */
public class DateTimeHelperUtils {

    public static Instant getFirstEventTime(List<? extends Event> events) {
        if (events == null || events.size() == 0) return Instant.now();

        Instant time = events.get(0).getDateTime();

        for (Event e : events) {
            if (time.isAfter(e.getDateTime())) time = e.getDateTime();
        }
        return time.truncatedTo(ChronoUnit.DAYS);
    }

    public static Instant getLastEventTime(List<? extends Event> events) {
        if (events == null || events.size() == 0) return Instant.now();

        Instant time = events.get(0).getDateTime();

        for (Event e : events) {
            if (time.isBefore(e.getDateTime())) time = e.getDateTime();
        }
        return time;
    }

    public static Instant getDate(int daysBack){
        Instant date = Instant.now().truncatedTo(ChronoUnit.DAYS).minus(daysBack, ChronoUnit.DAYS);
        return date;
    }

    public static Instant getMinTime(Instant[] instants) {
        Instant minTime = Instant.now();
        for (int i=0; i<instants.length; i++) {
            if (instants[i].isBefore(minTime)) minTime = instants[i];
        }
        return minTime;
    }
    public static Pair<Integer,Integer> getEventsInTimeRangeIdx(List<? extends Event> events, Instant start, Instant end) {

        int startIndex = -1, endIndex = events.size()-1;
        for (int i = 0; i < events.size(); i++) {
            if (events.get(i).getDateTime().equals(start) ||
                    events.get(i).getDateTime().isAfter(start)) {
                startIndex = i;
                break;
            }
        }
        if (startIndex >= 0){
            for (int i = startIndex; i < events.size(); i++) {
                if (events.get(i).getDateTime().equals(end) ||
                        events.get(i).getDateTime().isAfter(end)) {
                    endIndex = i;
                    break;
                }
            }
        }
        return new Pair<>(startIndex, endIndex);
    }
}
