package fortscale.domain.events;

import com.fasterxml.jackson.annotation.JsonAutoDetect;
import fortscale.utils.time.TimestampUtils;
import org.apache.commons.lang3.builder.EqualsBuilder;
import org.apache.commons.lang3.builder.HashCodeBuilder;
import org.apache.commons.lang3.builder.ToStringBuilder;
import org.springframework.data.mongodb.core.index.CompoundIndex;
import org.springframework.data.mongodb.core.index.CompoundIndexes;
import org.springframework.data.mongodb.core.mapping.Document;
import org.springframework.data.mongodb.core.mapping.Field;

@Document(collection = PxGridIPEvent.collectionName)
@CompoundIndexes({ @CompoundIndex(name = "ipaddressTimeIdx", def = "{'ipaddress': 1, 'timestampepoch': -1}") })
@JsonAutoDetect(fieldVisibility = JsonAutoDetect.Visibility.ANY, getterVisibility = JsonAutoDetect.Visibility.NONE, setterVisibility = JsonAutoDetect.Visibility.NONE)
public class PxGridIPEvent extends IpToHostname {

	public static final int PXGRID_PRIORITY = 1;

	// collection properties
	public static final String collectionName = "PxGridEvent";

	public static final String IS_AD_HOSTNAME_FIELD_NAME = "isADHostName";
	public static final String EXPIRATION_FIELD_NAME = "expiration";

	//public static final String

	@Field(EXPIRATION_FIELD_NAME) private long expiration;

	@Field(IS_AD_HOSTNAME_FIELD_NAME) protected Boolean adHostName;

	public PxGridIPEvent() {
		super();
		setEventPriority(PXGRID_PRIORITY);
	}

	@Override public boolean checkIsAdHostname() {
		return (adHostName == null) ? false : adHostName;
	}

	public Boolean getAdHostName() {
		return adHostName;
	}

	public void setAdHostName(Boolean adHostName) {
		this.adHostName = adHostName;
	}

	@Override public long getExpiration() {
		return expiration;
	}

	public void setExpiration(long expiration) {
		this.expiration = TimestampUtils.convertToMilliSeconds(expiration);
	}

	@Override public boolean equals(Object obj) {
		if (obj == null)
			return false;
		if (obj == this)
			return true;
		if (obj.getClass() != getClass())
			return false;

		PxGridIPEvent other = (PxGridIPEvent) obj;
		return new EqualsBuilder().append(expiration, other.expiration).append(timestampepoch, other.timestampepoch).isEquals();
	}

	@Override public int hashCode() {
		return new HashCodeBuilder(17, 31). // two randomly chosen prime numbers
				append(expiration).
				append(hostname).
				append(ipaddress).
				append(timestampepoch).
				append(adHostName).
				toHashCode();
	}

	@Override public String toString() {
		return new ToStringBuilder(this).
				append("expiration", expiration).
				append("hostname", hostname).
				append("ip", ipaddress).
				append("timestamp", timestampepoch).
				append("isADHost", adHostName).
				build();
	}
}