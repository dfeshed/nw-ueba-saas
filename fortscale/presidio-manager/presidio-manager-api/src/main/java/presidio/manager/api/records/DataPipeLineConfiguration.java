package presidio.manager.api.records;


import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.node.ArrayNode;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

public class DataPipeLineConfiguration {

    private String startTime = null;

    private String[] schemas = null;

    private boolean isStracturValid = false;

    private List<String> badParams = null;

    private final String START_TIME = "startTime";
    private final String SCHEMAS = "schemas";


    public DataPipeLineConfiguration(JsonNode node) {
        this.badParams = new ArrayList<>();
        Iterator<String> itr = node.fieldNames();
        String key;
        while (itr.hasNext()) {
            key = itr.next().toString();
            setKeyValue(key, node.get(key));
        }
        if (badParams.isEmpty())
            isStracturValid = true;
    }

    public void setStartTime(String startTime) {
        this.startTime = startTime;
    }

    public void setSchemas(String[] schemas) {
        this.schemas = schemas;
    }

    private void setKeyValue(String key, JsonNode value) {
        switch (key) {
            case START_TIME:
                setStartTime(value.asText());
                break;
            case SCHEMAS:
                setSchemas(nodeToString((ArrayNode) value));
                break;
            default:
                badParams.add(key);
        }
    }

    private String[] nodeToString(ArrayNode jn) {
        String[] str = new String[jn.size()];
        for (int i = 0; i < jn.size(); i++) {
            str[i] = jn.get(i).asText();
        }
        return str;
    }

    public String getStartTime() {
        return startTime;
    }

    public String[] getSchemas() {
        return schemas;
    }

    public boolean isStracturValid() {
        return isStracturValid;
    }

    public List<String> getBadParams() {
        return badParams;
    }
}
