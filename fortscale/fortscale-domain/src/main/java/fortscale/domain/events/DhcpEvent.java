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
	/**
	 * 
	 */
	private static final long serialVersionUID = -8351203425124420491L;


	public static final String collectionName =  "DhcpEvent";
	
	
	public static final String MAC_ADDRESS_FIELD_NAME = "macAddress";
	
	
	
	@Field(MAC_ADDRESS_FIELD_NAME)
	private String macAddress;
	
	
	

	public String getMacAddress() {
		return macAddress;
	}

	public void setMacAddress(String macAddress) {
		this.macAddress = macAddress;
	}
	
}
