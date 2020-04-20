package presidio.nw.flume.domain.test;

import com.fasterxml.jackson.annotation.JsonAnyGetter;
import com.fasterxml.jackson.annotation.JsonAnySetter;
import fortscale.domain.core.AbstractDocument;
import org.apache.commons.lang.builder.ToStringBuilder;

import java.util.Map;

public class NetwitnessStoredData extends AbstractDocument {

    Map<String, Object> netwitnessEvent;

    public NetwitnessStoredData() {
    }

    public NetwitnessStoredData(Map<String, Object> netwitnessEvent){
        this.netwitnessEvent = netwitnessEvent;
    }

    @JsonAnyGetter
    public Map<String, Object> getNetwitnessEvent() {
        return netwitnessEvent;
    }

    public void setNetwitnessEvent(Map<String, Object> netwitnessEvent) {
        this.netwitnessEvent = netwitnessEvent;
    }

    @JsonAnySetter
    public void setNetwitnessEventField(String name, Object value) {
        netwitnessEvent.put(name, value);
    }


    @Override
    public String toString() {
        return ToStringBuilder.reflectionToString(this);
    }
}