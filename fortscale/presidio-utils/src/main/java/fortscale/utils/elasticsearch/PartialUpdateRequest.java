package fortscale.utils.elasticsearch;

import java.util.HashMap;
import java.util.Map;

public class PartialUpdateRequest {

    private final String documentId;

    private Map<String, Object> fields = new HashMap<>();

    public PartialUpdateRequest(String documentId){
        this.documentId = documentId;
    }

    public PartialUpdateRequest withField(String fieldName, Object fieldValue) {
        fields.put(fieldName, fieldValue);
        return this;
    }

    public String getDocumentId() {
        return documentId;
    }

    public Map<String, Object> getFields() {
        return fields;
    }


}
