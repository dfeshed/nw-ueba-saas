package fortscale.domain.analyst;

import org.springframework.data.mongodb.core.index.Indexed;
import org.springframework.data.mongodb.core.mapping.Document;
import org.springframework.data.mongodb.core.mapping.Field;
import org.springframework.util.Assert;

import fortscale.domain.core.AbstractDocument;
import fortscale.domain.core.EmailAddress;
import fortscale.domain.core.User;





@Document
public class Analyst extends AbstractDocument{
	
	
	/**
	 * 
	 */
	private static final long serialVersionUID = -5268282649328230835L;

	@Indexed(unique=true)
	private String userName;
	
	private String firstName;
	private String lastName;
	
	@Field("email")
	@Indexed(unique=true)
	private EmailAddress emailAddress;
	@Indexed
	private boolean isDisabled;
	
	
	
	/**
	 * Creates a new {@link User} from the given adDn.
	 * 
	 * @param userName must not be {@literal null} or empty.
	 * @param emailAddress must not be {@literal null}.
	 * @param firstName must not be {@literal null} or empty.
	 * @param lastName must not be {@literal null} or empty.
	 */
	public Analyst(String userName, EmailAddress emailAddress, String firstName, String lastName) {
		Assert.hasText(userName);
		Assert.notNull(emailAddress);
		Assert.hasText(firstName);
		Assert.hasText(lastName);

		this.userName = userName;
		this.emailAddress = emailAddress;
		this.firstName = firstName;
		this.lastName = lastName;
	}
	
	
	
	public String getFirstName() {
		return firstName;
	}
	public void setFirstName(String firstName) {
		this.firstName = firstName;
	}
	public String getLastName() {
		return lastName;
	}
	public void setLastName(String lastName) {
		this.lastName = lastName;
	}
	public String getUserName() {
		return userName;
	}
	public void setUserName(String userName) {
		this.userName = userName;
	}
	public EmailAddress getEmailAddress() {
		return emailAddress;
	}
	public void setEmailAddress(EmailAddress emailAddress) {
		this.emailAddress = emailAddress;
	}
	public boolean isDisabled() {
		return isDisabled;
	}
	public void setDisabled(boolean isDisabled) {
		this.isDisabled = isDisabled;
	}
}
