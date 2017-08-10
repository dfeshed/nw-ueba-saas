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
            setKeyValue(key, node.get(key).asText());
        }
    }

    public static DataPipeLineConfiguration dataPipeLineConfigurationFactory(JsonNode node) {
        DataPipeLineConfiguration dataPipeLineConfiguration = new DataPipeLineConfiguration();
        dataPipeLineConfiguration.setParameters(node);
        return dataPipeLineConfiguration;
    }

    private void setKeyValue(String key, String value) {
        switch (key) {
            case "startTime":
                setStartTime(Instant.parse(value));
                break;
            case "schemas":
                setSchemasEnum(SchemasEnum.fromValue((value.substring(1, value.length() - 1)).split(",")));
                break;
        }
    }

}
