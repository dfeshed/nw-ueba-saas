package fortscale.domain.core;

import java.util.Collections;
import java.util.Date;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

import org.codehaus.jackson.annotate.JsonCreator;
import org.codehaus.jackson.annotate.JsonProperty;
import org.springframework.data.annotation.PersistenceConstructor;
import org.springframework.data.mongodb.core.index.Indexed;
import org.springframework.data.mongodb.core.mapping.Document;
import org.springframework.data.mongodb.core.mapping.Field;
import org.springframework.util.Assert;

import fortscale.domain.ad.AdUserGroup;







@Document
public class User extends AbstractDocument {	
	
	public static final String appField = "app";
	public static final String logUsernameField = "logUsername";
	public static final String usernameField = "username";
	public static final String classifierScoreField = "scores";
	public static final String followedField = "followed";
	
	
	@Indexed(unique = true)
	@JsonProperty
	private String adDn;
	
	private String adEmployeeID;
	
	private String adEmployeeNumber;
	

	private String firstname, lastname;

	@Field("email")
	@Indexed
	private EmailAddress emailAddress;
	
	private String managerDN;
	@Indexed
	@Field(usernameField)
	private String username;
	@Indexed
	private String adUserPrincipalName;

	private String adSAMAccountName;
	
	private String telephoneNumber;
	
	private String otherFacsimileTelephoneNumber;
	
	private String otherHomePhone;
	
	private String homePhone;
	
	private String otherMobile;
	
	private String mobile;
	
	private String otherTelephone;

	private String thumbnailPhoto;
	
	private String position;
	
	private String department;
	
	private String adDisplayName;
	
	private Date accountExpires;
	
	private Integer adUserAccountControl;
	
	private String adLogonHours;
	
	private Date adWhenChanged;
	
	private Date adWhenCreated;
	
	private String adDescription;
	
	private String adStreetAddress;
		
	private String adCompany;
	
	private String adC;
				
	private String adDivision;
			
	private String adL;
	
	private String adO;
	
	private String adRoomNumber;
	@Field(followedField)
	private Boolean followed = false;
	
	
	
	
	
	
	
	
	@JsonProperty
	private Set<AdUserDirectReport> adDirectReports = new HashSet<AdUserDirectReport>();
	
	@JsonProperty
	private Set<AdUserGroup> groups = new HashSet<AdUserGroup>();
	
	@Field(appField)
	@JsonProperty
	Map<String, ApplicationUserDetails> appUserDetailsMap = new HashMap<>();
	
	@Field(logUsernameField)
	@JsonProperty
	Map<String, String> logUsernameMap = new HashMap<>();
	
	@Indexed
	@Field(classifierScoreField)
	private HashMap<String, ClassifierScore> scores = new HashMap<String, ClassifierScore>();
	
	@Field("sf")
	@Indexed
	private String searchField;

	/**
	 * Creates a new {@link User} from the given adDn.
	 * 
	 * @param adDn must not be {@literal null} or empty.
	 */
	@PersistenceConstructor
	@JsonCreator
	public User(@JsonProperty("adDn") String adDn) {

		Assert.hasText(adDn);

		this.adDn = adDn;
	}
	
	
	public String getSearchField() {
		return searchField;
	}

	public void setSearchField(String searchField) {
		this.searchField = searchField;
	}

