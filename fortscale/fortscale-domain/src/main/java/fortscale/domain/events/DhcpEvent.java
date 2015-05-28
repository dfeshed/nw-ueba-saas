package fortscale.domain.events;

import com.fasterxml.jackson.annotation.JsonAutoDetect;
import fortscale.utils.TimestampUtils;
import org.apache.commons.lang3.builder.EqualsBuilder;
import org.apache.commons.lang3.builder.ToStringBuilder;
import org.springframework.data.mongodb.core.index.CompoundIndex;
import org.springframework.data.mongodb.core.index.CompoundIndexes;
import org.springframework.data.mongodb.core.mapping.Document;
import org.springframework.data.mongodb.core.mapping.Field;


@Document(collection=DhcpEvent.collectionName)
@CompoundIndexes({
	@CompoundIndex(name="ipaddressTimeIdx", def = "{'ipaddress': 1, 'timestampepoch': -1}")
})
@JsonAutoDetect(fieldVisibility= JsonAutoDetect.Visibility.ANY, getterVisibility= JsonAutoDetect.Visibility.NONE, setterVisibility= JsonAutoDetect.Visibility.NONE)
public class DhcpEvent extends IpToHostname{

	private static final long serialVersionUID = -8351203425124420491L;

	public static final String IS_AD_HOSTNAME_FIELD_NAME = "isADHostName";

	// action codes
	public static final String RELEASE_ACTION = "RELEASE";
	public static final String EXPIRED_ACTION = "EXPIRED";
	public static final String ASSIGN_ACTION = "ASSIGN"; 
	
	// collection properties
	public static final String collectionName =  "DhcpEvent";
	public static final String MAC_ADDRESS_FIELD_NAME = "macAddress";
	public static final String EXPIRATION_FIELD_NAME = "expiration";
	public static final String ACTION_FIELD_NAME = "action";
	public static final String IS_AD_HOSTNAME_FIELD_NAME = "isADHostName";

	@Field(IS_AD_HOSTNAME_FIELD_NAME)
	private Boolean adHostName;


	public boolean isAdHostName() {
		return (adHostName==null)? false : adHostName;
	}

	public void setAdHostName(boolean adHostName) {
		this.adHostName = adHostName;
	}


	@Field(MAC_ADDRESS_FIELD_NAME)
	private String macAddress;
	
	@Field(EXPIRATION_FIELD_NAME)
	private long expiration;
	
	@Field(ACTION_FIELD_NAME)
	private String action;

	@Field(IS_AD_HOSTNAME_FIELD_NAME)
	protected Boolean adHostName;

	/*
	public boolean isAdHostName() {
		return (adHostName==null)?  false : adHostName;
	}*/

	@Override
	public boolean checkIsAdHostname()
	{
		return (adHostName==null)?  false : adHostName;
	}

	public void setAdHostName(boolean adHostName) {
		this.adHostName = adHostName;
	}


	public String getAction() {
		return action;
	}
	
	public void setAction(String action) {
		this.action = action;
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
		
		DhcpEvent other = (DhcpEvent)obj;
		return new EqualsBuilder()
				.append(expiration, other.expiration)
				.append(timestampepoch, other.timestampepoch)
				.append(ipaddress, other.ipaddress)
				.append(hostname, other.hostname)
				.isEquals();
	}
	
	@Override
	public String toString() {
		return new ToStringBuilder(this)
				.append("ip", ipaddress)
				.append("hostname", hostname)
				.append("mac", macAddress)
				.append("timestamp", timestampepoch)
				.append("expiration", expiration)
				.append("action", action)
				.append("isADHost", adHostName)
				.build();
	}
	
}
