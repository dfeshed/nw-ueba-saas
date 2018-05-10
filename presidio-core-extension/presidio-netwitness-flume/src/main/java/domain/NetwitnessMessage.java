package domain;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonProperty;
import fortscale.domain.core.AbstractAuditableDocument;

import java.util.Map;

public class NetwitnessMessage extends AbstractAuditableDocument {

    public static final String META_FIELDS = "META_FIELDS";

    public NetwitnessMessage() {
        super();
    }

    @JsonIgnore
    protected Map<String, Object> metaFields;


    @JsonProperty
    public Map<String, Object> getMetaFields() {
        return metaFields;
    }


    public void setMetaFields(Map<String, Object> metaFields) {
        this.metaFields = metaFields;
    }

    @Override
    public String toString() {
        return "NetwitnessMessage{" +
                "metaFields=" + metaFields +
                '}';
    }
}
