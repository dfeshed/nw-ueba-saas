package presidio.output.domain.records.users;

import org.apache.commons.collections.CollectionUtils;

import java.util.LinkedList;
import java.util.List;

/**
 * Created by shays on 27/08/2017.
 */
public enum UserSeverity {
    CRITICAL, HIGH, MEDIUM, LOW;

    private static List<UserSeverity> severitiesOrderedAsc;

    public static List<UserSeverity> getSeveritiesOrderedAsc() {
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
