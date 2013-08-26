package fortscale.domain.ad;

import java.util.Map;

import org.springframework.data.annotation.PersistenceConstructor;
import org.springframework.data.annotation.Transient;
import org.springframework.data.mongodb.core.mapping.Document;
import org.springframework.data.mongodb.core.mapping.Field;



@Document(collection="ad_user")
public class AdUser extends AdObject{
	
	
	@Field("sn")
	private String lastname;
	@Field("givenName")
	private String firstname;

	@Field("mail")
	private String emailAddress;
	
	private String TimeStamp;
	
	private String isCriticalSystemObject;
	
	private String badPwdCount;
	
	private String logonCount;
	
	private String primaryGroupID;
	
	private String sAMAccountType;
	
	private String userAccountControl;
	
	private String accountExpires;
	
	private String badPasswordTime;
	
	private String lastLogoff;
	
	private String lockoutTime;
	
	private String assistant;
	
	private String memberOf;
	
	private String managedObjects;
	
	private String manager;
	
	private String masteredBy;
	
	private String directReports;
	
	private String secretary;
	
	private String logonHours;
	
	private String whenChanged;
	
	private String streetAddress;
	
	private String cn;
	
	private String company;
	
	private String c;
	
	private String department;
	
	private String description;
	
	private String displayName;
	
	private String division;
	
	private String employeeID;
	
	private String employeeNumber;
	
	private String employeeType;
		
	private String l;
	
	private String o;
	
	private String personalTitle;
	
	private String otherFacsimileTelephoneNumber;
	
	private String otherHomePhone;
	
	private String homePhone;
	
	private String otherMobile;
	
	private String mobile;
	
	private String otherTelephone;
	
	private String roomNumber;
	
	private String userPrincipalName;
	
	private String telephoneNumber;
	
	private String title;
	
	private String userParameters;
	
	private String userWorkstations;
	
	private String lastLogon;
	
	private String pwdLastSet;
	
	private String whenCreated;
		
	
	
	public String getWhenChanged() {
		return whenChanged;
	}

	public void setWhenChanged(String whenChanged) {
		this.whenChanged = whenChanged;
	}

	public String getUserPrincipalName() {
		return userPrincipalName;
	}

	public void setUserPrincipalName(String userPrincipalName) {
		this.userPrincipalName = userPrincipalName;
	}

	public String getUserWorkstations() {
		return userWorkstations;
	}

	public void setUserWorkstations(String userWorkstations) {
		this.userWorkstations = userWorkstations;
	}

	public String getWhenCreated() {
		return whenCreated;
	}

	public void setWhenCreated(String whenCreated) {
		this.whenCreated = whenCreated;
	}

	public String getIsCriticalSystemObject() {
		return isCriticalSystemObject;
	}

	public void setIsCriticalSystemObject(String isCriticalSystemObject) {
		this.isCriticalSystemObject = isCriticalSystemObject;
	}

	public String getBadPwdCount() {
		return badPwdCount;
	}

	public void setBadPwdCount(String badPwdCount) {
		this.badPwdCount = badPwdCount;
	}

	public String getLogonCount() {
		return logonCount;
	}

	public void setLogonCount(String logonCount) {
		this.logonCount = logonCount;
	}

	public String getPrimaryGroupID() {
		return primaryGroupID;
	}

	public void setPrimaryGroupID(String primaryGroupID) {
		this.primaryGroupID = primaryGroupID;
	}

	public String getsAMAccountType() {
		return sAMAccountType;
	}

	public void setsAMAccountType(String sAMAccountType) {
		this.sAMAccountType = sAMAccountType;
	}

	public String getUserAccountControl() {
		return userAccountControl;
	}

	public void setUserAccountControl(String userAccountControl) {
		this.userAccountControl = userAccountControl;
	}

	public String getAccountExpires() {
		return accountExpires;
	}

	public void setAccountExpires(String accountExpires) {
		this.accountExpires = accountExpires;
	}

	public String getBadPasswordTime() {
		return badPasswordTime;
	}

	public void setBadPasswordTime(String badPasswordTime) {
		this.badPasswordTime = badPasswordTime;
	}

	public String getLastLogoff() {
		return lastLogoff;
	}

	public void setLastLogoff(String lastLogoff) {
		this.lastLogoff = lastLogoff;
	}

	public String getLockoutTime() {
		return lockoutTime;
	}

	public void setLockoutTime(String lockoutTime) {
		this.lockoutTime = lockoutTime;
	}

	public String getAssistant() {
		return assistant;
	}

	public void setAssistant(String assistant) {
		this.assistant = assistant;
	}

	public String getMemberOf() {
		return memberOf;
	}

	public void setMemberOf(String memberOf) {
		this.memberOf = memberOf;
	}

	public String getManagedObjects() {
		return managedObjects;
	}

	public void setManagedObjects(String managedObjects) {
		this.managedObjects = managedObjects;
	}

	public String getManager() {
		return manager;
	}

