package presidio.ade.domain.record.enriched;

import org.springframework.data.annotation.Transient;

/**
 * Created by YaronDL on 6/14/2017.
 */
public interface DlpFileRecord {
    public static final String DLP_FILE_STR = "dlpfile";

    @Transient
    default String getDataSource(){ return DLP_FILE_STR;}
}
