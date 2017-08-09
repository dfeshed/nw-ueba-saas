package presidio.manager.api.records;


import com.fasterxml.jackson.databind.JsonNode;

import java.time.Instant;
import java.util.Iterator;

public class DataPipeLineConfiguration {

    private Instant startTime;

    private SchemasEnum[] schemasEnum;

    private DataPipeLineConfiguration() {
        this.startTime = null;
        this.schemasEnum = null;
    }

    public void setStartTime(Instant startTime) {
        this.startTime = startTime;
    }

    public void setSchemasEnum(SchemasEnum[] schemasEnum) {
        this.schemasEnum = schemasEnum;
    }

    private void setParameters(JsonNode node) {
        Iterator<String> itr = node.fieldNames();
        String key;
        while (itr.hasNext()) {
            key = itr.next().toString();
            setKeyValue(key, node.get(key));
        }
    }

    public static DataPipeLineConfiguration dataPipeLineConfigurationFactory(JsonNode node) {
        DataPipeLineConfiguration dataPipeLineConfiguration = new DataPipeLineConfiguration();
        dataPipeLineConfiguration.setParameters(node);
        return dataPipeLineConfiguration;
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
