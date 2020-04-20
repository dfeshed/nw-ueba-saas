package presidio.output.domain.records.entity;

import java.util.Arrays;
import java.util.List;

public enum EntitySeverity {
    LOW, MEDIUM, HIGH, CRITICAL;
    private static final List<EntitySeverity> severitiesOrderedAsc = Arrays.asList(EntitySeverity.values());
    public static List<EntitySeverity> getSeveritiesOrderedAsc() {
        return severitiesOrderedAsc;
    }
}
