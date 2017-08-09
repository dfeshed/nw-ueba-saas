package presidio.manager.api.records;


import com.fasterxml.jackson.databind.JsonNode;

import java.time.Instant;
import java.util.Iterator;

public class DataPipeLineConfiguration {

    private Instant startTime;

    private SchemasEnum[] schemasEnum;

    public DataPipeLineConfiguration() {
        this.startTime = null;
        this.schemasEnum = null;
    }

    public void setStartTime(Instant startTime) {
        this.startTime = startTime;
    }

    public void setSchemasEnum(SchemasEnum[] schemasEnum) {
        this.schemasEnum = schemasEnum;
    }

    public void setParameters(JsonNode node) {
        Iterator<String> itr = node.fieldNames();
        String key;
        while (itr.hasNext()) {
            key = itr.next().toString();
            setKeyValue(key, node.get(key));
        }
    }

    private void setKeyValue(String key, Object value) {
        switch (key) {
            case "startTime":
                setStartTime(Instant.parse((String) value));
                break;
            case "schemasEnum":
                setSchemasEnum(SchemasEnum.fromValue((String[]) value));
                break;
        }
    }

}
