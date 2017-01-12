package fortscale.domain.ad;

import org.springframework.data.mongodb.core.mapping.Document;



@Document(collection=AdComputer.COLLECTION_NAME)
public class AdComputer extends AdObject{

	public static final String COLLECTION_NAME = "ad_computer";
	/**
	 * 
	 */
	private static final long serialVersionUID = -4210757717351979604L;
	private String operatingSystem;
	private String operatingSystemHotfix;
	private String operatingSystemServicePack;
	private String operatingSystemVersion;
	private String lastLogoff;
	private String lastLogon;
	private String lastLogonTimestamp;
	private String logonCount;
	private String whenChanged;
	private String whenCreated;
	private String cn;
	private String description;
	private String pwdLastSet;
	private String memberOf;
	private String ou;
	
	
	
	/**
	 * Creates a new {@link Customer} from the given distinguishedName.
	 * 
	 * @param distinguishedName must not be {@literal null} or empty.
	 */
//	@PersistenceConstructor
//	public AdComputer(String distinguishedName) {
//		super(distinguishedName);
//	}



	public String getOperatingSystem() {
		return operatingSystem;
	}



	public void setOperatingSystem(String operatingSystem) {
		this.operatingSystem = operatingSystem;
	}



	public String getOperatingSystemHotfix() {
		return operatingSystemHotfix;
	}



	public void setOperatingSystemHotfix(String operatingSystemHotfix) {
		this.operatingSystemHotfix = operatingSystemHotfix;
	}



	public String getOperatingSystemServicePack() {
		return operatingSystemServicePack;
	}



	public void setOperatingSystemServicePack(String operatingSystemServicePack) {
		this.operatingSystemServicePack = operatingSystemServicePack;
	}



	public String getOperatingSystemVersion() {
		return operatingSystemVersion;
	}



	public void setOperatingSystemVersion(String operatingSystemVersion) {
		this.operatingSystemVersion = operatingSystemVersion;
	}



	public String getLastLogoff() {
		return lastLogoff;
	}



	public void setLastLogoff(String lastLogoff) {
		this.lastLogoff = lastLogoff;
	}



	public String getLastLogon() {
		return lastLogon;
	}



	public void setLastLogon(String lastLogon) {
		this.lastLogon = lastLogon;
	}



	public String getLastLogonTimestamp() {
		return lastLogonTimestamp;
	}



	public void setLastLogonTimestamp(String lastLogonTimestamp) {
		this.lastLogonTimestamp = lastLogonTimestamp;
	}



	public String getLogonCount() {
		return logonCount;
	}



	public void setLogonCount(String logonCount) {
		this.logonCount = logonCount;
	}



	public String getWhenChanged() {
		return whenChanged;
	}



	public void setWhenChanged(String whenChanged) {
		this.whenChanged = whenChanged;
	}



	public String getWhenCreated() {
		return whenCreated;
	}



	public void setWhenCreated(String whenCreated) {
		this.whenCreated = whenCreated;
	}



	public String getCn() {
		return cn;
	}



	public void setCn(String cn) {
		this.cn = cn;
	}



	public String getDescription() {
		return description;
	}



	public void setDescription(String description) {
		this.description = description;
	}



	public String getPwdLastSet() {
		return pwdLastSet;
	}



	public void setPwdLastSet(String pwdLastSet) {
		this.pwdLastSet = pwdLastSet;
	}



	public String getMemberOf() {
		return memberOf;
	}



	public void setMemberOf(String memberOf) {
		this.memberOf = memberOf;
	}



	public String getOu() {
		return ou;
	}



	public void setOu(String ou) {
		this.ou = ou;
	}
	
	
	
}
