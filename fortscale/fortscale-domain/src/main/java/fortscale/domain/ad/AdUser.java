package fortscale.domain.ad;

import java.util.HashMap;
import java.util.Map;

import org.codehaus.jackson.annotate.JsonCreator;
import org.codehaus.jackson.annotate.JsonIgnore;
import org.codehaus.jackson.annotate.JsonProperty;
import org.springframework.data.annotation.PersistenceConstructor;
import org.springframework.data.annotation.Transient;
import org.springframework.data.mongodb.core.mapping.Document;
import org.springframework.data.mongodb.core.mapping.Field;



@Document(collection=AdUser.COLLECTION_NAME)
public class AdUser extends AdObject{
	public static final String COLLECTION_NAME = "ad_user";
	public static final String thumbnailPhotoField = "thumbnailPhoto";
	
	@Field("sn")
	private String lastname  = "";
	@Field("givenName")
	private String firstname  = "";

	@Field("mail")
	private String emailAddress  = "";
	
	private String isCriticalSystemObject = "";
	
	private Integer badPwdCount;
	
	private Integer logonCount;
	
	private Integer primaryGroupID;
	
	private Long sAMAccountType;
	
	private Integer userAccountControl;
	
	private String accountExpires = "";
	
	private String badPasswordTime = "";
	
	private String lastLogoff = "";
	
	private String lockoutTime = "";
	
	private String assistant = "";
	
	private String memberOf = "";
	
	private String managedObjects = "";
	
	private String manager = "";
	
	private String masteredBy = "";
	
	private String directReports = "";
	
	private String secretary = "";
	
	private String logonHours = "";
	
	private String whenChanged = "";
	
	private String streetAddress = "";
	
	private String cn = "";
	
	private String company = "";
	
	private String c = "";
	
	private String department = "";
	
	private String description = "";
	
	private String displayName = "";
	
	private String division = "";
	
	private String employeeID = "";
	
	private String employeeNumber = "";
	
	private String employeeType = "";
		
	private String l = "";
	
	private String o = "";
	
	private String personalTitle = "";
	
	private String otherFacsimileTelephoneNumber = "";
	
	private String otherHomePhone = "";
	
	private String homePhone = "";
	
	private String otherMobile = "";
	
	private String mobile = "";
	
	private String otherTelephone = "";
	
	private String roomNumber = "";
	
	private String userPrincipalName = "";
	
	private String telephoneNumber = "";
	
	private String title = "";
	
	private String userParameters = "";
	
	private String userWorkstations = "";
	
	private String lastLogon = "";
	
	private String pwdLastSet = "";
	
	private String whenCreated = "";
	
	private String sAMAccountName = "";
	
	private Long timestampepoch;
	
	// Contains the users's photo in Base64 format 
	@Field(thumbnailPhotoField)
	private String thumbnailPhoto = "";
	
	
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

	public Integer getBadPwdCount() {
		return badPwdCount;
	}

	public void setBadPwdCount(Integer badPwdCount) {
		this.badPwdCount = badPwdCount;
	}

	public Integer getLogonCount() {
		return logonCount;
	}

	public void setLogonCount(Integer logonCount) {
		this.logonCount = logonCount;
	}

	public Integer getPrimaryGroupID() {
		return primaryGroupID;
	}

	public void setPrimaryGroupID(Integer primaryGroupID) {
		this.primaryGroupID = primaryGroupID;
	}

	public Long getsAMAccountType() {
		return sAMAccountType;
	}

	public void setsAMAccountType(Long sAMAccountType) {
		this.sAMAccountType = sAMAccountType;
	}

	public Integer getUserAccountControl() {
		return userAccountControl;
	}

