package fortscale.streaming.model;

import static com.google.common.base.Preconditions.checkNotNull;

import com.fasterxml.jackson.annotation.JsonAutoDetect;
import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;
import java.util.HashMap;
import java.util.Map;

/**
 * Task state the hold a runtime barrier and event discriminator encountered for each user
 */
@JsonAutoDetect(fieldVisibility= JsonAutoDetect.Visibility.ANY, getterVisibility= JsonAutoDetect.Visibility.NONE, setterVisibility= JsonAutoDetect.Visibility.NONE)
public class UserTimeBarrierModel {

    private long timestamp;
    private String discriminator;

    public long getTimestamp() {
        return timestamp;
    }

    public void setTimestamp(long timestamp) {
        this.timestamp = timestamp;
    }

    public String getDiscriminator() {
        return discriminator==null? "" : discriminator;
    }

    public void setDiscriminator(String discriminator) {
        this.discriminator = discriminator;
    }

}
