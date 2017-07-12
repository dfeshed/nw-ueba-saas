package presidio.ade.domain.record.enriched;

import org.springframework.data.annotation.Transient;

public interface ActiveDirectoryRecord {
    public static final String ACTIVE_DIRECTORY_STR = "activeDirectory";

    @Transient
    default String getEventType() {
        return ACTIVE_DIRECTORY_STR;
    }
}