	public void setUserAccountControl(Integer userAccountControl) {
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

	public String getsAMAccountName() {
		return sAMAccountName;
	}

	public void setsAMAccountName(String sAMAccountName) {
		this.sAMAccountName = sAMAccountName;
	}

	
	public String getThumbnailPhoto() {
		return thumbnailPhoto;
	}

	public void setThumbnailPhoto(String tumbnailPhoto) {
		this.thumbnailPhoto = tumbnailPhoto;
	}

	


	@Transient
	private Map<String, String> attrVals;

	private void fillAttrVals() {
		this.attrVals = new HashMap<String,String>();
		
		this.attrVals.put("accountExpires",accountExpires);
		this.attrVals.put("assistant",assistant);
		this.attrVals.put("badPasswordTime",badPasswordTime);
		this.attrVals.put("badPwdCount",String.valueOf(badPwdCount));
		this.attrVals.put("c",c);
		this.attrVals.put("cn",cn);
		this.attrVals.put("company",company);
		this.attrVals.put("department",department);
		this.attrVals.put("description",description);
		this.attrVals.put("directReports",directReports);
		this.attrVals.put("displayName",displayName);
		this.attrVals.put("distinguishedName",this.getDistinguishedName());
		this.attrVals.put("division",division);
		this.attrVals.put("emailAddress",emailAddress);
		this.attrVals.put("emailAddress ",emailAddress );
		this.attrVals.put("employeeID",employeeID);
		this.attrVals.put("employeeNumber",employeeNumber);
		this.attrVals.put("employeeType",employeeType);
		this.attrVals.put("firstname",firstname);
		this.attrVals.put("homePhone",homePhone);
		this.attrVals.put("isCriticalSystemObject",isCriticalSystemObject);
		this.attrVals.put("l",l);
		this.attrVals.put("lastLogoff",lastLogoff);
		this.attrVals.put("lastLogon",lastLogon);
		this.attrVals.put("lastname",lastname);
		this.attrVals.put("lastname ",lastname );
		this.attrVals.put("lockoutTime",lockoutTime);
		this.attrVals.put("logonCount",String.valueOf(logonCount));
		this.attrVals.put("logonHours",logonHours);
		this.attrVals.put("managedObjects",managedObjects);
		this.attrVals.put("manager",manager);
		this.attrVals.put("masteredBy",masteredBy);
		this.attrVals.put("memberOf",memberOf);
		this.attrVals.put("mobile",mobile);
		this.attrVals.put("o",o);
		this.attrVals.put("otherFacsimileTelephoneNumber",otherFacsimileTelephoneNumber);
		this.attrVals.put("otherHomePhone",otherHomePhone);
		this.attrVals.put("otherMobile",otherMobile);
		this.attrVals.put("otherTelephone",otherTelephone);
		this.attrVals.put("personalTitle",personalTitle);
		this.attrVals.put("primaryGroupID",String.valueOf(primaryGroupID));
		this.attrVals.put("pwdLastSet",pwdLastSet);
		this.attrVals.put("roomNumber",roomNumber);
		this.attrVals.put("sAMAccountType",String.valueOf(sAMAccountType));
		this.attrVals.put("secretary",secretary);
		this.attrVals.put("streetAddress",streetAddress);
		this.attrVals.put("telephoneNumber",telephoneNumber);
		this.attrVals.put("title",title);
		this.attrVals.put("userAccountControl",String.valueOf(userAccountControl));
		this.attrVals.put("userParameters",userParameters);
		this.attrVals.put("userPrincipalName",userPrincipalName);
		this.attrVals.put("userWorkstations",userWorkstations);
		this.attrVals.put("whenChanged",whenChanged);
		this.attrVals.put("whenCreated",whenCreated);
	}
	
	
	public void updateAttrVals(Map<String, String> AttrVals) {
		this.setAccountExpires(attrVals.get("accountExpires"));
		this.setAssistant(attrVals.get("assistant"));
		this.setBadPasswordTime(attrVals.get("badPasswordTime"));
		this.setBadPwdCount(Integer.parseInt(attrVals.get("badPwdCount")));
		this.setC(attrVals.get("c"));
		this.setCn(attrVals.get("cn"));
		this.setCompany(attrVals.get("company"));
		this.setDepartment(attrVals.get("department"));
		this.setDescription(attrVals.get("description"));
		this.setDirectReports(attrVals.get("directReports"));
		this.setDisplayName(attrVals.get("displayName"));
		this.setDistinguishedName(attrVals.get("distinguishedName"));
		this.setDivision(attrVals.get("division"));
		this.setEmailAddress(attrVals.get("emailAddress"));
		this.setEmailAddress (attrVals.get("emailAddress "));
		this.setEmployeeID(attrVals.get("employeeID"));
		this.setEmployeeNumber(attrVals.get("employeeNumber"));
		this.setEmployeeType(attrVals.get("employeeType"));
		this.setFirstname (attrVals.get("firstname"));
		this.setHomePhone(attrVals.get("homePhone"));
		this.setIsCriticalSystemObject(attrVals.get("isCriticalSystemObject"));
		this.setL(attrVals.get("l"));
		this.setLastLogoff(attrVals.get("lastLogoff"));
		this.setLastLogon(attrVals.get("lastLogon"));
		this.setLastname(attrVals.get("lastname"));
		this.setLastname (attrVals.get("lastname "));
		this.setLockoutTime(attrVals.get("lockoutTime"));
		this.setLogonCount(Integer.parseInt(attrVals.get("logonCount")));
		this.setLogonHours(attrVals.get("logonHours"));
		this.setManagedObjects(attrVals.get("managedObjects"));
		this.setManager(attrVals.get("manager"));
		this.setMasteredBy(attrVals.get("masteredBy"));
		this.setMemberOf(attrVals.get("memberOf"));
		this.setMobile(attrVals.get("mobile"));
		this.setO(attrVals.get("o"));
		this.setOtherFacsimileTelephoneNumber(attrVals.get("otherFacsimileTelephoneNumber"));
		this.setOtherHomePhone(attrVals.get("otherHomePhone"));
		this.setOtherMobile(attrVals.get("otherMobile"));
		this.setOtherTelephone(attrVals.get("otherTelephone"));
		this.setPersonalTitle(attrVals.get("personalTitle"));
		this.setPrimaryGroupID(Integer.parseInt(attrVals.get("primaryGroupID")));
		this.setPwdLastSet(attrVals.get("pwdLastSet"));
		this.setRoomNumber(attrVals.get("roomNumber"));
		this.setsAMAccountType(Long.parseLong(attrVals.get("sAMAccountType")));
		this.setSecretary(attrVals.get("secretary"));
		this.setStreetAddress(attrVals.get("streetAddress"));
		this.setTelephoneNumber(attrVals.get("telephoneNumber"));
		this.setTitle(attrVals.get("title"));
		this.setUserAccountControl(Integer.parseInt(attrVals.get("userAccountControl")));
		this.setUserParameters(attrVals.get("userParameters"));
		this.setUserPrincipalName(attrVals.get("userPrincipalName"));
		this.setUserWorkstations(attrVals.get("userWorkstations"));
		this.setWhenChanged(attrVals.get("whenChanged"));
		this.setWhenCreated(attrVals.get("whenCreated"));
	}
	
	@JsonIgnore
	public Map<String, String> getAttrVals() {
		this.fillAttrVals();
		
		return attrVals;
	}

	public void setAttrVals(Map<String, String> attrVals) {
		this.attrVals = attrVals;
	}

	/**
	 * Creates a new {@link Customer} from the given distinguishedName.
	 * 
	 * @param distinguishedName must not be {@literal null} or empty.
	 */
	@PersistenceConstructor
	@JsonCreator
	public AdUser(@JsonProperty("distinguishedName") String distinguishedName) {
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

	public Long getTimestampepoch() {
		return timestampepoch;
	}

	public void setTimestampepoch(Long timestampepoch) {
		this.timestampepoch = timestampepoch;
	}
}
