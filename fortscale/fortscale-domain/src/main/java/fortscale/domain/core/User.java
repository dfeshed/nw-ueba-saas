package fortscale.domain.core;

import java.util.Collections;
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
	
	
	@Indexed(unique = true)
	@JsonProperty
	private String adDn;
	
	private String employeeID;
	

	

	private String firstname, lastname;

	@Field("email")
	@Indexed
	private EmailAddress emailAddress;
	
	private String managerDN;
	@Indexed
	private String adUserPrincipalName;
	
	private String telephoneNumber;

	private String mobile;
	
	private String position;
	
	private String department;
	
	@JsonProperty
	private Set<AdUserGroup> groups = new HashSet<AdUserGroup>();
	
	@Field(appField)
	@JsonProperty
	Map<String, ApplicationUserDetails> appUserDetailsMap = new HashMap<>();
	
	
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
	
	public String getEmployeeID() {
		return employeeID;
	}

	public void setEmployeeID(String employeeID) {
		this.employeeID = employeeID;
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
	
	public void addGroup(AdUserGroup adUserGroup) {

		Assert.notNull(adUserGroup);
		this.groups.add(adUserGroup);
	}
	
	public Set<AdUserGroup> getGroups() {
		return Collections.unmodifiableSet(groups);
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

	public HashMap<String, ClassifierScore> getScores() {
		return scores;
	}
	
	public ClassifierScore getScore(String classifierId) {
		return scores.get(classifierId);
	}

	public void putClassifierScore(ClassifierScore score) {
		this.scores.put(score.getClassifierId(), score);
	}	
	
	
}