	public String getAdDn() {
		return adDn;
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
	
	/**
	 * Returns the {@link EmailAddress} of the {@link Customer}.
	 * 
	 * @return
	 */
	public EmailAddress getEmailAddress() {
		return emailAddress;
	}

	/**
	 * Sets the {@link Customer}'s {@link EmailAddress}.
	 * 
	 * @param emailAddress must not be {@literal null}.
	 */
	public void setEmailAddress(EmailAddress emailAddress) {
		this.emailAddress = emailAddress;
	}
	
	public String getAdEmployeeID() {
		return adEmployeeID;
	}

	public void setAdEmployeeID(String employeeID) {
		this.adEmployeeID = employeeID;
	}

	public String getManagerDN() {
		return managerDN;
	}

	public void setManagerDN(String managerDN) {
		this.managerDN = managerDN;
	}

	public String getAdUserPrincipalName() {
		return adUserPrincipalName;
	}

	public void setAdUserPrincipalName(String adUserPrincipalName) {
		if(adUserPrincipalName != null && adUserPrincipalName.length() > 0){
			this.adUserPrincipalName = adUserPrincipalName;
		}
	}
	
	public String getUsername() {
		return username;
	}


	public void setUsername(String username) {
		this.username = username;
	}


	public String getAdSAMAccountName() {
		return adSAMAccountName;
	}


	public void setAdSAMAccountName(String adSAMAccountName) {
		this.adSAMAccountName = adSAMAccountName;
	}


	public String getTelephoneNumber() {
		return telephoneNumber;
	}

	public void setTelephoneNumber(String telephoneNumber) {
		this.telephoneNumber = telephoneNumber;
	}

	public String getMobile() {
		return mobile;
	}

	public void setMobile(String mobile) {
		this.mobile = mobile;
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


	public String getOtherTelephone() {
		return otherTelephone;
	}


	public void setOtherTelephone(String otherTelephone) {
		this.otherTelephone = otherTelephone;
	}


	public String getThumbnailPhoto() {
		return thumbnailPhoto;
	}

	public void setThumbnailPhoto(String thumbnailPhoto) {
		this.thumbnailPhoto = thumbnailPhoto;
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
	
	public String getAdEmployeeNumber() {
		return adEmployeeNumber;
	}


	public void setAdEmployeeNumber(String adEmployeeNumber) {
		this.adEmployeeNumber = adEmployeeNumber;
	}


	public String getAdDisplayName() {
		return adDisplayName;
	}


	public void setAdDisplayName(String adDisplayName) {
		this.adDisplayName = adDisplayName;
	}


	public Date getAccountExpires() {
		return accountExpires;
	}


	public void setAccountExpires(Date accountExpires) {
		this.accountExpires = accountExpires;
	}


	public Integer getAdUserAccountControl() {
		return adUserAccountControl;
	}


	public void setAdUserAccountControl(Integer adUserAccountControl) {
		this.adUserAccountControl = adUserAccountControl;
	}


	public String getAdLogonHours() {
		return adLogonHours;
	}


	public void setAdLogonHours(String adLogonHours) {
		this.adLogonHours = adLogonHours;
	}


	public Date getAdWhenChanged() {
		return adWhenChanged;
	}


	public void setAdWhenChanged(Date adWhenChanged) {
		this.adWhenChanged = adWhenChanged;
	}


	public Date getAdWhenCreated() {
		return adWhenCreated;
	}


	public void setAdWhenCreated(Date adWhenCreated) {
		this.adWhenCreated = adWhenCreated;
	}


	public String getAdDescription() {
		return adDescription;
	}


	public void setAdDescription(String adDescription) {
		this.adDescription = adDescription;
	}


	public String getAdStreetAddress() {
		return adStreetAddress;
	}


	public void setAdStreetAddress(String adStreetAddress) {
		this.adStreetAddress = adStreetAddress;
	}


	public String getAdCompany() {
		return adCompany;
	}


	public void setAdCompany(String adCompany) {
		this.adCompany = adCompany;
	}


	public String getAdC() {
		return adC;
	}


	public void setAdC(String adC) {
		this.adC = adC;
	}


	public String getAdDivision() {
		return adDivision;
	}


	public void setAdDivision(String adDivision) {
		this.adDivision = adDivision;
	}


	public String getAdL() {
		return adL;
	}


	public void setAdL(String adL) {
		this.adL = adL;
	}


	public String getAdO() {
		return adO;
	}


	public void setAdO(String adO) {
		this.adO = adO;
	}
	

	public String getAdRoomNumber() {
		return adRoomNumber;
	}


	public void setAdRoomNumber(String adRoomNumber) {
		this.adRoomNumber = adRoomNumber;
	}


	public void setAdDn(String adDn) {
		this.adDn = adDn;
	}

	public Boolean getFollowed() {
		return followed;
	}


	public void setFollowed(Boolean followed) {
		this.followed = followed;
	}


	public void setAdDirectReports(Set<AdUserDirectReport> adDirectReports) {
		this.adDirectReports = adDirectReports;
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
	
	public Set<AdUserGroup> getGroups() {
		return Collections.unmodifiableSet(groups);
	}
	
	public void addAdDirectReport(AdUserDirectReport adUserDirectReport) {

		Assert.notNull(adUserDirectReport);
		this.adDirectReports.add(adUserDirectReport);
	}
	
	public void clearAdDirectReport(){
		adDirectReports.clear();
	}
	
	public Set<AdUserDirectReport> getAdDirectReports() {
		return Collections.unmodifiableSet(adDirectReports);
	}
	
	public boolean containsApplicationUserDetails(ApplicationUserDetails applicationUserDetails) {
		return appUserDetailsMap.containsKey(applicationUserDetails.getApplicationName());
	}
	
	public void addApplicationUserDetails(ApplicationUserDetails applicationUserDetails) {
		Assert.notNull(applicationUserDetails);
		appUserDetailsMap.put(applicationUserDetails.getApplicationName(), applicationUserDetails);
	}
	
	public Map<String, ApplicationUserDetails> getApplicationUserDetails(){
		return appUserDetailsMap;
	}
	
	public boolean containsLogUsername(String logname) {
		return logUsernameMap.containsKey(logname);
	}
	
	public void addLogUsername(String logname, String username) {
		Assert.hasText(logname);
		Assert.hasText(username);
		logUsernameMap.put(logname, username);
	}
	
	public Map<String, String> getLogUsernameMap(){
		return logUsernameMap;
	}

	public HashMap<String, ClassifierScore> getScores() {
		return scores;
	}
	
	public ClassifierScore getScore(String classifierId) {
		return scores.get(classifierId);
	}

	public void putClassifierScore(ClassifierScore score) {
		this.scores.put(score.getClassifierId(), score);
	}	
	
	public void removeClassifierScore(String classifierId) {
		this.scores.remove(classifierId);
	}	
	
	public void removeAllScores(){
		this.scores.clear();
	}
	
	
	
	public static String getClassifierScoreCurrentTimestampField(String classifierId) {
		return String.format("%s.%s.%s", User.classifierScoreField, classifierId, ScoreInfo.timestampField);
	}
	
	public static String getClassifierScoreCurrentScoreField(String classifierId) {
		return String.format("%s.%s.%s", User.classifierScoreField, classifierId, ScoreInfo.scoreField);
	}
	
	public static String getClassifierScoreCurrentTrendField(String classifierId) {
		return String.format("%s.%s.%s", User.classifierScoreField, classifierId, ScoreInfo.trendField);
	}
	
	public static String getClassifierScoreCurrentTrendScoreField(String classifierId) {
		return String.format("%s.%s.%s", User.classifierScoreField, classifierId, ScoreInfo.trendScoreField);
	}
	
	public static String getAppUserNameField(String applicationName) {
		return String.format("%s.%s.%s", User.appField,applicationName,ApplicationUserDetails.userNameField);
	}
	
	public static String getLogUserNameField(String logname) {
		return String.format("%s.%s", User.logUsernameField,logname);
	}
}
