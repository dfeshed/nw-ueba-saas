package fortscale.streaming.model.tagging;

import com.fasterxml.jackson.annotation.JsonAutoDetect;
import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;
import fortscale.domain.core.ComputerUsageType;
import org.apache.commons.lang.builder.EqualsBuilder;
import org.apache.commons.lang.builder.HashCodeBuilder;

/**
 * Created by idanp on 7/7/2014.
 * This class will represent a machine state (for example if some event take action on machine X this class will represent the state of Machine X at the same time)
 */


@JsonAutoDetect(fieldVisibility= JsonAutoDetect.Visibility.ANY, getterVisibility= JsonAutoDetect.Visibility.NONE, setterVisibility= JsonAutoDetect.Visibility.NONE)
public class MachineState {

    private String hostName;
    private ComputerUsageType type;
    private long lastEventTimeStamp;



    @JsonCreator
    public MachineState(@JsonProperty("hostName") String hostName) {
        this.hostName = hostName;

    }

    public String getHostName() {
        return hostName;
    }

    public void setHostName(String hostName) {
        this.hostName = hostName;
    }

    public ComputerUsageType getType() {
        return type;
    }

    public void setType(ComputerUsageType type) {
        this.type = type;
    }

    public long getLastEventTimeStamp() {
        return lastEventTimeStamp;
    }

    public void setLastEventTimeStamp(long lastEventTimeStamp) {
        this.lastEventTimeStamp = lastEventTimeStamp;
    }


    //override to hashCode due to the equal overriding
    // ( equal objects must have the same hashCode, we change the equal logic there for we need to change the hashCode logic)
    public int hashCode() {
        return new HashCodeBuilder(17, 31). // two randomly chosen prime numbers
                // if deriving: appendSuper(super.hashCode()).
                append(hostName).
                append(type).
                toHashCode();
    }

    public boolean equals(Object obj) {
        if (!(obj instanceof MachineState))
            return false;
        if (obj == this)
            return true;

        MachineState rhs = (MachineState) obj;
        return new EqualsBuilder().
                   append(hostName, rhs.hostName).
                   append(type, rhs.type).
                   isEquals();
    }



}
