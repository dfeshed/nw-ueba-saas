package fortscale.domain.core;

import java.util.Collections;
import java.util.HashSet;
import java.util.Set;

import org.springframework.data.annotation.PersistenceConstructor;
import org.springframework.data.mongodb.core.index.Indexed;
import org.springframework.data.mongodb.core.mapping.Document;
import org.springframework.data.mongodb.core.mapping.Field;
import org.springframework.util.Assert;







@Document
public class User extends AbstractDocument {
	@Indexed(unique = true)
	private String username;
	
	@Indexed(unique = true)
	private String adDn;
	public String getAdDn() {
		return adDn;
	}

	private String firstname, lastname;

	@Field("email")
	@Indexed(unique = true)
	private EmailAddress emailAddress;
	private Set<Address> addresses = new HashSet<Address>();

	/**
	 * Creates a new {@link Customer} from the given username.
	 * 
	 * @param username must not be {@literal null} or empty.
	 */
	@PersistenceConstructor
	public User(String username, String adDn) {

		Assert.hasText(username);
		Assert.hasText(adDn);

		this.username = username;
		this.adDn = adDn;
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

	public String getUsername() {
		return username;
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

	/**
	 * Return the {@link Customer}'s addresses.
	 * 
	 * @return
	 */
	public Set<Address> getAddresses() {
		return Collections.unmodifiableSet(addresses);
	}
	
	/**
	 * Adds the given {@link Address} to the {@link Customer}.
	 * 
	 * @param address must not be {@literal null}.
	 */
	public void add(Address address) {

		Assert.notNull(address);
		this.addresses.add(address);
	}
}
