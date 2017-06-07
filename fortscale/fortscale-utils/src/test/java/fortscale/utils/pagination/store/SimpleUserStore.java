package fortscale.utils.pagination.store;

import fortscale.utils.pagination.events.SimpleUserEvent;

import java.time.Instant;
import java.util.*;
import java.util.stream.Collectors;

public class SimpleUserStore {

    private Map<String, List<SimpleUserEvent>> simpleUserEventsMap;

    public SimpleUserStore(Map<String, List<SimpleUserEvent>> simpleUserEventsMap) {
        this.simpleUserEventsMap = simpleUserEventsMap;
    }

    /**
     * Add default values to map.
     * The map contains contextId and SimpleUserEvent list.
     * a - 1 event
     * b - 1 event
     * c - 2 events
     * d - 3 events
     * e - 4 events
     * f - 10 events
     * g - 20 events
     * h - 40 events
     * @param map - contextId, SimpleUserEvent list
     */
    public static void addDefaultValues(Map<String, List<SimpleUserEvent>> map) {
        Instant now = Instant.now();
        SimpleUserEvent event_a = new SimpleUserEvent("a", now, "dlp_mail");
        SimpleUserEvent event_b = new SimpleUserEvent("b", now, "dlp_mail");
        SimpleUserEvent event_c = new SimpleUserEvent("c", now, "dlp_mail");
        SimpleUserEvent event_d = new SimpleUserEvent("d", now, "dlp_mail");
        SimpleUserEvent event_e = new SimpleUserEvent("e", now, "dlp_mail");
        SimpleUserEvent event_f = new SimpleUserEvent("f", now, "dlp_mail");
        SimpleUserEvent event_g = new SimpleUserEvent("g", now, "dlp_mail");
        SimpleUserEvent event_h = new SimpleUserEvent("h", now, "dlp_mail");

        setValueMultipleTimes(event_h, 40, map);
        setValueMultipleTimes(event_d, 3, map);
        setValueMultipleTimes(event_a, 1, map);
        setValueMultipleTimes(event_b, 1, map);
        setValueMultipleTimes(event_c, 2, map);
        setValueMultipleTimes(event_e, 4, map);
        setValueMultipleTimes(event_f, 10, map);
        setValueMultipleTimes(event_g, 20, map);
    }

    /**
     * Add event to map multiple times.
     * @param event
     * @param amount
     * @param map
     */
    private static void setValueMultipleTimes(SimpleUserEvent event, int amount, Map<String, List<SimpleUserEvent>> map) {
        List<SimpleUserEvent> set = new ArrayList<>();
        for (int i = 0; i < amount; i++) {
            set.add(event);
        }
        map.put(event.getName(), set);
    }

    public Map<String, List<SimpleUserEvent>> getSimpleUserEventsMap() {
        return this.simpleUserEventsMap;
    }

    /**
     * Filter the contextIdToSimpleUserEventsMap and return list of SimpleUserEvent, who contain contextIds.
     * @param contextIds Set<String>
     * @return List<SimpleUserEvent>
     */
    public List<SimpleUserEvent> getListOfSimpleUserEvents(Set<String> contextIds) {
        Map<String, List<SimpleUserEvent>> contextIdToSimpleUserEventsMap = this.simpleUserEventsMap.entrySet().stream().filter(map -> contextIds.contains(map.getKey())).collect(Collectors.toMap(p -> p.getKey(), p -> p.getValue()));
        List<SimpleUserEvent> list = contextIdToSimpleUserEventsMap.values().stream().collect(ArrayList::new, List::addAll, List::addAll);
        return list;
    }


}
