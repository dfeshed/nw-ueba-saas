package presidio.ade.domain.record.enriched.activedirectory;

import org.springframework.data.annotation.Transient;

public interface AdeActiveDirectoryRecord {
    public static final String ACTIVE_DIRECTORY_STR = "active_directory";

    @Transient
    default String getAdeEventType() {
        return ACTIVE_DIRECTORY_STR;
    }
}
