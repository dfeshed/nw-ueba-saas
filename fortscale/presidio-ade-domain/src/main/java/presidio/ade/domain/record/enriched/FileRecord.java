package presidio.ade.domain.record.enriched;

import org.springframework.data.annotation.Transient;

public interface FileRecord {
    public static final String FILE_STR = "file";

    @Transient
    default String getEventType() {
        return FILE_STR;
    }
}
