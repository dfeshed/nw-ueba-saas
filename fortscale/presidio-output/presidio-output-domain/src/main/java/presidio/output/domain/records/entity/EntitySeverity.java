package presidio.output.domain.records.entity;

import org.apache.commons.collections.CollectionUtils;

import java.util.LinkedList;
import java.util.List;

public enum EntitySeverity {
    CRITICAL, HIGH, MEDIUM, LOW;

    private static List<EntitySeverity> severitiesOrderedAsc;

    public static List<EntitySeverity> getSeveritiesOrderedAsc() {
        if (CollectionUtils.isEmpty(severitiesOrderedAsc)) {
            severitiesOrderedAsc = new LinkedList<>();
            severitiesOrderedAsc.add(LOW);
            severitiesOrderedAsc.add(MEDIUM);
            severitiesOrderedAsc.add(HIGH);
            severitiesOrderedAsc.add(CRITICAL);
        }
        return severitiesOrderedAsc;
    }
}
