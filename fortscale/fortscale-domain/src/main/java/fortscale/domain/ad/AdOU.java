package fortscale.domain.ad;

import org.springframework.data.mongodb.core.mapping.Document;




@Document(collection= AdOU.COLLECTION_NAME)
public class AdOU extends AdObject{
	public static final String COLLECTION_NAME = "ad_ou";

	private static final long serialVersionUID = -7154327202343258119L;

	private String isCriticalSystemObject;
	private String isDeleted;
	private String defaultGroup;
	private String memberOf;
	private String managedBy;
	private String managedObjects;
	private String masteredBy;
	private String nonSecurityMemberBL;
	private String directReports;
	private String whenChanged;
	private String whenCreated;
	private String cn;
	private String c;
	private String description;
	private String displayName;
	private String l;
	private String ou;

	/**
	 * Creates a new {@link Customer} from the given distinguishedName.
	 * 
	 * @param distinguishedName must not be {@literal null} or empty.
	 */
//	@PersistenceConstructor
//	public AdOU(String distinguishedName) {
//		super(distinguishedName);
//	}

	public String getIsCriticalSystemObject() {
		return isCriticalSystemObject;
	}

	public void setIsCriticalSystemObject(String isCriticalSystemObject) {
		this.isCriticalSystemObject = isCriticalSystemObject;
	}

	public String getIsDeleted() {
		return isDeleted;
	}

	public void setIsDeleted(String isDeleted) {
		this.isDeleted = isDeleted;
	}

	public String getDefaultGroup() {
		return defaultGroup;
	}

	public void setDefaultGroup(String defaultGroup) {
		this.defaultGroup = defaultGroup;
	}

	public String getMemberOf() {
		return memberOf;
	}

	public void setMemberOf(String memberOf) {
		this.memberOf = memberOf;
	}

	public String getManagedBy() {
		return managedBy;
	}

	public void setManagedBy(String managedBy) {
		this.managedBy = managedBy;
	}

	public String getManagedObjects() {
		return managedObjects;
	}

	public void setManagedObjects(String managedObjects) {
		this.managedObjects = managedObjects;
	}

	public String getMasteredBy() {
		return masteredBy;
	}

	public void setMasteredBy(String masteredBy) {
		this.masteredBy = masteredBy;
	}

	public String getNonSecurityMemberBL() {
		return nonSecurityMemberBL;
	}

	public void setNonSecurityMemberBL(String nonSecurityMemberBL) {
		this.nonSecurityMemberBL = nonSecurityMemberBL;
	}

	public String getDirectReports() {
		return directReports;
	}

	public void setDirectReports(String directReports) {
		this.directReports = directReports;
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

	public String getC() {
		return c;
	}

	public void setC(String c) {
		this.c = c;
	}

	public String getDescription() {
		return description;
	}

	public void setDescription(String description) {
		this.description = description;
	}

	public String getDisplayName() {
		return displayName;
	}

	public void setDisplayName(String displayName) {
		this.displayName = displayName;
	}

	public String getL() {
		return l;
	}

	public void setL(String l) {
		this.l = l;
	}

	public String getOu() {
		return ou;
	}

	public void setOu(String ou) {
		this.ou = ou;
	}
}
