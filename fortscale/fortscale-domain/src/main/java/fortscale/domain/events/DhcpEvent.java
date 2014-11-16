package fortscale.domain.events;

import org.springframework.data.mongodb.core.index.CompoundIndex;
import org.springframework.data.mongodb.core.index.CompoundIndexes;
import org.springframework.data.mongodb.core.mapping.Document;
import org.springframework.data.mongodb.core.mapping.Field;



@Document(collection=DhcpEvent.collectionName)
@CompoundIndexes({
	@CompoundIndex(name="ipaddressTimeIdx", def = "{'ipaddress': 1, 'timestampepoch': -1}"),
	@CompoundIndex(name="hostnameTimeIdx", def = "{'hostname': 1, 'timestampepoch': -1}"),
})
public class DhcpEvent extends IpToHostname{

	private static final long serialVersionUID = -8351203425124420491L;

	// action codes
	public static final String RELEASE_ACTION = "RELEASE";
	public static final String EXPIRED_ACTION = "EXPIRED";
	public static final String ASSIGN_ACTION = "ASSIGN"; 
	

	public static final String collectionName =  "DhcpEvent";
	public static final String MAC_ADDRESS_FIELD_NAME = "macAddress";
	public static final String EXPIRATION_FIELD_NAME = "expiration";
	public static final String ACTION_FIELD_NAME = "action";
	public static final String IS_AD_HOSTNAME_FIELD_NAME = "isADHostName";
	
	
	@Field(IS_AD_HOSTNAME_FIELD_NAME)
	private Boolean adHostName;
	
	@Field(MAC_ADDRESS_FIELD_NAME)
	private String macAddress;
	
	@Field(EXPIRATION_FIELD_NAME)
	private long expiration;
	
	@Field(ACTION_FIELD_NAME)
	private String action;
	
	
	public Boolean isADHostName() {
		return adHostName;
	}
	
	public void setADHostName(Boolean adHostName) {
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
		this.expiration = expiration;
	}

	public String getMacAddress() {
		return macAddress;
	}

	public void setMacAddress(String macAddress) {
		this.macAddress = macAddress;
	}
	
}
