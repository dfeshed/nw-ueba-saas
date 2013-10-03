package fortscale.domain.ad;

import org.codehaus.jackson.annotate.JsonCreator;
import org.codehaus.jackson.annotate.JsonProperty;
import org.springframework.data.annotation.PersistenceConstructor;
import org.springframework.data.mongodb.core.mapping.Document;



@Document(collection=AdGroup.COLLECTION_NAME)
public class AdGroup extends AdObject{
	public static final String COLLECTION_NAME = "ad_group";

	private String name;
	private String isCriticalSystemObject;
	private String isDeleted;
	private String groupType;
	private String sAMAccountType;
	private String memberOf;
	private String managedBy;
	private String managedObjects;
	private String masteredBy;
	private String member;
	private String nonSecurityMember;
	private String nonSecurityMemberBL;
	private String directReports;
	private String secretary;
	private String whenChanged;
	private String whenCreated;
	private String accountNameHistory;
	private String cn;
	private String description;
	private String displayName;
	private String mail;
	private String sAMAccountName;
	
	/**
	 * Creates a new {@link Customer} from the given distinguishedName.
	 * 
	 * @param distinguishedName must not be {@literal null} or empty.
	 */
	@PersistenceConstructor
	@JsonCreator
	public AdGroup(@JsonProperty("distinguishedName") String distinguishedName) {
		super(distinguishedName);
	}
	
	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

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

	public String getGroupType() {
		return groupType;
	}

	public void setGroupType(String groupType) {
		this.groupType = groupType;
	}

	public String getsAMAccountType() {
		return sAMAccountType;
	}

	public void setsAMAccountType(String sAMAccountType) {
		this.sAMAccountType = sAMAccountType;
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

	public String getMember() {
		return member;
	}

	public void setMember(String member) {
		this.member = member;
	}

	public String getNonSecurityMember() {
		return nonSecurityMember;
	}

	public void setNonSecurityMember(String nonSecurityMember) {
		this.nonSecurityMember = nonSecurityMember;
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

	public String getSecretary() {
		return secretary;
	}

	public void setSecretary(String secretary) {
		this.secretary = secretary;
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

	public String getAccountNameHistory() {
		return accountNameHistory;
	}

	public void setAccountNameHistory(String accountNameHistory) {
		this.accountNameHistory = accountNameHistory;
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

	public String getDisplayName() {
		return displayName;
	}

	public void setDisplayName(String displayName) {
		this.displayName = displayName;
	}

	public String getMail() {
		return mail;
	}

	public void setMail(String mail) {
		this.mail = mail;
	}

	public String getsAMAccountName() {
		return sAMAccountName;
	}

	public void setsAMAccountName(String sAMAccountName) {
		this.sAMAccountName = sAMAccountName;
	}
	
	
}
