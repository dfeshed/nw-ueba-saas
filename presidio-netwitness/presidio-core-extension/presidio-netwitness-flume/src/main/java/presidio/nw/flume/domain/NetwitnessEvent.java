package presidio.nw.flume.domain;

import com.fasterxml.jackson.annotation.JsonAnyGetter;
import com.fasterxml.jackson.annotation.JsonAnySetter;
import fortscale.domain.core.AbstractDocument;

import java.util.HashMap;
import java.util.Map;

public class NetwitnessEvent extends AbstractDocument {

    private Map<String, Object> metaFields;

    public NetwitnessEvent() {
        super();
        metaFields = new HashMap<String, Object>();
    }

    @JsonAnyGetter
    public Map<String, Object> getMetaFields() {
        return metaFields;
    }

    @JsonAnySetter
    public void setMetaField(String name, Object value) {
        metaFields.put(name, value);
    }


    @Override
    public String toString() {
        return "NetwitnessEvent {" + metaFields + '}';
    }
}
