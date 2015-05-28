package fortscale.domain.events;

import com.fasterxml.jackson.annotation.JsonAutoDetect;
import fortscale.utils.TimestampUtils;
import org.apache.commons.lang3.builder.EqualsBuilder;
import org.apache.commons.lang3.builder.HashCodeBuilder;
import org.apache.commons.lang3.builder.ToStringBuilder;
import org.springframework.data.mongodb.core.index.CompoundIndex;
import org.springframework.data.mongodb.core.index.CompoundIndexes;
import org.springframework.data.mongodb.core.mapping.Document;
import org.springframework.data.mongodb.core.mapping.Field;

import java.util.Date;

@Document(collection = IseEvent.collectionName)
@CompoundIndexes({
        @CompoundIndex(name = "ipaddressTimeIdx", def = "{'ipaddress': 1, 'timestampepoch': -1}")
})
@JsonAutoDetect(fieldVisibility = JsonAutoDetect.Visibility.ANY, getterVisibility = JsonAutoDetect.Visibility.NONE, setterVisibility = JsonAutoDetect.Visibility.NONE)
public class IseEvent extends IpToHostname {

    // collection properties
    public static final String collectionName = "IseEvent";

    public static final String EXPIRATION_FIELD_NAME = "expiration";
    public static final String MAC_ADDRESS_FIELD_NAME = "macAddress";
    //public static final String LOCATION_FIELD_NAME = "location";

    @Field(EXPIRATION_FIELD_NAME)
    private long expiration;


    @Field(MAC_ADDRESS_FIELD_NAME)
    private String macAddress;

    @Field(IS_AD_HOSTNAME_FIELD_NAME)
    protected Boolean adHostName;

    @Override
    public boolean isAdHostname() {
        return (adHostName==null)? false : adHostName;
    }

    public void setAdHostName(boolean adHostName) {
        this.adHostName = adHostName;
    }


    public long getExpiration() {
        return expiration;
    }

    public void setExpiration(long expiration) {
        this.expiration = TimestampUtils.convertToMilliSeconds(expiration);
    }

    public String getMacAddress() {
        return macAddress;
    }

    public void setMacAddress(String macAddress) {
        this.macAddress = macAddress;
    }
    
    @Override
    public boolean equals(Object obj) {
        if (obj == null) return false;
        if (obj == this) return true;
        if (obj.getClass() != getClass()) return false;

        IseEvent other = (IseEvent) obj;
        return new EqualsBuilder()
                .append(expiration, other.expiration)
                .append(macAddress, other.macAddress)
                .append(timestampepoch, other.timestampepoch)
                .isEquals();
    }

    @Override
    public int hashCode() {
        return new HashCodeBuilder(17, 31). // two randomly chosen prime numbers
                append(expiration).
                append(hostname).
                append(ipaddress).
                append(macAddress).
                append(timestampepoch).
                append(adHostName).
                toHashCode();
    }


    @Override
    public String toString() {
        return new ToStringBuilder(this)
                .append("expiration", expiration)
                .append("hostname", hostname)
                .append("ip", ipaddress)
                .append("mac", macAddress)
                .append("timestamp", timestampepoch)
                .append("isADHost", adHostName)
                .build();
    }


}
