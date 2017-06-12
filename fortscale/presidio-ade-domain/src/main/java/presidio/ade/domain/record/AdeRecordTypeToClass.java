package presidio.ade.domain.record;

import org.springframework.context.annotation.Bean;
import presidio.ade.domain.record.enriched.EnrichedDlpFileRecord;

import java.util.HashMap;
import java.util.Map;


public class AdeRecordTypeToClass {

    private static Map<String, Class> dataSourceToPojoClass;

    public AdeRecordTypeToClass() {
        dataSourceToPojoClass = new HashMap<>();
        addItemsToMap();
    }

    public void addItemsToMap() {
        dataSourceToPojoClass.put("dlp_file", EnrichedDlpFileRecord.class);
    }

    public static Class getPojoClass(String dataSource) {
        return dataSourceToPojoClass.get(dataSource);
    }

}
