package presidio.ade.domain.record.enriched.file;

import org.springframework.data.annotation.Transient;

/**
 * Created by YaronDL on 8/2/2017.
 */
public interface AdeFileRecord {
    public static final String FILE_STR = "file";

    @Transient
    default String getAdeEventType() {
        return FILE_STR;
    }
}
