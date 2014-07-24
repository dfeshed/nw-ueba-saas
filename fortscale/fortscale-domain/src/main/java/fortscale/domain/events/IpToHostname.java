package fortscale.domain.events;

import org.joda.time.DateTime;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.mongodb.core.index.Indexed;
import org.springframework.data.mongodb.core.mapping.Field;

import fortscale.domain.core.AbstractDocument;
import fortscale.utils.TimestampUtils;

public class IpToHostname extends AbstractDocument{

	/**
	 * 
	 */
	private static final long serialVersionUID = 4135830670048978765L;
	public static final String CREATED_AT_FIELD_NAME = "createdAt";
	public static final String TIMESTAMP_EPOCH_FIELD_NAME = "timestampepoch";
	public static final String IP_ADDRESS_FIELD_NAME = "ipaddress";
	public static final String HOSTNAME_FIELD_NAME = "hostname";
	
	@Indexed(unique = false, expireAfterSeconds=60*60*24*14)
	@CreatedDate
	@Field(CREATED_AT_FIELD_NAME)
	private DateTime createdAt;
	
	@Field(TIMESTAMP_EPOCH_FIELD_NAME)
	private Long timestampepoch;
	
	@Field(IP_ADDRESS_FIELD_NAME)
	private String ipaddress;
	
	@Field(HOSTNAME_FIELD_NAME)
	private String hostname;
	
	
	

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
}
