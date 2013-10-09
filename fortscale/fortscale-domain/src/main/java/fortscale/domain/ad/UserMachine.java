package fortscale.domain.ad;

import java.util.Date;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonPropertyOrder;

@JsonPropertyOrder({UserMachine.USERNAME_FIELD_NAME, UserMachine.HOSTNAME_FIELD_NAME, UserMachine.LOGONCOUNT_FIELD_NAME, UserMachine.LASTLOGON_FIELD_NAME, UserMachine.HOSTNAMEIP_FIELD_NAME})
public class UserMachine {
	public static final Object jsonOrder[] = {UserMachine.USERNAME_FIELD_NAME, UserMachine.HOSTNAME_FIELD_NAME, UserMachine.LOGONCOUNT_FIELD_NAME, UserMachine.LASTLOGON_FIELD_NAME, UserMachine.HOSTNAMEIP_FIELD_NAME};
	public static final String implaValueTypeOrder = String.format("%s string, %s string, %s bigint, %s timestamp, %s string", jsonOrder);
	public static final String TABLE_NAME = "usernametohostname";
	public static final String USERNAME_FIELD_NAME = "username";
	public static final String HOSTNAME_FIELD_NAME = "hostname";
	public static final String LOGONCOUNT_FIELD_NAME = "logoncount";
	public static final String LASTLOGON_FIELD_NAME = "lastlogon";
	public static final String HOSTNAMEIP_FIELD_NAME = "hostnameip";
	public static final String DATE_FORMAT = "yyyy-MM-dd HH:mm:ss" ;
	
	
	private String username;
	private String hostname;
	private int logoncount;
	private Date lastlogon;
	private String hostnameip;
	
	
	
	public String getUsername() {
		return username;
	}
	public void setUsername(String username) {
		this.username = username;
	}
	public String getHostname() {
		return hostname;
	}
	public void setHostname(String hostname) {
		this.hostname = hostname;
	}
	
	public int getLogoncount() {
		return logoncount;
	}
	public void setLogoncount(int logoncount) {
		this.logoncount = logoncount;
	}
	@JsonFormat(shape=JsonFormat.Shape.STRING, pattern=DATE_FORMAT)
	public Date getLastlogon() {
		return lastlogon;
	}
	public void setLastlogon(Date lastlogon) {
		this.lastlogon = lastlogon;
	}
	public String getHostnameip() {
		return hostnameip;
	}
	public void setHostnameip(String hostnameip) {
		this.hostnameip = hostnameip;
	}
	
	
	
	
}
