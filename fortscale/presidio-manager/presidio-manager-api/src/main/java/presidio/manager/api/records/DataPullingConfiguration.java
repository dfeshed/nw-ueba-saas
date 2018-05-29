package presidio.manager.api.records;


import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.databind.JsonNode;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

public class DataPullingConfiguration {

    @JsonProperty("source")
    private String source;

    private List<String> unknownFields;
    private boolean isStructureValid;

    private final String SOURCE = "source";

    public DataPullingConfiguration() {
    }


    public DataPullingConfiguration(JsonNode node) {
        this.unknownFields = new ArrayList();

        Iterator<String> itr = node.fieldNames();
        String key;
        while (itr.hasNext()) {
            key = itr.next();
            setKeyValue(key, node.get(key).asText());
        }

        if(node.size() == 0) {
            isStructureValid = false;
            return;
        }

        if (unknownFields.isEmpty())
            isStructureValid = true;

    }

    public String getSource() {
        return source;
    }

    public void setSource(String source) {
        this.source = source;
    }


    private void setKeyValue(String key, String value) {
        switch (key) {
            case SOURCE:
                setSource(value);
                break;
            default:
                unknownFields.add(key);
        }
    }

    public List<String> getUnknownFields() {
        return unknownFields;
    }

    public List<String> getEmptyFields() {
        List<String> emptyFields = new ArrayList<>();
        if (source == null || source.isEmpty()) {
            emptyFields.add(SOURCE);
        }

        return emptyFields;
    }

    public boolean isStructureValid() {
        return isStructureValid;
    }

}
