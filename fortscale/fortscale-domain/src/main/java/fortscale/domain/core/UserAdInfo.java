package fortscale.domain.core;

import java.util.Date;
import java.util.HashSet;
import java.util.Set;

import org.joda.time.DateTime;
import org.springframework.data.mongodb.core.mapping.Field;
import org.springframework.util.Assert;

import fortscale.domain.ad.AdUserGroup;

public class UserAdInfo {
	public static final String objectGUIDField = "objectGUID";
	public static final String adDnField = "dn";
	public static final String sAMAccountNameField = "sAMAccountName";
	public static final String emailAddressField = "email";
	public static final String lastnameField = "lastname";
	public static final String userPrincipalNameField = "userPrincipalName";
	public static final String groupsField = "groups";
	public static final String whenCreatedField = "whenCreated";
	public static final String disableAccountTimeField = "disableAccountTime";
	public static final String isAccountDisabledField = "isAccountDisabled";
	public static final String terminationDateField = "terminationDate";

	@Field(objectGUIDField)
	private String objectGUID;

	@Field(adDnField)
	private String dn;

	private String employeeID;

	private String employeeNumber;


	private String firstname;
	@Field(lastnameField)
	private String lastname;

	@Field(emailAddressField)
	private EmailAddress emailAddress;

	private String managerDN;

	@Field(userPrincipalNameField)
	private String userPrincipalName;

	private String sAMAccountName;

	private String telephoneNumber;

	private String otherFacsimileTelephoneNumber;

	private String otherHomePhone;

	private String homePhone;

	private String otherMobile;

	private String mobile;

	private String otherTelephone;

	private String position;

	private String department;

	private String displayName;

	private Date accountExpires;

	private Integer userAccountControl;

	private String logonHours;

	private Date whenChanged;

	@Field(whenCreatedField)
	private Date whenCreated;

	private String description;

	private String streetAddress;

	private String company;

	private String c;

	private String division;

	private String l;

	private String o;

	private String roomNumber;

	@Field(terminationDateField)
	private DateTime terminationDate;

	@Field(disableAccountTimeField)
	private DateTime disableAccountTime;

	@Field(isAccountDisabledField)
	private boolean isAccountDisabled;






	public String getObjectGUID() {
		return objectGUID;
	}

	public void setObjectGUID(String objectGUID) {
		this.objectGUID = objectGUID;
	}

	private Set<AdUserDirectReport> directReports = new HashSet<AdUserDirectReport>();

	@Field(groupsField)
	private Set<AdUserGroup> groups = new HashSet<AdUserGroup>();

	public String getDn() {
		return dn;
	}

	public void setDn(String dn) {
		this.dn = dn;
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

	public String getFirstname() {
		return firstname;
	}

	public void setFirstname(String firstname) {
		this.firstname = firstname;
	}

	public String getLastname() {
		return lastname;
	}

	public void setLastname(String lastname) {
		this.lastname = lastname;
	}

	public EmailAddress getEmailAddress() {
		return emailAddress;
	}

	public void setEmailAddress(EmailAddress emailAddress) {
		this.emailAddress = emailAddress;
	}

	public String getManagerDN() {
		return managerDN;
	}

	public void setManagerDN(String managerDN) {
		this.managerDN = managerDN;
	}

	public String getUserPrincipalName() {
		return userPrincipalName;
	}

	public void setUserPrincipalName(String userPrincipalName) {
		this.userPrincipalName = userPrincipalName;
	}

	public String getsAMAccountName() {
		return sAMAccountName;
	}

	public void setsAMAccountName(String sAMAccountName) {
		this.sAMAccountName = sAMAccountName;
	}

	public String getTelephoneNumber() {
		return telephoneNumber;
	}

	public void setTelephoneNumber(String telephoneNumber) {
		this.telephoneNumber = telephoneNumber;
	}

	public String getOtherFacsimileTelephoneNumber() {
		return otherFacsimileTelephoneNumber;
	}

	public void setOtherFacsimileTelephoneNumber(String otherFacsimileTelephoneNumber) {
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

	public String getPosition() {
		return position;
	}

	public void setPosition(String position) {
		this.position = position;
	}

	public String getDepartment() {
		return department;
	}

	public void setDepartment(String department) {
		this.department = department;
	}

	public String getDisplayName() {
		return displayName;
	}

	public void setDisplayName(String displayName) {
		this.displayName = displayName;
	}

	public Date getAccountExpires() {
		return accountExpires;
	}

	public void setAccountExpires(Date accountExpires) {
		this.accountExpires = accountExpires;
	}

	public Integer getUserAccountControl() {
		return userAccountControl;
	}

	public void setUserAccountControl(Integer userAccountControl) {
		this.userAccountControl = userAccountControl;
	}

	public String getLogonHours() {
		return logonHours;
	}

	public void setLogonHours(String logonHours) {
		this.logonHours = logonHours;
	}

	public Date getWhenChanged() {
		return whenChanged;
	}

	public void setWhenChanged(Date whenChanged) {
		this.whenChanged = whenChanged;
	}

	public Date getWhenCreated() {
		return whenCreated;
	}

	public void setWhenCreated(Date whenCreated) {
		this.whenCreated = whenCreated;
	}

	public String getDescription() {
		return description;
	}

	public void setDescription(String description) {
		this.description = description;
	}

	public String getStreetAddress() {
		return streetAddress;
	}

	public void setStreetAddress(String streetAddress) {
		this.streetAddress = streetAddress;
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

	public String getDivision() {
		return division;
	}

	public void setDivision(String division) {
		this.division = division;
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

	public String getRoomNumber() {
		return roomNumber;
	}

	public void setRoomNumber(String roomNumber) {
		this.roomNumber = roomNumber;
	}

	public Set<AdUserDirectReport> getDirectReports() {
		return directReports;
	}

	public void setAdDirectReports(Set<AdUserDirectReport> directReports) {
		this.directReports = directReports;
	}

	public Set<AdUserGroup> getGroups() {
		return groups;
	}

	public void setGroups(Set<AdUserGroup> groups) {
		this.groups = groups;
	}




	public void addGroup(AdUserGroup adUserGroup) {

		Assert.notNull(adUserGroup);
		this.groups.add(adUserGroup);
	}

	public void clearGroups(){
		groups.clear();
	}


	public void addDirectReport(AdUserDirectReport userDirectReport) {

		Assert.notNull(userDirectReport);
		this.directReports.add(userDirectReport);
	}

	public void clearDirectReport(){
		directReports.clear();
	}

	public DateTime getDisableAccountTime() {
		return disableAccountTime;
	}

	public void setDisableAccountTime(DateTime disableAccountTime) {
		this.disableAccountTime = disableAccountTime;
	}

	public boolean getIsAccountDisabled() {
		return isAccountDisabled;
	}

	public void setIsAccountDisabled(boolean isAccountIsDisabled) {
		this.isAccountDisabled = isAccountIsDisabled;
	}


	public DateTime getTerminationDate() {
		return terminationDate;
	}

	public void setTerminationDate(DateTime terminationDate) {
		this.terminationDate = terminationDate;
	}
}