	public void setManager(String manager) {
		this.manager = manager;
	}

	public String getMasteredBy() {
		return masteredBy;
	}

	public void setMasteredBy(String masteredBy) {
		this.masteredBy = masteredBy;
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

	public String getLogonHours() {
		return logonHours;
	}

	public void setLogonHours(String logonHours) {
		this.logonHours = logonHours;
	}

	public String getStreetAddress() {
		return streetAddress;
	}

	public void setStreetAddress(String streetAddress) {
		this.streetAddress = streetAddress;
	}

	public String getCn() {
		return cn;
	}

	public void setCn(String cn) {
		this.cn = cn;
	}

	public String getCompany() {
		return company;
	}

	public void setCompany(String company) {
		this.company = company;
	}

	public String getC() {
		return c;
	}

	public void setC(String c) {
		this.c = c;
	}

	public String getDepartment() {
		return department;
	}

	public void setDepartment(String department) {
		this.department = department;
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

	public String getDivision() {
		return division;
	}

	public void setDivision(String division) {
		this.division = division;
	}

	public String getEmployeeID() {
		return employeeID;
	}

	public void setEmployeeID(String employeeID) {
		this.employeeID = employeeID;
	}

	public String getEmployeeNumber() {
		return employeeNumber;
	}

	public void setEmployeeNumber(String employeeNumber) {
		this.employeeNumber = employeeNumber;
	}

	public String getEmployeeType() {
		return employeeType;
	}

	public void setEmployeeType(String employeeType) {
		this.employeeType = employeeType;
	}

	public String getL() {
		return l;
	}

	public void setL(String l) {
		this.l = l;
	}

	public String getO() {
		return o;
	}

	public void setO(String o) {
		this.o = o;
	}

	public String getPersonalTitle() {
		return personalTitle;
	}

	public void setPersonalTitle(String personalTitle) {
		this.personalTitle = personalTitle;
	}

	public String getOtherFacsimileTelephoneNumber() {
		return otherFacsimileTelephoneNumber;
	}

	public void setOtherFacsimileTelephoneNumber(
			String otherFacsimileTelephoneNumber) {
		this.otherFacsimileTelephoneNumber = otherFacsimileTelephoneNumber;
	}

	public String getOtherHomePhone() {
		return otherHomePhone;
	}

	public void setOtherHomePhone(String otherHomePhone) {
		this.otherHomePhone = otherHomePhone;
	}

	public String getHomePhone() {
		return homePhone;
	}

	public void setHomePhone(String homePhone) {
		this.homePhone = homePhone;
	}

	public String getOtherMobile() {
		return otherMobile;
	}

	public void setOtherMobile(String otherMobile) {
		this.otherMobile = otherMobile;
	}

	public String getMobile() {
		return mobile;
	}

	public void setMobile(String mobile) {
		this.mobile = mobile;
	}

	public String getOtherTelephone() {
		return otherTelephone;
	}

	public void setOtherTelephone(String otherTelephone) {
		this.otherTelephone = otherTelephone;
	}

	public String getRoomNumber() {
		return roomNumber;
	}

	public void setRoomNumber(String roomNumber) {
		this.roomNumber = roomNumber;
	}

	public String getTelephoneNumber() {
		return telephoneNumber;
	}

	public void setTelephoneNumber(String telephoneNumber) {
		this.telephoneNumber = telephoneNumber;
	}

	public String getTitle() {
		return title;
	}

	public void setTitle(String title) {
		this.title = title;
	}

	public String getUserParameters() {
		return userParameters;
	}

	public void setUserParameters(String userParameters) {
		this.userParameters = userParameters;
	}

	public String getLastLogon() {
		return lastLogon;
	}

	public void setLastLogon(String lastLogon) {
		this.lastLogon = lastLogon;
	}

	public String getPwdLastSet() {
		return pwdLastSet;
	}

	public void setPwdLastSet(String pwdLastSet) {
		this.pwdLastSet = pwdLastSet;
	}

	@Transient
	private Map<String, String> attrVals;

	public Map<String, String> getAttrVals() {
		return attrVals;
	}

	public void setAttrVals(Map<String, String> attrVals) {
		this.attrVals = attrVals;
	}

	/**
	 * Creates a new {@link Customer} from the given username.
	 * 
	 * @param username must not be {@literal null} or empty.
	 */
	@PersistenceConstructor
	public AdUser(String distinguishedName) {
		super(distinguishedName);
	}

	public String getLastname() {
		return lastname;
	}

	public void setLastname(String lastname) {
		this.lastname = lastname;
	}

	public String getFirstname() {
		return firstname;
	}

	public void setFirstname(String firstname) {
		this.firstname = firstname;
	}

	public String getEmailAddress() {
		return emailAddress;
	}

	public void setEmailAddress(String emailAddress) {
		this.emailAddress = emailAddress;
	}

	public String getTimeStamp() {
		return TimeStamp;
	}

	public void setTimeStamp(String timeStamp) {
		TimeStamp = timeStamp;
	}
}
