package fortscale.domain.events;

import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import fortscale.domain.core.AbstractDocument;
import fortscale.utils.json.JodaDateSerializer;
import fortscale.utils.time.TimestampUtils;
import org.joda.time.DateTime;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.mongodb.core.mapping.Field;

public class IpToHostname extends AbstractDocument{

	/**
	 *
	 */
	private static final long serialVersionUID = 4135830670048978765L;
	public static final String CREATED_AT_FIELD_NAME = "createdAt";
	public static final String TIMESTAMP_EPOCH_FIELD_NAME = "timestampepoch";
	public static final String IP_ADDRESS_FIELD_NAME = "ipaddress";
	public static final String HOSTNAME_FIELD_NAME = "hostname";


	@CreatedDate
	@Field(CREATED_AT_FIELD_NAME)
	@JsonSerialize(using = JodaDateSerializer.class)
	protected DateTime createdAt;

	@Field(TIMESTAMP_EPOCH_FIELD_NAME)
	protected Long timestampepoch;

	@Field(IP_ADDRESS_FIELD_NAME)
	protected String ipaddress;

	@Field(HOSTNAME_FIELD_NAME)
	protected String hostname;

	protected int eventPriority;

	public DateTime getCreatedAt() {
		return createdAt;
	}

	public void setCreatedAt(DateTime createdAt) {
		this.createdAt = createdAt;
	}

	public Long getTimestampepoch() {
		return timestampepoch;
	}

	public void setTimestampepoch(Long timestampepoch) {
		this.timestampepoch = TimestampUtils.convertToMilliSeconds(timestampepoch);
	}

	public String getIpaddress() {
		return ipaddress;
	}

	public void setIpaddress(String ipaddress) {
		this.ipaddress = ipaddress;
	}

	public String getHostname() {
		return hostname;
	}

	public void setHostname(String hostname) {
		this.hostname = hostname;
	}

	public int getEventPriority() {
		return eventPriority;
	}

	public void setEventPriority(int eventPriority) {
		this.eventPriority = eventPriority;
	}

	//This method will be override by the extending class that will hold the adHostname field
	public boolean checkIsAdHostname(){return true;}

	public long getExpiration() {
		return 0;
	}
}