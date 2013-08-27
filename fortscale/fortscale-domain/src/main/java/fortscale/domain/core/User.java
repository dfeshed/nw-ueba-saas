package fortscale.domain.core;

import org.springframework.data.annotation.PersistenceConstructor;
import org.springframework.data.mongodb.core.index.Indexed;
import org.springframework.data.mongodb.core.mapping.Document;
import org.springframework.data.mongodb.core.mapping.Field;
import org.springframework.util.Assert;







@Document
public class User extends AbstractDocument {	
	@Indexed(unique = true)
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
	
	@Field("sf")
	@Indexed
	private String searchField;

	/**
	 * Creates a new {@link Customer} from the given adDn.
	 * 
	 * @param adDn must not be {@literal null} or empty.
	 */
	@PersistenceConstructor
	public User(String adDn) {

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
}
