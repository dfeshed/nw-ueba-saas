package fortscale.domain.events;

import com.fasterxml.jackson.annotation.JsonAutoDetect;
import org.springframework.data.mongodb.core.index.CompoundIndex;
import org.springframework.data.mongodb.core.index.CompoundIndexes;
import org.springframework.data.mongodb.core.mapping.Document;
import org.springframework.data.mongodb.core.mapping.Field;


@Document(collection=ComputerLoginEvent.collectionName)
@CompoundIndexes({
		@CompoundIndex(name="ipaddressTimeIdx", def = "{'ipaddress': 1, 'timestampepoch': -1}")
})
@JsonAutoDetect(fieldVisibility= JsonAutoDetect.Visibility.ANY, getterVisibility= JsonAutoDetect.Visibility.NONE, setterVisibility= JsonAutoDetect.Visibility.NONE)
public class ComputerLoginEvent extends IpToHostname{
	/**
	 *
	 */


	private static final long serialVersionUID = 8490137402047557595L;
	public static final String collectionName =  "ComputerLogin";
	public static final String PART_OF_VPN_FIELD = "partOfVpn";
	public static final String EXPIRATION_VPN_SESSION_TIME_FIELD ="expirationVpnSessiondt";

	public static final int COMPUTER_LOGIN_PRIORITY = 2;



	@Field(PART_OF_VPN_FIELD)
	private boolean partOfVpn;
	@Field(EXPIRATION_VPN_SESSION_TIME_FIELD)
	private long expirationVpnSessiondt;



	public ComputerLoginEvent() {
		super();
		setEventPriority(COMPUTER_LOGIN_PRIORITY);
	}

	public boolean isPartOfVpn() {
		return partOfVpn;
	}

	public void setPartOfVpn(boolean partOfVpn) {
		this.partOfVpn = partOfVpn;
	}

	public long getExpirationVpnSessiondt() {
		return expirationVpnSessiondt;
	}

	public void setExpirationVpnSessiondt(long expirationVpnSessiondt) {
		this.expirationVpnSessiondt = expirationVpnSessiondt;
	}
}
