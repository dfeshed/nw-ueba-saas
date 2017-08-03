package presidio.ade.domain.record.enriched.dlpfile;

import org.springframework.data.annotation.Transient;

/**
 * Created by YaronDL on 6/14/2017.
 */
public interface AdeDlpFileRecord {
    public static final String DLP_FILE_STR = "dlpfile";

    @Transient
    default String getAdeEventType(){ return DLP_FILE_STR;}
}
