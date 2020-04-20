package presidio.output.forwarder;

import org.apache.commons.lang3.builder.ReflectionToStringBuilder;

import java.util.Map;

public class ForwardMassage {

    String id;
    String payload;
    Map<String, String> header;

    public ForwardMassage(String id, String payload) {
        this.id = id;
        this.payload = payload;
    }

    public ForwardMassage(String id, String payload, Map<String, String> header) {
        this(id, payload);
        this.header = header;
    }

    public String getId() {
        return id;
    }

    public String getPayload() {
        return payload;
    }

    public Map<String, String> getHeader() {
        return header;
    }

    @Override
    public String toString() {
        return ReflectionToStringBuilder.toString(this);
    }
}
